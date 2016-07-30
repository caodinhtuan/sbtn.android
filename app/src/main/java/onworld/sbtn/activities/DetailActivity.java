package onworld.sbtn.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;

import java.util.ArrayList;

import onworld.sbtn.MyApplication;
import onworld.sbtn.R;
import onworld.sbtn.fragments.homedetails.GridDetailFragment;
import onworld.sbtn.fragments.homes.HomeFragment;
import onworld.sbtn.fragments.homes.OWLoginFragment;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.settings.CastPreference;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.NetworkUtils;
import onworld.sbtn.utils.Utils;

public class DetailActivity extends AppCompatActivity {
    public static final String ASGRID_DETAIL_DATA = "asgriddetaildata";
    public static final String ASGRID_DETAIL_TITLE = "asgriddetailtitle";
    private Toolbar mToolbar;
    private TextView titleActionBar;
    private FragmentManager mFragmentManager;
    private ArrayList<DataDetailItem> dataDetailItems = new ArrayList<>();
    private VideoCastManager mCastManager;
    private VideoCastConsumer mCastConsumer;
    private MiniController mMini;
    private MenuItem mediaRouteMenuItem;
    private boolean mIsHoneyCombOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    private String toolbarTitleString;
    private ImageView toolbarAsiaLogo;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asdetail);
        mCastManager = VideoCastManager.getInstance();
        setUpToolbar();
        setupCastListener();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dataDetailItems = bundle.getParcelableArrayList(ASGRID_DETAIL_DATA);
            toolbarTitleString = bundle.getString(ASGRID_DETAIL_TITLE);
            type = bundle.getInt(HomeFragment.AS_HOME_TYPE);
            setUpContent();
        }

    }

    private void setUpToolbar() {
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
        mMini = (MiniController) findViewById(R.id.miniController1);
        mCastManager.addMiniController(mMini);
    }

    private void setUpContent() {
        //titleActionBar.setText(toolbarTitleString);
        mFragmentManager = getSupportFragmentManager();
        Fragment mFragment = GridDetailFragment.newInstance(dataDetailItems, toolbarTitleString, type);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_asdetail, mFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mediaRouteMenuItem = mCastManager.
                addMediaRouterButton(menu, R.id.media_route_menu_item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_show_queue).setVisible(mCastManager.isConnected());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_show_queue:
                if (mMini.isVisible()) {
                    mCastManager.updateMiniControllersVisibility(false);
                } else {
                    mCastManager.updateMiniControllersVisibility(true);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setupCastListener() {
        mCastConsumer = new VideoCastConsumerImpl() {

            @Override
            public void onFailed(int resourceId, int statusCode) {
                String reason = "Not Available";
                if (resourceId > 0) {
                    reason = getString(resourceId);
                }
            }

            @Override
            public void onApplicationConnected(ApplicationMetadata appMetadata, String sessionId,
                                               boolean wasLaunched) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDisconnected() {
                invalidateOptionsMenu();
            }

            @Override
            public void onConnectionSuspended(int cause) {
                //Log.d(TAG, "onConnectionSuspended() was called with cause: " + cause);
                onworld.sbtn.utils.Utils.
                        showToast(DetailActivity.this, R.string.connection_temp_lost);
            }

            @Override
            public void onConnectivityRecovered() {
                onworld.sbtn.utils.Utils.
                        showToast(DetailActivity.this, R.string.connection_recovered);
            }

            @Override
            public void onCastDeviceDetected(final MediaRouter.RouteInfo info) {
                if (!CastPreference.isFtuShown(DetailActivity.this) && mIsHoneyCombOrAbove) {
                    CastPreference.setFtuShown(DetailActivity.this);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (mediaRouteMenuItem.isVisible()) {
                            }
                        }
                    }, 1000);
                }
            }
        };
        mCastManager.reconnectSessionIfPossible();

    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return mCastManager.onDispatchVolumeKeyEvent(event, MyApplication.VOLUME_INCREMENT)
                || super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        mCastManager = VideoCastManager.getInstance();
        if (null != mCastManager) {
            mCastManager.addVideoCastConsumer(mCastConsumer);
            mCastManager.incrementUiCounter();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        mCastManager.decrementUiCounter();
        mCastManager.removeVideoCastConsumer(mCastConsumer);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (null != mCastManager && NetworkUtils.getConnectivityStatus(this) != 0) {
            mMini.removeOnMiniControllerChangedListener(mCastManager);
            mCastManager.removeMiniController(mMini);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == OWLoginFragment.LOGIN_FRAGMENT_RESULT_CODE) {
            Intent intent = getIntent();
            intent.putExtra(OWLoginFragment.IS_LOGIN, true);
            setResult(Constant.LOGIN_MAIN_RESULT_CODE, intent);

        }
    }
}
