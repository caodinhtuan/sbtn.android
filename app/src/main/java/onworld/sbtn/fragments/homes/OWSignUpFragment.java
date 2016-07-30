package onworld.sbtn.fragments.homes;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import onworld.sbtn.R;
import onworld.sbtn.callbacks.DataSignUpLoadedListener;
import onworld.sbtn.josonmodel.SignUpInfo;
import onworld.sbtn.tasks.TaskLoadDataSignUp;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.HeaderHelper;
import onworld.sbtn.utils.DateTimeUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.VotingUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class OWSignUpFragment extends Fragment implements DataSignUpLoadedListener {
    private EditText signupPassword, signupEmail, confirmPassword;
    private TextView backtoSignIn;
    private Button btnSignUp;
    private String email, password, cfPassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword, inputLayoutConfirm;
    private String dateTime, requestToken;

    public OWSignUpFragment() {
        // Required empty public constructor
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owsign_up, container, false);
        // Inflate the layout for this fragment
        btnSignUp = (Button) view.findViewById(R.id.btn_sign_up);
        signupPassword = (EditText) view.findViewById(R.id.signup_password);
        signupEmail = (EditText) view.findViewById(R.id.signup_email);
        confirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        inputLayoutEmail = (TextInputLayout) view.findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) view.findViewById(R.id.input_layout_password);
        inputLayoutConfirm = (TextInputLayout) view.findViewById(R.id.input_layout_confirm);
        signupEmail.requestFocus();

        signupEmail.addTextChangedListener(new MyTextWatcher(signupEmail));
        signupPassword.addTextChangedListener(new MyTextWatcher(signupPassword));
        confirmPassword.addTextChangedListener(new MyTextWatcher(confirmPassword));
        backtoSignIn = (TextView) view.findViewById(R.id.sign_in);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
        backtoSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        //Facebook login
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateTime = DateTimeUtils.getCurrentUTCDateTime();
        requestToken = HeaderHelper.createRequestTokenValue("POST", dateTime, Constant.PRIVATE_KEY, 78);
    }

    private void createUser() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        VotingUtils.showDialog(getContext());
        String loginApi = URL.SIGN_UP;
        new TaskLoadDataSignUp(getActivity(),this, loginApi, email, password).execute();

    }

    /*
     * Validate form
     */
    private void submitForm() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!validateConfirmPassword()) {
            return;
        }
        if (!validateMatchPassword()) {
            return;
        }
        createUser();

    }

    private boolean validateEmail() {
        email = signupEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(signupEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        password = signupPassword.getText().toString().trim();
        if (password.isEmpty() || password.length() < 6) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(signupPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        cfPassword = confirmPassword.getText().toString().trim();
        if (cfPassword.isEmpty() || cfPassword.length() < 6) {
            inputLayoutConfirm.setError(getString(R.string.err_msg_password));
            requestFocus(confirmPassword);
            return false;
        } else {
            inputLayoutConfirm.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateMatchPassword() {
        if (!cfPassword.equals(password)) {
            inputLayoutConfirm.setError(getString(R.string.err_msg_password_match));
            requestFocus(confirmPassword);
            return false;
        } else {
            inputLayoutConfirm.setErrorEnabled(false);
        }
        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void showAlertWithMessage(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.title_failed_to_signup));
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getString(R.string.text_button_close),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onDataSignUpLoaded(String jsonObject) {
        if (jsonObject != null) {
            VotingUtils.hideDialog();
            Gson gson = new Gson();
            SignUpInfo signUpInfo = gson.fromJson(jsonObject.toString(), SignUpInfo.class);
            int error = signUpInfo.getCode();
            String message = signUpInfo.getMessage();

            if (error == 50001) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage(getResources().getString(R.string.msg_signup_success));
                alertDialog.setPositiveButton(getString(R.string.text_button_close),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().onBackPressed();
                            }
                        });
                alertDialog.show();
            } else {
                showAlertWithMessage(message);
            }
        }

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.signup_email:
                    validateEmail();
                    break;
                case R.id.signup_password:
                    validatePassword();
                    break;
                case R.id.confirm_password:
                    validateConfirmPassword();
                    break;
            }
        }
    }
}
