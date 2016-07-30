package onworld.sbtn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import onworld.sbtn.R;
import onworld.sbtn.fragments.homedetails.PackageDetailFragment;
import onworld.sbtn.josonmodel.GroupPackage;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.Utils;

public class PackageDetailActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView titleActionBar;
    private FragmentManager mFragmentManager;
    private String toolbarTitleString;
    private ImageView toolbarAsiaLogo;
    private GroupPackage mGroupPackage;
    private PackageDetailFragment packageDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_detail);
        setupToolbar();
        mFragmentManager = getSupportFragmentManager();

        Bundle bundle = getIntent().getBundleExtra(PackageDetailFragment.PACKAGE_DETAIL_BUNDLE);
        if (bundle != null) {
            mGroupPackage = bundle.getParcelable(PackageDetailFragment.PACKAGE_DETAIL);
        }
        showFragment();

    }

    private void showFragment() {

        packageDetailFragment = PackageDetailFragment.newInstance(mGroupPackage, mGroupPackage.getGroupName(), mGroupPackage.getDescription(), mGroupPackage.getItems().get(0).getId());
        mFragmentManager.beginTransaction().replace(R.id.content_packagedetail, packageDetailFragment, PackageDetailFragment.TAG).commit();
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        titleActionBar = (TextView) mToolbar.findViewById(R.id.title_actionbar);
        toolbarAsiaLogo = (ImageView) mToolbar.findViewById(R.id.toolbar_asia_logo);
        toolbarAsiaLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = getIntent();
                setResult(Constant.MAIN_DETAIL_ACTIVITY_RESULT_CODE, intent);
            }
        });
        Utils.setLobsterRegularFont(this, titleActionBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (packageDetailFragment != null) {
            packageDetailFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
