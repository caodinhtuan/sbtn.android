package onworld.sbtn.fragments.homes;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.activities.MusicPlayerActivity;
import onworld.sbtn.adapters.BannerPagerAdapter;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.BannerItemClickListener;
import onworld.sbtn.callbacks.DataLoadedWithoutParamsListener;
import onworld.sbtn.callbacks.MoreHomeClickListener;
import onworld.sbtn.infinitepager.AutoScrollViewPager;
import onworld.sbtn.josonmodel.ASHomeData;
import onworld.sbtn.josonmodel.ASHomeDataObject;
import onworld.sbtn.josonmodel.BannerItems;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.HomeItem;
import onworld.sbtn.josonmodel.enums.HomeType;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.tasks.TaskLoadDataWithoutParams;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.views.HomeViewItem;
import onworld.sbtn.views.pagerindicators.CirclePageIndicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements DataLoadedWithoutParamsListener,
        BannerPagerAdapter.ChangeBannerListener, HomeViewItem.MoreHomeViewClickListener, HomeViewItem.HomeViewItemClickListener {
    public static final String HOME_DATA_SAVED = "homedatasaved";
    public static final String IS_RELOAD_HOME = "is.reload.home";
    public static final int HOME_FRAGMENT_REQUEST_CODE = 1000;
    public static final int VIDEO_TYPE = 1;
    public static final int AUDIO_TYPE = 0;
    public static final String AS_HOME_TYPE = "ashometype";
    private LinearLayout homeContent;
    private RelativeLayout banner;
    private ArrayList<ASHomeData> homeDatas = new ArrayList<>();
    private ArrayList<ASHomeData> mHomeDatas = new ArrayList<>();
    private AutoScrollViewPager autoScrollViewPager;
    private CirclePageIndicator indicator;
    private VideoCastManager mCastManager;
    private VideoCastConsumerImpl mCastConsumer;
    private String urlHome;
    private ProgressBar mLoading;
    private String[] bannerLink;
    private ArrayList<BannerItems> bannerItemses = new ArrayList<>();
    private ArrayList<HomeItem> items;
    private BannerPagerAdapter bannerPagerAdapter;
    private Context context;
    private BannerItemClickListener mBannerItemClickListener;
    private MoreHomeClickListener mMoreHomeClickListener;
    private CacheDataManager mCacheDataManager;
    private boolean isReloadHome;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(boolean isReloadHome) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_RELOAD_HOME, isReloadHome);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mCastManager = VideoCastManager.getInstance();
        mBannerItemClickListener = (BannerItemClickListener) getActivity();
        mMoreHomeClickListener = (MoreHomeClickListener) getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeContent = (LinearLayout) view.findViewById(R.id.home_content);
        autoScrollViewPager = (AutoScrollViewPager) view.findViewById(R.id.auto_scroll_pager);
        indicator = (CirclePageIndicator) view.findViewById(R.id.banner_indicator);
        mLoading = (ProgressBar) view.findViewById(R.id.home_loading);
        banner = (RelativeLayout) view.findViewById(R.id.home_banner_layout);

        urlHome = URL.HOME + "&lang_of_content=" + mCastManager.langOfContentId;
