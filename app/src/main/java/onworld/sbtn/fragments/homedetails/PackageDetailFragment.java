package onworld.sbtn.fragments.homedetails;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onworld.sbtn.MyApplication;
import onworld.sbtn.R;
import onworld.sbtn.activities.DetailActivity;
import onworld.sbtn.activities.LoginActivity;
import onworld.sbtn.activities.MusicPlayerActivity;
import onworld.sbtn.activities.PackageActivity;
import onworld.sbtn.adapters.PackageContentHorizontalHolder;
import onworld.sbtn.adapters.PackageContentVerticalApdater;
import onworld.sbtn.billingutils.IabHelper;
import onworld.sbtn.billingutils.IabResult;
import onworld.sbtn.billingutils.Inventory;
import onworld.sbtn.billingutils.Purchase;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.DataDetailLoadedListener;
import onworld.sbtn.fragments.homes.HomeFragment;
import onworld.sbtn.fragments.homes.OWLoginFragment;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.GroupPackage;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.josonmodel.Shows;
import onworld.sbtn.josonmodel.ShowsHome;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.tasks.TaskLoadDataDetail;
import onworld.sbtn.utils.CommonUtils;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.HeaderHelper;
import onworld.sbtn.utils.DateTimeUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class PackageDetailFragment extends Fragment implements RecyclerView.OnItemTouchListener,
        DataDetailLoadedListener,
        PackageContentVerticalApdater.PackageChildAdapterBuyListener {
    public static final String PACKAGE_ID = "package.id";
    public static final String PACKAGE_DETAIL = "package.detail";
    public static final String PACKAGE_DETAIL_BUNDLE = "package.detail.bundle";
    public static final String PACKAGE_DETAIL_PARENT_TITLE = "parent.title";
    public static final String PACKAGE_DETAIL_PARENT_DESCRIPTION = "parent.description";
    public static final int PACKAGE_DETAIL_FRAGMENT_REQUEST_CODE = 2220;
    public static final int RC_REQUEST = 10001;
    public static final String TAG = "PackageDetailFragment";
    private String requestToken;
    private RecyclerView recyclerView;
    private PackageContentVerticalApdater packageDetailAdapter;
    private ArrayList<Shows> showses;
    private GroupPackage mPackageDetail;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private boolean reRequestData;
    private String urlPackageDetail;
    private String inAppId = "android.test.purchased";
    //private String inAppId;
    private GestureDetector gestureDetector;
    private IabHelper mHelper;
    private CacheDataManager mCacheDataManager;
    private ProgressBar packageDetailLoading;
    private String payLoad = "ottapi.com/sbtn";
    private int packageIdDetail, packageId;
    private ArrayList<PackageDetail> myPackageDetails;
    private PackageDetail firstPackageDetail;
    private int sizePackageChild;
    private String parentTitle, parentDescription;
    private int positionBuyPackage;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            if (mHelper == null) return;

            if (result.isSuccess()) {
                updatePackageStatusToServer(purchase.getOrderId(), packageId + "");
                //show loading verifying


            } else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (mHelper == null) return;

            if (result.isFailure()) {
                //complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            if (purchase.getSku().equals(inAppId)) {
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            for (int i = 0; i < myPackageDetails.size(); i++) {
                PackageDetail packageData = myPackageDetails.get(i);
                //inAppId = packageData.getProductId();
                Purchase gasPurchase = inventory.getPurchase(inAppId);
                if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                    try {
                        mHelper.consumeAsync(inventory.getPurchase(inAppId), mConsumeFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            /*inAppId = mPackageDetail.getProductId();
            Purchase gasPurchase = inventory.getPurchase(inAppId);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(inAppId), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                return;
            }*/

        }
    };

    public PackageDetailFragment() {
        // Required empty public constructor
    }

    public static PackageDetailFragment newInstance(GroupPackage packageDetail, String parentTitle, String parentDescription, int packageId) {
        PackageDetailFragment packageDetailFragment = new PackageDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PACKAGE_ID, packageId);
        bundle.putParcelable(PACKAGE_DETAIL, packageDetail);
        bundle.putString(PACKAGE_DETAIL_PARENT_TITLE, parentTitle);
        bundle.putString(PACKAGE_DETAIL_PARENT_DESCRIPTION, parentDescription);
        packageDetailFragment.setArguments(bundle);
        return packageDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_detail, container, false);
        packageDetailLoading = (ProgressBar) view.findViewById(R.id.package_detail_loading);
        recyclerView = (RecyclerView) view.findViewById(R.id.package_detail_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reRequestData = false;
        packageIdDetail = getArguments().getInt(PACKAGE_ID);
        mPackageDetail = getArguments().getParcelable(PACKAGE_DETAIL);
        parentTitle = getArguments().getString(PACKAGE_DETAIL_PARENT_TITLE);
        parentDescription = getArguments().getString(PACKAGE_DETAIL_PARENT_DESCRIPTION);

        myPackageDetails = mPackageDetail.getItems();
        firstPackageDetail = myPackageDetails.get(0);
        sizePackageChild = myPackageDetails.size();
        packageDetailAdapter = new PackageContentVerticalApdater(getActivity());
        packageDetailAdapter.setPackageChildAdapterBuyListener(this);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .paintProvider(packageDetailAdapter)
                .visibilityProvider(packageDetailAdapter)
                .marginProvider(packageDetailAdapter)
                .build());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(packageDetailAdapter);
        urlPackageDetail = URL.PACKAGE_LIST_DETAIL + packageIdDetail;

        new TaskLoadDataDetail(this, urlPackageDetail).execute();
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        setUpBillingInapp();
        mCacheDataManager = CacheDataManager.getInstance(getActivity());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = recyclerView.getChildAdapterPosition(child);
            if (position == 0) {

            } else if (position <= sizePackageChild) {

            } else {
                if (showses != null && showses.size() > 0) {
                    Shows shows = showses.get(position - 1 - sizePackageChild);
                    if (shows.isHeader()) {
                        ArrayList<DataDetailItem> dataDetailItems = shows.getShowsDetails();
                        if (dataDetailItems.size() > 6) {
                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                            intent.putParcelableArrayListExtra(DetailActivity.ASGRID_DETAIL_DATA, dataDetailItems);
                            intent.putExtra(DetailActivity.ASGRID_DETAIL_TITLE, shows.getShowsName());
                            intent.putExtra(HomeFragment.AS_HOME_TYPE, shows.getMode());
                            startActivity(intent);
                        }
                    } else {
                        PackageContentHorizontalHolder homeRecyclerHorizontalHolder =
                                (PackageContentHorizontalHolder) recyclerView.findViewHolderForAdapterPosition(position);
                        int childPosition = homeRecyclerHorizontalHolder.getChildItemPosition(e.getX() - child.getX(), e.getY() - child.getY());
                        if (childPosition < 0) {
                            return false;
                        }
                        if (showses != null & showses.size() > 0) {
                            ArrayList<DataDetailItem> showsDetails = shows.getShowsDetails();
                            DataDetailItem dataDetailItem = showsDetails.get(childPosition);
                            final String url = URL.DETAIL + dataDetailItem.getId();
                            Intent intent;
                            if (shows.getMode() == HomeFragment.AUDIO_TYPE) {
                                intent = new Intent(getActivity(), MusicPlayerActivity.class);
                                intent.putExtra(MusicPlayerActivity.THUMB, dataDetailItem.getImage());

                            } else {
                                intent = new Intent(getActivity(), LocalPlayerActivity.class);
                                intent.putExtra(LocalPlayerActivity.SHOULD_START, false);
                            }
                            intent.putExtra(Constant.DETAIL_ID_KEY, dataDetailItem.getId());

                            startActivity(intent);
                        }
                    }

                }

            }

        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.addOnItemTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerView.removeOnItemTouchListener(this);
    }

    @Override
    public void onDataDetailLoaded(String jsonObject) {
        packageDetailLoading.setVisibility(View.GONE);
        Gson gson = new Gson();
        ShowsHome packageContent = gson.fromJson(jsonObject.toString(), ShowsHome.class);
        ArrayList<Shows> showDetailList = packageContent.getShowses();
        showses = new ArrayList<>();
        boolean isBuy = packageContent.getIsBuy();
        if (showDetailList != null) {
            if (reRequestData) {
                if (isBuy == true) {
                    //check code or package
                    if (firstPackageDetail.getPromotion() == 0) {
                        packageDetailAdapter.setBuyToBoughtNoNotify();
                    } else {
                        packageDetailAdapter.setAddToAdded();
                    }

                }

            } else {
                int realShowSize = showDetailList.size() * 2;
                for (int i = 0; i < realShowSize; i++) {
                    Shows tempShow = showDetailList.get(i / 2);
                    Shows mShows;
                    if (i % 2 == 0) {
                        mShows = new Shows(tempShow.getShowsDetails(), tempShow.getShowsId(), tempShow.getShowsName(), tempShow.getMode(), tempShow.getKaraoke(), true);
                    } else {
                        mShows = new Shows(tempShow.getShowsDetails(), tempShow.getShowsId(), tempShow.getShowsName(), tempShow.getMode(), tempShow.getKaraoke(), false);
                    }
                    this.showses.add(mShows);
                }
                firstPackageDetail.setBuy(isBuy);
                packageDetailAdapter.setHomeVerticalData(parentTitle, parentDescription, myPackageDetails, showses);
            }
        }

    }

    public void checkProCode(final String proCode) {
        Utils.hideSoftKeyboard(getActivity());
        final String packagePromotionUrl = URL.PACKAGE_PROMOTION;

        final String dateTime = DateTimeUtils.getCurrentUTCDateTime();
        final String agent = MyApplication.getUserAgent();

        final String authenticate = HeaderHelper.createAuthorizationValue("POST", dateTime,
                CommonUtils.getAccessTokenSecu(getActivity()), 78);
        requestToken = HeaderHelper.createRequestTokenValue("POST", dateTime, Constant.PRIVATE_KEY, 78);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                packagePromotionUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("error");
                    String message = jsonObject.getString("message");
                    if (code == Utils.OLD_REQUEST_SUCCESS) {
                        packageDetailAdapter.setAddToAdded();

                    } else {
                        VotingUtils.showAlertWithMessage(getActivity(), message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return super.getBody();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pk_id", firstPackageDetail.getId() + "");
                params.put("proCode", proCode);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("DateTime", dateTime);
                headers.put("Authorization", authenticate);
                headers.put("RequestToken", requestToken);
                headers.put("User-Agent", agent);

                return headers;
            }
        };

        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public void openDiaLogAskLogin(boolean isSignInFromPackage) {

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.msg_sign_in_to_view_content));

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(PackageActivity.IS_SIGN_IN_FROM_PACKAGE, true);
                startActivityForResult(intent, PACKAGE_DETAIL_FRAGMENT_REQUEST_CODE);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_REQUEST) {
            if (mHelper == null) return;

            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.d(TAG, "onActivityResult handled by IABUtil.");
            }
        } else if (requestCode == PACKAGE_DETAIL_FRAGMENT_REQUEST_CODE) {
            //update login status
            reRequestData = true;
            new TaskLoadDataDetail(this, urlPackageDetail).execute();

            Intent intent = getActivity().getIntent();
            intent.putExtra(OWLoginFragment.IS_LOGIN, true);
            getActivity().setResult(Constant.LOGIN_MAIN_RESULT_CODE, intent);
            //Login xong
            //new la code thi check code + verify code
            // neu la package thi request lai json roi check permistion
            // neu duoc xem thi button di + khong thi mua

           /* if (promotionCodeStatus == 0) {
                //buy package
                onBuyPackageListener(mPackageDetail);

            } else {
                //get code then post to server
                int proCodeLength = proCode.length();
                if (proCodeLength == 0) {
                    Toast.makeText(MyApplication.getAppContext(), "Please enter package code?", Toast.LENGTH_SHORT).show();
                } else if (proCodeLength > 0 && proCodeLength < 7 || proCodeLength > 10) {
                    Toast.makeText(MyApplication.getAppContext(), "Invalid package code.", Toast.LENGTH_SHORT).show();
                } else {
                    checkProCode(proCode);
                }
            }*/
        }
    }

    private void setUpBillingInapp() {
        String base64EncodedPublicKey = getResources().getString(R.string.sbtn_license_key);

        mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    complain("Billing service unavailable on device.");
                    return;
                }

                if (mHelper == null) return;
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    void complain(String message) {
        alert(message);
    }

    void alert(String message) {
        AlertDialog.Builder packageAlert = new AlertDialog.Builder(getActivity());
        packageAlert.setMessage(message);
        packageAlert.setNeutralButton("OK", null);
        packageAlert.create().show();
    }

    private void updatePackageStatusToServer(final String orderID, final String packageId) {
        mCacheDataManager.clearCachePackageData();
        mCacheDataManager.clearCacheYourContent();
        final String updatePaymentUrl = URL.UPDATE_PAYMENT;

        final String dateTime = DateTimeUtils.getCurrentUTCDateTime();
        final String agent = MyApplication.getUserAgent();

        final String authenticate = HeaderHelper.createAuthorizationValue("POST", dateTime,
                CommonUtils.getAccessTokenSecu(getActivity()), 78);
        requestToken = HeaderHelper.createRequestTokenValue("POST", dateTime, Constant.PRIVATE_KEY, 78);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                updatePaymentUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("error");
                    String message = jsonObject.getString("message");
                    if (code == Utils.OLD_REQUEST_SUCCESS) {
                        packageDetailAdapter.setBuyToBought(positionBuyPackage + 1);
                    } else {
                        VotingUtils.showAlertWithMessage(getActivity(), message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return super.getBody();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pkd_id", packageId);
                params.put("transactionId", orderID);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("DateTime", dateTime);
                headers.put("Authorization", authenticate);
                headers.put("RequestToken", requestToken);
                headers.put("User-Agent", agent);

                return headers;
            }
        };

        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public void onBuyPackageListener(int position) {
        positionBuyPackage = position;
        PackageDetail packageDetail = myPackageDetails.get(position);
        packageId = packageDetail.getPk_id();
        //inAppId = packageDetail.getProductId();
        try {
            mHelper.launchPurchaseFlow(getActivity(), inAppId, RC_REQUEST,
                    mPurchaseFinishedListener, payLoad);
        } catch (IabHelper.IabAsyncInProgressException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            mHelper = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onPackageChildAdapterBuyListener(int position, PackageDetail packageDetail, int promotionCodeStatus, String inputCode) {
        if (VotingUtils.checkLoginStatus(getActivity()) == true) {
            if (promotionCodeStatus == 0) {
                //buy package
                onBuyPackageListener(position);

            } else {
                //get code then post to server
                int proCodeLength = inputCode.length();
                if (proCodeLength == 0) {
                    Toast.makeText(MyApplication.getAppContext(), "Please enter package code?", Toast.LENGTH_SHORT).show();
                } else if (proCodeLength > 0 && proCodeLength < 7 || proCodeLength > 10) {
                    Toast.makeText(MyApplication.getAppContext(), "Invalid package code.", Toast.LENGTH_SHORT).show();
                } else {
                    checkProCode(inputCode);
                }
            }
        } else {
            openDiaLogAskLogin(true);
        }
    }
}
