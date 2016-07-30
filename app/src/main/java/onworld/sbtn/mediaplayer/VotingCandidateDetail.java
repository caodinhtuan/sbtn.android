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
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
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
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import onworld.sbtn.MyApplication;
import onworld.sbtn.R;
import onworld.sbtn.activities.MainActivity;
import onworld.sbtn.activities.LoginActivity;
import onworld.sbtn.activities.PhotoViewerActivity;
import onworld.sbtn.adapters.ImageSlidePagerAdapter;
import onworld.sbtn.billingutils.IabHelper;
import onworld.sbtn.billingutils.IabResult;
import onworld.sbtn.billingutils.Inventory;
import onworld.sbtn.billingutils.Purchase;
import onworld.sbtn.dao.VastDAO;
import onworld.sbtn.josonmodel.Adv;
import onworld.sbtn.josonmodel.Advertisement;
import onworld.sbtn.josonmodel.CandidateData;
import onworld.sbtn.josonmodel.FileCandidate;
import onworld.sbtn.josonmodel.VotingCandidateDetailData;
import onworld.sbtn.josonmodel.VotingFile;
import onworld.sbtn.josonmodel.VotingUser;
import onworld.sbtn.service.MusicService;
import onworld.sbtn.utils.CommonUtils;
import onworld.sbtn.utils.HeaderHelper;
import onworld.sbtn.utils.NetworkUtils;
import onworld.sbtn.utils.DateTimeUtils;
import onworld.sbtn.utils.DeviceUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;
import onworld.sbtn.views.CircleBitmapDisplayer;

import static com.google.android.libraries.cast.companionlibrary.utils.Utils.formatMillis;

public class VotingCandidateDetail extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ImageSlidePagerAdapter.ChangeImageSlideListener, ImageSlidePagerAdapter.ViewPhotoListener {

