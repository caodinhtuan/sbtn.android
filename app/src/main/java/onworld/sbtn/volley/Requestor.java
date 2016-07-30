package onworld.sbtn.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import onworld.sbtn.MyApplication;
import onworld.sbtn.utils.CommonUtils;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.HeaderHelper;
import onworld.sbtn.utils.DateTimeUtils;
import onworld.sbtn.utils.DeviceUtils;

/**
 * Created by linhnguyen on 12/3/15.
 */
public class Requestor {
    public static String requestDataJson(RequestQueue requestQueue, String url) {
        String response = null;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        MyJsonStringRequest request = new MyJsonStringRequest(Request.Method.POST, url, requestFuture, requestFuture);

        Map<String, String> mapsParam = new HashMap<>();
        mapsParam.put("device_id", DeviceUtils.getDeviceId(MyApplication.getAppContext()));
        request.setMapsParams(mapsParam);
        requestQueue.add(request);
        try {
            response = requestFuture.get(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.e(e + "");
        } catch (ExecutionException e) {
            L.e(e + "");
        } catch (TimeoutException e) {
            L.e(e + "");
        }
        return response;
    }

    public static String requestEndUserView(RequestQueue requestQueue, String url) {
        String response = null;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        MyJsonStringRequest request = new MyJsonStringRequest(Request.Method.POST, url, requestFuture, requestFuture);

        Map<String, String> mapsParam = new HashMap<>();
        mapsParam.put("device_id", DeviceUtils.getDeviceId(MyApplication.getAppContext()));
        request.setMapsParams(mapsParam);
        requestQueue.add(request);
        try {
            response = requestFuture.get(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.e(e + "");
        } catch (ExecutionException e) {
            L.e(e + "");
        } catch (TimeoutException e) {
            L.e(e + "");
        }
        return response;
    }

    public static JSONObject requestDataJsonWithoutHeader(RequestQueue requestQueue, String url) {
        JSONObject response = null;
        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        MyJsonObjectRequest request = new MyJsonObjectRequest(Request.Method.POST, url, null, requestFuture, requestFuture);
        requestQueue.add(request);
        try {
            response = requestFuture.get(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.e(e + "");
        } catch (ExecutionException e) {
            L.e(e + "");
        } catch (TimeoutException e) {
            L.e(e + "");
        }
        return response;
    }

    public static String requestDataJsonSignUp(RequestQueue requestQueue, String url, String email, String password) {
        String response = null;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        MyJsonStringRequest request = new MyJsonStringRequest(Request.Method.POST, url, requestFuture, requestFuture);

        Map<String, String> mapsParam = new HashMap<>();
        mapsParam.put("email", email);
        mapsParam.put("password", password);
        request.setMapsParams(mapsParam);
        requestQueue.add(request);
        try {
            response = requestFuture.get(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.e(e + "");
        } catch (ExecutionException e) {
            L.e(e + "");
        } catch (TimeoutException e) {
            L.e(e + "");
        }
        return response;
    }

    public static String requestDataJsonSignIn(RequestQueue requestQueue, String url, String email, String password) {
        String response = null;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        MyJsonStringRequest request = new MyJsonStringRequest(Request.Method.POST, url, requestFuture, requestFuture);
        Map<String, String> mapsParam = new HashMap<>();
        mapsParam.put("email", email);
        mapsParam.put("password", password);
        request.setMapsParams(mapsParam);
        requestQueue.add(request);
        try {
            response = requestFuture.get(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.e(e + "");
        } catch (ExecutionException e) {
            L.e(e + "");
        } catch (TimeoutException e) {
            L.e(e + "");
        }
        return response;
    }

    public static String requestDataJsonSignInFacebook(RequestQueue requestQueue, String url, String facebookId, String facebookToken, String avataString,
                                                       String fullName, String facebookEmail) {
        String response = null;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        MyJsonStringRequest request = new MyJsonStringRequest(Request.Method.POST, url, requestFuture, requestFuture);
        Map<String, String> mapsParam = new HashMap<>();
        mapsParam.put("facebookID", facebookId);
        mapsParam.put("facebookToken", facebookToken);
        mapsParam.put("avatar", avataString);
        mapsParam.put("fullName", fullName);
        mapsParam.put("emailAddress", facebookEmail);
        request.setMapsParams(mapsParam);
        requestQueue.add(request);
        try {
            response = requestFuture.get(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.e(e + "");
        } catch (ExecutionException e) {
            L.e(e + "");
        } catch (TimeoutException e) {
            L.e(e + "");
        }
        return response;
    }

    public static String requestDataJsonStartTracking(RequestQueue requestQueue, String url, int id) {
        String response = null;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        MyJsonStringRequest request = new MyJsonStringRequest(Request.Method.POST, url, requestFuture, requestFuture);
        Map<String, String> mapsParam = new HashMap<>();
        mapsParam.put("token", CommonUtils.getAccessTokenSecu(MyApplication.getAppContext()));
        mapsParam.put("content_id", id + "");
        mapsParam.put("device", DeviceUtils.getDeviceName());
        mapsParam.put("device_id", DeviceUtils.getDeviceId(MyApplication.getAppContext()));
        mapsParam.put("os", DeviceUtils.getPlatform());
        request.setMapsParams(mapsParam);
        requestQueue.add(request);
        try {
            response = requestFuture.get(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.e(e + "");
        } catch (ExecutionException e) {
            L.e(e + "");
        } catch (TimeoutException e) {
            L.e(e + "");
        }
        return response;
    }

    public static String requestDataJsonStopTracking(RequestQueue requestQueue, String url, int trackingId) {
        String response = null;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        MyJsonStringRequest request = new MyJsonStringRequest(Request.Method.POST, url, requestFuture, requestFuture);
        Map<String, String> mapsParam = new HashMap<>();
        mapsParam.put("token", CommonUtils.getAccessTokenSecu(MyApplication.getAppContext()));
        mapsParam.put("id", trackingId + "");
        request.setMapsParams(mapsParam);
        requestQueue.add(request);
        try {
            response = requestFuture.get(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.e(e + "");
        } catch (ExecutionException e) {
            L.e(e + "");
        } catch (TimeoutException e) {
            L.e(e + "");
        }
        return response;
    }


    public static class MyJsonObjectRequest extends JsonObjectRequest {
        String dateTime;
        String authenticate;
        String httpVerb;
        private Map<String, String> mapsHeader;
        private Map<String, String> mapsParam;
        private String requestToken;

        public MyJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
            if (method == Method.GET) {
                httpVerb = "GET";
            } else if (method == Method.POST) {
                httpVerb = "POST";
            }
            dateTime = DateTimeUtils.getCurrentUTCDateTime();
            requestToken = HeaderHelper.createRequestTokenValue(httpVerb, dateTime, Constant.PRIVATE_KEY, 78);
            authenticate = HeaderHelper.createAuthorizationValue(httpVerb, dateTime,
                    CommonUtils.getAccessTokenSecu(MyApplication.getAppContext()), 78);

        }

        public void setMapsHeader(Map<String, String> mapsHeader) {
            this.mapsHeader = mapsHeader;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            if (mapsHeader == null) {
                initHeader();
            }

            return mapsHeader;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            if (mapsParam == null) {
                initParams();
            }
            return mapsParam;
        }

        private void initParams() {
            mapsParam = new HashMap<>();
        }

        private void initHeader() {
            mapsHeader = new HashMap<>();
            mapsHeader.put("RequestToken", requestToken);
            mapsHeader.put("DateTime", dateTime);
            mapsHeader.put("Authorization", authenticate);
        }
    }

    public static class MyJsonStringRequest extends StringRequest {
        String dateTime;
        String httpVerb;
        String authenticate;
        private Map<String, String> mapsHeader;
        private Map<String, String> mapsParam;
        private String requestToken;


        public MyJsonStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
            if (method == Method.GET) {
                httpVerb = "GET";
            } else if (method == Method.POST) {
                httpVerb = "POST";
            }
            dateTime = DateTimeUtils.getCurrentUTCDateTime();
            requestToken = HeaderHelper.createRequestTokenValue(httpVerb, dateTime, Constant.PRIVATE_KEY, 78);
            authenticate = HeaderHelper.createAuthorizationValue(httpVerb, dateTime,
                    CommonUtils.getAccessTokenSecu(MyApplication.getAppContext()), 78);

        }

        public void setMapsParams(Map<String, String> mapsParams) {
            this.mapsParam = mapsParams;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            if (mapsHeader == null) {
                initHeader();
            }

            return mapsHeader;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            if (mapsParam == null) {
                initParams();
            }
            return mapsParam;
        }

        private void initParams() {
            mapsParam = new HashMap<>();
        }

        private void initHeader() {
            mapsHeader = new HashMap<>();
            mapsHeader.put("RequestToken", requestToken);
            mapsHeader.put("DateTime", dateTime);
            mapsHeader.put("Authorization", authenticate);

        }
    }

}