//        new TaskLoadDataDetail(this, urlHome).execute();

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCacheDataManager = CacheDataManager.getInstance(getContext());
        ASHomeDataObject asHomeDataObject = null;
        isReloadHome = getArguments().getBoolean(IS_RELOAD_HOME);

        if (isReloadHome) {
            new TaskLoadDataWithoutParams(this, urlHome).execute();
        } else {
            if (mCacheDataManager != null) {
                asHomeDataObject = mCacheDataManager.getCacheHomeData();
            }
            if (asHomeDataObject != null) {
                setupDataForHome(asHomeDataObject);
            } else {
                new TaskLoadDataWithoutParams(this, urlHome).execute();
            }
        }


    }

    @Override
    public void onDetach() {
        mCastManager.removeVideoCastConsumer(mCastConsumer);
        context = null;
        super.onDetach();
    }

    @Override
    public void onDataLoadedWithoutParams(JSONObject jsonObject) {
        Gson gson = new Gson();
        ASHomeDataObject asHomeDataObject = gson.fromJson(jsonObject.toString(), ASHomeDataObject.class);
        int error = asHomeDataObject.getError();
        if (error == Constant.ERROR_VERSION_CODE) {
            Utils.showUpdateNewVersionDialog(getActivity());
        } else if (error == Utils.OLD_REQUEST_SUCCESS) {
            mCacheDataManager.cacheHomeData(asHomeDataObject);
            setupDataForHome(asHomeDataObject);
        } else {

        }

    }

    private void setupDataForHome(ASHomeDataObject asHomeDataObject) {
        mLoading.setVisibility(View.GONE);
        mHomeDatas = new ArrayList<>();
        homeDatas = new ArrayList<>();
        ArrayList<HomeItem> result = new ArrayList<>();
        ArrayList<DataDetailItem> popularDatas = null;
        ArrayList<DataDetailItem> addDatas = null;
        ArrayList<DataDetailItem> recommendData = null;
        HomeItem viewItem = asHomeDataObject.getView();
        HomeItem listenItem = asHomeDataObject.getListen();
        bannerItemses = asHomeDataObject.getBanners();

        viewItem.setType(HomeType.Video);
        viewItem.updateImageUrl("320x180");
        result.add(viewItem);

        listenItem.setType(HomeType.Audio);
        listenItem.updateImageUrl("250x250");
        result.add(listenItem);


        HomeFragment.this.items = result;

        int homeSize = this.items.size();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        HomeType homeType = HomeType.Video;
        HomeViewItem homeViewItem = null;
        String headerTitle = "";
        for (int i = 0; i < homeSize; i++) {
            HomeItem homeItem = this.items.get(i);
            for (int j = 0; j < 3; j++) {
                homeViewItem = null;
                if (i == 0) {
                    homeType = HomeType.Video;
                } else if (i == 1) {
                    homeType = HomeType.Audio;
                }
                if (j == 0) {
                    popularDatas = homeItem.getPopular();
                    if (popularDatas.size() > 0) {
                        if (i == 1) {
                            headerTitle = "Popular Audios";
                        } else {
                            headerTitle = "Popular Videos";
                        }
                        homeViewItem = new HomeViewItem(getActivity(), headerTitle, popularDatas, homeType);
                    }


                } else if (j == 1) {
                    addDatas = homeItem.getAdded();
                    if (addDatas.size() > 0) {
                        if (i == 1) {
                            headerTitle = "Recently Audios";
                        } else {
                            headerTitle = "Recently Videos";
                        }
                        homeViewItem = new HomeViewItem(getActivity(), headerTitle, addDatas, homeType);
                    }

                } else {
                    recommendData = homeItem.getRecommend();
                    if (recommendData.size() > 0) {
                        if (i == 1) {
                            headerTitle = "Recommendation Audios";
                        } else {
                            headerTitle = "Recommendation Videos";

                        }
                        homeViewItem = new HomeViewItem(getActivity(), headerTitle, recommendData, homeType);
                    }

                }
                if (homeViewItem != null) {
                    homeViewItem.setMoreHomeViewClickListener(this);
                    homeViewItem.setHomeViewItemClickListener(this);
                    if (homeViewItem.getParent() != null) {
                        ((ViewGroup) homeViewItem.getParent()).removeView(homeViewItem);
                    }
                    this.homeContent.addView(homeViewItem, params);
                }

            }
        }

        if (bannerItemses != null) {
            int bannerSize = bannerItemses.size();
            if (bannerSize > 0) {
                bannerLink = new String[bannerSize];
                BannerItems banner;
                for (int i = 0; i < bannerSize; i++) {
                    banner = bannerItemses.get(i);
                    bannerLink[i] = banner.getLinkImageBanner();
                }
                this.banner.setVisibility(View.VISIBLE);
            } else {
                this.banner.setVisibility(View.GONE);
            }
        }

        bannerPagerAdapter = new BannerPagerAdapter(context, bannerLink, bannerItemses);
        autoScrollViewPager.setAdapter(bannerPagerAdapter);
        indicator.setViewPager(autoScrollViewPager);
        bannerPagerAdapter.setChangeBannerListener(this);
        autoScrollViewPager.startAutoScroll();
        autoScrollViewPager.setCycle(true);
        autoScrollViewPager.setInterval(2000);
    }

    @Override
    public void onChangeBannerListener(int position, ArrayList<BannerItems> bannerItemses) {
        mBannerItemClickListener.onBannerItemClickListener(position, bannerItemses);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMoreHomeClickListener(HomeViewItem homeViewItem, int type, String titleHeader, ArrayList<DataDetailItem> dataDetailItems) {
        mMoreHomeClickListener.onMoreHomeClickListener(type, titleHeader, dataDetailItems);
    }

    @Override
    public void onHomeViewItemClickListener(DataDetailItem dataDetailItem, HomeType homeType) {
        Intent intent;
        if (homeType == HomeType.Audio) {
            intent = new Intent(getActivity(), MusicPlayerActivity.class);
            intent.putExtra(MusicPlayerActivity.THUMB, dataDetailItem.getImage());
        } else {
            intent = new Intent(getActivity(), LocalPlayerActivity.class);
            intent.putExtra(LocalPlayerActivity.SHOULD_START, false);
        }
        intent.putExtra(Constant.DETAIL_ID_KEY, dataDetailItem.getId());
        getActivity().startActivityForResult(intent, Constant.LOGIN_MAIN_REQUEST_CODE);
    }
}
