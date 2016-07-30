package onworld.sbtn.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onworld.sbtn.MyApplication;
import onworld.sbtn.R;
import onworld.sbtn.adapters.PackageActivityListAdapter;
import onworld.sbtn.billingutils.IabHelper;
import onworld.sbtn.billingutils.IabResult;
import onworld.sbtn.billingutils.Inventory;
import onworld.sbtn.billingutils.Purchase;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.utils.CommonUtils;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.HeaderHelper;
import onworld.sbtn.utils.DateTimeUtils;
import onworld.sbtn.utils.DeviceUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;
import onworld.sbtn.views.PackageViewItem;

public class PackageActivity extends AppCompatActivity implements PackageViewItem.BuyPackageListener,
        PackageActivityListAdapter.PackageActivityBuyPackageListener {
    public static final String PACKAGE_DATA = "package.data";
    public static final String IS_SIGN_IN_FROM_PACKAGE = "isfrompackage";
    public static final int PACKAGE_RESULT_CODE = 2001;
    static final int RC_REQUEST = 10001;
    private IabHelper mHelper;
    private String inAppId = "android.test.purchased";
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
    //private String inAppId;
    private CacheDataManager mCacheDataManager;
    private RecyclerView listPackageView;
    private ArrayList<PackageDetail> mPackages;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            for (int i = 0; i < mPackages.size(); i++) {
                PackageDetail packageData = mPackages.get(i);
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

        }
    };
    private GestureDetector gestureDetector;
    private String TAG = "PackageActivity";
    private String payLoad = "ottapi.com/sbtn";
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
            Log.d(TAG, "End consumption flow.");
        }
    };
    private PackageActivityListAdapter mPackageActivityListAdapter;
    private String deviceName, deviceOS;
    private int trackingID = 0;
    private String accessToken = "";
    private LinearLayout packageContent;
    private PackageViewItem mPackageViewItem;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private int packageIdDetail;
    private int promotionStatus;
    private String requestToken;
    private RecyclerView mlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pakage);
        setupToolbar();
        setupView();

        if (getIntent() != null) {
            mPackages = getIntent().getParcelableArrayListExtra(PACKAGE_DATA);
        }
        setupData();
        setUpBillingInapp();
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        mCacheDataManager = CacheDataManager.getInstance(this);

    }

    private void setupData() {
        deviceName = DeviceUtils.getDeviceName();
        deviceOS = DeviceUtils.getPlatform();
        accessToken = CommonUtils.getAccessTokenSecu(this);
        mPackageActivityListAdapter.setData(mPackages);
    }

    private void setupView() {
        //packageContent = (LinearLayout) findViewById(R.id.package_content);
        mlist = (RecyclerView) findViewById(R.id.package_activity_list);
        mlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPackageActivityListAdapter = new PackageActivityListAdapter(this);
        mPackageActivityListAdapter.setPackageActivityBuyPackageListener(this);
        mlist.setAdapter(mPackageActivityListAdapter);

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) findViewById(R.id.package_activity_title);
        title.setText("Package List");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setUpBillingInapp() {
        String base64EncodedPublicKey = getResources().getString(R.string.sbtn_license_key);

        mHelper = new IabHelper(this, base64EncodedPublicKey);
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
        AlertDialog.Builder packageAlert = new AlertDialog.Builder(this);
        packageAlert.setMessage(message);
        packageAlert.setNeutralButton("OK", null);
        packageAlert.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }

    }

    private void updatePackageStatusToServer(final String orderID, final String packageId) {
        final String updatePaymentUrl = URL.UPDATE_PAYMENT;

        final String dateTime = DateTimeUtils.getCurrentUTCDateTime();
        final String agent = MyApplication.getUserAgent();

        final String authenticate = HeaderHelper.createAuthorizationValue("POST", dateTime,
                CommonUtils.getAccessTokenSecu(PackageActivity.this), 78);
        final String requestToken = HeaderHelper.createRequestTokenValue("POST", dateTime,
                Constant.PRIVATE_KEY, 78);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                updatePaymentUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("error");
                    String message = jsonObject.getString("message");
                    if (code == Utils.OLD_REQUEST_SUCCESS) {
                        //waiting for verify
                        mCacheDataManager.clearCachePackageData();
                        mCacheDataManager.clearCacheYourContent();
                        Intent intent = getIntent();
                        setResult(PACKAGE_RESULT_CODE, intent);
                        PackageActivity.this.finish();

                    } else {
                        VotingUtils.showAlertWithMessage(PackageActivity.this, message);
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
    protected void onDestroy() {
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            mHelper = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBuyPackageListener(PackageDetail packageDetail, String inputCode) {
        packageId = packageDetail.getPk_id();
        packageIdDetail = packageDetail.getId();
        promotionStatus = packageDetail.getPromotion();
        if (promotionStatus == 0) {
            //inAppId = packageDetail.getProductId();
            try {
                mHelper.launchPurchaseFlow(PackageActivity.this, inAppId, RC_REQUEST,
                        mPurchaseFinishedListener, payLoad);
            } catch (IabHelper.IabAsyncInProgressException e1) {
                e1.printStackTrace();
            }
        } else {
            int proCodeLength = inputCode.length();
            if (proCodeLength == 0) {
                Toast.makeText(MyApplication.getAppContext(), "Please enter package code?", Toast.LENGTH_SHORT).show();
            } else if (proCodeLength < 7 || proCodeLength > 10) {
                Toast.makeText(MyApplication.getAppContext(), "Invalid package code.", Toast.LENGTH_SHORT).show();
            } else {
                checkProCode(inputCode);
            }
        }


    }

    private void openDialogAddCode() {
        alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.code_input_dialog, null);
        alertDialogBuilder.setView(dialogView);

        final EditText proCodeInputEdt = (EditText) dialogView.findViewById(R.id.code_input);

        alertDialogBuilder.setTitle("Add Promotion Code");
        alertDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                checkProCode(proCodeInputEdt.getText().toString().trim());
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass

            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void checkProCode(final String proCode) {
        Utils.hideSoftKeyboard(this);
        final String packagePromotionUrl = URL.PACKAGE_PROMOTION;

        final String dateTime = DateTimeUtils.getCurrentUTCDateTime();
        final String agent = MyApplication.getUserAgent();

        final String authenticate = HeaderHelper.createAuthorizationValue("POST", dateTime,
                CommonUtils.getAccessTokenSecu(PackageActivity.this), 78);
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
                        Intent intent = getIntent();
                        setResult(PACKAGE_RESULT_CODE, intent);
                        PackageActivity.this.finish();

                    } else {
                        VotingUtils.showAlertWithMessage(PackageActivity.this, message);
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
                params.put("pk_id", packageIdDetail + "");
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

    @Override
    public void onPackageActivityBuyPackageListener(PackageDetail packageDetail, String inputCode) {
        packageId = packageDetail.getPk_id();
        packageIdDetail = packageDetail.getId();
        promotionStatus = packageDetail.getPromotion();
        if (promotionStatus == 0) {
            //inAppId = packageDetail.getProductId();
            try {
                mHelper.launchPurchaseFlow(PackageActivity.this, inAppId, RC_REQUEST,
                        mPurchaseFinishedListener, payLoad);
            } catch (IabHelper.IabAsyncInProgressException e1) {
                e1.printStackTrace();
            }
        } else {
            int proCodeLength = inputCode.length();
            if (proCodeLength == 0) {
                Toast.makeText(MyApplication.getAppContext(), "Please enter package code?", Toast.LENGTH_SHORT).show();
            } else if (proCodeLength < 7 || proCodeLength > 10) {
                Toast.makeText(MyApplication.getAppContext(), "Invalid package code.", Toast.LENGTH_SHORT).show();
            } else {
                checkProCode(inputCode);
            }
        }
    }
}
