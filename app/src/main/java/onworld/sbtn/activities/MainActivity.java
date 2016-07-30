package onworld.sbtn.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;

import java.util.ArrayList;

import onworld.sbtn.MyApplication;
import onworld.sbtn.R;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.BannerItemClickListener;
import onworld.sbtn.callbacks.BoughtContentClickListener;
import onworld.sbtn.callbacks.LanguageSelectListener;
import onworld.sbtn.callbacks.MoreHomeClickListener;
import onworld.sbtn.callbacks.PackageListClickListener;
import onworld.sbtn.fragments.homedetails.CategoryFragment;
import onworld.sbtn.fragments.homedetails.PackageDetailFragment;
import onworld.sbtn.fragments.homedetails.PackageFragment;
import onworld.sbtn.fragments.homedetails.VotingProgramFragment;
import onworld.sbtn.fragments.homedetails.VotingRoundDetailFragment;
import onworld.sbtn.fragments.homes.ChangeLanguageFragment;
import onworld.sbtn.fragments.homes.NavDrawerFragment;
import onworld.sbtn.fragments.homes.BoughtFragment;
import onworld.sbtn.fragments.homes.HomeFragment;
import onworld.sbtn.fragments.homes.OWLoginFragment;
import onworld.sbtn.fragments.homes.PurchasedPackageFragment;
import onworld.sbtn.fragments.homes.VotingFragment;
import onworld.sbtn.josonmodel.BannerItems;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.NavDrawerRowItem;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.service.PingServerService;
import onworld.sbtn.settings.CastPreference;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.NetworkUtils;
import onworld.sbtn.utils.URL;


