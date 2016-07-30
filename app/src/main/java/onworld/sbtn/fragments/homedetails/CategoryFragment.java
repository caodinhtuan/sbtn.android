package onworld.sbtn.fragments.homedetails;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.gson.Gson;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.activities.DetailActivity;
import onworld.sbtn.activities.MusicPlayerActivity;
import onworld.sbtn.adapters.CategoryRecyclerHorizontalHolder;
import onworld.sbtn.adapters.CategoryRecyclerVerticalApdater;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.DataDetailLoadedListener;
import onworld.sbtn.fragments.homes.HomeFragment;
import onworld.sbtn.josonmodel.ASHomeData;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.Shows;
import onworld.sbtn.josonmodel.ShowsHome;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.tasks.TaskLoadDataDetail;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.URL;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment implements DataDetailLoadedListener, RecyclerView.OnItemTouchListener {

    public static final String CATEGORY_DATA_SAVED = "categorydatasaved";
    public static final String URL_DETAIL = "urldetail";
    public static final String CATEGORY_ID = "categoryid";
    public static final String CATEGORY_URL = "categoryurl";
    public static final String IS_PROVIDER = "is.provider";
    ArrayList<ASHomeData> mHomeDatas = new ArrayList<>();
    private RecyclerView homeRecyclerViewVertical;
    private CategoryRecyclerVerticalApdater homeRecyclerVerticalApdater;
    private GestureDetector gestureDetector;
    private VideoCastManager mCastManager;
    private VideoCastConsumerImpl mCastConsumer;
    private int categoryId;
    private ArrayList<Shows> showses;
    private String urlHome;
    private ProgressBar mLoading;
    private String urlCategoryOrProvider;
    private CacheDataManager mCacheDataManager;
    private boolean isProvider;

    public CategoryFragment() {
    }

    public static CategoryFragment newInstance(String url, int id, boolean isProvider) {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CATEGORY_ID, id);
        bundle.putString(CATEGORY_URL, url);
        bundle.putBoolean(IS_PROVIDER, isProvider);
        categoryFragment.setArguments(bundle);
        return categoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCastManager = VideoCastManager.getInstance();
        categoryId = getArguments().getInt(CATEGORY_ID);
        isProvider = getArguments().getBoolean(IS_PROVIDER);
        urlCategoryOrProvider = getArguments().getString(CATEGORY_URL);
        mCacheDataManager = CacheDataManager.getInstance(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.as_fragment_category, container, false);
        mLoading = (ProgressBar) view.findViewById(R.id.home_loading);
        homeRecyclerViewVertical = (RecyclerView) view.findViewById(R.id.home_vertical_recyclerview);
        homeRecyclerViewVertical.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeRecyclerVerticalApdater = new CategoryRecyclerVerticalApdater(getActivity());
        homeRecyclerViewVertical.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .paintProvider(homeRecyclerVerticalApdater)
                .visibilityProvider(homeRecyclerVerticalApdater)
                .marginProvider(homeRecyclerVerticalApdater)
                .build());
        homeRecyclerViewVertical.setHasFixedSize(true);
        homeRecyclerViewVertical.setAdapter(homeRecyclerVerticalApdater);
        showses = new ArrayList<>();
        ShowsHome showsHome = null;
        if (mCacheDataManager != null) {
            if (isProvider == false) {
                showsHome = mCacheDataManager.getCacheCategoryData(categoryId);
            } else {
                showsHome = mCacheDataManager.getCacheProviderData(categoryId);
            }

        }
        if (showsHome != null) {
            setupCategoryData(showsHome);
        } else {
            getCategoryDataFromServer();
        }
        mCastConsumer = new VideoCastConsumerImpl() {
            @Override
            public void onConnected() {
                super.onConnected();
            }

            @Override
            public void onDisconnected() {
                super.onDisconnected();
            }
        };
        mCastManager.addVideoCastConsumer(mCastConsumer);
        return view;
    }

    private void getCategoryDataFromServer() {
        urlHome = urlCategoryOrProvider + categoryId + "&lang_of_content=" + mCastManager.langOfContentId;
        new TaskLoadDataDetail(this, urlHome).execute();
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

    @Override
    public void onResume() {
        super.onResume();
        homeRecyclerViewVertical.addOnItemTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        homeRecyclerViewVertical.removeOnItemTouchListener(this);
    }

    @Override
    public void onDetach() {
        mCastManager.removeVideoCastConsumer(mCastConsumer);
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(CATEGORY_DATA_SAVED, mHomeDatas);
    }

    @Override
    public void onDataDetailLoaded(String jsonObject) {
        if (jsonObject != null) {
            ArrayList<Shows> showses;
            Gson viewGson = new Gson();
            ShowsHome showsHome = viewGson.fromJson(jsonObject.toString(), ShowsHome.class);
            if (showsHome != null) {
                if (isProvider == false) {
                    mCacheDataManager.cacheCategoryData(categoryId, showsHome);
                } else {
                    mCacheDataManager.cacheProviderData(categoryId, showsHome);
                }
                setupCategoryData(showsHome);
            }
        }

    }

    private void setupCategoryData(ShowsHome showsHome) {
        ArrayList<Shows> showsesList = showsHome.getShowses();
        int realShowSize = showsesList.size() * 2;
        for (int i = 0; i < realShowSize; i++) {
            Shows mShows = new Shows();
            if (i % 2 == 0) {
                mShows = showsesList.get(i / 2);
            } else {
                mShows = showsesList.get(i / 2);
            }
            this.showses.add(mShows);
        }
        homeRecyclerVerticalApdater.setHomeVerticalData(this.showses);
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = homeRecyclerViewVertical.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = homeRecyclerViewVertical.getChildAdapterPosition(child);
            if (position % 2 == 0) {
                Shows shows = showses.get(position);
                ArrayList<DataDetailItem> dataDetailItems = shows.getShowsDetails();
                if (dataDetailItems.size() > 6) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putParcelableArrayListExtra(DetailActivity.ASGRID_DETAIL_DATA, dataDetailItems);
                    intent.putExtra(DetailActivity.ASGRID_DETAIL_TITLE, shows.getShowsName());
                    intent.putExtra(HomeFragment.AS_HOME_TYPE, shows.getMode());
                    getActivity().startActivityForResult(intent, Constant.LOGIN_MAIN_REQUEST_CODE);
                }

            } else {
                CategoryRecyclerHorizontalHolder homeRecyclerHorizontalHolder =
                        (CategoryRecyclerHorizontalHolder) homeRecyclerViewVertical.findViewHolderForAdapterPosition(position);
                int childPosition = homeRecyclerHorizontalHolder.getChildItemPosition(e.getX() - child.getX(), e.getY() - child.getY());
                if (childPosition < 0) {
                    return false;
                }
                if (showses != null) {
                    Shows shows = showses.get(position);
                    ArrayList<DataDetailItem> showsDetails = shows.getShowsDetails();
                    DataDetailItem dataDetailItem = showsDetails.get(childPosition);
                    final String url = URL.DETAIL + dataDetailItem.getId();
                    Intent intent;
                    if (shows.getMode() == HomeFragment.AUDIO_TYPE) {
                        intent = new Intent(getActivity(), MusicPlayerActivity.class);
                        intent.putExtra(MusicPlayerActivity.THUMB, dataDetailItem.getImage());

                    } else {
                        intent = new Intent(getActivity(), LocalPlayerActivity.class);
                        intent.putExtra(LocalPlayerActivity.SHOULD_START, false);
                    }
                    intent.putExtra(Constant.DETAIL_ID_KEY, dataDetailItem.getId());

                    //startActivity(intent);
                    getActivity().startActivityForResult(intent, Constant.LOGIN_MAIN_REQUEST_CODE);
                }
            }

        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
