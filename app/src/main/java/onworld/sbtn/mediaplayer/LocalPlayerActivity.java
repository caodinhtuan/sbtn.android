/*
 * Copyright (C) 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package onworld.sbtn.mediaplayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import onworld.sbtn.MyApplication;
import onworld.sbtn.R;
import onworld.sbtn.activities.LoginActivity;
import onworld.sbtn.activities.PackageActivity;
import onworld.sbtn.adapters.ListAdapter;
import onworld.sbtn.adapters.PagerAdapterViewDetail;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.DataDetailLoadedListener;
import onworld.sbtn.callbacks.DataLoadedListener;
import onworld.sbtn.callbacks.DataLoadedNextEpisodeListener;
import onworld.sbtn.callbacks.DataStartTrackingLoadedListener;
import onworld.sbtn.callbacks.DataStopTrackingLoadedListener;
import onworld.sbtn.callbacks.EndUserViewDataLoadedListener;
import onworld.sbtn.dao.VastDAO;
import onworld.sbtn.fragments.homes.OWLoginFragment;
import onworld.sbtn.josonmodel.Adv;
import onworld.sbtn.josonmodel.Advertisement;
import onworld.sbtn.josonmodel.ContentModel;
import onworld.sbtn.josonmodel.Episode;
import onworld.sbtn.josonmodel.GroupPackage;
import onworld.sbtn.josonmodel.InfoVideoArrayModel;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.josonmodel.Related;
import onworld.sbtn.josonmodel.TimeLine;
import onworld.sbtn.josonmodel.TimeLines;
import onworld.sbtn.josonmodel.TrackingInfo;
import onworld.sbtn.josonmodel.VideoInfoData;
import onworld.sbtn.josonmodel.ViewDetail;
import onworld.sbtn.service.PingServerService;
import onworld.sbtn.tasks.TaskLoadData;
import onworld.sbtn.tasks.TaskLoadDataDetail;
import onworld.sbtn.tasks.TaskLoadDataNextEpisode;
import onworld.sbtn.tasks.TaskLoadDataStartTracking;
import onworld.sbtn.tasks.TaskLoadDataStopTracking;
import onworld.sbtn.tasks.TaskRequestEndUserView;
import onworld.sbtn.utils.CommonUtils;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.NetworkUtils;
import onworld.sbtn.utils.DeviceUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;

public class LocalPlayerActivity extends AppCompatActivity implements DataLoadedListener, RecyclerView.OnItemTouchListener,
        DataDetailLoadedListener, DataLoadedNextEpisodeListener, DataStartTrackingLoadedListener, DataStopTrackingLoadedListener,
        EndUserViewDataLoadedListener {

    public static final String ADV_TIMER_RUNNING = "This adv will close in ";
    public static final String SHOULD_START = "shouldStart";
    public static final String START_POSITION = "startPosition";
    public static final int PLAYBACK_STATE_PLAYING = 0;
    public static final int PLAYBACK_STATE_PAUSED = 1;
    public static final int PLAYBACK_STATE_BUFFERING = 2;
    public static final int PLAYBACK_STATE_IDLE = 3;
    public static final int PLAYBACK_LOCAL = 0;
    public static final int PLAYBACK_REMOTE = 1;
    public static final int LOCAL_PLAYER_ACTIVITY_LOGIN_REQUEST_CODE = 1000;
    public static final int PACKAGE_REQUEST_CODE = 2000;
    private static final String TAG = "LocalPlayerActivity";
    private static final String STOP_POSITION = "STOP_POSITION";
    private final Handler mHandler = new Handler();
    private final float mAspectRatio = 72f / 128;
    private final Handler handler = new Handler();
    private final Handler handlerLocal = new Handler();
    private final Handler handlerImageAdv = new Handler();
    private final Handler handlerTimerPing = new Handler();
    public VideoCastManager mCastManager;
    protected MediaInfo mRemoteMediaInformation;
    boolean shouldStartPlayback;
    int startPosition;
    int contentId;
    private MediaInfo singerMediaInfo;
    private int stopPosition = -1;
    private Timer timer, timerLocal, mSeekbarTimer, mControllersTimer, timerImageAdv, timerPing;
    private TimerTask timerTask, timerTaskLocal, timerImageAdvTask, timerTaskPing;
    private VideoView mVideoView;
    private TextView mEndText, mStartText;
    private SeekBar mSeekbar;
    private ImageView mPlayPause, mFullScreen;
    private ProgressBar mLoading;
    private View mControllers, mContainer;
    private ImageView mCoverArt;
    private int mLocation;
    private int mPlaybackState = PLAYBACK_STATE_PLAYING;
    private MediaInfo mSelectedMedia;
    private boolean mControllersVisible;
    private int mDuration;
    private MiniController mMini;
    private VideoCastConsumerImpl mCastConsumer;
    private ImageButton mPlayCircle;
    private ViewPager viewPager;
    private PagerAdapterViewDetail pagerAdapterViewDetail;
    private JSONObject jsonReceiveData;
    private ArrayList<Episode> episodes = new ArrayList<>();
    private ArrayList<Related> relateds = new ArrayList<>();
    private ArrayList<TimeLines> timeLines = new ArrayList<>();
    private ArrayList<Advertisement> advertisements = new ArrayList<>();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private Adv adImage, adVideo;
    private String adVideoXml, adImageXml;
    private TextView mAdvTextTime;
    private ImageButton mAdvSkipButton;
    private RelativeLayout mAdvSkipLayout;
    private LinearLayout mAdvTimeLayout;
    private Advertisement advertisementVieo, advertisementImage;
    private RelativeLayout mControllerBar;
    private MediaInfo mMediaInfo;
    private int modeViewOrListen, type;
    private ContentModel contentModel;
    private RelativeLayout mAdvImageLayout;
    private ImageButton mAdvCloseImage;
    private ImageView mAdvImage;
    private Menu menu;
    private ArrayList<TimeLine> timeLinesList = new ArrayList<>();
    private int currentEpisodeId, firstEpisodeId, endEpisodeId, karaoke, summEpisode;
    private boolean karaokeMode;
    private AlertDialog alertDialog;
    private ViewDetail jsonHome;
    private String karaokeLink, singerLink;
    private ImageView karaokeController;
    private RelativeLayout playerContainer;
    private TabLayout mTabHostHomeDetail;
    private Toolbar mToolbar;
    private RecyclerView videoEpisodeRecyclerView;
    private ListAdapter mListAdapter;
    private GestureDetector gestureDetector;
    private VideoInfoData videoInfoData;
    private ProgressBar infoLoading;
    private TextView mTitleToolbar;
    private ImageView mLogoToolbar;
    private String mTitle = "";
    private int videoListTouchPlace;
    private ArrayList<Episode> episodesTimelines = new ArrayList<>();
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private boolean isLive;
    private boolean isStartTrackingVideo;
    private boolean isTrackingPause;
    private TrackingCountDown trackingCountDown;
    private String deviceName, deviceOS;
    private int trackingID = 0;
    private int permistion;
    private String accessToken = "";
    private boolean isCheckPackage;
    private AlertDialog.Builder alertDialogBuilder;
    private int currentEpisodePosition;
    private String imageThumbLink;
    private ArrayList<PackageDetail> packageDetails;
    private ArrayList<GroupPackage> mGroupPackages;
    private int packageIdDetail;
    private boolean isTimeline;
    private boolean isUserViewing;
    private boolean isJustBuyPackage;
    private int numberUserViewing;
    private int packageContentType;
    private String deviceImei;
    private CacheDataManager mCacheDataManager;
    private boolean isStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);

        setupActionBar();
        loadViews();
        showVideoLoading();

        deviceName = DeviceUtils.getDeviceName();
        deviceOS = DeviceUtils.getPlatform();
        accessToken = CommonUtils.getAccessTokenSecu(this);
        mCacheDataManager = CacheDataManager.getInstance(this);
        mCastManager = VideoCastManager.getInstance();
        mCastManager.isAdvertisement = false;

        setupControlsCallbacks();
        setupMiniController();
        setupCastListener();

        pagerAdapterViewDetail = new PagerAdapterViewDetail(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapterViewDetail);

        Bundle b = getIntent().getExtras();
        if (null != b) {
            shouldStartPlayback = b.getBoolean(SHOULD_START);
            startPosition = b.getInt(START_POSITION, 0);
            contentId = b.getInt(Constant.DETAIL_ID_KEY, 0);
            String urlDetail = URL.DETAIL + contentId;
            new TaskLoadData(this, urlDetail).execute();
        }
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar_layout);
        mLogoToolbar = (ImageView) findViewById(R.id.logo_actionbar);
        mTitleToolbar = (TextView) findViewById(R.id.title_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Play video or show package to buy or update detail again if not login
        if (requestCode == LOCAL_PLAYER_ACTIVITY_LOGIN_REQUEST_CODE) {
            if (resultCode == OWLoginFragment.LOGIN_FRAGMENT_RESULT_CODE) {
                isCheckPackage = true;
                String urlDetail = URL.DETAIL + contentId;
                new TaskLoadData(this, urlDetail).execute();
                Intent intent = getIntent();
                intent.putExtra(OWLoginFragment.IS_LOGIN, true);
                setResult(Constant.LOGIN_MAIN_RESULT_CODE, intent);

            }
        } else if (requestCode == PACKAGE_REQUEST_CODE) {
            if (resultCode == PackageActivity.PACKAGE_RESULT_CODE) {
                isJustBuyPackage = true;
                new TaskLoadData(this, URL.DETAIL + contentId).execute();
                /*permistion = 1;
                startPlayingVideo(mMediaInfo, "");*/

            }
        }

    }

    private void setIsPromotion(ArrayList<PackageDetail> packageDetails) {
        if (packageDetails != null && packageDetails.size() == 1) {
            PackageDetail packageDetail = packageDetails.get(0);
            packageIdDetail = packageDetail.getId();
        }
    }

    @Override
    public void onDataLoaded(String jsonObject) {
        if (jsonObject != null) {
            playerContainer.setVisibility(View.VISIBLE);
            String link;
            long duration;
            Gson gson = new Gson();
            jsonHome = gson.fromJson(jsonObject.toString(), ViewDetail.class);

            if (jsonHome.getError() == Utils.OLD_REQUEST_SUCCESS) {
                permistion = jsonHome.getPermission();
                numberUserViewing = jsonHome.getNumberUserView();
                packageContentType = jsonHome.getPermissionType();

                if (isJustBuyPackage) {
                    isJustBuyPackage = false;
                    if (permistion == 1) {
                        startPlayingVideo(mMediaInfo, "");
                    } else {
                        openDiaLogBuyPackage(jsonHome);
                    }
                } else {
                    //packageDetails = jsonHome.getPackages();
                    mGroupPackages = jsonHome.getPackages();
                    //setIsPromotion(packageDetails);

                    ContentModel contentModel = jsonHome.getContent();
                    link = contentModel.getLink();
                    duration = contentModel.getDuration();
                    imageThumbLink = contentModel.getImage();

                    mTitle = contentModel.getName();
                    isLive = contentModel.getIsLive();

                    setUpVideoInfoData(mTitle, contentModel.getDuration(), contentModel.getYear(),
                            contentModel.getDescription(), jsonHome.getGenres(), jsonHome.getCountries(),
                            jsonHome.getDirectors(), jsonHome.getActors(), jsonHome.getTags());
                    MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                    movieMetadata.putString(MediaMetadata.KEY_TITLE, mTitle);
                    movieMetadata.addImage(new WebImage(Uri.parse(imageThumbLink)));
                    try {
                        mSelectedMedia = new MediaInfo.Builder(link).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                .setContentType("application/vnd.apple.mpegurl")
                                .setMetadata(movieMetadata)
                                .setCustomData(new JSONObject(jsonObject))
                                .setStreamDuration(duration)
                                .build();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mMediaInfo = mSelectedMedia;

                    mVideoView.setVideoURI(Uri.parse(mMediaInfo.getContentId()));

                    if (shouldStartPlayback) {
                        mPlaybackState = PLAYBACK_STATE_PLAYING;
                        updatePlaybackLocation(PLAYBACK_LOCAL);
                        updatePlayButton(mPlaybackState);
                        if (startPosition > 0) {
                            mVideoView.seekTo(startPosition);
                        }

                        startVideoView(contentModel.getId());
                        startControllersTimer();
                    } else {
                        if (mCastManager.isConnected()) {
                            updatePlaybackLocation(PLAYBACK_REMOTE);
                            mPlaybackState = PLAYBACK_STATE_PAUSED;
                        } else {
                            updatePlaybackLocation(PLAYBACK_LOCAL);
                            mPlaybackState = PLAYBACK_STATE_IDLE;
                        }
                    }
                    setUpJsonReceive(contentModel);
                }


            } else {
                VotingUtils.showAlertWithMessage(LocalPlayerActivity.this, jsonHome.getMessage());
            }

        } else {
            if (NetworkUtils.getConnectivityStatus(LocalPlayerActivity.this) == 0) {
                Utils.alert(this, "No internet connection. Please check your connection settings and try again.");
            }
        }

    }

    private MediaInfo updateMediaInfo(String link, String title, String thumb) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
        movieMetadata.addImage(new WebImage(Uri.parse(thumb)));
        MediaInfo item = new MediaInfo.Builder(link).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("application/vnd.apple.mpegurl")
                .setMetadata(movieMetadata)
                .setCustomData(jsonReceiveData)
                .build();
        return item;
    }

    private void setUpVideoInfoData(String mTitle, long duration, String releaseYear, String description,
                                    ArrayList<InfoVideoArrayModel> genres, ArrayList<InfoVideoArrayModel> nation,
                                    ArrayList<InfoVideoArrayModel> directors,
                                    ArrayList<InfoVideoArrayModel> actors, ArrayList<InfoVideoArrayModel> tag) {
        videoInfoData = new VideoInfoData();
        videoInfoData.setVideoTitle(mTitle);
        videoInfoData.setVideoDuration(duration + "");
        videoInfoData.setVideoReleaseYear(releaseYear);
        videoInfoData.setVideoDescription(description);
        videoInfoData.setGenre(genres);
        videoInfoData.setNations(nation);
        videoInfoData.setDirectors(directors);
        videoInfoData.setActors(actors);
        videoInfoData.setTags(tag);
    }

    private void setUpJsonReceive(ContentModel contentModel) {
        //optimize code here - steve
        relateds = jsonHome.getRelated();
        episodes = jsonHome.getEpisodes();
        timeLines = jsonHome.getTimelines();
        karaoke = contentModel.getKaraoke();
        currentEpisodeId = contentModel.getId();
        type = contentModel.getType();//type = 0: sigle; 1: serries; 2: episode
        modeViewOrListen = contentModel.getMode();// 0 is listen, 1 is view;

        if (karaoke == 1) {
            karaokeMode = true;
            karaokeLink = mSelectedMedia.getContentId();
            singerLink = karaokeLink.replace("kara", "sing");
            mCastManager.karaoke = 1;
            singerMediaInfo = updateMediaInfo(singerLink, contentModel.getName(), contentModel.getImage());
        } else {
            mCastManager.karaoke = 0;
        }

        if (timeLines.size() != 0) {
            for (int i = 0; i < timeLines.size(); i++) {
                TimeLines timeLineData = timeLines.get(i);
                String dayTitle = timeLineData.getTitle();
                ArrayList<TimeLine> timeLines = timeLineData.getTimeLine();
                for (int j = 0; j < timeLines.size(); j++) {
                    TimeLine timeLine = new TimeLine();
                    timeLine = timeLines.get(j);
                    timeLine.setDayTitle(dayTitle);
                    timeLinesList.add(timeLine);
                }
            }
            infoLoading.setVisibility(View.GONE);
            pagerAdapterViewDetail.setData(videoInfoData, episodes, relateds, timeLinesList, isLive);

        } else {
            infoLoading.setVisibility(View.GONE);
            pagerAdapterViewDetail.setData(videoInfoData, episodes, relateds, timeLinesList, isLive);
        }

        viewPager.setVisibility(View.VISIBLE);
        mListAdapter = new ListAdapter(this, true);
        videoEpisodeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        videoEpisodeRecyclerView.setAdapter(mListAdapter);
        if (episodes.size() != 0) {
            summEpisode = episodes.size();
            firstEpisodeId = episodes.get(0).getEpisodeId();
            endEpisodeId = episodes.get(summEpisode - 1).getEpisodeId();
            mListAdapter.setData(episodes);
            videoListTouchPlace = Constant.EPISODE;

            updateCurrentPosition(currentEpisodeId);
        } else if (timeLines.size() != 0) {
            mListAdapter.setData(mapTimelineToEpisodeData(timeLines));

        } else if (relateds.size() != 0) {
            videoListTouchPlace = Constant.RELATED;
            mListAdapter.setData(mapRelatedToEpisodeData(relateds));

        }

        setupTabhost();

        if (modeViewOrListen == 0) {
            mLoading.setVisibility(View.GONE);
            mPlaybackState = PLAYBACK_STATE_IDLE;
            updatePlayButton(mPlaybackState);
            setCoverArtStatus(com.google.android.libraries.cast.companionlibrary.utils.Utils.
                    getImageUrl(mSelectedMedia, 0));
        } else {
            getAdv();
        }
    }

    private ArrayList<Episode> mapTimelineToEpisodeData(ArrayList<TimeLines> timeLines) {
        episodesTimelines = new ArrayList<>();
        for (int j = 0; j < timeLines.size(); j++) {
            TimeLines timeLineData = timeLines.get(j);
            ArrayList<TimeLine> timeLineArrayList = timeLineData.getTimeLine();
            String dayTitle = timeLineData.getTitle();
            videoListTouchPlace = Constant.TIMELINES;

            Episode episode;
            TimeLine timeLine;
            for (int i = 0; i < timeLineArrayList.size(); i++) {
                timeLine = timeLineArrayList.get(i);
                episode = new Episode();
                episode.setEpisodeId(timeLine.getId());
                episode.setEpisodeImage(timeLine.getImage());
                episode.setEpisodeLink(timeLine.getLink());
                episode.setEpisodeName(timeLine.getName());
                episode.setStartTime(timeLine.getStart());
                episode.setEndTime(timeLine.getEnd());
                episode.setDayTimeLine(dayTitle);
                episodesTimelines.add(episode);
            }
        }
        return episodesTimelines;
    }

    private ArrayList<Episode> mapRelatedToEpisodeData(ArrayList<Related> relateds) {
        ArrayList<Episode> episodeRelated = new ArrayList<>();

        Episode episode;
        Related related;
        for (int i = 0; i < relateds.size(); i++) {
            related = relateds.get(i);
            episode = new Episode();
            episode.setEpisodeId(related.getRelatedId());
            episode.setEpisodeImage(related.getRelatedImage());
            episode.setEpisodeLink(related.getRelatedLink());
            episode.setEpisodeName(related.getRelatedName());
            episodeRelated.add(episode);
        }
        return episodeRelated;
    }

    private void updateCurrentPosition(int currentEpisodeId) {
        for (int i = 0; i < summEpisode; i++) {
            if (currentEpisodeId == episodes.get(i).getEpisodeId()) {
                currentEpisodePosition = i;
            }
        }
    }

    private void updateCurrentEpisodeId(int currentEpisodeId) {
        this.currentEpisodeId = currentEpisodeId;
        updateCurrentPosition(currentEpisodeId);
    }

    private void setupTabhost() {
        mTabHostHomeDetail.setupWithViewPager(viewPager);
        if (episodes.size() != 0 && episodesTimelines.size() != 0) {
            mTabHostHomeDetail.setTabMode(TabLayout.MODE_FIXED);
            mTabHostHomeDetail.setTabGravity(TabLayout.GRAVITY_FILL);
        }
    }

    private void getAdv() {
        if (modeViewOrListen == 1) {
            advertisements = jsonHome.getAdvertisement();
            if (advertisements != null && advertisements.size() > 0) {
                mCastManager.isAdvertisement = true;
                for (int i = 0; i < advertisements.size(); i++) {
                    Advertisement advertisement = advertisements.get(i);
                    if (advertisement.getType().equals("video")) {
                        adVideoXml = advertisement.getLink();
                        if (adVideoXml.trim().length() == 0) {
                            mCastManager.isAdvertisement = false;
                        } else {
                            advertisementVieo = advertisement;
                            mCastManager.skippableTimeVideoAdv = advertisementVieo.getSkippableTime();
                            new LoadAdvDataTask().execute(adVideoXml);
                        }
                    } else if (advertisement.getType().equals("image")) {
                        adImageXml = advertisement.getLink();
                        if (adImageXml.trim().length() == 0) {
                            mCastManager.isAdvertisement = false;
                        } else {
                            advertisementImage = advertisement;
                            new LoadAdvDataTask().execute(adImageXml);
                        }
                    }
                }
            } else {
                if (permistion == 1) {
                    startPlayingVideo(mMediaInfo, "");
                } else {
                    updatePlayButton(mPlaybackState);
                    if (VotingUtils.checkLoginStatus(LocalPlayerActivity.this) == true) {
                        openDiaLogBuyPackage(jsonHome);

                    } else {
                        openDiaLogAskLogin(true);

                    }
                }

            }
        } else {
        }
    }

    private void setupCastListener() {
        mCastConsumer = new VideoCastConsumerImpl() {
            @Override
            public void onApplicationConnected(ApplicationMetadata appMetadata,
                                               String sessionId, boolean wasLaunched) {

                if (null != mMediaInfo) {

                    if (mPlaybackState == PLAYBACK_STATE_PLAYING) {
                        mVideoView.pause();
                        try {
                            if (mCastManager.isAdvertisement == true) {
                                stopTimerTaskLocal();
                                loadRemoteMedia(mSeekbar.getProgress(), true);
                                startTimerTask();
                            } else {
                                loadRemoteMedia(mSeekbar.getProgress(), true);
                            }

                        } catch (Exception e) {
                            Utils.handleException(LocalPlayerActivity.this, e);
                        }
                        return;
                    } else {
                        mPlaybackState = PLAYBACK_STATE_IDLE;
                        updatePlaybackLocation(PLAYBACK_REMOTE);
                    }
                }
                if (mPlaybackState != -1) {
                    updatePlayButton(mPlaybackState);
                }
                invalidateOptionsMenu();
            }


            @Override
            public void onApplicationDisconnected(int errorCode) {
                updatePlaybackLocation(PLAYBACK_LOCAL);
            }

            @Override
            public void onDisconnected() {
                mPlaybackState = PLAYBACK_STATE_IDLE;
                mLocation = PLAYBACK_LOCAL;
                updatePlayButton(mPlaybackState);
                invalidateOptionsMenu();
            }

            //Get data from here to play from minicontroller
            @Override
            public void onRemoteMediaPlayerMetadataUpdated() {
                try {
                    mRemoteMediaInformation = mCastManager.getRemoteMediaInformation();
                } catch (Exception e) {
                    // silent
                }
            }

            @Override
            public void onFailed(int resourceId, int statusCode) {

            }

            @Override
            public void onConnectionSuspended(int cause) {
                Utils.showToast(LocalPlayerActivity.this,
                        R.string.connection_temp_lost);
            }

            @Override
            public void onConnectivityRecovered() {
                Utils.showToast(LocalPlayerActivity.this,
                        R.string.connection_recovered);
            }

        };
    }

    private void setupMiniController() {
        mMini = (MiniController) findViewById(R.id.miniController1);
        mCastManager.addMiniController(mMini);
    }

    private void updatePlaybackLocation(int location) {
        mLocation = location;
        if (location == PLAYBACK_LOCAL) {
            if (mPlaybackState == PLAYBACK_STATE_PLAYING ||
                    mPlaybackState == PLAYBACK_STATE_BUFFERING) {
                setCoverArtStatus(null);
                startControllersTimer();
            } else {
                stopControllersTimer();
                setCoverArtStatus(com.google.android.libraries.cast.companionlibrary.utils.Utils.
                        getImageUrl(mSelectedMedia, 0));

            }

        } else {
            stopControllersTimer();
            if (mSelectedMedia != null) {
                setCoverArtStatus(com.google.android.libraries.cast.companionlibrary.utils.Utils.
                        getImageUrl(mSelectedMedia, 0));
            }

            updateControllersVisibility(false);
        }

    }

    private void play(int position) {
        startControllersTimer();
        switch (mLocation) {
            case PLAYBACK_LOCAL:
                mVideoView.seekTo(position);
                startVideoView(contentId);
                break;
            case PLAYBACK_REMOTE:
                mPlaybackState = PLAYBACK_STATE_BUFFERING;
                updatePlayButton(mPlaybackState);
                try {
                    mCastManager.play(position);
                } catch (Exception e) {
                    Utils.handleException(this, e);
                }
                break;
            default:
                break;
        }
        restartTrickplayTimer();
    }

    public void togglePlayback(MediaInfo mediaInfo) {
        mTitleToolbar.setText(mTitle);
        String adVideoLink = null;
        if (mCastManager.isAdvertisement == true && adVideo != null) {
            adVideoLink = adVideo.getLink_src();
        }
        mMediaInfo = mediaInfo;
        if (isStop) {
            startTracking(contentId);
        }
        stopControllersTimer();
        switch (mPlaybackState) {
            case PLAYBACK_STATE_PAUSED:
                switch (mLocation) {
                    case PLAYBACK_LOCAL:
                        if (stopPosition != 0) {
                            mVideoView.seekTo(stopPosition);
                        }
                        startVideoView(contentId);
                        isTrackingPause = false;
                        if (!mCastManager.isConnecting()) {
                            mCastManager.clearPersistedConnectionInfo(
                                    VideoCastManager.CLEAR_SESSION);
                        }
                        mPlaybackState = PLAYBACK_STATE_PLAYING;
                        startControllersTimer();
                        restartTrickplayTimer();
                        updatePlaybackLocation(PLAYBACK_LOCAL);
                        break;
                    case PLAYBACK_REMOTE:
                        try {
                            mCastManager.checkConnectivity();
                            if (adVideoLink != null) {
                                mCastManager.isAdvertisement = true;
                                mMediaInfo = updateMediaInfo(adVideoLink, " ", imageThumbLink);
                                loadRemoteMedia(0, true);
                                startTimerTask();
                            } else {
                                loadRemoteMedia(0, true);
                            }
                        } catch (Exception e) {
                            Utils.handleException(LocalPlayerActivity.this, e);
                            return;
                        }
                        break;
                    default:
                        break;
                }
                break;

            case PLAYBACK_STATE_PLAYING:
                mPlaybackState = PLAYBACK_STATE_PAUSED;
                mVideoView.pause();
                isTrackingPause = true;
                break;

            case PLAYBACK_STATE_IDLE:
                isTrackingPause = false;
                switch (mLocation) {
                    case PLAYBACK_LOCAL:
                        mPlayPause.setVisibility(View.INVISIBLE);
                        mStartText.setVisibility(View.INVISIBLE);
                        mEndText.setVisibility(View.INVISIBLE);
                        mSeekbar.setVisibility(View.INVISIBLE);
                        mFullScreen.setVisibility(View.INVISIBLE);

                        mAdvSkipLayout.setVisibility(View.INVISIBLE);
                        if (adVideoLink != null) {
                            mCastManager.isAdvertisement = true;
                            mMediaInfo = updateMediaInfo(adVideoLink, " ", imageThumbLink);
                            mAdvTextTime.setText(ADV_TIMER_RUNNING + adVideo.getDuration() + "s");
                            mVideoView.setVideoURI(Uri.parse(adVideoLink));
                            mVideoView.seekTo(0);
                            startVideoView(contentId);
                            mPlaybackState = PLAYBACK_STATE_PLAYING;
                            restartTrickplayTimer();
                        } else {
                            playVideo(contentId, mediaInfo.getContentId(), mTitle, false);

                        }
                        updatePlaybackLocation(PLAYBACK_LOCAL);
                        break;
                    case PLAYBACK_REMOTE:

                        try {
                            mCastManager.checkConnectivity();
                            if (adVideoLink != null) {
                                mCastManager.isAdvertisement = true;
                                mMediaInfo = updateMediaInfo(adVideoLink, " ", imageThumbLink);
                                loadRemoteMedia(0, true);
                                startTimerTask();
                            } else {
                                loadRemoteMedia(0, true);
                            }

                        } catch (Exception e) {
                            Utils.handleException(LocalPlayerActivity.this, e);
                            return;
                        }
                        break;
                }
            default:
                break;
        }
        updatePlayButton(mPlaybackState);
    }

    public void toggPlaybackPrepare(MediaInfo mediaInfo, ViewDetail jsonHome, ContentModel contentModel) {
        if (contentModel != null) {
            updateID(contentModel.getId());
        }
        timeLinesList = new ArrayList<>();
        //setIsPromotion(jsonHome.getPackages());
        isLive = contentModel.getIsLive();

        setUpVideoInfoData(contentModel.getName(), contentModel.getDuration(), contentModel.getYear(), contentModel.getDescription(),
                jsonHome.getGenres(), jsonHome.getCountries(), jsonHome.getDirectors(), jsonHome.getActors(), jsonHome.getTags());

        if (jsonHome.getTimelines() != null && jsonHome.getTimelines().size() > 0) {
            ArrayList<TimeLines> timeLinesData = jsonHome.getTimelines();
            for (int i = 0; i < timeLinesData.size(); i++) {
                TimeLines timeLineData = timeLinesData.get(i);
                String dayTitle = timeLineData.getTitle();
                ArrayList<TimeLine> timeLines = timeLineData.getTimeLine();
                for (int j = 0; j < timeLines.size(); j++) {
                    TimeLine timeLine = new TimeLine();
                    timeLine = timeLines.get(j);
                    timeLine.setDayTitle(dayTitle);
                    timeLinesList.add(timeLine);
                }
            }
            mListAdapter.setData(mapTimelineToEpisodeData(timeLinesData));
            infoLoading.setVisibility(View.GONE);
            pagerAdapterViewDetail.setData(videoInfoData, jsonHome.getEpisodes(), jsonHome.getRelated(), timeLinesList, isLive);

        } else {
            infoLoading.setVisibility(View.GONE);
            ArrayList<Episode> episodes = jsonHome.getEpisodes();
            ArrayList<Related> relateds = jsonHome.getRelated();
            pagerAdapterViewDetail.setData(videoInfoData, episodes, relateds, timeLinesList, isLive);
            if ((episodes != null && episodes.size() > 0)) {
                mListAdapter.setData(episodes);
            } else if (relateds != null && relateds.size() > 0) {
                mListAdapter.setData(mapRelatedToEpisodeData(relateds));
            }
        }

        setupTabhost();

        isTrackingPause = false;
        stopTracking();
        permistion = jsonHome.getPermission();
        numberUserViewing = jsonHome.getNumberUserView();
        packageContentType = jsonHome.getPermissionType();

        jsonReceiveData = new JSONObject();
        jsonReceiveData = mediaInfo.getCustomData();
        mSelectedMedia = mediaInfo;

        if (jsonReceiveData != null) {
            modeViewOrListen = contentModel.getMode();// 0 is listen, 1 is view;
            type = contentModel.getType();//type = 0: sigle; 1: serries; 2: episode
            updateCurrentEpisodeId(contentModel.getId());
            mTitle = contentModel.getName();
            if (modeViewOrListen == 1) {
                advertisements = jsonHome.getAdvertisement();
                if (advertisements != null && advertisements.size() > 0) {
                    mCastManager.isAdvertisement = true;
                    mAdvImage.setVisibility(View.INVISIBLE);
                    mAdvCloseImage.setVisibility(View.INVISIBLE);
                    for (int i = 0; i < advertisements.size(); i++) {
                        Advertisement advertisement = advertisements.get(i);
                        if (advertisement.getType().equals("video")) {
                            adVideoXml = advertisement.getLink();
                            advertisementVieo = advertisement;
                            mCastManager.skippableTimeVideoAdv = advertisementVieo.getSkippableTime();
                            new LoadAdvDataTask().execute(adVideoXml);
                        } else if (advertisement.getType().equals("image")) {
                            adImageXml = advertisement.getLink();
                            advertisementImage = advertisement;
                            new LoadAdvDataTask().execute(adImageXml);
                        }
                    }
                }
            }

            if (mCastManager.isAdvertisement == true) {
                stopTimerTaskLocal();
            }
            boolean shouldStartPlayback = false;
            int startPosition = 0;
            mVideoView.setVideoURI(Uri.parse(mediaInfo.getContentId()));
            if (shouldStartPlayback) {
                mPlaybackState = PLAYBACK_STATE_PLAYING;
                updatePlaybackLocation(PLAYBACK_LOCAL);
                updatePlayButton(mPlaybackState);
                if (startPosition > 0) {
                    mVideoView.seekTo(startPosition);
                }
                startVideoView(contentId);
                startControllersTimer();
            } else {
                mPlaybackState = PLAYBACK_STATE_IDLE;
                if (mCastManager.isConnected()) {
                    updatePlaybackLocation(PLAYBACK_REMOTE);
                } else {
                    updatePlaybackLocation(PLAYBACK_LOCAL);
                }

                updatePlayButton(mPlaybackState);
                updateMetadata(true);
            }
        }
        if (permistion == 1) {
            togglePlayback(mediaInfo);
        } else {
            if (VotingUtils.checkLoginStatus(LocalPlayerActivity.this) == true) {
                openDiaLogBuyPackage(jsonHome);

            } else {
                openDiaLogAskLogin(true);

            }
        }
    }

    private void startPlayingVideo(MediaInfo mediaInfo, String adVideoLink) {
        isTrackingPause = false;
        switch (mLocation) {
            case PLAYBACK_LOCAL:
                mPlayPause.setVisibility(View.INVISIBLE);
                mStartText.setVisibility(View.INVISIBLE);
                mEndText.setVisibility(View.INVISIBLE);
                mSeekbar.setVisibility(View.INVISIBLE);
                mFullScreen.setVisibility(View.INVISIBLE);

                mAdvSkipLayout.setVisibility(View.INVISIBLE);
                if (adVideoLink != null && adVideoLink.length() > 0) {
                    mCastManager.isAdvertisement = true;
                    mMediaInfo = updateMediaInfo(adVideoLink, " ", imageThumbLink);
                    mAdvTextTime.setText(ADV_TIMER_RUNNING + adVideo.getDuration() + "s");
                    mVideoView.setVideoURI(Uri.parse(adVideoLink));
                    mVideoView.seekTo(0);
                    startVideoView(contentId);
                    mPlaybackState = PLAYBACK_STATE_PLAYING;
                    restartTrickplayTimer();
                } else {
                    playVideo(contentId, mediaInfo.getContentId(), mTitle, false);

                }
                updatePlaybackLocation(PLAYBACK_LOCAL);
                break;
            case PLAYBACK_REMOTE:
                try {
                    mCastManager.checkConnectivity();
                    if (adVideoLink != null & adVideoLink.length() > 0) {
                        mCastManager.isAdvertisement = true;
                        mMediaInfo = updateMediaInfo(adVideoLink, " ", imageThumbLink);
                        loadRemoteMedia(0, true);
                        startTimerTask();
                    } else {
                        loadRemoteMedia(0, true);
                    }

                } catch (Exception e) {
                    Utils.handleException(LocalPlayerActivity.this, e);
                    return;
                }
                break;
        }
        updatePlayButton(mPlaybackState);
    }

    private void startTimerTask() {
        stopTimerTask();
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 5000, 1000);
    }

    private void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    private void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Long tempAdvTimeRemaining = null;
                        try {
                            if (mCastManager.isAdvertisement) {
                                if (mCastManager.getCurrentMediaPosition() / 1000 != 0) {
                                    //Log.e("TimerTask", "Remain at LocalPlayerActivity: " + mCastManager.getMediaTimeRemaining() / 1000);
                                    if (mCastManager.getMediaTimeRemaining() / 1000 == 1) {
                                        //Log.e("TimerTask", "Finished Player " + mCastManager.getMediaTimeRemaining() / 1000);
                                        stopTimerTask();
                                        mCastManager.isAdvertisement = false;
                                        mCastManager.loadMedia(mSelectedMedia, true, 0, null);
                                    }
                                }
                            } else {
                                stopTimerTask();
                                //Log.e("TimerTask", "No adv LocalPlayerActivity: ");
                            }


                        } catch (TransientNetworkDisconnectionException e) {
                            e.printStackTrace();
                        } catch (NoConnectionException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
    }

    private void startTimerPing() {
        stopTimerPing();
        timerPing = new Timer();
        initializeTimerPing();
        timerPing.schedule(timerTaskPing, 30000, 30000);


    }

    private void stopTimerPing() {
        mCastManager.isPing = false;
        if (timerPing != null) {
            timerPing.cancel();
            timerPing = null;
            isStop = true;
        }
    }

    private void initializeTimerPing() {
        timerTaskPing = new TimerTask() {
            @Override
            public void run() {
                handlerTimerPing.post(new Runnable() {
                    @Override
                    public void run() {
                        mCastManager.isPing = true;
                        Log.e(TAG,"Ping server");
                        new TaskRequestEndUserView(LocalPlayerActivity.this, URL.PING_SERVER).execute();
                    }
                });
            }
        };
    }

    private void startTimerTaskLocal() {
        stopTimerTaskLocal();
        timerLocal = new Timer();
        initializeTimerTaskLocal();
        timerLocal.schedule(timerTaskLocal, 1000, 1000);
    }

    private void stopTimerTaskLocal() {
        if (timerLocal != null) {
            timerLocal.cancel();
            timerLocal = null;
        }
    }

    private void initializeTimerTaskLocal() {

        timerTaskLocal = new TimerTask() {
            public void run() {
                handlerLocal.post(new Runnable() {
                    @Override
                    public void run() {
                        int tempTimerCurrent = mVideoView.getCurrentPosition() / 1000;
                        if (mCastManager.isAdvertisement == true) {
                            if (adVideo != null) {
                                long tempTimerRunning = (adVideo.getDuration() - tempTimerCurrent);
                                mAdvTextTime.setText(ADV_TIMER_RUNNING + tempTimerRunning + "s");
                                //Log.e("TimerLocal Position ", tempTimerRunning + "");
                                if (tempTimerCurrent >= advertisementVieo.getSkippableTime()) {
                                    mAdvSkipLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                mCastManager.isAdvertisement = false;
                            }

                        } else {
                        }
                    }
                });
            }
        };
    }

    private void startTimerImageAdv() {
        stopTimerImageAdv();
        timerImageAdv = new Timer();
        initializeTimerImageAdv();
        timerImageAdv.schedule(timerImageAdvTask, 1000, 1000);
    }

    private void stopTimerImageAdv() {
        if (timerImageAdv != null) {
            timerImageAdv.cancel();
            timerImageAdv = null;
        }
    }

    private void initializeTimerImageAdv() {

        timerImageAdvTask = new TimerTask() {
            public void run() {
                handlerImageAdv.post(new Runnable() {
                    @Override
                    public void run() {
                        int tempTimerCurrent = mVideoView.getCurrentPosition() / 1000;

                        if (advertisementImage != null) {
                            int startTimeImageAdv = advertisementImage.getStartTime();
                            if (tempTimerCurrent == startTimeImageAdv) {
                                imageLoader.getInstance().displayImage(adImage.getLink_src(), mAdvImage, options, animateFirstListener);
                                mAdvImage.setVisibility(View.VISIBLE);
                                mCastManager.isAdvImage = true;
                            } else if (tempTimerCurrent >= advertisementImage.getSkippableTime() + startTimeImageAdv) {
                                if (mAdvImage.getVisibility() == View.VISIBLE) {
                                    mAdvCloseImage.setVisibility(View.VISIBLE);
                                }
                                mCastManager.isAdvImage = false;
                                stopTimerImageAdv();
                            }
                        }

                    }
                });
            }
        };
    }

    public void playVideo(int id, String url, String title, boolean isTimeline) {
        this.isTimeline = isTimeline;
        if (permistion == 1) {
            updateID(id);
            isTrackingPause = false;
            mAdvTimeLayout.setVisibility(View.GONE);
            mAdvSkipLayout.setVisibility(View.GONE);
            mPlayPause.setVisibility(View.VISIBLE);
            mStartText.setVisibility(View.VISIBLE);
            mEndText.setVisibility(View.VISIBLE);
            mSeekbar.setVisibility(View.VISIBLE);
            mFullScreen.setVisibility(View.VISIBLE);
            mVideoView.setVideoURI(Uri.parse(url));
            mVideoView.seekTo(0);
            startVideoView(contentId);
            mTitleToolbar.setText(title);
            mTitle = title;
            mPlaybackState = PLAYBACK_STATE_PLAYING;
            restartTrickplayTimer();
            mCastManager.isAdvertisement = false;
            updatePlaybackLocation(PLAYBACK_LOCAL);
            updatePlayButton(mPlaybackState);
        } else {
            if (VotingUtils.checkLoginStatus(LocalPlayerActivity.this) == true) {
                openDiaLogBuyPackage(jsonHome);
            } else {
                openDiaLogAskLogin(true);
            }
        }

    }
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
    private void loadRemoteMedia(int position, boolean autoPlay) {
        mCastManager.startVideoCastControllerActivity(this, mMediaInfo, mSelectedMedia, singerMediaInfo, position, autoPlay);
        intent = new Intent(this, PingServerService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        if(mServiceBound){
            mBoundService.servicePingserver();
        }
    }

    private void setCoverArtStatus(String url) {
        if (null != url) {
            imageLoader.getInstance().displayImage(url, mCoverArt, options, animateFirstListener);
            mCoverArt.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.INVISIBLE);
        } else {
            mCoverArt.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
        }
    }

    private void stopTrickplayTimer() {
        if (null != mSeekbarTimer) {
            mSeekbarTimer.cancel();
        }
    }

    private void restartTrickplayTimer() {
        stopTrickplayTimer();
        mSeekbarTimer = new Timer();
        mSeekbarTimer.scheduleAtFixedRate(new UpdateSeekbarTask(), 100, 1000);
    }

    private void stopControllersTimer() {
        if (null != mControllersTimer) {
            mControllersTimer.cancel();
        }
    }

    private void startControllersTimer() {
        if (null != mControllersTimer) {
            mControllersTimer.cancel();
        }
        if (mLocation == PLAYBACK_REMOTE) {
            return;
        }
        mControllersTimer = new Timer();
        mControllersTimer.schedule(new HideControllersTask(), 5000);
    }

    private void updateControllersVisibility(boolean show) {
        if (show) {
            getSupportActionBar().show();
            mControllerBar.setVisibility(View.VISIBLE);
        } else {
            getSupportActionBar().hide();
            mControllerBar.setVisibility(View.INVISIBLE);


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mLocation == PLAYBACK_LOCAL) {
            if (!isLive || mCastManager.isAdvertisement) {
                stopPosition = mVideoView.getCurrentPosition();
                //mVideoView.stopPlayback();
                persistStopPosition(stopPosition);
            }
            if (null != mSeekbarTimer) {
                mSeekbarTimer.cancel();
                mSeekbarTimer = null;
            }
            if (null != mControllersTimer) {
                mControllersTimer.cancel();
            }
            mVideoView.pause();
            mPlaybackState = PLAYBACK_STATE_PAUSED;
            updatePlayButton(mPlaybackState);


        }
        mCastManager.removeVideoCastConsumer(mCastConsumer);
        mMini.removeOnMiniControllerChangedListener(mCastManager);
        mCastManager.decrementUiCounter();

        stopTimerTask();
        stopTimerTaskLocal();
        stopTimerImageAdv();

        stopTracking();
        stopTimerPing();
        if(!mCastManager.isConnected()){
            stopUserViewing();
        }

        videoEpisodeRecyclerView.removeOnItemTouchListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (null != mCastManager) {
            mMini.removeOnMiniControllerChangedListener(mCastManager);
            mCastManager.removeMiniController(mMini);
            mCastConsumer = null;
        }
        stopTracking();
        stopControllersTimer();
        stopTrickplayTimer();
        stopTimerImageAdv();

        if (mAdvSkipButton != null) {
            mAdvSkipButton.setOnClickListener(null);
        }

        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        stopUserViewing();
        stopTimerPing();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        this.onPrepareOptionsMenu(menu);
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        mCastManager = VideoCastManager.getInstance();
        mCastManager.addVideoCastConsumer(mCastConsumer);
        mMini.setOnMiniControllerChangedListener(mCastManager);
        mCastManager.incrementUiCounter();
        videoEpisodeRecyclerView.addOnItemTouchListener(this);

        if (!mCastManager.isConnected() && !mVideoView.isPlaying()) {
            if (!isLive || mCastManager.isAdvertisement) {
                SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                stopPosition = preferences.getInt(STOP_POSITION, -1);
                if (stopPosition != -1) {
                    mVideoView.seekTo(stopPosition);
                    mStartText.setText(com.google.android.libraries.cast.companionlibrary.utils.Utils
                            .formatMillis(stopPosition));
                }
            }
        }

        if (mCastManager.isConnected()) {
            updatePlaybackLocation(PLAYBACK_REMOTE);
        } else {
            updatePlaybackLocation(PLAYBACK_LOCAL);
        }

        if (!mToolbar.isShown()) {
            mToolbar.setVisibility(View.VISIBLE);
        }

        if (mCastManager.isAdvImage == true) {
            startTimerImageAdv();
        }
        super.onResume();
    }

    private void setupControlsCallbacks() {
        mVideoView.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String msg;
                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                    msg = getString(R.string.video_error_media_load_timeout);
                } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    msg = getString(R.string.video_error_server_unaccessible);
                } else {
                    msg = getString(R.string.video_error_unknown_error);
                }
                Utils.showErrorDialog(LocalPlayerActivity.this, msg);
                mVideoView.stopPlayback();
                mPlaybackState = PLAYBACK_STATE_IDLE;
                updatePlayButton(mPlaybackState);
                return true;
            }
        });

        mVideoView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mCastManager.isAdvertisement == true) {
                    mAdvTimeLayout.setVisibility(View.VISIBLE);
                    mStartText.setVisibility(View.INVISIBLE);
                    mEndText.setVisibility(View.INVISIBLE);
                    mSeekbar.setVisibility(View.INVISIBLE);
                    mFullScreen.setVisibility(View.INVISIBLE);

                    startTimerTaskLocal();
                } else {
                    if (mCastManager.isAdvImage == true) {
                        if (timerImageAdv == null) {
                            startTimerImageAdv();
                        }

                    }
                }
                mLoading.setVisibility(View.INVISIBLE);
                mDuration = mp.getDuration();
                mEndText.setText(com.google.android.libraries.cast.companionlibrary.utils.Utils
                        .formatMillis(mDuration));
                mSeekbar.setMax(mDuration);
                restartTrickplayTimer();
            }
        });
        mVideoView.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                stopTimerTaskLocal();
                stopTrickplayTimer();

                mPlaybackState = PLAYBACK_STATE_IDLE;
                if (mCastManager.isAdvertisement == true) {
                    mCastManager.isAdvertisement = false;
                    mMediaInfo = mSelectedMedia;
                    mAdvTimeLayout.setVisibility(View.GONE);
                    mAdvSkipLayout.setVisibility(View.GONE);
                    mStartText.setVisibility(View.VISIBLE);
                    mEndText.setVisibility(View.VISIBLE);
                    mSeekbar.setVisibility(View.VISIBLE);
                    mVideoView.setVideoURI(Uri.parse(mMediaInfo.getContentId()));
                    if (karaoke == 1) {
                        karaokeController.setVisibility(View.VISIBLE);
                    }
                    mVideoView.seekTo(0);
                    startVideoView(contentId);
                    mPlaybackState = PLAYBACK_STATE_PLAYING;
                    if (adImage != null) {
                        startTimerImageAdv();
                    }
                    restartTrickplayTimer();
                    updatePlaybackLocation(PLAYBACK_LOCAL);
                    updatePlayButton(mPlaybackState);
                } else {
                    if (type == 1 || type == 2) {
                        if (currentEpisodePosition < episodes.size() - 1) {
                            currentEpisodePosition = currentEpisodePosition + 1;
                            currentEpisodeId = episodes.get(currentEpisodePosition).getEpisodeId();
                            openDiaLogNextEpisode();
                        }


                    }
                    updatePlayButton(mPlaybackState);
                }


            }
        });

        mVideoView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCastManager.isAdvertisement == true) {
                    if (adVideo != null) {
                        if (adVideo.getLinkClick() != null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adVideo.getLinkClick()));
                            startActivity(browserIntent);
                        }
                    }

                }

                if (!mControllersVisible) {
                    updateControllersVisibility(true);
                    if (videoEpisodeRecyclerView.getVisibility() == View.VISIBLE) {
                        videoEpisodeRecyclerView.setVisibility(View.GONE);
                    }
                } else {
                    updateControllersVisibility(false);
                }
                startControllersTimer();
                return false;
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPlaybackState == PLAYBACK_STATE_PLAYING) {
                    play(seekBar.getProgress());

                } else if (mPlaybackState != PLAYBACK_STATE_IDLE) {
                    mVideoView.seekTo(seekBar.getProgress());
                }
                startControllersTimer();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopTrickplayTimer();
                mVideoView.pause();
                stopControllersTimer();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mStartText.setText(com.google.android.libraries.cast.companionlibrary.utils.Utils
                        .formatMillis(progress));
            }
        });

        mPlayPause.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLocation == PLAYBACK_LOCAL) {
                    togglePlayback(mMediaInfo);
                    if (mVideoView.isPlaying()) {
                        mLoading.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
        mFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    mFullScreen.setImageResource(R.drawable.ic_action_return_from_full_screen);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                    updateMetadata(false);
                    mContainer.setBackgroundColor(getResources().getColor(R.color.black));

                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mFullScreen.setImageResource(R.drawable.ic_action_full_screen);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                    updateMetadata(true);
                    mContainer.setBackgroundColor(getResources().getColor(R.color.white));
                }


            }
        });
        mPlayCircle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modeViewOrListen == 1) {
                    if (permistion == 1) {
                        togglePlayback(mMediaInfo);
                        if (mVideoView.isPlaying()) {
                            mLoading.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (VotingUtils.checkLoginStatus(LocalPlayerActivity.this) == true) {
                            openDiaLogBuyPackage(jsonHome);
                        } else {
                            openDiaLogAskLogin(true);
                        }
                    }

                } else if (modeViewOrListen == 0) {
                    String urlCoverImage = com.google.android.libraries.cast.companionlibrary.utils.Utils.
                            getImageUrl(mSelectedMedia, 0);
                    mAdvSkipLayout.setVisibility(View.INVISIBLE);
                    mAdvTimeLayout.setVisibility(View.INVISIBLE);
                    mControllerBar.setVisibility(View.INVISIBLE);
                    Intent mpdIntent = new Intent(LocalPlayerActivity.this, PlayerActivity.class)
                            .setData(Uri.parse(mSelectedMedia.getContentId()))
                            .putExtra("COVERIMAGE", urlCoverImage)
                            .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, PlayerActivity.TYPE_HLS);
                    startActivity(mpdIntent);
                }
            }
        });
        karaokeController.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (karaokeMode == true) {
                    mMediaInfo = updateMediaInfo(singerLink, contentModel.getName(), contentModel.getImage());
                    karaokeMode = false;
                    int nowPosition = mSeekbar.getProgress();

                    mVideoView.setVideoURI(Uri.parse(singerLink));

                    mVideoView.seekTo(nowPosition);
                    startVideoView(contentId);
                    mPlaybackState = PLAYBACK_STATE_PLAYING;
                    if (adImage != null) {
                        startTimerImageAdv();
                    }
                    restartTrickplayTimer();
                    updatePlaybackLocation(PLAYBACK_LOCAL);
                    updatePlayButton(mPlaybackState);
                    karaokeController.setImageDrawable(getResources().getDrawable(R.drawable.karaoke_white));
                } else {
                    mMediaInfo = updateMediaInfo(karaokeLink, contentModel.getName(), contentModel.getImage());
                    karaokeMode = true;
                    int nowPosition = mSeekbar.getProgress();

                    mVideoView.setVideoURI(Uri.parse(karaokeLink));

                    mVideoView.seekTo(nowPosition);
                    startVideoView(contentId);
                    mPlaybackState = PLAYBACK_STATE_PLAYING;
                    if (adImage != null) {
                        startTimerImageAdv();
                    }
                    restartTrickplayTimer();
                    updatePlaybackLocation(PLAYBACK_LOCAL);
                    updatePlayButton(mPlaybackState);
                    karaokeController.setImageDrawable(getResources().getDrawable(R.drawable.karaoke_blue));
                }
            }
        });
        mAdvSkipButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimerTaskLocal();
                mCastManager.isAdvertisement = false;
                mMediaInfo = mSelectedMedia;
                playVideo(contentId, mMediaInfo.getContentId(), mTitle, false);
                if (adImage != null) {
                    startTimerImageAdv();
                }
                if (karaoke == 1) {
                    karaokeController.setVisibility(View.VISIBLE);
                }
            }
        });

        mAdvCloseImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdvImage.setVisibility(View.INVISIBLE);
                mAdvCloseImage.setVisibility(View.INVISIBLE);
                mCastManager.isAdvImage = false;
                stopTimerImageAdv();
            }
        });
        mAdvImage.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adImage.getLinkClick()));
                startActivity(browserIntent);
                return false;
            }
        });
    }

    public void openDiaLogAskLogin(boolean isSignInFromPackage) {

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.msg_signin_before_vote));

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(LocalPlayerActivity.this, LoginActivity.class);
                intent.putExtra(PackageActivity.IS_SIGN_IN_FROM_PACKAGE, true);
                startActivityForResult(intent, LOCAL_PLAYER_ACTIVITY_LOGIN_REQUEST_CODE);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void openDiaLogBuyPackage(ViewDetail jsonHome) {
        //packageDetails = jsonHome.getPackages();
        packageDetails = new ArrayList<>();
        mGroupPackages = jsonHome.getPackages();
        if (mGroupPackages != null) {
            for (int i = 0; i < mGroupPackages.size(); i++) {
                PackageDetail packageDetail = null;
                GroupPackage groupPackage = mGroupPackages.get(i);
                ArrayList<PackageDetail> tmpPackageDetail = groupPackage.getItems();
                if (tmpPackageDetail != null) {
                    for (int j = 0; j <= tmpPackageDetail.size(); j++) {
                        if (j == 0) {
                            packageDetail = new PackageDetail(groupPackage.getGroupName(), groupPackage.getMaxViewer(), true, groupPackage.getDescription(), groupPackage.getImage());
                        } else {
                            packageDetail = tmpPackageDetail.get(j - 1);
                        }
                        packageDetails.add(packageDetail);
                    }
                }
            }
        }

        alertDialogBuilder = new AlertDialog.Builder(this);
        boolean isShowBuyPackage = false;


        for (int i = 0; i < packageDetails.size(); i++) {
            PackageDetail packageDetail = packageDetails.get(i);
            if (packageDetail.getIsBuy() == false) {
                isShowBuyPackage = true;
            }
        }


        if (numberUserViewing > 0 && packageContentType == 1 && isShowBuyPackage == false) {
            alertDialogBuilder.setMessage(getString(R.string.msg_package_limit_device));
            alertDialogBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            alertDialogBuilder.setMessage(getString(R.string.msg_buy_package));
            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent(LocalPlayerActivity.this, PackageActivity.class);
                    intent.putParcelableArrayListExtra(PackageActivity.PACKAGE_DATA, packageDetails);
                    intent.putExtra(PackageActivity.IS_SIGN_IN_FROM_PACKAGE, true);
                    startActivityForResult(intent, PACKAGE_REQUEST_CODE);
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void openDiaLogNextEpisode() {
        final CountDownTimer autoPlayVideoTimer =
                new AutoPlayEpisodeCountDownTimer(10000, 1000);
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Starting next episode in 10 seconds. Press YES to start now.");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                playNextEpisode();
                autoPlayVideoTimer.cancel();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                autoPlayVideoTimer.cancel();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        autoPlayVideoTimer.start();
    }

    private void startVideoView(int contentId) {
        mVideoView.start();
        mLoading.setVisibility(View.INVISIBLE);
        if (!mCastManager.isAdvertisement && !isStartTrackingVideo && !isTrackingPause) {
            startTracking(contentId);
        }
    }

    public void playNextEpisode() {
        String url = URL.DETAIL + currentEpisodeId;
        new TaskLoadDataNextEpisode(this, url).execute();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mCastManager.onDispatchVolumeKeyEvent(event, MyApplication.VOLUME_INCREMENT)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void updateSeekbar(int position, int duration) {
        mSeekbar.setProgress(position);
        mSeekbar.setMax(duration);
        mStartText.setText(com.google.android.libraries.cast.companionlibrary.utils.Utils
                .formatMillis(position));
        mEndText.setText(com.google.android.libraries.cast.companionlibrary.utils.Utils
                .formatMillis(duration));
    }

    private void updatePlayButton(int state) {
        boolean isConnected = mCastManager.isConnected() || mCastManager.isConnecting();
        //mPlayCircle.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        switch (state) {
            case PLAYBACK_STATE_PLAYING:
                mPlayPause.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_av_pause_dark));
                showVideoLoading();
                mPlayCircle.setVisibility(isConnected ? View.VISIBLE : View.GONE);
                mFullScreen.setVisibility(View.VISIBLE);
                break;
            case PLAYBACK_STATE_IDLE:
                mPlayCircle.setVisibility(View.VISIBLE);
                mCoverArt.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.INVISIBLE);
                setCoverArtStatus(com.google.android.libraries.cast.companionlibrary.utils.Utils.
                        getImageUrl(mSelectedMedia, 0));
                break;
            case PLAYBACK_STATE_PAUSED:
                mLoading.setVisibility(View.INVISIBLE);
                mPlayPause.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_av_play_dark));
                //mPlayCircle.setVisibility(isConnected ? View.GONE : View.VISIBLE);
                mPlayCircle.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.VISIBLE);
                break;
            case PLAYBACK_STATE_BUFFERING:
                mPlayPause.setVisibility(View.INVISIBLE);
                showVideoLoading();
                mFullScreen.setVisibility(View.INVISIBLE);
                karaokeController.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getSupportActionBar().show();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
            updateMetadata(false);
            mContainer.setBackgroundColor(getResources().getColor(R.color.black));
            invalidateOptionsMenu();
            updateControllersVisibility(false);

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            updateMetadata(true);
            mContainer.setBackgroundColor(getResources().getColor(R.color.white));
            videoEpisodeRecyclerView.setVisibility(View.GONE);
            invalidateOptionsMenu();
            updateControllersVisibility(true);
        }
    }

    private void updateMetadata(boolean visible) {
        Point displaySize;
        if (!visible) {
            displaySize = Utils.getDisplaySize(this);//1794,1080
            RelativeLayout.LayoutParams lp = new
                    RelativeLayout.LayoutParams(displaySize.x, displaySize.y + getSupportActionBar().getHeight());
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mVideoView.setLayoutParams(lp);
            mVideoView.invalidate();
            mLogoToolbar.setVisibility(View.GONE);
            mTitleToolbar.setVisibility(View.VISIBLE);
            mTitleToolbar.setText(mTitle);
        } else {
            displaySize = Utils.getDisplaySize(this);
            RelativeLayout.LayoutParams lp = new
                    RelativeLayout.LayoutParams(displaySize.x,
                    (int) (displaySize.x * mAspectRatio));
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            mVideoView.setLayoutParams(lp);
            mVideoView.invalidate();
            mLogoToolbar.setVisibility(View.VISIBLE);
            mTitleToolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_player, menu);
        mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            menu.findItem(R.id.action_show_queue).setVisible(mCastManager.isConnected());
            if (Utils.isOrientationPortrait(this)) {
                menu.findItem(R.id.action_show_episode).setVisible(false);
            } else {
                menu.findItem(R.id.action_show_episode).setVisible(true);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_queue:
                if (mMini.isVisible()) {
                    mCastManager.updateMiniControllersVisibility(false);
                } else {
                    mCastManager.updateMiniControllersVisibility(true);
                }
                break;
            case android.R.id.home:
                if (getResources().getConfiguration().orientation == 2) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mFullScreen.setImageResource(R.drawable.ic_action_full_screen);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                    updateMetadata(true);
                    mContainer.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    ActivityCompat.finishAfterTransition(this);
                }
                break;
            case R.id.action_show_episode:
                if (videoEpisodeRecyclerView.getVisibility() == View.VISIBLE) {
                    videoEpisodeRecyclerView.setVisibility(View.GONE);
                    updateControllersVisibility(false);
                } else {
                    videoEpisodeRecyclerView.setVisibility(View.VISIBLE);
                    updateControllersVisibility(false);
                }
                break;
        }
        return true;
    }

    private void loadViews() {
        playerContainer = (RelativeLayout) findViewById(R.id.container);
        mVideoView = (VideoView) findViewById(R.id.videoView1);
        mStartText = (TextView) findViewById(R.id.startText);
        mEndText = (TextView) findViewById(R.id.endText);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar1);
        mPlayPause = (ImageView) findViewById(R.id.imageView2);
        mFullScreen = (ImageView) findViewById(R.id.image_full_screen);
        mLoading = (ProgressBar) findViewById(R.id.progressBar1);
        mControllerBar = (RelativeLayout) findViewById(R.id.control_bar);
        mControllers = findViewById(R.id.controllers);
        mContainer = findViewById(R.id.container);
        mCoverArt = (ImageView) findViewById(R.id.coverArtView);
        ViewCompat.setTransitionName(mCoverArt, getString(R.string.transition_image));
        mPlayCircle = (ImageButton) findViewById(R.id.play_circle);
        mAdvTextTime = (TextView) findViewById(R.id.text_adv_time_play);
        mAdvSkipButton = (ImageButton) findViewById(R.id.btn_skip_adv);
        mAdvTimeLayout = (LinearLayout) findViewById(R.id.adv_time_play_layout);
        mAdvSkipLayout = (RelativeLayout) findViewById(R.id.adv_skip_layout);
        mAdvImageLayout = (RelativeLayout) findViewById(R.id.adv_image_layout);
        mAdvImage = (ImageView) findViewById(R.id.adv_image);
        mAdvCloseImage = (ImageButton) findViewById(R.id.adv_close_image);
        viewPager = (ViewPager) findViewById(R.id.viewpager_detailview);
        karaokeController = (ImageView) findViewById(R.id.image_karaoke);
        mTabHostHomeDetail = (TabLayout) findViewById(R.id.tabhost_home_detail);
        videoEpisodeRecyclerView = (RecyclerView) findViewById(R.id.video_episode);
        infoLoading = (ProgressBar) findViewById(R.id.info_loading);
        options = ImageUtils.getOptionsImageRectangle();
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        mControllerBar.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
    }

    private void persistStopPosition(int stopPosition) {
        SharedPreferences.Editor preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE).edit();
        preferences.putInt(STOP_POSITION, stopPosition);
        preferences.commit(); // need this to ensure data is persisted.
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == 2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mFullScreen.setImageResource(R.drawable.ic_action_full_screen);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            updateMetadata(true);
            mContainer.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            super.onBackPressed();
            finish();

        }

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = videoEpisodeRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = videoEpisodeRecyclerView.getChildAdapterPosition(child);
            if (videoListTouchPlace == Constant.EPISODE) {
                onVideoEpisodeClick(position);
            } else if (videoListTouchPlace == Constant.TIMELINES) {
                onVideoTimeLinesClick(position);
            } else {
                onVideoRelatedClick(position);
            }

        }
        return false;
    }

    public void onVideoEpisodeClick(int position) {
        videoEpisodeRecyclerView.setVisibility(View.GONE);
        Episode episode = episodes.get(position);
        final String url = URL.DETAIL + episode.getEpisodeId();
        new TaskLoadDataDetail(this, url).execute();
    }

    public void onVideoTimeLinesClick(int position) {
        videoEpisodeRecyclerView.setVisibility(View.GONE);
        Episode episode = episodesTimelines.get(position);
        String link = episode.getEpisodeLink();
        String title = episode.getEpisodeName();
        mTitle = title;
        if (isLive) {
            playVideo(contentId, link, mTitle, true);
        } else {
            playTimeline(episode);
        }

    }

    public void onVideoRelatedClick(int position) {
        videoEpisodeRecyclerView.setVisibility(View.GONE);
        Related related = relateds.get(position);
        String url = URL.DETAIL + related.getRelatedId();
        new TaskLoadDataDetail(this, url).execute();
    }

    public void playTimeline(Episode episode) {
        isTimeline = true;
        if (mCastManager.isAdvertisement) {
            Toast.makeText(this, getResources().getString(R.string.msg_play_timeline_when_play_ads), Toast.LENGTH_SHORT).show();
        } else {
            if (permistion == 1) {
                int startTime = Utils.Singleton.parseTime(episode.getStartTime()) * 1000;
                mStartText.setText(com.google.android.libraries.cast.companionlibrary.utils.Utils
                        .formatMillis(startTime));
                stopTrickplayTimer();
                mVideoView.pause();
                stopControllersTimer();
                updateID(episode.getEpisodeId());
                if (mPlaybackState == PLAYBACK_STATE_PLAYING) {
                    play(startTime);

                } else if (mPlaybackState != PLAYBACK_STATE_IDLE) {
                    mVideoView.seekTo(startTime);
                }
                startControllersTimer();
            } else {
                if (VotingUtils.checkLoginStatus(LocalPlayerActivity.this) == true) {
                    openDiaLogBuyPackage(jsonHome);
                } else {
                    openDiaLogAskLogin(true);
                }
            }
        }

    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onDataDetailLoaded(String jsonObject) {
        if (jsonObject != null) {
            String jsonString = jsonObject.toString();
            Gson gson = new Gson();
            ViewDetail jsonHome = gson.fromJson(jsonString, ViewDetail.class);
            if (jsonHome.getError() == 0) {
                ContentModel contentModel = jsonHome.getContent();
                MediaInfo item;
                String link;
                long duration;
                String title;
                JSONObject jsonDataSend = null;
                link = contentModel.getLink();

                duration = contentModel.getDuration();
                title = contentModel.getName();
                mTitle = title;
                imageThumbLink = contentModel.getImage();

                try {
                    jsonDataSend = new JSONObject(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
                movieMetadata.addImage(new WebImage(Uri.parse(imageThumbLink)));
                item = new MediaInfo.Builder(link).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setContentType("application/vnd.apple.mpegurl")
                        .setMetadata(movieMetadata)
                        .setCustomData(jsonDataSend)
                        .setStreamDuration(duration * 1000).build();

                (this).toggPlaybackPrepare(item, jsonHome, contentModel);
                updateMetadata(false);
            }

        }

    }

    private void stopTracking() {
        if (isStartTrackingVideo) {
            if (trackingCountDown != null) {
                trackingCountDown.cancel();
            }
            new TaskLoadDataStopTracking(this, URL.TRACKING_END, trackingID);
        }
        isStartTrackingVideo = false;

    }

    private void startTracking(final int contentID) {
        stopTracking();
        isStartTrackingVideo = true;
        isUserViewing = true;
        new TaskLoadDataStartTracking(this, URL.TRACKING_START, contentID).execute();

        startTimerPing();
    }

    private void stopUserViewing() {
        if (isUserViewing) {
            new TaskRequestEndUserView(this, URL.MEMBER_END_VIEW).execute();
            isUserViewing = false;
        }

    }


    private void showVideoLoading() {
        if (mLoading.getVisibility() == View.GONE || mLoading.getVisibility() == View.INVISIBLE) {
            mLoading.setVisibility(View.VISIBLE);
        }
    }

    private void updateID(int id) {
        this.contentId = id;
    }

    @Override
    public void onDataLoadedNextEpisode(String jsonObject) {
        if (jsonObject != null) {
            Gson gson = new Gson();
            ViewDetail jsonHome = gson.fromJson(jsonObject.toString(), ViewDetail.class);
            ContentModel contentModel = jsonHome.getContent();
            String link = contentModel.getLink();

            long duration = contentModel.getDuration();
            String title = contentModel.getName();
            mTitle = title;
            imageThumbLink = contentModel.getImage();

            JSONObject jsonDataSend = new JSONObject();
            try {
                jsonDataSend = new JSONObject(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
            movieMetadata.addImage(new WebImage(Uri.parse(imageThumbLink)));
            MediaInfo item = new MediaInfo.Builder(link).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("application/vnd.apple.mpegurl")//"application/vnd.apple.mpegurl"
                    .setMetadata(movieMetadata)
                    .setCustomData(jsonDataSend)
                    .setStreamDuration(duration * 1000).build();

            toggPlaybackPrepare(item, jsonHome, contentModel);

            if (getResources().getConfiguration().orientation == 2) {
                updateMetadata(false);
            }
        }

    }

    @Override
    public void onDataStartTrackingLoaded(String jsonObject) {
        if (jsonObject != null) {
            Gson gson = new Gson();
            TrackingInfo trackingInfo = gson.fromJson(jsonObject.toString(), TrackingInfo.class);
            int error = trackingInfo.getError();
            if (error == Utils.OLD_REQUEST_SUCCESS) {
                trackingID = trackingInfo.getId();
                trackingCountDown = new TrackingCountDown(30000, 1000);
                trackingCountDown.start();
            } else {
            }
        }


    }

    @Override
    public void onDataStopTrackingLoaded(String jsonObject) {
        Gson gson = new Gson();
        TrackingInfo trackingInfo = gson.fromJson(jsonObject.toString(), TrackingInfo.class);
        int error = trackingInfo.getError();
        if (error == Utils.OLD_REQUEST_SUCCESS) {

        } else {
        }
    }

    @Override
    public void onEndUserDataLoaded(String jsonObject) {
        if (jsonObject != null) {
        }
    }

    /*
     * indicates whether we are doing a local or a remote playback
     */
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public class AutoPlayEpisodeCountDownTimer extends CountDownTimer {

        public AutoPlayEpisodeCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            playNextEpisode();
            alertDialog.dismiss();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            alertDialog.setMessage("Starting next episode in " + millisUntilFinished / 1000 + " seconds. Press YES to start now.");
        }
    }

    public class TrackingCountDown extends CountDownTimer {
        public TrackingCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            stopTracking();
        }
    }

    private class LoadAdvDataTask extends AsyncTask<String, Void, Adv> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Adv doInBackground(String... params) {
            String link = params[0];
            Adv adItem = VastDAO.Singleton.getAdvContent(link);
            return adItem;
        }

        @Override
        protected void onPostExecute(Adv adv) {
            super.onPostExecute(adv);
            if (adv.getType().equals("video")) {
                adVideo = adv;
                //---auto play----
                togglePlayback(mMediaInfo);
                if (!mCastManager.isConnected()) {
                    mLoading.setVisibility(View.VISIBLE);
                    mPlayCircle.setVisibility(View.INVISIBLE);
                    mPlayPause.setVisibility(View.INVISIBLE);
                    mFullScreen.setVisibility(View.INVISIBLE);
                }
                if (mVideoView.isPlaying()) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            } else if (adv.getType().equals("image")) {
                adImage = adv;
            }

        }
    }

    private class HideControllersTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateControllersVisibility(false);
                    mControllersVisible = false;
                }
            });

        }
    }

    private class UpdateSeekbarTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (mLocation == PLAYBACK_LOCAL) {
                        int currentPos = mVideoView.getCurrentPosition();
                        updateSeekbar(currentPos, mDuration);
                    }
                }
            });
        }
    }
}
