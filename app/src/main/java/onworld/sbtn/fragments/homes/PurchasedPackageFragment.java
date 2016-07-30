package onworld.sbtn.fragments.homes;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import onworld.sbtn.adapters.PackagePurchasedListAdapter;
import onworld.sbtn.billingutils.IabHelper;
import onworld.sbtn.billingutils.IabResult;
import onworld.sbtn.billingutils.Inventory;
import onworld.sbtn.billingutils.Purchase;
import onworld.sbtn.callbacks.BoughtContentClickListener;
import onworld.sbtn.callbacks.DataDetailLoadedListener;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.josonmodel.PurchasedPackageData;
import onworld.sbtn.josonmodel.PurchasedPackageListDetailData;
import onworld.sbtn.tasks.TaskLoadDataDetail;
import onworld.sbtn.utils.CommonUtils;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.HeaderHelper;
import onworld.sbtn.utils.DateTimeUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PurchasedPackageFragment extends Fragment implements DataDetailLoadedListener, RecyclerView.OnItemTouchListener {
    public static final String TAG = "PurchasedPackageFragment";
    public static final int RC_REQUEST = 10111;
    private IabHelper mHelper;
    private RecyclerView mRecyclerView;
    private ImageView mEmptyBought;
    private PackagePurchasedListAdapter mPackagePurchasedListAdapter;
    private GestureDetector gestureDetector;
    private Context mContext;
    private ProgressBar mProgressBar;
    private LinearLayout emptyLayout;
    private ArrayList<PackageDetail> packageDetails = new ArrayList<>();
    private String requestToken;
    //private String inAppId = "android.test.purchased";
    private String inAppId;
    private int packageId;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            if (mHelper == null) return;

            if (result.isSuccess()) {
                updatePackageStatusToServer(purchase.getOrderId(), packageId + "");
                //show loading verifying
            } else {
                complain("Error while consuming: " + result);
            }
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

            for (int i = 0; i < packageDetails.size(); i++) {
                PackageDetail packageData = packageDetails.get(i);
                inAppId = packageData.getProductId();
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

        }
    };
    private String payLoad = "ottapi.com/sbtn";
    private Button mBuyContent;
    private BoughtContentClickListener boughtContentClickListener;

    public PurchasedPackageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchased_package, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boughtContentClickListener = (BoughtContentClickListener) getActivity();
        mProgressBar.setVisibility(View.VISIBLE);
        new TaskLoadDataDetail(this, URL.PACKAGE_LIST_PURCHASED).execute();
        setUpBillingInapp();
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

    private void initView(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.purchased_package_loading);
        emptyLayout = (LinearLayout) view.findViewById(R.id.purchased_package_layout_empty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.purchased_package_recyclerview);
        mBuyContent = (Button) view.findViewById(R.id.purchased_package_buy_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mPackagePurchasedListAdapter = new PackagePurchasedListAdapter(mContext);
        mRecyclerView.setAdapter(mPackagePurchasedListAdapter);

        mBuyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boughtContentClickListener.onBoughtBuyContentClickListener();
            }
        });
    }

    @Override
    public void onDataDetailLoaded(String jsonObject) {
        mProgressBar.setVisibility(View.GONE);
        Gson gson = new Gson();
        PurchasedPackageData purchasedPackageData = gson.fromJson(jsonObject, PurchasedPackageData.class);
        int error = purchasedPackageData.getError();
        if (error == Constant.ERROR_VERSION_CODE) {
            Utils.showUpdateNewVersionDialog(getActivity());
        } else if (error == Utils.OLD_REQUEST_SUCCESS) {
            ArrayList<PurchasedPackageListDetailData> datas = purchasedPackageData.getData();

            if (datas != null && datas.size() > 0) {
                packageDetails = new ArrayList<>();
                for (int j = 0; j < datas.size(); j++) {
                    PurchasedPackageListDetailData data = datas.get(j);
                    ArrayList<PackageDetail> temp = data.getPackageExtraList();

                    int listExtrasSize = temp.size();
                    for (int i = 0; i <= listExtrasSize; i++) {
                        PackageDetail packageDetail;
                        if (i == 0) {
                            packageDetail = new PackageDetail(data.getName(), data.getPrice(), data.getDescription(), true, data.isBuy(), data.getImage());
                        } else {
                            packageDetail = temp.get(i - 1);
                        }
                        packageDetails.add(packageDetail);
                    }
                }
            } else if (datas != null && datas.size() == 0) {
                emptyLayout.setVisibility(View.VISIBLE);
            }

            mPackagePurchasedListAdapter.setData(packageDetails);

        }
    }

    private void updatePackageStatusToServer(final String orderID, final String packageId) {
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
                        //packageDetailAdapter.setBuyToBought();


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

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = mRecyclerView.getChildAdapterPosition(child);
            PackageDetail packageDetail = packageDetails.get(position);
            if (packageDetail.isHeader()) {

            } else {
                packageId = packageDetail.getPk_id();
                //buy package
                inAppId = packageDetail.getProductId();
                if (!packageDetail.getIsBuy()) {
                    try {
                        mHelper.launchPurchaseFlow(getActivity(), inAppId, RC_REQUEST,
                                mPurchaseFinishedListener, payLoad);
                    } catch (IabHelper.IabAsyncInProgressException e1) {
                        e1.printStackTrace();
                    }
                }


            }
        }

        return false;
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

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onResume() {
        mRecyclerView.addOnItemTouchListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRecyclerView.removeOnItemTouchListener(this);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_REQUEST) {
            if (mHelper == null) return;

            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            } else {
            }
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
}
