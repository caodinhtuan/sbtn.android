package onworld.sbtn.fragments.homes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.adapters.NavDrawerAdapter;
import onworld.sbtn.adapters.HeaderViewHolder;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.CheckLanguageOfContentListener;
import onworld.sbtn.callbacks.DataLoadedWithoutParamsListener;
import onworld.sbtn.josonmodel.CategoryIdList;
import onworld.sbtn.josonmodel.Drawer;
import onworld.sbtn.josonmodel.DrawerItem;
import onworld.sbtn.josonmodel.Language;
import onworld.sbtn.josonmodel.LanguageItem;
import onworld.sbtn.josonmodel.NavDrawerRowItem;
import onworld.sbtn.tasks.TaskCheckLanguageOfContent;
import onworld.sbtn.tasks.TaskLoadDataWithoutParams;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

public class NavDrawerFragment extends Fragment implements RecyclerView.OnItemTouchListener, DataLoadedWithoutParamsListener,
        CheckLanguageOfContentListener, NavDrawerAdapter.NavClickListener {
    public static final String DRAWER_DATA_SAVED = "drawerdatasaved";
    public static final String PROVIDERS = "providers";
    public static final String CATEGORIES = "categories";

    public static final String HOME = "home";
    public static final String PACKAGE = "PACKAGE";
    public static final String BOUGHT = "Your Purchased Content";
    public static final String PACKAGE_PURCHASED = "Your Package Purchased";
    public static final String LANGUAGE_OF_CONTENT = "LANGUAGE OF CONTENT";
    public static final String VOTING = "voting";
    public VideoCastManager mCastManager;
    ArrayList<DrawerItem> drawerItemsProviders;
    ArrayList<DrawerItem> drawerItemsCategories;
    private RecyclerView mRecyclerView;
    private NavDrawerAdapter mNavDrawerAdapter;
    private View mViewContainer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private ArrayList<NavDrawerRowItem> navDrawerRowItems;
    private GestureDetector gestureDetector;
    private ASNavDrawerClickListener navASDrawerClickListener;
    private boolean showLanguageOfContent;
    private boolean isShowProvider;
    private CacheDataManager mCacheDataManager;

    public NavDrawerFragment() {
    }

    public ArrayList<NavDrawerRowItem> getData(ArrayList<DrawerItem> providers, ArrayList<DrawerItem> categories) {

        NavDrawerRowItem navDrawerRowItem;
        ArrayList<NavDrawerRowItem> navDrawerRowItems = new ArrayList<>();
        String[] hardMenuItem;
        int providerSize = providers.size();
        if (providerSize > 1) {
            isShowProvider = true;
        } else {
            isShowProvider = false;
        }
        if (mCastManager.isShowLanguageOfContent == true) {
            if (isShowProvider == true) {
                hardMenuItem = new String[]{"HEADER", HOME, PACKAGE, LANGUAGE_OF_CONTENT, PROVIDERS, CATEGORIES};
            } else {
                hardMenuItem = new String[]{"HEADER", HOME, PACKAGE, LANGUAGE_OF_CONTENT, CATEGORIES};
            }

        } else {
            if (isShowProvider == true) {
                hardMenuItem = new String[]{"HEADER", HOME, PACKAGE, PROVIDERS, CATEGORIES};
            } else {
                hardMenuItem = new String[]{"HEADER", HOME, PACKAGE, CATEGORIES};
            }

        }

        for (int i = 0; i < hardMenuItem.length; i++) {
            navDrawerRowItem = new NavDrawerRowItem();
            if (hardMenuItem[i].equalsIgnoreCase(HOME)) {
                navDrawerRowItem.setIconId(R.mipmap.as_icon_home);
                navDrawerRowItem.setTitle(HOME);
                navDrawerRowItem.setType(NavDrawerAdapter.TYPE_ITEM_NON_CHILD);
            } else if (hardMenuItem[i].equalsIgnoreCase(PACKAGE)) {
                navDrawerRowItem.setIconId(R.mipmap.icon_package);
                navDrawerRowItem.setTitle(PACKAGE);
                navDrawerRowItem.setType(NavDrawerAdapter.TYPE_ITEM_NON_CHILD);
            } else if (hardMenuItem[i].equalsIgnoreCase(BOUGHT)) {
                navDrawerRowItem.setIconId(R.mipmap.as_icon_home);
                navDrawerRowItem.setTitle(BOUGHT);
                navDrawerRowItem.setType(NavDrawerAdapter.TYPE_ITEM_NON_CHILD);
            } else if (hardMenuItem[i].equalsIgnoreCase(LANGUAGE_OF_CONTENT)) {
                navDrawerRowItem.setIconId(R.drawable.as_icon_language);
                navDrawerRowItem.setTitle(LANGUAGE_OF_CONTENT);
                navDrawerRowItem.setType(NavDrawerAdapter.TYPE_ITEM_NON_CHILD);
            } else if (hardMenuItem[i].equalsIgnoreCase(VOTING)) {
                navDrawerRowItem.setIconId(R.mipmap.icon_voting_white);
                navDrawerRowItem.setTitle(VOTING);
                navDrawerRowItem.setType(NavDrawerAdapter.TYPE_ITEM_NON_CHILD);
            } else if (hardMenuItem[i].equalsIgnoreCase(PROVIDERS)) {
                navDrawerRowItem.setIconId(R.drawable.as_icon_provider);
                navDrawerRowItem.setTitle("PROVIDERS");
                navDrawerRowItem.setType(NavDrawerAdapter.TYPE_ITEM);
                navDrawerRowItem.invisibleChildren = new ArrayList<>();
                for (int j = 0; j < providerSize; j++) {
                    NavDrawerRowItem child = new NavDrawerRowItem();
                    child.setType(NavDrawerAdapter.TYPE_CHILD);
                    child.setTitle(providers.get(j).getName());
                    child.setId(providers.get(j).getId());
                    child.setProvider(true);
                    navDrawerRowItem.invisibleChildren.add(child);
                }
            } else if (hardMenuItem[i].equalsIgnoreCase(CATEGORIES)) {
                navDrawerRowItem.setTitle("CATEGORIES");
                navDrawerRowItem.setIconId(R.drawable.as_icon_category);
                navDrawerRowItem.setType(NavDrawerAdapter.TYPE_ITEM);
                navDrawerRowItem.invisibleChildren = new ArrayList<>();
                CategoryIdList categoryIdList = new CategoryIdList();
                for (int j = 0; j < categories.size(); j++) {
                    DrawerItem drawerItem = categories.get(j);
                    String categoryTitle = drawerItem.getName();
                    int categoryId = drawerItem.getId();
                    if (categoryTitle.equalsIgnoreCase(CacheDataManager.CATEGORY_SERIES_TITLE)) {
                        categoryIdList.setCategorySeries(categoryId);
                    } else if (categoryTitle.equalsIgnoreCase(CacheDataManager.CATEGORY_SHOWES_TITLE)) {
                        categoryIdList.setCategoryShows(categoryId);
                    } else if (categoryTitle.equalsIgnoreCase(CacheDataManager.CATEGORY_LIVETV_TITLE)) {
                        categoryIdList.setCategoryLiveTv(categoryId);
                    } else if (categoryTitle.equalsIgnoreCase(CacheDataManager.CATEGORY_KARAOKE_TITLE)) {
                        categoryIdList.setCategoryKaraoke(categoryId);
                    } else if (categoryTitle.equalsIgnoreCase(CacheDataManager.CATEGORY_MUSIC_TITLE)) {
                        categoryIdList.setCategoryMusic(categoryId);
                    } else if (categoryTitle.equalsIgnoreCase(CacheDataManager.CATEGORY_MUSIC_VIDEO_TITLE)) {
                        categoryIdList.setCategoryMusicVideo(categoryId);
                    } else if (categoryTitle.equalsIgnoreCase(CacheDataManager.CATEGORY_RADIO_TITLE)) {
                        categoryIdList.setCategoryRadio(categoryId);
                    }
                    NavDrawerRowItem child = new NavDrawerRowItem();
                    child.setType(NavDrawerAdapter.TYPE_CHILD);
                    child.setTitle(categoryTitle);
                    child.setId(categoryId);
                    child.setProvider(false);
                    navDrawerRowItem.invisibleChildren.add(child);

                }
                mCacheDataManager.cacheCategoryIdList(categoryIdList);
            } else {
                navDrawerRowItem.setType(NavDrawerAdapter.TYPE_ITEM_NON_CHILD);
            }
            navDrawerRowItems.add(navDrawerRowItem);
        }

        return navDrawerRowItems;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCastManager = VideoCastManager.getInstance();
        mCacheDataManager = CacheDataManager.getInstance(getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.as_fragment_nav_drawer, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_nav_drawer_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mNavDrawerAdapter = new NavDrawerAdapter(getActivity());
        mNavDrawerAdapter.setNavClickListenter(this);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .paintProvider(mNavDrawerAdapter)
                .visibilityProvider(mNavDrawerAdapter)
                .marginProvider(mNavDrawerAdapter)
                .build());

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mNavDrawerAdapter);
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        if (savedInstanceState != null) {
            navDrawerRowItems = savedInstanceState.getParcelableArrayList(DRAWER_DATA_SAVED);
            mNavDrawerAdapter.setDataDrawer(navDrawerRowItems);
        } else {
            String urlMenuDrawer = URL.MENU + "&lang_of_content=" + mCastManager.langOfContentId;
            new TaskLoadDataWithoutParams(this, urlMenuDrawer).execute();
        }
        return view;
    }

    public void setDataDrawer(String url) {
        new TaskLoadDataWithoutParams(this, url).execute();
    }

    public void navDrawerNotifyDataChange(boolean statusLogin) {
        mNavDrawerAdapter.notifyChangeHeader(0, statusLogin);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    public void setUp(int idNavDrawer, DrawerLayout drawerLayout, Toolbar toolbar, final LinearLayout mMainView) {
        mViewContainer = getActivity().findViewById(idNavDrawer);
        mDrawerLayout = drawerLayout;
        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, (R.string.drawer_open), (R.string.drawer_close)) {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (mNavDrawerAdapter.isLoginVisibleAdapter()) {
                    if (VotingUtils.checkLoginStatus(getActivity()) == true) {
                        mNavDrawerAdapter.notifyChangeHeader(0, true);
                        mNavDrawerAdapter.setLoginVisible(false);
                    }
                }
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mMainView.setTranslationX(slideOffset * drawerView.getWidth());
            }
        };
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
                //open when first open
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DRAWER_DATA_SAVED, navDrawerRowItems);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.addOnItemTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRecyclerView.removeOnItemTouchListener(this);
    }

    @Override
    public void onDataLoadedWithoutParams(JSONObject jsonObject) {
        String jsonString = jsonObject.toString();
        Gson gson = new Gson();
        Drawer drawer = gson.fromJson(jsonString, Drawer.class);
        int errorCode = drawer.getError();
        if (errorCode == Constant.ERROR_VERSION_CODE) {
            //Utils.showUpdateNewVersionDialog(getActivity());
        } else if (errorCode == Utils.OLD_REQUEST_SUCCESS) {
            drawerItemsProviders = drawer.getProvider();
            drawerItemsCategories = drawer.getCategory();
            //new TaskCheckLanguageOfContent(this, URL.LANGUAGE).execute();
            ArrayList<LanguageItem> language = null;
            if (mCacheDataManager != null) {
                language = mCacheDataManager.getCacheLanguageOfContent();
            }

            if (language != null && language.size() > 1) {
                this.navDrawerRowItems = getData(drawerItemsProviders, drawerItemsCategories);
                mNavDrawerAdapter.setDataDrawer(navDrawerRowItems);
            } else {
                new TaskCheckLanguageOfContent(this, URL.LANGUAGE).execute();
            }
        } else {

        }

    }

    @Override
    public void onCheckLanguageOfContent(JSONObject jsonObject) {
        if (jsonObject != null) {
            Gson gson = new Gson();
            Language language = gson.fromJson(jsonObject.toString(), Language.class);
            final ArrayList<LanguageItem> languageItems = language.getLanguageItems();
            mCastManager.isShowLanguageOfContent = checkShowLanguageOfContent(languageItems.size());
            mCacheDataManager.cacheLanguageOfContent(languageItems);
            this.navDrawerRowItems = getData(drawerItemsProviders, drawerItemsCategories);
            mNavDrawerAdapter.setDataDrawer(navDrawerRowItems);
        } else {
            mCastManager.isShowLanguageOfContent = checkShowLanguageOfContent(0);
            this.navDrawerRowItems = getData(drawerItemsProviders, drawerItemsCategories);
            mNavDrawerAdapter.setDataDrawer(navDrawerRowItems);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = mRecyclerView.getChildAdapterPosition(child);
            if (mRecyclerView.getChildViewHolder(child) instanceof HeaderViewHolder) {
                /*navASDrawerClickListener.onASNavDrawerClick(position, null);
                mDrawerLayout.closeDrawer(GravityCompat.START);*/

            } else if (mRecyclerView.getChildViewHolder(child) instanceof NavDrawerAdapter.ItemViewHolder) {
                mNavDrawerAdapter.expandClick(navDrawerRowItems.get(position), (NavDrawerAdapter.ItemViewHolder) mRecyclerView.getChildViewHolder(child));

            } else {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                NavDrawerRowItem navDrawerRowItem = navDrawerRowItems.get(position);
                navASDrawerClickListener.onASNavDrawerClick(position, navDrawerRowItem);
            }
        }
        return false;
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public boolean getDrawerOpen() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public void setASNavDrawerClickListener(ASNavDrawerClickListener asNavDrawerClickListener) {
        this.navASDrawerClickListener = asNavDrawerClickListener;
    }

    public boolean checkShowLanguageOfContent(int languageSize) {
        if (languageSize > 1) {
            return true;
        }
        return false;
    }

    @Override
    public void onNavClickListener(int placeClick) {
        if (placeClick == HeaderViewHolder.PACKAGE_CONTENT) {
            navASDrawerClickListener.onASNavDrawerClick(100, null);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (placeClick == HeaderViewHolder.PACKAGE_PURCHASED) {
            navASDrawerClickListener.onASNavDrawerClick(200, null);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            navASDrawerClickListener.onASNavDrawerClick(0, null);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public interface ASNavDrawerClickListener {
        void onASNavDrawerClick(int position, NavDrawerRowItem navDrawerRowItem);
    }
}