    public static final String IS_VOTED = "isvoted";
    public static final int TOGGLE_SCREEN = 1;
    public static final int TOGGLE_LIST = 2;
    public static final String PHOTO_POSITION = "photoposition";
    public static final String PHOTO_ARRAY = "photoarray";
    static final int RC_REQUEST = 10001;
    private static final String TAG = "LocalPlayerActivity";
    private static final String KEY_POSITION = "KEY_POSITION";
    private final Handler mHandler = new Handler();
    private final float mAspectRatio = 72f / 128;
    private final Handler handler = new Handler();
    private final Handler handlerLocal = new Handler();
    public VideoCastManager mCastManager;
    protected MediaInfo mRemoteMediaInformation;
    String songTitle;
    int imageSlideLength;
    String inAppId = "onworld.onasia.mp3votedownload";
    //String inAppId = "android.test.purchased";
    IabHelper mHelper;
    private DownloadManager downloadManager;
    private MusicService mBoundService;
    private boolean mServiceBound = false;
    private int stopPosition = -1;
    private Timer timer, timerLocal, mSeekbarTimer, mControllersTimer;
    private TimerTask timerTask, timerTaskLocal;
    private VideoView mVideoView;
    private TextView mTitleView, mEndText, mStartText, mAdvTextTime;
    private SeekBar mSeekbar, audioSeekbar;
    private ImageView mPlayPause, mFullScreen, mCoverArt, mAdvImage, infoAvata;
    private ProgressBar mLoading;
    private View mControllers, mContainer;
    private PlaybackLocation mLocation;
    private PlaybackState mPlaybackState;
    private AudioPlaybackState audioPlaybackState;
    private MediaInfo mSelectedMedia, mMediaInfo;
    private boolean mControllersVisible;
    private int mDuration;
    private MiniController mMini;
    private VideoCastConsumerImpl mCastConsumer;
    private ImageButton mPlayCircle, mAdvCloseImage;
    private JSONObject jsonReceiveData;
    private ArrayList<Advertisement> advertisements = new ArrayList<>();
    private DisplayImageOptions options, optionsCircle;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader;
    private Adv adImage, adVideo;
    private String adVideoXml, adImageXml, android_id;
    private ImageButton mAdvSkipButton, playPauseAudio;
    private RelativeLayout mAdvSkipLayout, mAdvImageLayout;
    private LinearLayout mAdvTimeLayout;
    private Advertisement advertisementVieo, advertisementImage;
    private RelativeLayout mControllerBar;
    private int modeViewOrListen, type;
    private Menu menu;
    private AlertDialog alertDialog;
    private GoogleApiClient mGoogleApiClient;
    private int candidateId, round, infoIdValue;
    private String candidateIdString;
    private String token = "";
    private TextView infoName, infoDob, infoSex, infoId, fileTitle, titleProgram;
    private Button voteButton, downloadButton;
    private TextView startTimeAudio, endTimeAudio;
    private String startTimeAudioValue, endTimeAudioValue, infoNameValue, infoDobValue, infoSexValue, infoFileTitleValue, infoAvataValue;
    private VotingUser votingUser;
    private int isActive, isVote, isDownload, isPay, fileType, roundId, voteNum, roundStatus;
    private double price;
    private String[] listPhotos;
    private FileCandidate fileCandidates;
    private String urlMp3, urlMp3Download, urlVideo;
    private VotingFile votingFile;
    private JSONObject jsonData;
    private long myDownloadReference;
    private BroadcastReceiver receiverDownloadComplete, receiverNotificationClicked;
    private TypePlay typePlay;
    private double currentTime = 0;
    private double finalTime = 0;
    private IabHelper mIabHelper;
    private Handler myHandler = new Handler();
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;

        }
    };
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (mBoundService != null) {
                currentTime = mBoundService.getCurrentTime();
                finalTime = mBoundService.getDuaration();
                if (mServiceBound) {
                    startTimeAudio.setText(formatMillis((int) currentTime));
                    endTimeAudio.setText(formatMillis((int) finalTime));
                    audioSeekbar.setMax((int) finalTime);
                    audioSeekbar.setProgress((int) currentTime);

                }
            }
            myHandler.postDelayed(this, 100);
        }
    };
    private TextView candidateDescription;
    private String savedFilePath;
    private String fileDir;
    private ViewPager slidePhotoViewPager;
    private RelativeLayout videoContainer, photoContainer;
    private LinearLayout audioContainer;
    private int fileId;
    private ScrollView scrollView;
    private boolean showResult;
    private double totalVote;
    private float votePercent;
    private TextView totalVoteView, totalVoteViewTitle;
    private int totalVoteFromUser, remainVote;
    private int voteType;
    private TextView voteNumberFromUser;
    private String authenticate;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            if (mHelper == null) return;

            if (result.isSuccess()) {
                downloadFile(urlMp3Download);
                setButonDisable(downloadButton, "Download");
                updateDownloadStatusToServer(purchase.getOrderId());
            } else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            Purchase gasPurchase = inventory.getPurchase(inAppId);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(inAppId), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                return;
            }

        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (mHelper == null) return;

            if (result.isFailure()) {
                //complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            if (purchase.getSku().equals(inAppId)) {
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voting_candidate_detail);
        loadViews();
        setUpUIL();
        mCastManager = VideoCastManager.getInstance();
        mCastManager.isAdvertisement = false;
        setupMiniController();
        setupActionBar();
        setupControlsCallbacks();
        setupCastListener();
        Bundle bundle = getIntent().getExtras();
        candidateId = bundle.getInt(MainActivity.CANDIDATE_ID);
        roundId = bundle.getInt(MainActivity.ROUND_ID);
        candidateIdString = Integer.toString(candidateId);
        android_id = DeviceUtils.getUuid(this);
        getData();

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        audioPlaybackState = AudioPlaybackState.IDLE;
        setUpBillingInapp();


    }

    private void setUpBillingInapp() {
        String base64EncodedPublicKey = getResources().getString(R.string.sbtn_license_key);

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    //complain("Problem setting up in-app billing: " + result);
                    complain("Billing service unavailable on device.");
                    downloadButton.setVisibility(View.GONE);
                    return;
                }

                if (mHelper == null) return;
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        return true;
    }

    void complain(String message) {
        alert(message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    public void setUpUIL() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.thum_default)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        optionsCircle = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.thum_default)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(this.getResources().getColor(R.color.colorPrimary), 5))
                .build();
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
    }

    private void getData() {
        if (NetworkUtils.getConnectivityStatus(this) != NetworkUtils.TYPE_NOT_CONNECTED) {
            VotingUtils.showDialog(VotingCandidateDetail.this);
            final String candidateDetailUrl = URL.VOTING_CANDIDATE_DETAIL + candidateId + "&r_id=" + roundId;
            String date = DateTimeUtils.getCurrentUTCDateString();
            String toke = CommonUtils.getAccessTokenSecu(VotingCandidateDetail.this);
            authenticate = HeaderHelper.createAuthorizationValue("POST", DateTimeUtils.getCurrentUTCDateString(),
                    CommonUtils.getAccessTokenSecu(VotingCandidateDetail.this), 78);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    candidateDetailUrl, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    scrollView.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    CandidateData votingProgramData = gson.fromJson(response.toString(), CandidateData.class);
                    if (votingProgramData.getCode() == VotingUtils.REQUEST_SUCCESS) {
                        VotingCandidateDetailData votingCandidateDetailData = votingProgramData.getData();
                        setCandidateInfoValue(votingCandidateDetailData);
                        setupControlsCallbacks();
                    } else {
                        VotingUtils.showAlertWithMessage(VotingCandidateDetail.this, votingProgramData.getMessage());
                    }
                    VotingUtils.hideDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("kaka", "Error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", authenticate);
                    return headers;
                }
            };

            MyApplication.getInstance().addToRequestQueue(strReq);
        } else {
            Utils.alert(this, getString(R.string.msg_no_internet_connection));
        }

    }

    private void setCandidateInfoValue(VotingCandidateDetailData votingCandidateDetailData) {
        infoNameValue = votingCandidateDetailData.getFirstName() + " " + votingCandidateDetailData.getLastName();
        infoDobValue = votingCandidateDetailData.getDob();
        infoSexValue = votingCandidateDetailData.getGender();
        infoIdValue = votingCandidateDetailData.getExamineeId();
        infoAvataValue = votingCandidateDetailData.getAvatarUrl();
        showResult = votingCandidateDetailData.getShowResult();

        infoName.setText(infoNameValue);
        infoDob.setText(infoDobValue);
        if (infoSexValue.equals("1")) {
            infoSex.setText("male");
        } else if (infoSexValue.equals("2")) {
            infoSex.setText("female");
        } else {
            infoSex.setText("N/A");
        }
        infoId.setText(infoIdValue + "");
        votingFile = votingCandidateDetailData.getFile();
        voteType = votingFile.getVoteType();
        setUpVotingFile(votingFile);
        votingUser = votingCandidateDetailData.getUsers();
        setUpVotingUser(votingUser);
        if (showResult == true || roundStatus == 0) {
            totalVote = votingCandidateDetailData.getTotalVote();
            votePercent = votingCandidateDetailData.getVotePercent();
            totalVoteView.setText(VotingUtils.reformatDouble(totalVote) + "/" + VotingUtils.reformatDouble(votePercent) + "%");
            totalVoteView.setVisibility(View.VISIBLE);
            totalVoteViewTitle.setVisibility(View.VISIBLE);
        }
        mTitleView.setText(infoFileTitleValue);
        candidateDescription.setText(votingCandidateDetailData.getDescription());
        if (null != mTitleView) {
            updateMetadata(true);
        }

        imageLoader.getInstance().displayImage(infoAvataValue, infoAvata, optionsCircle, animateFirstListener);
    }

    private void setUpVotingUser(VotingUser votingUser) {
        if (roundStatus == 1) {
            if (votingUser != null) {
                isVote = votingUser.getIsVote();
                isPay = votingUser.getIsPay();
                isDownload = votingUser.getIsDownLoad();
                totalVoteFromUser = votingUser.getTotalVoteFromUser();
                if (voteType == 1 && totalVoteFromUser >= 1) {
                    voteNumberFromUser.setVisibility(View.VISIBLE);
                    voteNumberFromUser.setText(totalVoteFromUser + "");
                }
                if (totalVoteFromUser != 0) {
                    voteButton.setText("Voted");
                }
                remainVote = votingUser.getRemainVote();
                //
                if (voteType == 0 && totalVoteFromUser == 1) {
                    setButonDisable(voteButton, "Voted");
                }
                if (isPay == 0) {
                    downloadButton.setText(price + "$");
                } else if (isPay == 1) {
                    if (checkFileLocalExist(songTitle)) {
                        downloadButton.setVisibility(View.GONE);
                    } else {
                        downloadButton.setText("DownLoad");
                    }

                }
            } else {
                downloadButton.setText(price + "$");
            }
        } else {
            voteButton.setVisibility(View.INVISIBLE);
            downloadButton.setVisibility(View.GONE);
        }

    }

    private void setUpVotingFile(VotingFile votingFile) {

        if (votingFile != null) {
            voteNum = votingFile.getVoteNum();
            roundStatus = votingFile.getRoundStatus();
            //roundId = votingFile.getRoundId();
            infoFileTitleValue = votingFile.getSubject();
            //titleProgram.setText(votingFile.getRoundName());
            price = votingFile.getPrice();
            fileId = votingFile.getFileID();
            //voteType = votingFile.getVoteType();
            advertisements = votingFile.getAdv();
            if (advertisements.size() != 0) {
                getAdv();
            }
            //listFile = votingFile.getFiles();
            fileType = votingFile.getFileType();
            if (fileType == 1) {
                photoContainer.setVisibility(View.VISIBLE);
                listPhotos = votingFile.getPhotos();
                imageSlideLength = listPhotos.length;
                ImageSlidePagerAdapter imageSlidePagerAdapter = new ImageSlidePagerAdapter(VotingCandidateDetail.this, listPhotos, true);
                slidePhotoViewPager.setAdapter(imageSlidePagerAdapter);
                imageSlidePagerAdapter.setViewPhotoListener(VotingCandidateDetail.this);
                imageSlidePagerAdapter.setChangeImageSlideListener(VotingCandidateDetail.this);
            } else if (fileType == 2) {
                videoContainer.setVisibility(View.VISIBLE);
                audioContainer.setVisibility(View.VISIBLE);
                fileCandidates = votingFile.getFiles();
                if (fileCandidates != null) {
                    urlMp3 = fileCandidates.getMp3Url();
                    //urlMp3Download = "http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3";

                    urlMp3Download = fileCandidates.getMp3UrlDownload();
                    urlVideo = fileCandidates.getVideoUrl();
                    if (urlMp3Download != null) {
                        songTitle = getSongTitleFromUrl(urlMp3Download);
                    }
                    if (urlVideo != null) {
                        if (urlVideo.length() != 0) {
                            MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                            mediaMetadata.putString(MediaMetadata.KEY_TITLE, infoFileTitleValue);
                            mediaMetadata.addImage(new WebImage(Uri.parse(infoAvataValue)));

                            mSelectedMedia = new MediaInfo.Builder(urlVideo)
                                    .setContentType("application/vnd.apple.mpegurl")
                                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                    .setMetadata(mediaMetadata)
                                    .build();
                            mMediaInfo = mSelectedMedia;

                            boolean shouldStartPlayback = false;
                            int startPosition = 0;
                            //Set play video
                            modeViewOrListen = 1;
                            mVideoView.setVideoURI(Uri.parse(mMediaInfo.getContentId()));
                            if (shouldStartPlayback) {
                                // this will be the case only if we are coming from the
                                // CastControllerActivity by disconnecting from a device
                                mPlaybackState = PlaybackState.PLAYING;
                                updatePlaybackLocation(PlaybackLocation.LOCAL, mMediaInfo);

                                updatePlayButton(mPlaybackState);
                                if (startPosition > 0) {
                                    mVideoView.seekTo(startPosition);
                                }
                                mVideoView.start();
                                startControllersTimer();
                            } else {
                                // we should load the video but pause it
                                // and show the album art.
                                if (mCastManager.isConnected()) {
                                    updatePlaybackLocation(PlaybackLocation.REMOTE, mMediaInfo);
                                } else {
                                    updatePlaybackLocation(PlaybackLocation.LOCAL, mMediaInfo);
                                }
                                mPlaybackState = PlaybackState.IDLE;
                                updatePlayButton(mPlaybackState);
                            }

                        }
                    }

                }
            } else if (fileType == 3) {
                videoContainer.setVisibility(View.VISIBLE);
                fileCandidates = votingFile.getFiles();
                if (fileCandidates != null) {
                    urlVideo = fileCandidates.getVideoUrl();
                    if (urlMp3Download != null) {
                        songTitle = getSongTitleFromUrl(urlMp3Download);
                    }
                    if (urlVideo != null) {
                        if (urlVideo.length() != 0) {
                            MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                            mediaMetadata.putString(MediaMetadata.KEY_TITLE, infoFileTitleValue);
                            mediaMetadata.addImage(new WebImage(Uri.parse(infoAvataValue)));

                            mSelectedMedia = new MediaInfo.Builder(urlVideo)
                                    .setContentType("application/vnd.apple.mpegurl")
                                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                    .setMetadata(mediaMetadata)
                                    .build();
                            mMediaInfo = mSelectedMedia;

                            setupControlsCallbacks();

                            setupCastListener();

                            boolean shouldStartPlayback = false;
                            int startPosition = 0;
                            //Set play video
                            modeViewOrListen = 1;
                            mVideoView.setVideoURI(Uri.parse(mMediaInfo.getContentId()));
                            if (shouldStartPlayback) {
                                // this will be the case only if we are coming from the
                                // CastControllerActivity by disconnecting from a device
                                mPlaybackState = PlaybackState.PLAYING;
                                updatePlaybackLocation(PlaybackLocation.LOCAL, mMediaInfo);

                                updatePlayButton(mPlaybackState);
                                if (startPosition > 0) {
                                    mVideoView.seekTo(startPosition);
                                }
                                mVideoView.start();
                                startControllersTimer();
                            } else {
                                // we should load the video but pause it
                                // and show the album art.
                                if (mCastManager.isConnected()) {
                                    updatePlaybackLocation(PlaybackLocation.REMOTE, mMediaInfo);
                                } else {
                                    updatePlaybackLocation(PlaybackLocation.LOCAL, mMediaInfo);
                                }
                                mPlaybackState = PlaybackState.IDLE;
                                updatePlayButton(mPlaybackState);
                            }

                        }
                    }

                }
            }
        }
    }

    private void getAdv() {
        if (advertisements != null) {
            mCastManager.isAdvertisement = true;
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

    private void updateDownloadStatusToServer(final String orderID) {
        final String votingApi = URL.DOWNLOAD;
        jsonData = new JSONObject();
        try {
            jsonData.put("deviceID", android_id);
            jsonData.put("fileID", fileId);
            jsonData.put("typeID", "1");
            jsonData.put("transactionID", orderID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = getEntityFromString(jsonData);
        final String dateTime = DateTimeUtils.getCurrentUTCDateString();
        final String agent = MyApplication.getUserAgent();

        authenticate = HeaderHelper.createAuthorizationValue("POST", dateTime,
                CommonUtils.getAccessTokenSecu(VotingCandidateDetail.this), 78);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                votingApi, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    String message = jsonObject.getString("message");
                    if (code == VotingUtils.REQUEST_SUCCESS) {

                    } else {
                        VotingUtils.showAlertWithMessage(VotingCandidateDetail.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return super.getBody();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("deviceID", android_id);
                params.put("fileID", fileId + "");
                params.put("typeID", "1");
                params.put("transactionID", orderID);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("DateTime", dateTime);
                headers.put("Authorization", authenticate);
                headers.put("User-Agent", agent);

                return headers;
            }
        };

        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private MediaInfo updateMediaInfo(String link) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, "Ads by onworldtv.com");
        movieMetadata.addImage(new WebImage(Uri.parse("http://static.onworldtv.com/themes/front/images/logo.png")));
        MediaInfo item = new MediaInfo.Builder(link).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("application/vnd.apple.mpegurl")
                .setMetadata(movieMetadata)
                .setCustomData(jsonReceiveData)
                //skippableTime
                .build();
        return item;
    }

    private void setupCastListener() {
        mCastConsumer = new VideoCastConsumerImpl() {
            @Override
            public void onApplicationConnected(ApplicationMetadata appMetadata,
                                               String sessionId, boolean wasLaunched) {

                Log.d(TAG, "onApplicationLaunched() is reached");
                if (null != mMediaInfo) {

                    if (mPlaybackState == PlaybackState.PLAYING) {
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
                            Utils.handleException(VotingCandidateDetail.this, e);
                        }
                        return;
                    } else {
                        mPlaybackState = PlaybackState.IDLE;
                        updatePlaybackLocation(PlaybackLocation.REMOTE, mMediaInfo);
                    }
                }
                updatePlayButton(mPlaybackState);
                invalidateOptionsMenu();
            }


            @Override
            public void onApplicationDisconnected(int errorCode) {
                Log.d(TAG, "onApplicationDisconnected() is reached with errorCode: " + errorCode);
                updatePlaybackLocation(PlaybackLocation.LOCAL, mMediaInfo);
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "onDisconnected() is reached");
                mPlaybackState = PlaybackState.IDLE;
                mLocation = PlaybackLocation.LOCAL;
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
                Utils.showToast(VotingCandidateDetail.this,
                        R.string.connection_temp_lost);
            }

            @Override
            public void onConnectivityRecovered() {
                Utils.showToast(VotingCandidateDetail.this,
                        R.string.connection_recovered);
            }

        };
    }

    private void setupMiniController() {
        mMini = (MiniController) findViewById(R.id.miniController1);
        mCastManager.addMiniController(mMini);
    }

    private void updatePlaybackLocation(PlaybackLocation location, MediaInfo mediaInfo) {
        mLocation = location;
        if (location == PlaybackLocation.LOCAL) {
            if (mPlaybackState == PlaybackState.PLAYING ||
                    mPlaybackState == PlaybackState.BUFFERING) {
                setCoverArtStatus(null);
                startControllersTimer();
            } else {
                stopControllersTimer();
                setCoverArtStatus(com.google.android.libraries.cast.companionlibrary.utils.Utils.
                        getImageUrl(mSelectedMedia, 0));

            }

        } else {
            stopControllersTimer();
            setCoverArtStatus(com.google.android.libraries.cast.companionlibrary.utils.Utils.
                    getImageUrl(mSelectedMedia, 0));
            updateControllersVisibility(false);
        }

    }

    private void play(int position) {
        startControllersTimer();
        switch (mLocation) {
            case LOCAL:
                mVideoView.seekTo(position);
                mVideoView.start();
                break;
            case REMOTE:
                mPlaybackState = PlaybackState.BUFFERING;
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

    public void togglePlayback(MediaInfo mediaInfo, int togglePlace) {
        //String adVideoLink = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/ForBiggerFun.m3u8";
        String adVideoLink = null;
        if (mCastManager.isAdvertisement == true && adVideo != null) {
            adVideoLink = adVideo.getLink_src();
        }
        this.mMediaInfo = mediaInfo;
        stopControllersTimer();
        if (togglePlace == TOGGLE_SCREEN) {
            switch (mPlaybackState) {
                case PAUSED:
                    switch (mLocation) {
                        case LOCAL:
                            mVideoView.start();
                            if (!mCastManager.isConnecting()) {
                                mCastManager.clearPersistedConnectionInfo(
                                        VideoCastManager.CLEAR_SESSION);
                            }
                            mPlaybackState = PlaybackState.PLAYING;
                            startControllersTimer();
                            restartTrickplayTimer();
                            updatePlaybackLocation(PlaybackLocation.LOCAL, mediaInfo);
                            break;
                        case REMOTE:
                            try {
                                mCastManager.checkConnectivity();
                                if (adVideoLink != null) {
                                    mCastManager.isAdvertisement = true;
                                    mMediaInfo = updateMediaInfo(adVideoLink);
                                    loadRemoteMedia(0, true);
                                    startTimerTask();
                                } else {
                                    loadRemoteMedia(0, true);
                                }
                            } catch (Exception e) {
                                Utils.handleException(VotingCandidateDetail.this, e);
                                return;
                            }
                            break;
                        default:
                            break;
                    }
                    break;

                case PLAYING:
                    mPlaybackState = PlaybackState.PAUSED;
                    mVideoView.pause();
                    break;

                case IDLE:
                    switch (mLocation) {
                        case LOCAL:
                            mPlayPause.setVisibility(View.INVISIBLE);
                            mStartText.setVisibility(View.INVISIBLE);
                            mEndText.setVisibility(View.INVISIBLE);
                            mSeekbar.setVisibility(View.INVISIBLE);

                            mAdvSkipLayout.setVisibility(View.INVISIBLE);
                            if (adVideoLink != null) {
                                mCastManager.isAdvertisement = true;
                                mMediaInfo = updateMediaInfo(adVideoLink);
                                mAdvTextTime.setText(adVideo.getDuration() + "s");
                                mVideoView.setVideoURI(Uri.parse(adVideoLink));
                                mVideoView.seekTo(0);
                                mVideoView.start();
                                mPlaybackState = PlaybackState.PLAYING;
                                restartTrickplayTimer();
                            } else {
                                playVideo(mediaInfo.getContentId());

                            }
                            updatePlaybackLocation(PlaybackLocation.LOCAL, mediaInfo);
                            break;
                        case REMOTE:

                            try {
                                mCastManager.checkConnectivity();
                                if (adVideoLink != null) {
                                    mCastManager.isAdvertisement = true;
                                    mMediaInfo = updateMediaInfo(adVideoLink);
                                    loadRemoteMedia(0, true);
                                    startTimerTask();
                                } else {
                                    loadRemoteMedia(0, true);
                                }

                            } catch (Exception e) {
                                Utils.handleException(VotingCandidateDetail.this, e);
                                return;
                            }
                            break;
                    }
                default:
                    break;
            }
            updatePlayButton(mPlaybackState);

        }
    }

    private void startTimerTask() {
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

    private void startTimerTaskLocal() {
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

                            long tempTimerRunning = (adVideo.getDuration() - tempTimerCurrent);
                            mAdvTextTime.setText(tempTimerRunning + "s");
                            //Log.e("TimerLocal Position ", tempTimerRunning + "");
                            if (tempTimerCurrent >= advertisementVieo.getSkippableTime()) {
                                mAdvSkipLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            //Log.e("TimerLocal Position ", "Adv Image: " + tempTimerCurrent);
                            if (advertisementImage != null) {
                                int startTimeImageAdv = advertisementImage.getStartTime();
                                if (tempTimerCurrent == startTimeImageAdv) {
                                    mAdvImage.setVisibility(View.VISIBLE);
                                    mCastManager.isAdvImage = true;
                                } else if (tempTimerCurrent >= advertisementImage.getSkippableTime() + startTimeImageAdv) {
                                    if (mAdvImage.getVisibility() == View.VISIBLE) {
                                        mAdvCloseImage.setVisibility(View.VISIBLE);
                                    }
                                    mCastManager.isAdvImage = false;
                                    stopTimerTaskLocal();
                                }
                            }
                        }
                    }
                });
            }
        };
    }

    private void playVideo(String url) {
        mAdvTimeLayout.setVisibility(View.GONE);
        mAdvSkipLayout.setVisibility(View.GONE);
        mPlayPause.setVisibility(View.VISIBLE);
        mStartText.setVisibility(View.VISIBLE);
        mEndText.setVisibility(View.VISIBLE);
        mSeekbar.setVisibility(View.VISIBLE);
        mVideoView.setVideoURI(Uri.parse(url));
        mVideoView.seekTo(0);
        mVideoView.start();
        mPlaybackState = PlaybackState.PLAYING;
        restartTrickplayTimer();
        mCastManager.isAdvertisement = false;
        updatePlaybackLocation(PlaybackLocation.LOCAL, mMediaInfo);
        updatePlayButton(mPlaybackState);
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {
        mCastManager.startVideoCastControllerActivity(this, mMediaInfo, mSelectedMedia, null, position, autoPlay);

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
        Log.d(TAG, "Stopped TrickPlay Timer");
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
        if (mLocation == PlaybackLocation.REMOTE) {
            return;
        }
        mControllersTimer = new Timer();
        mControllersTimer.schedule(new HideControllersTask(), 5000);
    }

    // should be called from the main thread
    private void updateControllersVisibility(boolean show) {
        if (show) {
            getSupportActionBar().show();
            mControllerBar.setVisibility(View.VISIBLE);
        } else {
            if (!Utils.isOrientationPortrait(this)) {
                getSupportActionBar().hide();
            }

            mControllerBar.setVisibility(View.INVISIBLE);


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (type == 1 || type == 2 || mCastManager.isAdvertisement) {
            stopPosition = mVideoView.getCurrentPosition();
            mVideoView.stopPlayback();
            persistStopPosition(stopPosition);
        }

        if (mLocation == PlaybackLocation.LOCAL) {

            if (null != mSeekbarTimer) {
                mSeekbarTimer.cancel();
                mSeekbarTimer = null;
            }
            if (null != mControllersTimer) {
                mControllersTimer.cancel();
            }
            mVideoView.pause();
            mPlaybackState = PlaybackState.PAUSED;
            updatePlayButton(PlaybackState.PAUSED);


        }
        mCastManager.removeVideoCastConsumer(mCastConsumer);
        mMini.removeOnMiniControllerChangedListener(mCastManager);
        mCastManager.decrementUiCounter();
        stopTimerTask();
        stopTimerTaskLocal();
        unregisterReceiver(receiverDownloadComplete);
        unregisterReceiver(receiverNotificationClicked);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy() is called");
        if (null != mCastManager) {
            mMini.removeOnMiniControllerChangedListener(mCastManager);
            mCastManager.removeMiniController(mMini);
            mCastConsumer = null;
        }
        stopControllersTimer();
        stopTrickplayTimer();
        if (typePlay == TypePlay.AUDIO_TYPE) {
            stopService(new Intent(this, MusicService.class));
            myHandler.removeCallbacks(UpdateSongTime);
            if (mServiceBound) {
                unbindService(mServiceConnection);
                mServiceBound = false;
            }
        }
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            mHelper = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        this.onPrepareOptionsMenu(menu);
        super.onRestart();
    }

    @Override
    protected void onResume() {

        mCastManager = VideoCastManager.getInstance();
        mCastManager.addVideoCastConsumer(mCastConsumer);
        mMini.setOnMiniControllerChangedListener(mCastManager);
        mCastManager.incrementUiCounter();
        if (mCastManager.isConnected()) {
            if (mMediaInfo != null) {
                updatePlaybackLocation(PlaybackLocation.REMOTE, mMediaInfo);
            }

        } else {
            if (mMediaInfo != null) {
                updatePlaybackLocation(PlaybackLocation.LOCAL, mMediaInfo);
            }
        }
        if (!mCastManager.isConnected()) {
            if (type == 1 || type == 2 || mCastManager.isAdvertisement) {
                SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                stopPosition = preferences.getInt(KEY_POSITION, -1);
                if (stopPosition != -1) {
                    mVideoView.seekTo(stopPosition);
                    mStartText.setText(
                            formatMillis(stopPosition));
                }
                mVideoView.start();
            }
        }

        IntentFilter filter = new IntentFilter(DownloadManager
                .ACTION_NOTIFICATION_CLICKED);

        receiverNotificationClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String extraId = DownloadManager
                        .EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] references = intent.getLongArrayExtra(extraId);
                for (long reference : references) {
                    if (reference == myDownloadReference) {
                    }
                }
            }
        };
        registerReceiver(receiverNotificationClicked, filter);
        IntentFilter intentFilter = new IntentFilter(DownloadManager
                .ACTION_DOWNLOAD_COMPLETE);

        receiverDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager
                        .EXTRA_DOWNLOAD_ID, -1);
                if (myDownloadReference == reference) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = downloadManager.query(query);

                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(DownloadManager
                            .COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);
                    String ab = cursor.getColumnNames().toString();

                    int fileNameIndex = cursor.getColumnIndex(DownloadManager
                            .COLUMN_LOCAL_FILENAME);
                    savedFilePath = cursor.getString(fileNameIndex);
                    String temp = cursor.getString(0);

                    int columnReason = cursor.getColumnIndex(DownloadManager
                            .COLUMN_REASON);
                    int reason = cursor.getInt(columnReason);

                    switch (status) {
                        case DownloadManager.STATUS_SUCCESSFUL:

                           /* Intent intentDisplay = new Intent(VotingCandidateDetail.this,
                                    SearchableActivity.class);
                            intentDisplay.putExtra("uri", savedFilePath);*/

                            break;
                        case DownloadManager.STATUS_FAILED:
                            Toast.makeText(VotingCandidateDetail.this,
                                    "FAILED: " + reason,
                                    Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            Toast.makeText(VotingCandidateDetail.this,
                                    "PAUSED: " + reason,
                                    Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_PENDING:
                            Toast.makeText(VotingCandidateDetail.this,
                                    "PENDING!",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            Toast.makeText(VotingCandidateDetail.this,
                                    "RUNNING!",
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                    cursor.close();
                }
            }
        };
        registerReceiver(receiverDownloadComplete, intentFilter);
        super.onResume();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mCastManager.onDispatchVolumeKeyEvent(event, MyApplication.VOLUME_INCREMENT)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void updateSeekbar(int position, int duration) {
        mSeekbar.setProgress(position);
        mSeekbar.setMax(duration);
        mStartText.setText(
                formatMillis(position));
        mEndText.setText(
                formatMillis(duration));
    }

    private void updatePlayButton(PlaybackState state) {
        boolean isConnected = mCastManager.isConnected() || mCastManager.isConnecting();
        mControllers.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        mPlayCircle.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        switch (state) {
            case PLAYING:
                mPlayPause.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_av_pause_dark));
                if (mLoading.getVisibility() == View.INVISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
                mControllers.setVisibility(View.VISIBLE);
                mPlayCircle.setVisibility(isConnected ? View.VISIBLE : View.GONE);
                break;
            case IDLE:
                mPlayCircle.setVisibility(View.VISIBLE);
                mControllers.setVisibility(View.INVISIBLE);
                mCoverArt.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.INVISIBLE);
                break;
            case PAUSED:
                mLoading.setVisibility(View.INVISIBLE);
                mPlayPause.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_av_play_dark));
                //mPlayCircle.setVisibility(isConnected ? View.VISIBLE : View.GONE);
                mPlayCircle.setVisibility(View.VISIBLE);
                break;
            case BUFFERING:
                mPlayPause.setVisibility(View.INVISIBLE);
                if (mLoading.getVisibility() == View.INVISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
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
        }
    }

    private void updateMetadata(boolean visible) {
        Point displaySize;
        if (!visible) {
            mTitleView.setVisibility(View.GONE);
            displaySize = Utils.getDisplaySize(this);//1794,1080
            RelativeLayout.LayoutParams lp = new
                    RelativeLayout.LayoutParams(displaySize.x, displaySize.y + getSupportActionBar().getHeight());
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mVideoView.setLayoutParams(lp);
            mVideoView.invalidate();
        } else {
            if (mMediaInfo != null) {
                MediaMetadata mm = mMediaInfo.getMetadata();
                mTitleView.setText(mm.getString(MediaMetadata.KEY_TITLE));
            }
            mTitleView.setVisibility(View.VISIBLE);
            displaySize = Utils.getDisplaySize(this);
            RelativeLayout.LayoutParams lp = new
                    RelativeLayout.LayoutParams(displaySize.x,
                    (int) (displaySize.x * mAspectRatio));
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            mVideoView.setLayoutParams(lp);
            mVideoView.invalidate();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        getMenuInflater().inflate(R.menu.voting_player_menu, menu);
        mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //menu.findItem(R.id.action_show_queue).setVisible(mCastManager.isConnected());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            /*case R.id.action_show_queue:
                if (mMini.isVisible()) {
                    mCastManager.updateMiniControllersVisibility(false);
                } else {
                    mCastManager.updateMiniControllersVisibility(false);
                }
                break;*/
            case android.R.id.home:
                //ActivityCompat.finishAfterTransition(this);
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
        }
        return true;
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void loadViews() {
        mVideoView = (VideoView) findViewById(R.id.videoView1);
        mTitleView = (TextView) findViewById(R.id.txt_title_video);
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

        infoName = (TextView) findViewById(R.id.name_candidate);
        infoDob = (TextView) findViewById(R.id.dob_candidate);
        infoSex = (TextView) findViewById(R.id.sex_candidate);
        infoId = (TextView) findViewById(R.id.id_candidate);
        infoAvata = (ImageView) findViewById(R.id.candidate_avata);
        fileTitle = (TextView) findViewById(R.id.txt_title_video);
        playPauseAudio = (ImageButton) findViewById(R.id.audio_play_pause);
        voteButton = (Button) findViewById(R.id.btn_vote_content);
        downloadButton = (Button) findViewById(R.id.btn_audio_download);
        startTimeAudio = (TextView) findViewById(R.id.start_time_audio);
        endTimeAudio = (TextView) findViewById(R.id.end_time_audio);
        audioSeekbar = (SeekBar) findViewById(R.id.audio_seekbar);
        titleProgram = (TextView) findViewById(R.id.title_program);
        candidateDescription = (TextView) findViewById(R.id.candidate_description);

        slidePhotoViewPager = (ViewPager) findViewById(R.id.pager_slide_voting_photo);
        audioContainer = (LinearLayout) findViewById(R.id.audio_container);
        videoContainer = (RelativeLayout) findViewById(R.id.video_container);
        photoContainer = (RelativeLayout) findViewById(R.id.photo_container);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        totalVoteView = (TextView) findViewById(R.id.vote_number);
        totalVoteViewTitle = (TextView) findViewById(R.id.vote_number_title);
        voteNumberFromUser = (TextView) findViewById(R.id.vote_number_from_user);
    }

    private void setupControlsCallbacks() {
        mVideoView.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //Log.e(TAG, "OnErrorListener.onError(): VideoView encountered an " + "error, what: " + what + ", extra: " + extra);
                String msg;
                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                    msg = getString(R.string.video_error_media_load_timeout);
                } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    msg = getString(R.string.video_error_server_unaccessible);
                } else {
                    msg = getString(R.string.video_error_unknown_error);
                }
                Utils.showErrorDialog(VotingCandidateDetail.this, msg);
                mVideoView.stopPlayback();
                mPlaybackState = PlaybackState.IDLE;
                updatePlayButton(mPlaybackState);
                return true;
            }
        });

        mVideoView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                //Check if have Adv
                if (mCastManager.isAdvertisement == true) {
                    //mPlayPause.setVisibility(View.INVISIBLE);
                    mAdvTimeLayout.setVisibility(View.VISIBLE);
                    mStartText.setVisibility(View.INVISIBLE);
                    mEndText.setVisibility(View.INVISIBLE);
                    mSeekbar.setVisibility(View.INVISIBLE);

                    startTimerTaskLocal();
                } else if (mCastManager.isAdvImage == true) {
                    startTimerTaskLocal();
                }


                mLoading.setVisibility(View.INVISIBLE);
                mDuration = mp.getDuration();
                mEndText.setText(
                        formatMillis(mDuration));
                mSeekbar.setMax(mDuration);
                restartTrickplayTimer();
                if (!mVideoView.isPlaying() && !mCastManager.isConnected()) {
                    mVideoView.start();
                    mPlaybackState = PlaybackState.PLAYING;

                    restartTrickplayTimer();
                    updatePlaybackLocation(PlaybackLocation.LOCAL, mMediaInfo);
                    updatePlayButton(mPlaybackState);
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
        });
        mVideoView.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                stopTimerTaskLocal();
                stopTrickplayTimer();

                mPlaybackState = PlaybackState.IDLE;
                if (mCastManager.isAdvertisement == true) {
                    mCastManager.isAdvertisement = false;
                    mMediaInfo = mSelectedMedia;
                    mAdvTimeLayout.setVisibility(View.GONE);
                    mAdvSkipLayout.setVisibility(View.GONE);
                    //mPlayPause.setVisibility(View.VISIBLE);
                    mStartText.setVisibility(View.VISIBLE);
                    mEndText.setVisibility(View.VISIBLE);
                    mSeekbar.setVisibility(View.VISIBLE);
                    mVideoView.setVideoURI(Uri.parse(mMediaInfo.getContentId()));
                    mVideoView.seekTo(0);
                    mVideoView.start();
                    mPlaybackState = PlaybackState.PLAYING;
                    if (adImage != null) {
                        startTimerTaskLocal();
                    }
                    restartTrickplayTimer();
                    updatePlaybackLocation(PlaybackLocation.LOCAL, mMediaInfo);
                    updatePlayButton(mPlaybackState);
                } else {
                    updatePlayButton(mPlaybackState);
                }


            }
        });

        mVideoView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCastManager.isAdvertisement == true) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adVideo.getLinkClick()));
                    startActivity(browserIntent);
                }

                if (!mControllersVisible) {
                    updateControllersVisibility(true);
                }
                startControllersTimer();
                return false;
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPlaybackState == PlaybackState.PLAYING) {
                    play(seekBar.getProgress());

                } else if (mPlaybackState != PlaybackState.IDLE) {
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
                mStartText.setText(
                        formatMillis(progress));
            }
        });

        mPlayPause.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLocation == PlaybackLocation.LOCAL) {
                    //mLoading.setVisibility(View.VISIBLE);
                    togglePlayback(mMediaInfo, TOGGLE_SCREEN);
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
                    togglePlayback(mMediaInfo, TOGGLE_SCREEN);
                    if (mVideoView.isPlaying()) {
                        mLoading.setVisibility(View.INVISIBLE);
                    }
                } else if (modeViewOrListen == 0) {
                    if (mSelectedMedia != null) {
                        String urlCoverImage = com.google.android.libraries.cast.companionlibrary.utils.Utils.
                                getImageUrl(mSelectedMedia, 0);
                        mAdvSkipLayout.setVisibility(View.INVISIBLE);
                        mAdvTimeLayout.setVisibility(View.INVISIBLE);
                        mControllerBar.setVisibility(View.INVISIBLE);
                        Intent mpdIntent = new Intent(VotingCandidateDetail.this, PlayerActivity.class)
                                .setData(Uri.parse(mSelectedMedia.getContentId()))
                                .putExtra("COVERIMAGE", urlCoverImage)
                                .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, PlayerActivity.TYPE_HLS);
                        startActivity(mpdIntent);
                    }
                }
            }
        });

        mAdvSkipButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimerTaskLocal();
                mCastManager.isAdvertisement = false;
                mMediaInfo = mSelectedMedia;
                playVideo(mMediaInfo.getContentId());
                if (adImage != null) {
                    startTimerTaskLocal();
                }
            }
        });

        mAdvCloseImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdvImage.setVisibility(View.INVISIBLE);
                mAdvCloseImage.setVisibility(View.INVISIBLE);
                mCastManager.isAdvImage = false;
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
        voteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (VotingUtils.checkLoginStatus(VotingCandidateDetail.this) == true) {
                    if (remainVote <= 0) {
                        VotingUtils.showAlertWithMessage(VotingCandidateDetail.this, getString(R.string.msg_cannot_vote_anymore));
                    } else {
                        openDialogConfirmVote();
                    }

                } else {
                    openDiaLogAskLogin();

                }
            }
        });
        downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (VotingUtils.checkLoginStatus(VotingCandidateDetail.this) == true) {
                    String payload = "";
                    if (isPay == 0) {
                        try {
                            mHelper.launchPurchaseFlow(VotingCandidateDetail.this, inAppId, RC_REQUEST,
                                    mPurchaseFinishedListener, payload);
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            e.printStackTrace();
                        }
                    } else if (isPay == 1) {
                        downloadFile(urlMp3Download);
                        setButonDisable(downloadButton, "Download");
                    }

                } else {
                    openDiaLogAskLogin();
                }

            }
        });
        playPauseAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataPath = null;
                boolean isOffline = false;
                if (fileCandidates != null) {
                    if (checkFileLocalExist(songTitle)) {
                        dataPath = fileDir + songTitle;
                        isOffline = true;
                    } else {
                        if (urlMp3 != null) {
                            dataPath = urlMp3;
                            isOffline = false;
                        }

                    }
                    //dataPath = Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/Download/MotNha.mp3").getPath();
                    if (audioPlaybackState == AudioPlaybackState.IDLE) {
                        Intent myIntent = new Intent(VotingCandidateDetail.this, MusicService.class);
                        myIntent.putExtra("isOffline", isOffline);
                        myIntent.putExtra("dataPath", dataPath);
                        startService(myIntent);
                        bindService(myIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                        myHandler.postDelayed(UpdateSongTime, 100);
                        audioPlaybackState = AudioPlaybackState.PLAYING;
                        typePlay = TypePlay.AUDIO_TYPE;
                    } else if (audioPlaybackState == AudioPlaybackState.PLAYING) {
                        Intent myIntent = new Intent(VotingCandidateDetail.this, MusicService.class);
                        myIntent.putExtra("isOffline", true);
                        startService(myIntent);
                        audioPlaybackState = AudioPlaybackState.PAUSED;
                        myHandler.removeCallbacks(UpdateSongTime);
                    } else if (audioPlaybackState == AudioPlaybackState.PAUSED) {
                        Intent myIntent = new Intent(VotingCandidateDetail.this, MusicService.class);
                        myIntent.putExtra("isOffline", true);
                        startService(myIntent);
                        audioPlaybackState = AudioPlaybackState.PLAYING;
                        myHandler.postDelayed(UpdateSongTime, 100);
                    }
                }


                updatePlayPauseButton();
            }
        });
        audioSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                startTimeAudio.setText(formatMillis(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public boolean checkFileLocalExist(String fileName) {

        fileDir = getDownloadFolder() + "/";// /data/data/com.sonworld/files
        File myFile = new File(fileDir + fileName);
        if (myFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public String getDownloadFolder() {
        return MyApplication.getAppContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                .toString();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setButonDisable(Button button, String text) {
        button.setText(text);
        button.setTextColor(getResources().getColor(R.color.white));
        button.setEnabled(false);
        button.setBackground(getResources().getDrawable(R.drawable.button_shape_disable));
    }

    public void openDiaLogAskLogin() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.msg_signin_before_vote));

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                startActivity(new Intent(VotingCandidateDetail.this, LoginActivity.class));
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

    public void openDialogConfirmVote() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.msg_vote_confirm) + " " + infoNameValue + " right now?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                final String votingApi = URL.VOTING;
                jsonData = new JSONObject();
                try {
                    jsonData.put("deviceID", android_id);
                    jsonData.put("examineeID", infoIdValue);
                    jsonData.put("roundID", roundId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringEntity entity = getEntityFromString(jsonData);
                final String dateTime = DateTimeUtils.getCurrentUTCDateString();
                final String agent = MyApplication.getUserAgent();

                authenticate = HeaderHelper.createAuthorizationValue("POST", dateTime,
                        CommonUtils.getAccessTokenSecu(VotingCandidateDetail.this), 78);


                StringRequest strReq = new StringRequest(Request.Method.POST,
                        votingApi, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("code");
                            String message = jsonObject.getString("message");
                            if (code == VotingUtils.REQUEST_SUCCESS) {
                                VotingUtils.showAlertWithMessage(VotingCandidateDetail.this, getString(R.string.msg_vote_successfully));
                                if (voteType == 1) {
                                    remainVote--;
                                    voteButton.setText("Voted");
                                    totalVoteFromUser = totalVoteFromUser + 1;
                                    if (totalVoteFromUser >= 1) {
                                        voteNumberFromUser.setVisibility(View.VISIBLE);
                                        voteNumberFromUser.setText("" + totalVoteFromUser);
                                    }

                                    if (remainVote <= 0) {
                                        //setButonDisable(voteButton, "Voted");
                                    }
                                } else {
                                    setButonDisable(voteButton, "Voted");
                                }

                            } else if (code == VotingUtils.REQUEST_DEVICE_VOTED) {
                                VotingUtils.showAlertWithMessage(VotingCandidateDetail.this, getString(R.string.msg_device_voted));
                            } else {
                                VotingUtils.showAlertWithMessage(VotingCandidateDetail.this, message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("kaka", "Error: " + error.getMessage());
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return super.getBody();
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("deviceID", android_id);
                        params.put("examineeID", infoIdValue + "");
                        params.put("roundID", roundId + "");
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        headers.put("DateTime", dateTime);
                        headers.put("Authorization", authenticate);
                        headers.put("User-Agent", agent);

                        return headers;
                    }
                };

                MyApplication.getInstance().addToRequestQueue(strReq);

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

    public void updatePlayPauseButton() {
        if (audioPlaybackState == AudioPlaybackState.PAUSED) {
            playPauseAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_play_light));
        } else if (audioPlaybackState == AudioPlaybackState.PLAYING) {
            playPauseAudio.setImageDrawable(getResources().getDrawable(R.mipmap.ic_av_pause_light));
        }
    }

    private StringEntity getEntityFromString(JSONObject input) {
        try {
            return new StringEntity(input.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private StringEntity getEntityFromString(String input) {
        try {
            return new StringEntity(input.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void persistStopPosition(int stopPosition) {
        SharedPreferences.Editor preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE).edit();
        preferences.putInt(KEY_POSITION, stopPosition);
        preferences.commit(); // need this to ensure data is persisted.
    }

    @Override
    public void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();
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
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void downloadFile(String downloadFileUrl) {
        Uri uri = Uri.parse(downloadFileUrl);
        String songTitle = getSongTitleFromUrl(downloadFileUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDescription("Onworld Download")
                .setTitle(songTitle);
        request.setVisibleInDownloadsUi(false);

        request.setDestinationInExternalFilesDir(VotingCandidateDetail.this,
                Environment.DIRECTORY_DOWNLOADS, songTitle);
        request.setVisibleInDownloadsUi(false);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE);


        myDownloadReference = downloadManager.enqueue(request);

    }

    public String getSongTitleFromUrl(String url) {
        String[] split = url.split("/");
        String nameTitle = split[split.length - 1];
        return nameTitle;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }

    }

    @Override
    public void onNextImageSlideListener() {
        int current = slidePhotoViewPager.getCurrentItem();
        int next = current + 1;
        if (next < imageSlideLength) {
            slidePhotoViewPager.setCurrentItem(next, true);
        } else {
            slidePhotoViewPager.setCurrentItem(0, true);
        }
    }

    @Override
    public void onPreviousImageSlideListener() {
        int current = slidePhotoViewPager.getCurrentItem();
        int pre = current - 1;
        if (pre >= 0) {
            slidePhotoViewPager.setCurrentItem(pre, true);
        } else {
            slidePhotoViewPager.setCurrentItem(imageSlideLength - 1, true);
        }
    }

    @Override
    public void onViewPhotoListener(int position, String[] url) {
        Intent intent = new Intent(this, PhotoViewerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(PHOTO_POSITION, position);
        bundle.putStringArray(PHOTO_ARRAY, url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public enum TypePlay {
        AUDIO_TYPE, VIDEO_TYPE
    }

    public enum CandidateType {
        VIDEO, IMAGE
    }

    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    public enum AudioPlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

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
                if (mMediaInfo != null) {
                    togglePlayback(mMediaInfo, TOGGLE_SCREEN);
                    mLoading.setVisibility(View.VISIBLE);
                    mPlayCircle.setVisibility(View.INVISIBLE);
                    mPlayPause.setVisibility(View.INVISIBLE);
                    mFullScreen.setVisibility(View.INVISIBLE);
                    if (mVideoView.isPlaying()) {
                        mLoading.setVisibility(View.INVISIBLE);
                    }
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
                    if (mLocation == PlaybackLocation.LOCAL) {
                        int currentPos = mVideoView.getCurrentPosition();
                        updateSeekbar(currentPos, mDuration);
                    }
                }
            });
        }
    }
}