public class MainActivity extends AppCompatActivity implements NavDrawerFragment.ASNavDrawerClickListener, BannerItemClickListener,
        MoreHomeClickListener, LanguageSelectListener, PackageListClickListener, BoughtContentClickListener {
    public static final String LANGUAGE_CONTENT_POSITION = "languageContentPosition";
    public static final String LOGIN_STATE = "loginstate";
    public static final String CANDIDATE_ID = "candidateId";
    public static final String ROUND_ID = "roundId";
    NavDrawerFragment navDrawerFragment;
    private Toolbar mToolbar;
    private Fragment mFragment;
    private LinearLayout mMainView;
    private VideoCastManager mCastManager;
    private VideoCastConsumer mCastConsumer;
    private MiniController mMini;
    private MenuItem mediaRouteMenuItem;
    private TextView mTitleToolbar;
    private ImageView logoToolbar;
    private boolean mIsHoneyCombOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    private int nowPosition;
    private FragmentManager mFragmentManager;
    private boolean isReloadHome;
    private CacheDataManager mCacheDataManager;
    private PingServerService mBoundService;
    private boolean mServiceBound = false;
    private Intent intent;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PingServerService.MyPingBinder myBinder = (PingServerService.MyPingBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_activity_main);
        mCastManager = VideoCastManager.getInstance();
        mFragmentManager = getSupportFragmentManager();
        SharedPreferences settings = getSharedPreferences(LanguageSelectionActivity.PREFS_NAME, 0);
        mCastManager.langSelectPosition = settings.getInt(LANGUAGE_CONTENT_POSITION, 0);
        mCastManager.langOfContentId = settings.getInt(LanguageSelectionActivity.LANGUAGE_CONTENT_ID, 0);
        // -- Adding MiniController
        mMini = (MiniController) findViewById(R.id.miniController1);
        mCastManager.addMiniController(mMini);

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
                onworld.sbtn.utils.Utils.
                        showToast(MainActivity.this, R.string.connection_temp_lost);
            }

            @Override
            public void onConnectivityRecovered() {
                onworld.sbtn.utils.Utils.
                        showToast(MainActivity.this, R.string.connection_recovered);
            }

            @Override
            public void onCastDeviceDetected(final MediaRouter.RouteInfo info) {
                if (!CastPreference.isFtuShown(MainActivity.this) && mIsHoneyCombOrAbove) {
                    CastPreference.setFtuShown(MainActivity.this);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                        }
                    }, 1000);
                }
            }
        };
        mCastManager.reconnectSessionIfPossible();
        setUpNavDrawer();
        displayHomeView(NavDrawerFragment.HOME, 1, null);
        mCacheDataManager = CacheDataManager.getInstance(this);

    }

    public void bannerClick(int position, ArrayList<BannerItems> bannerItemses) {
        if (bannerItemses != null) {
            int type = bannerItemses.get(position).getType();
            int objectId = bannerItemses.get(position).getObjectID();
            NavDrawerRowItem navDrawerRowItem;
            if (type == 1) {
                navDrawerRowItem = new NavDrawerRowItem();
                navDrawerRowItem.setId(objectId);
                navDrawerRowItem.setTitle(NavDrawerFragment.CATEGORIES);
                navDrawerRowItem.setProvider(true);
                displayHomeView(navDrawerRowItem.getTitle(), 0, navDrawerRowItem);

            } else if (type == 2) {
                navDrawerRowItem = new NavDrawerRowItem();
                navDrawerRowItem.setId(objectId);
                navDrawerRowItem.setTitle(NavDrawerFragment.CATEGORIES);
                navDrawerRowItem.setProvider(false);
                displayHomeView(navDrawerRowItem.getTitle(), 0, navDrawerRowItem);
            } else if (type == 3) {
                String urlImage = URL.DETAIL + objectId;
                Intent intent = new Intent(MainActivity.this, LocalPlayerActivity.class);
                intent.putExtra(Constant.DETAIL_ID_KEY, objectId);
                intent.putExtra(LocalPlayerActivity.SHOULD_START, false);
                startActivityForResult(intent, Constant.LOGIN_MAIN_REQUEST_CODE);
            }
        }

    }

    public void displayHomeView(String navTitle, int position, NavDrawerRowItem navDrawerRowItem) {

        if (NetworkUtils.getConnectivityStatus(this) == NetworkUtils.TYPE_NOT_CONNECTED) {
            alertNetwork(this, getString(R.string.msg_no_internet_connection));
        } else {
            int id;
            nowPosition = position;
            boolean isProvider = false;
            if (navDrawerRowItem == null) {
                id = 0;
            } else {
                id = navDrawerRowItem.getId();
                isProvider = navDrawerRowItem.getIsProvider();
            }
            String TAG = "";
            switch (navTitle) {
                case NavDrawerFragment.HOME:
                    mFragment = HomeFragment.newInstance(isReloadHome);
                    isReloadHome = false;
                    break;
                case NavDrawerFragment.LANGUAGE_OF_CONTENT:
                    mFragment = new ChangeLanguageFragment();
                    break;
                case NavDrawerFragment.VOTING:
                    mFragment = new VotingFragment();
                    break;
                case NavDrawerFragment.PACKAGE:
                    mFragment = new PackageFragment();
                    break;

                case NavDrawerFragment.BOUGHT:
                    mFragment = new BoughtFragment();
                    TAG = BoughtFragment.TAG;
                    break;
                case NavDrawerFragment.PACKAGE_PURCHASED:
                    mFragment = new PurchasedPackageFragment();
                    TAG = PurchasedPackageFragment.TAG;
                    break;
                default:
                    if (isProvider == true) {
                        mFragment = CategoryFragment.newInstance(URL.PROVIDER, id, true);
                    } else {
                        mFragment = CategoryFragment.newInstance(URL.CATEGORY, id, false);
                    }
                    break;
            }
            if (mFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_content, mFragment, TAG).commitAllowingStateLoss();
            }
            invalidateOptionsMenu();
        }

    }

    private void setUpNavDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        mMainView = (LinearLayout) findViewById(R.id.main_view);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitleToolbar = (TextView) mToolbar.findViewById(R.id.title_actionbar);
        logoToolbar = (ImageView) mToolbar.findViewById(R.id.toolbar_asia_logo);
        logoToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nowPosition != 1) {
                    displayHomeView(NavDrawerFragment.HOME, 1, null);
                }
            }
        });
        navDrawerFragment =
                (NavDrawerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.id_drawer_navigation_fragment);
        navDrawerFragment.setUp(R.id.id_drawer_navigation_fragment, (DrawerLayout) findViewById(R.id.id_nav_drawer), mToolbar, mMainView);

        navDrawerFragment.setASNavDrawerClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mediaRouteMenuItem = mCastManager.
                addMediaRouterButton(menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_show_queue).setVisible(mCastManager.isConnected());
        if (mFragment instanceof VotingFragment || mFragment instanceof VotingProgramFragment || mFragment instanceof VotingRoundDetailFragment) {
            menu.findItem(R.id.action_search).setVisible(false);
        } else {
            menu.findItem(R.id.action_search).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra("KEY", "VALUE");
        }
        super.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_show_queue:
                if (mMini.isVisible()) {
                    mCastManager.updateMiniControllersVisibility(false);
                } else {
                    mCastManager.updateMiniControllersVisibility(true);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        mCastManager = VideoCastManager.getInstance();
        if (null != mCastManager) {
            mCastManager.addVideoCastConsumer(mCastConsumer);
            mMini.setOnMiniControllerChangedListener(mCastManager);
            mCastManager.incrementUiCounter();
        }
        super.onResume();
    }

    @Override
    protected void onRestart() {
        invalidateOptionsMenu();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        mCastManager.decrementUiCounter();
        mCastManager.removeVideoCastConsumer(mCastConsumer);
        SharedPreferences settings = getSharedPreferences(LanguageSelectionActivity.PREFS_NAME, 0); // 0 - for private mode
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(LANGUAGE_CONTENT_POSITION, mCastManager.langSelectPosition);
        editor.putInt(LanguageSelectionActivity.LANGUAGE_CONTENT_ID, mCastManager.langOfContentId);
        editor.commit();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (null != mCastManager && NetworkUtils.getConnectivityStatus(this) != 0) {
            mMini.removeOnMiniControllerChangedListener(mCastManager);
            mCastManager.removeMiniController(mMini);
            //
            mCastConsumer.onDisconnected();
            mCastManager.disconnect();

        }
        intent = new Intent(this, PingServerService.class);
        startService(intent);
        //bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        if (mServiceBound) {
            mBoundService.castStopPingServer();
            unbindService(mServiceConnection);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (navDrawerFragment.getDrawerOpen()) {
            navDrawerFragment.closeDrawer();
        } else if (nowPosition == 1) {
            super.onBackPressed();
        } else {
            displayHomeView(NavDrawerFragment.HOME, 1, null);
        }
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return mCastManager.onDispatchVolumeKeyEvent(event, MyApplication.VOLUME_INCREMENT)
                || super.dispatchKeyEvent(event);
    }

    @Override
    public void onASNavDrawerClick(int position, NavDrawerRowItem navDrawerRowItem) {
        if (position == 0) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, Constant.LOGIN_MAIN_REQUEST_CODE);
        } else if (position == 100) {
            displayHomeView(NavDrawerFragment.BOUGHT, position, navDrawerRowItem);
        } else if (position == 200) {
            displayHomeView(NavDrawerFragment.PACKAGE_PURCHASED, position, navDrawerRowItem);
        } else {
            if (position == nowPosition) {

            } else {
                displayHomeView(navDrawerRowItem.getTitle(), position, navDrawerRowItem);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.LOGIN_MAIN_REQUEST_CODE) {
            if (resultCode == Constant.LOGIN_MAIN_RESULT_CODE) {
                boolean isLogin = data.getBooleanExtra(OWLoginFragment.IS_LOGIN, false);
                if (isLogin) {
                    navDrawerFragment.navDrawerNotifyDataChange(true);
                } else {
                    navDrawerFragment.navDrawerNotifyDataChange(false);
                    displayHomeView(NavDrawerFragment.HOME, 1, null);
                    mCacheDataManager.clearCacheYourContent();
                    mCacheDataManager.clearCachePackageData();
                }
            }
        } else if (requestCode == Constant.MAIN_DETAIL_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Constant.MAIN_DETAIL_ACTIVITY_RESULT_CODE) {
                if (nowPosition != 1) {
                    displayHomeView(NavDrawerFragment.HOME, 1, null);
                }
            }
        } else if (requestCode == Constant.PACKAGE_DETAIL_REQUEST_CODE || requestCode == PackageDetailFragment.RC_REQUEST) {
            if (resultCode == Constant.LOGIN_MAIN_RESULT_CODE || resultCode == -1) {
                PackageDetailFragment packageDetailFragment = (PackageDetailFragment) mFragmentManager.findFragmentByTag(PackageDetailFragment.TAG);
                if (packageDetailFragment != null) {
                    packageDetailFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        } else if (requestCode == BoughtFragment.BOUGHT_FRAGMENT_REQUEST) {
            BoughtFragment boughtFragment = (BoughtFragment) mFragmentManager.findFragmentByTag(BoughtFragment.TAG);
            if (boughtFragment != null) {
                boughtFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (resultCode == OWLoginFragment.LOGIN_FRAGMENT_RESULT_CODE) {
            boolean isLogin = data.getBooleanExtra(OWLoginFragment.IS_LOGIN, false);
            if (isLogin) {
                navDrawerFragment.navDrawerNotifyDataChange(true);
            } else {
                navDrawerFragment.navDrawerNotifyDataChange(false);
                displayHomeView(NavDrawerFragment.HOME, 1, null);
                mCacheDataManager.clearCacheYourContent();
                mCacheDataManager.clearCachePackageData();
            }
        } else if (requestCode == PurchasedPackageFragment.RC_REQUEST) {
            PurchasedPackageFragment purchasedPackageFragment = (PurchasedPackageFragment) mFragmentManager.findFragmentByTag(PurchasedPackageFragment.TAG);
            if (purchasedPackageFragment != null) {
                purchasedPackageFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBannerItemClickListener(int position, ArrayList<BannerItems> bannerItemses) {
        bannerClick(position, bannerItemses);
    }

    @Override
    public void onMoreHomeClickListener(int type, String titleHeader, ArrayList<DataDetailItem> dataDetailItems) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.ASGRID_DETAIL_TITLE, titleHeader);
        intent.putParcelableArrayListExtra(DetailActivity.ASGRID_DETAIL_DATA, dataDetailItems);
        intent.putExtra(HomeFragment.AS_HOME_TYPE, type);
        startActivityForResult(intent, Constant.MAIN_DETAIL_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onLanguageSelectListener() {
        isReloadHome = true;
        String urlMenuDrawer = URL.MENU + "&lang_of_content=" + mCastManager.langOfContentId;
        navDrawerFragment.setDataDrawer(urlMenuDrawer);
        displayHomeView(NavDrawerFragment.HOME, 1, null);
    }

    @Override
    public void onPackageListClickListener(PackageDetail packageDetail) {

    }

    @Override
    public void onBoughtBuyContentClickListener() {
        displayHomeView(NavDrawerFragment.PACKAGE, 0, null);
    }

    public void alertNetwork(Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context).create();
        alertDialog.setMessage(message);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();

            }
        });

        alertDialog.show();
    }
}
