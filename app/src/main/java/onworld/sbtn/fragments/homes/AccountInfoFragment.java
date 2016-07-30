package onworld.sbtn.fragments.homes;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import onworld.sbtn.R;
import onworld.sbtn.activities.MainActivity;
import onworld.sbtn.callbacks.LogoutListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountInfoFragment extends Fragment implements View.OnClickListener {

    private String firstNameValue, lastNameValue, emailValue, fullNameValue;
    private TextView firstNameInfo, lastNameInfo, email, fullName;
    private Button btnSignOut;
    private LogoutListener mLogoutListener;

    public AccountInfoFragment() {
        // Required empty public constructor
    }

    public static AccountInfoFragment newInstance(String firstName, String lastName, String fullName, String email) {
        AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(OWLoginFragment.FIRST_NAME, firstName);
        bundle.putString(OWLoginFragment.LAST_NAME, lastName);
        bundle.putString(OWLoginFragment.EMAIL, email);
        bundle.putString(OWLoginFragment.FULL_NAME, fullName);
        accountInfoFragment.setArguments(bundle);
        return accountInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstNameValue = getArguments().getString(OWLoginFragment.FIRST_NAME);
        lastNameValue = getArguments().getString(OWLoginFragment.LAST_NAME);
        emailValue = getArguments().getString(OWLoginFragment.EMAIL);
        fullNameValue = getArguments().getString(OWLoginFragment.FULL_NAME);
        mLogoutListener = (LogoutListener) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_info, container, false);
        firstNameInfo = (TextView) view.findViewById(R.id.accountinfo_first_name);
        lastNameInfo = (TextView) view.findViewById(R.id.accountinfo_last_name);
        email = (TextView) view.findViewById(R.id.accountinfo_email);
        fullName = (TextView) view.findViewById(R.id.accountinfo_full_name);
        btnSignOut = (Button) view.findViewById(R.id.btn_signout);
        btnSignOut.setOnClickListener(this);
        firstNameInfo.setText(firstNameValue);
        lastNameInfo.setText(lastNameValue);
        email.setText(emailValue);
        fullName.setText(fullNameValue);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        SharedPreferences loginstate = getActivity().getSharedPreferences(MainActivity.LOGIN_STATE, 0); // 0 - for private mode
        SharedPreferences.Editor editor = loginstate.edit();
        editor.putBoolean(OWLoginFragment.IS_LOGIN, false);
        editor.putString(OWLoginFragment.FULL_NAME, "");
        editor.putString(OWLoginFragment.TOKEN, "");
        editor.putString(OWLoginFragment.FIRST_NAME, "");
        editor.putString(OWLoginFragment.LAST_NAME, "");
        editor.putString(OWLoginFragment.EMAIL, "");
        editor.putInt(OWLoginFragment.USER_ID, 0);
        editor.commit();
        LoginManager.getInstance().logOut();
        //back to fragment sign in
        //backToSignInListener.onBackToSigin();
        mLogoutListener.onLogoutListener();
    }
}
