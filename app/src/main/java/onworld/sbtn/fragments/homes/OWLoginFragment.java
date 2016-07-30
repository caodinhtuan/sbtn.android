package onworld.sbtn.fragments.homes;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import onworld.sbtn.MyApplication;
import onworld.sbtn.R;
import onworld.sbtn.activities.MainActivity;
import onworld.sbtn.activities.PackageActivity;
import onworld.sbtn.callbacks.DataLoginFacebookLoadedListener;
import onworld.sbtn.callbacks.DataLoginLoadedListener;
import onworld.sbtn.callbacks.LoginListener;
import onworld.sbtn.josonmodel.AccountInfo;
import onworld.sbtn.josonmodel.AccountInfoData;
import onworld.sbtn.tasks.TaskLoadDataSignIn;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.HeaderHelper;
import onworld.sbtn.utils.DateTimeUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.VotingUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class OWLoginFragment extends Fragment implements View.OnClickListener, DataLoginLoadedListener, DataLoginFacebookLoadedListener {
    public static final String FULL_NAME = "fullname";
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String EMAIL = "email";
    public static final String TOKEN = "accesstoken";
    public static final String USER_ID = "userid";
    public static final String IS_LOGIN = "islogin";
    private static final String TAG = "OWLoginFragment";
    Button btnLogin;
    TextView txtUsername, txtPassword;
    LinearLayout bottomTextLayout, loginContentLayout;
    boolean isSignInFrom;
    private String email;
    private String password;
    private TextView mTextDetails, forgotPassword;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private LoginButton btnFacebookLogin;
    private String fullName, firstName, lastName, facebookEmail;
    private String facebookId;
    private String avataString;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private String facebookToken = "";
    private LoginListener mLoginListener;
    private TextView signUp;
    private ProgressBar loginLoading;
    private String requestToken;
    private String dateTime;
    public static final int LOGIN_FRAGMENT_RESULT_CODE = 1001;
    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            final AccessToken accessToken = loginResult.getAccessToken();
            if (accessToken != null) {
                facebookToken = accessToken.getToken();
            }

            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                Uri avata = profile.getProfilePictureUri(100, 100);
                avataString = avata.toString();
                fullName = profile.getName();
                firstName = profile.getFirstName();
                lastName = profile.getLastName();
                facebookId = profile.getId();
            }
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            facebookEmail = object.optString("email");
                            // Application code
                            final String loginFaceBookApi = URL.LOGIN_FACEBOOK;
                            StringRequest strReq = new StringRequest(Request.Method.POST,
                                    loginFaceBookApi, new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    Gson gson = new Gson();
                                    AccountInfo loginFacebookRespone = gson.fromJson(response, AccountInfo.class);
                                    int error = loginFacebookRespone.getCode();
                                    String message = loginFacebookRespone.getMessage();
                                    if (loginFacebookRespone.getCode() == VotingUtils.REQUEST_SUCCESS) {
                                        AccountInfoData loginFacebookData = loginFacebookRespone.getData();
                                        if (loginFaceBookApi != null) {
                                            SharedPreferences loginstate = getActivity().getSharedPreferences(MainActivity.LOGIN_STATE, 0); // 0 - for private mode
                                            SharedPreferences.Editor editor = loginstate.edit();
                                            editor.putBoolean(OWLoginFragment.IS_LOGIN, true);
                                            editor.putString(OWLoginFragment.FULL_NAME, fullName);
                                            editor.putString(OWLoginFragment.LAST_NAME, lastName);
                                            editor.putString(OWLoginFragment.FIRST_NAME, firstName);
                                            editor.putString(OWLoginFragment.EMAIL, facebookEmail);
                                            editor.putString(OWLoginFragment.TOKEN, loginFacebookData.getAccessToken());
                                            editor.commit();
                                            if (isSignInFrom) {
                                                Intent intent = getActivity().getIntent();
                                                intent.putExtra(OWLoginFragment.IS_LOGIN,true);
                                                getActivity().setResult(LOGIN_FRAGMENT_RESULT_CODE, intent);
                                                getActivity().finish();
                                            } else {
                                                mLoginListener.onLoginListener(fullName, facebookEmail);
                                            }
                                            loginLoading.setVisibility(View.GONE);
                                        }

                                    } else {
                                        showLoginConent();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("RequestToken", requestToken);
                                    params.put("DateTime", dateTime);
                                    return params;
                                }

                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("facebookID", facebookId);
                                    params.put("facebookToken", facebookToken);
                                    params.put("avatar", avataString);
                                    params.put("fullName", fullName);
                                    params.put("emailAddress", facebookEmail);
                                    return params;
                                }
                            };

                            MyApplication.getInstance().addToRequestQueue(strReq);


                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,email");
            request.setParameters(parameters);
            request.executeAsync();
        }


        @Override
        public void onCancel() {
            Log.d(TAG, "onCancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.d(TAG, "onError " + e);
            showAlert("Cannot Login to Facebook.");
            getActivity().finish();
        }
    };

   /* public OWLoginFragment() {
        // Required empty public constructor
    }*/

    public static OWLoginFragment newInstance(boolean isSignInFrom) {
        OWLoginFragment owLoginFragment = new OWLoginFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(PackageActivity.IS_SIGN_IN_FROM_PACKAGE, isSignInFrom);
        owLoginFragment.setArguments(bundle);
        return owLoginFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSignInFrom = getArguments().getBoolean(PackageActivity.IS_SIGN_IN_FROM_PACKAGE, false);
        mLoginListener = (LoginListener) getActivity();
        dateTime = DateTimeUtils.getCurrentUTCDateTime();
        requestToken = HeaderHelper.createRequestTokenValue("POST", dateTime, Constant.PRIVATE_KEY, 78);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);
        // Inflate the layout for this fragment
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        txtPassword = (TextView) view.findViewById(R.id.txtpassword);
        txtUsername = (TextView) view.findViewById(R.id.txtusername);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.sign_up);
        inputLayoutEmail = (TextInputLayout) view.findViewById(R.id.signin_layout_email);
        inputLayoutPassword = (TextInputLayout) view.findViewById(R.id.signin_layout_password);
        txtUsername.requestFocus();
        loginLoading = (ProgressBar) view.findViewById(R.id.login_loading);
        loginContentLayout = (LinearLayout) view.findViewById(R.id.login_content_group);
        bottomTextLayout = (LinearLayout) view.findViewById(R.id.login_bottom_text);
        //Facebook login
        btnFacebookLogin = (LoginButton) view.findViewById(R.id.login_button);
        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLoading.setVisibility(View.VISIBLE);
                hideLoginConent();
            }
        });

        btnLogin.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

        //Have to setFragment
        btnFacebookLogin.setFragment(this);
        btnFacebookLogin.setReadPermissions("user_friends");

        btnFacebookLogin.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile"));
        mCallbackManager = CallbackManager.Factory.create();

        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
        btnFacebookLogin.registerCallback(mCallbackManager, mFacebookCallback);
        return view;
    }

    private void signIn() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        final String loginApi = URL.SIGN_IN;
        new TaskLoadDataSignIn(getActivity(),this, loginApi, email, password).execute();

    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                submitForm();
                break;
            case R.id.sign_up:
                Fragment fragment = new OWSignUpFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_content, fragment).addToBackStack("OWSignUpFragment").commit();
                //saveSateLogin.onSaveStateLoginListener();
                break;
            case R.id.forgot_password:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL.WEB_FORGOT_PASSWORD));
                startActivity(browserIntent);
                break;

        }
    }

    private void submitForm() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }

        signIn();
    }

    private boolean validateEmail() {
        email = txtUsername.getText().toString().trim();
        if (email.isEmpty() || !OWSignUpFragment.isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(txtUsername);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        password = txtPassword.getText().toString().trim();
        if (password.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password_signin));
            requestFocus(txtPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showLoginConent() {
        loginContentLayout.setVisibility(View.VISIBLE);
        bottomTextLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoginConent() {
        loginContentLayout.setVisibility(View.INVISIBLE);
        bottomTextLayout.setVisibility(View.INVISIBLE);
    }

    private void showAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.title_failed_to_login));
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getString(R.string.text_button_close),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onDataLoginFacebookLoaded(String jsonObject) {

    }

    @Override
    public void onDataLoginLoaded(String jsonObject) {
        if (jsonObject != null) {
            Gson gson = new Gson();
            AccountInfo accountInfo = gson.fromJson(jsonObject.toString(), AccountInfo.class);
            int error = accountInfo.getCode();
            String message = accountInfo.getMessage();
            if (error == 50001) {
                AccountInfoData accountInfoData = accountInfo.getData();
                if (accountInfoData != null) {
                    String accessToken = accountInfoData.getAccessToken();
                    int userId = accountInfoData.getMemberId();
                    String fullName = accountInfoData.getFullName();
                    SharedPreferences loginstate = getActivity().getSharedPreferences(MainActivity.LOGIN_STATE, 0); // 0 - for private mode
                    SharedPreferences.Editor editor = loginstate.edit();
                    editor.putBoolean(OWLoginFragment.IS_LOGIN, true);
                    editor.putString(OWLoginFragment.FULL_NAME, fullName);
                    editor.putString(OWLoginFragment.FIRST_NAME, "N/A");
                    editor.putString(OWLoginFragment.LAST_NAME, "N/A");
                    editor.putString(OWLoginFragment.EMAIL, email);
                    editor.putString(OWLoginFragment.TOKEN, accessToken);
                    editor.putInt(OWLoginFragment.USER_ID, userId);
                    editor.commit();
                    if (isSignInFrom) {
                        Intent intent = getActivity().getIntent();
                        intent.putExtra(OWLoginFragment.IS_LOGIN,true);
                        getActivity().setResult(LOGIN_FRAGMENT_RESULT_CODE, intent);
                        getActivity().finish();
                    } else {
                        mLoginListener.onLoginListener(fullName, email);
                    }
                    loginLoading.setVisibility(View.GONE);
                }


            } else {
                showAlert(message);
            }
        }

    }
}
