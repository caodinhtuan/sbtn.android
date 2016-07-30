package onworld.sbtn.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import onworld.sbtn.R;
import onworld.sbtn.callbacks.LoginListener;
import onworld.sbtn.callbacks.LogoutListener;
import onworld.sbtn.fragments.homes.AccountInfoFragment;
import onworld.sbtn.fragments.homes.OWLoginFragment;
import onworld.sbtn.utils.Constant;

public class LoginActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener,
        LoginListener, LogoutListener {

    private Toolbar mToolbar;
    private FrameLayout mContainer;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private boolean isSignInFrom;
    private boolean isSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar_activity);
        mContainer = (FrameLayout) findViewById(R.id.frame_content);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isSignInFrom = bundle.getBoolean(PackageActivity.IS_SIGN_IN_FROM_PACKAGE);
        }
        SharedPreferences loginstate = getSharedPreferences(MainActivity.LOGIN_STATE, 0);
        isSignIn = loginstate.getBoolean(OWLoginFragment.IS_LOGIN, false);
        if (isSignIn) {
            String firstName = loginstate.getString(OWLoginFragment.FIRST_NAME, "N/A");
            String lastName = loginstate.getString(OWLoginFragment.LAST_NAME, "N/A");
            String email = loginstate.getString(OWLoginFragment.EMAIL, "N/A");
            String fullName = loginstate.getString(OWLoginFragment.FULL_NAME, "N/A");
            fragment = AccountInfoFragment.newInstance(firstName, lastName, fullName, email);
        } else {
            fragment = OWLoginFragment.newInstance(isSignInFrom);
        }
        transferFragment(fragment);

    }

    public void transferFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackStackChanged() {
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = count - 1; i >= 0; i--) {
            FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onLoginListener(String fullName, String email) {
        Fragment fragment = AccountInfoFragment.newInstance("N/A", "N/A", fullName, email);
        fragmentManager.beginTransaction()
                .replace(R.id.frame_content, fragment).commit();
        //notify change drawer
        Intent intent = getIntent();
        intent.putExtra(OWLoginFragment.IS_LOGIN, true);
        setResult(Constant.LOGIN_MAIN_RESULT_CODE, intent);
    }

    @Override
    public void onLogoutListener() {
        fragment = OWLoginFragment.newInstance(false);
        transferFragment(fragment);
        //notify change drawer
        Intent intent = getIntent();
        intent.putExtra(OWLoginFragment.IS_LOGIN, false);
        setResult(Constant.LOGIN_MAIN_RESULT_CODE, intent);
    }
}
