/*
 * Copyright (C) 2014 The Android Open Source Project
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
package onworld.sbtn.activities;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.adapters.MusicPagerAdapter;
import onworld.sbtn.callbacks.DataLoadedWithoutParamsListener;
import onworld.sbtn.fragments.homes.MusicListFragment;
import onworld.sbtn.josonmodel.ContentModel;
import onworld.sbtn.josonmodel.InfoVideoArrayModel;
import onworld.sbtn.josonmodel.Related;
import onworld.sbtn.josonmodel.ViewDetail;
import onworld.sbtn.service.MusicPlayerService;
import onworld.sbtn.tasks.TaskLoadDataWithoutParams;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.views.pagerindicators.PageIndicator;

import static com.google.android.libraries.cast.companionlibrary.utils.Utils.formatMillis;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MusicPlayerActivity extends AppCompatActivity implements DataLoadedWithoutParamsListener {
    public static final String THUMB = "thumb";
    public static OnRollingPlayerListener onRollingPlayerListener;
    public static OnShowPlayerListener onShowPlayerListener;
    Toolbar toolbar;
    ImageView mBlurredArt;
    String dataPath = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/ForBiggerFun.m3u8";
    ImageView mPlayPause, mRepeat, mNext, mPrevious;
    RelativeLayout mMusicControllers;
    MusicPlaybackState musicPlaybackState;
    LinearLayout mMusicTopController;
    boolean isRepeat;
    boolean isShuffle;
    TextView mMusicTitleToolbar, mMusicDescriptionToolbar;
    LinearLayout mMusicTitleToolbarLayout;
    ImageView mMusicLogoToolbar, mMusicLive;
    String title;
    boolean isLive;
    ArrayList<Related> relateds = new ArrayList<>();
    int relatedSize = 0;
    ProgressBar mMusicLoading;
    View mMusicBackground;
    String imageLink;
    PageIndicator mIndicator;
    int contentId;
    private Drawable mPauseDrawable, mRepeatDrawable, mUnRepeatDrawable, mShuffleDrawable, mUnShuffleDrawable;
    private Drawable mPlayDrawable;
    private SeekBar mSeekbar;
    private TextView mStartTime;
    private TextView mEndTime;
    private ViewPager musicViewPager;
    private MusicPagerAdapter mMusicPagerAdapter;
    private MusicPlayerService mBoundService;
    private boolean mServiceBound = false;
    private double currentTime = 0;
    private double finalTime = 0;
    private boolean isSeeking;
    private int playPosition = -1;
    private Handler myHandler = new Handler();
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MyBinder myBinder = (MusicPlayerService.MyBinder) service;
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
                    mStartTime.setText(formatMillis((int) currentTime));
                    mEndTime.setText(formatMillis((int) finalTime));
                    mSeekbar.setMax((int) finalTime);
                    mSeekbar.setProgress((int) currentTime);
                }

            }
            myHandler.postDelayed(this, 100);
        }
    };
    private String linkMusic;
    private ImageView mShuffle;
    private ArrayList<InfoVideoArrayModel> infoVideoArrayModels;
    private String mMusicDescriptonText = "";

    public static void setOnRollingPlayerListener(OnRollingPlayerListener listener) {
        onRollingPlayerListener = listener;
    }

    public static void setOnShowPlayerListener(OnShowPlayerListener listener) {
        onShowPlayerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setupToolBar();
        loadView();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            imageLink = bundle.getString(THUMB);
            contentId = bundle.getInt(Constant.DETAIL_ID_KEY, 0);
            String urlDetail = URL.DETAIL + contentId;
            new TaskLoadDataWithoutParams(this, urlDetail).execute();
        }
        updateBlurredImage(imageLink);
        initView();
        MusicPlayerService.OnMusicCompleteListener onMusicCompleteListener = new MusicPlayerService.OnMusicCompleteListener() {
            @Override
            public void onMusicComplete() {
                myHandler.removeCallbacks(UpdateSongTime);
                if (isShuffle) {
                    playNext();
                }

            }
        };
        MusicPlayerService.setOnMusicCompleteListener(onMusicCompleteListener);
        MusicPlayerService.OnMusicLoopingListener onMusicLoopingListener = new MusicPlayerService.OnMusicLoopingListener() {
            @Override
            public void onMusicLooping() {
                myHandler.postDelayed(UpdateSongTime, 100);
                musicPlaybackState = MusicPlaybackState.PLAYING;
            }
        };
        MusicPlayerService.setOnMusicLoopingListener(onMusicLoopingListener);
        MusicListFragment.OnMusicListClickListener onMusicListClickListener = new MusicListFragment.OnMusicListClickListener() {
            @Override
            public void onMusicListClick(String linkMusic, String title, String des, String imageLink, ArrayList<Related> relateds, boolean isLive) {
                MusicPlayerActivity.this.title = title;
                MusicPlayerActivity.this.relateds = relateds;
                MusicPlayerActivity.this.isLive = isLive;
                mMusicDescriptonText = des;
                mMusicPagerAdapter.setMusicPagerData(title, des, imageLink, relateds);
                updateBlurredImage(imageLink);
                startMusic(linkMusic);
                updatePlayPauseButton();
                musicViewPager.setCurrentItem(0);
                musicLoading(true);
                if (isLive == true) {
                    mMusicLive.setVisibility(View.VISIBLE);
                    mMusicTopController.setVisibility(View.INVISIBLE);

                } else {
                    mMusicLive.setVisibility(View.INVISIBLE);
                    mMusicTopController.setVisibility(View.VISIBLE);
                }
            }
        };
        MusicListFragment.setOnMusicListClickListener(onMusicListClickListener);
        MusicPlayerService.OnMusicStartListener onMusicStartListener = new MusicPlayerService.OnMusicStartListener() {
            @Override
            public void onMusicStart() {
                musicLoading(false);
                onShowPlayerListener.onShowPlayer();

            }
        };
        MusicPlayerService.setOnMusicStartListener(onMusicStartListener);

        musicLoading(true);
    }

    private void loadView() {
        mBlurredArt = (ImageView) findViewById(R.id.album_art_blurred);
        mPlayPause = (ImageView) findViewById(R.id.music_play_pause);
        mSeekbar = (SeekBar) findViewById(R.id.music_seekbar);
        mStartTime = (TextView) findViewById(R.id.music_start_text);
        mEndTime = (TextView) findViewById(R.id.music_end_text);
        mMusicControllers = (RelativeLayout) findViewById(R.id.music_controller_bar);
        musicViewPager = (ViewPager) findViewById(R.id.music_viewpager);
        mRepeat = (ImageView) findViewById(R.id.music_repeat);
        mNext = (ImageView) findViewById(R.id.music_next);
        mPrevious = (ImageView) findViewById(R.id.music_previous);
        mMusicLive = (ImageView) findViewById(R.id.music_live_icon);
        mMusicTopController = (LinearLayout) findViewById(R.id.music_top_controller);
        mMusicLoading = (ProgressBar) findViewById(R.id.music_player_loading);
        mMusicBackground = (View) findViewById(R.id.music_gradient_background);
        mShuffle = (ImageView) findViewById(R.id.music_shuffle);
        mIndicator = (PageIndicator) findViewById(R.id.indicator);
    }

    private void setupToolBar() {
        toolbar = (Toolbar) findViewById(R.id.music_toolbar);
        mMusicTitleToolbarLayout = (LinearLayout) toolbar.findViewById(R.id.music_title_toolbar_layout);
        mMusicTitleToolbar = (TextView) toolbar.findViewById(R.id.music_title_toolbar);
        mMusicLogoToolbar = (ImageView) toolbar.findViewById(R.id.music_logo_toolbar);
        mMusicDescriptionToolbar = (TextView) toolbar.findViewById(R.id.music_description_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        showToolbarTile(false);
    }

    private void showToolbarTile(boolean isShow) {
        if (isShow) {
            mMusicTitleToolbarLayout.setVisibility(View.VISIBLE);
            mMusicLogoToolbar.setVisibility(View.GONE);
            updateTitleToolbar();
        } else {
            mMusicTitleToolbarLayout.setVisibility(View.GONE);
            mMusicLogoToolbar.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            mPauseDrawable = getResources().getDrawable(R.mipmap.music_pause, getTheme());
            mPlayDrawable = getResources().getDrawable(R.mipmap.music_play, getTheme());
        } else {
            mPauseDrawable = getResources().getDrawable(R.mipmap.music_pause);
            mPlayDrawable = getResources().getDrawable(R.mipmap.music_play);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            mUnRepeatDrawable = getResources().getDrawable(R.mipmap.music_repeat, getTheme());
            mRepeatDrawable = getResources().getDrawable(R.drawable.music_repeat_blue, getTheme());
        } else {
            mUnRepeatDrawable = getResources().getDrawable(R.mipmap.music_repeat);
            mRepeatDrawable = getResources().getDrawable(R.drawable.music_repeat_blue);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            mUnShuffleDrawable = getResources().getDrawable(R.mipmap.music_shuffle, getTheme());
            mShuffleDrawable = getResources().getDrawable(R.drawable.music_shuffle_blue, getTheme());
        } else {
            mUnShuffleDrawable = getResources().getDrawable(R.mipmap.music_shuffle);
            mShuffleDrawable = getResources().getDrawable(R.drawable.music_shuffle_blue);
        }

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (musicPlaybackState == MusicPlaybackState.IDLE) {
                    startMusic(linkMusic);
                } else if (musicPlaybackState == MusicPlaybackState.PLAYING) {
                    pauseMusic();
                } else if (musicPlaybackState == MusicPlaybackState.PAUSED) {
                    playMusic();
                }
                updatePlayPauseButton();

            }
        });
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mStartTime.setText(formatMillis(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseMusic();
                isSeeking = true;
                updatePlayPauseButton();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicPlaybackState == MusicPlaybackState.PLAYING) {
                    Intent myIntent = new Intent(MusicPlayerActivity.this, MusicPlayerService.class);
                    myIntent.putExtra(MusicPlayerService.LINK_MP3, dataPath);
                    myIntent.putExtra(MusicPlayerService.IS_SEEKING, isSeeking);
                    myIntent.putExtra(MusicPlayerService.SEEKING_POSITION, seekBar.getProgress());
                    startService(myIntent);
                    bindService(myIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                    myHandler.postDelayed(UpdateSongTime, 100);
                    musicPlaybackState = MusicPlaybackState.PLAYING;
                } else if (musicPlaybackState != MusicPlaybackState.IDLE) {
                    Intent myIntent = new Intent(MusicPlayerActivity.this, MusicPlayerService.class);
                    myIntent.putExtra(MusicPlayerService.LINK_MP3, dataPath);
                    myIntent.putExtra(MusicPlayerService.IS_SEEKING, isSeeking);
                    myIntent.putExtra(MusicPlayerService.SEEKING_POSITION, seekBar.getProgress());
                    startService(myIntent);
                    bindService(myIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                    myHandler.postDelayed(UpdateSongTime, 100);
                    musicPlaybackState = MusicPlaybackState.PLAYING;
                } else if (musicPlaybackState != MusicPlaybackState.PAUSED) {
                    Intent myIntent = new Intent(MusicPlayerActivity.this, MusicPlayerService.class);
                    myIntent.putExtra(MusicPlayerService.LINK_MP3, dataPath);
                    myIntent.putExtra(MusicPlayerService.IS_SEEKING, isSeeking);
                    myIntent.putExtra(MusicPlayerService.SEEKING_POSITION, seekBar.getProgress());
                    startService(myIntent);
                    bindService(myIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                    myHandler.postDelayed(UpdateSongTime, 100);
                    musicPlaybackState = MusicPlaybackState.PLAYING;
                }
                updatePlayPauseButton();

            }
        });
        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat == true) {
                    isRepeat = false;
                    mBoundService.setLooping(false);
                } else {
                    mBoundService.setLooping(true);
                    isRepeat = true;
                    isShuffle = false;
                    mShuffle.setImageDrawable(mUnShuffleDrawable);
                }
                updateRepeatButton();

            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relateds.size() > 0) {
                    if (playPosition == -1 || playPosition == 0) {
                        playPosition = relatedSize;
                    } else {
                        playPosition = playPosition - 1;
                    }
                    Related related = relateds.get(playPosition);
                    mMusicDescriptonText = "";
                    MusicPlayerActivity.this.title = related.getRelatedName();
                    mMusicPagerAdapter.setMusicPagerData(title, "", related.getRelatedImage(), relateds);
                    updateBlurredImage(related.getRelatedImage());
                    musicViewPager.setCurrentItem(0);
                    musicLoading(true);
                    startMusic(related.getRelatedLink());
                }

            }
        });
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    showToolbarTile(true);
                } else {
                    showToolbarTile(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffle) {
                    isShuffle = false;
                    mShuffle.setImageDrawable(mUnShuffleDrawable);
                } else {
                    isShuffle = true;
                    mShuffle.setImageDrawable(mShuffleDrawable);
                    isRepeat = false;
                    mBoundService.setLooping(false);
                    updateRepeatButton();
                }
            }
        });

    }

    private void playNext() {
        if (relateds.size() > 0) {
            if (playPosition == -1) {
                playPosition = 0;
            }
            Related related = relateds.get(playPosition);
            mMusicDescriptonText = "";
            MusicPlayerActivity.this.title = related.getRelatedName();
            mMusicPagerAdapter.setMusicPagerData(title, "", related.getRelatedImage(), relateds);
            updateBlurredImage(related.getRelatedImage());
            if (relatedSize > playPosition) {
                playPosition = playPosition + 1;
            } else {
                playPosition = 0;
            }
            musicViewPager.setCurrentItem(0);
            musicLoading(true);
            startMusic(related.getRelatedLink());
        }
    }

    private void playMusic() {
        Intent myIntent = new Intent(MusicPlayerActivity.this, MusicPlayerService.class);
        startService(myIntent);
        musicPlaybackState = MusicPlaybackState.PLAYING;
        myHandler.postDelayed(UpdateSongTime, 100);
    }

    private void startMusic(String link) {
        Intent myIntent = new Intent(MusicPlayerActivity.this, MusicPlayerService.class);
        myIntent.putExtra(MusicPlayerService.IS_START, true);
        myIntent.putExtra(MusicPlayerService.LINK_MP3, link);
        startService(myIntent);
        bindService(myIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        myHandler.postDelayed(UpdateSongTime, 100);
        musicPlaybackState = MusicPlaybackState.PLAYING;
        updatePlayPauseButton();

    }

    private void pauseMusic() {
        Intent myIntent = new Intent(MusicPlayerActivity.this, MusicPlayerService.class);
        startService(myIntent);
        musicPlaybackState = MusicPlaybackState.PAUSED;
        myHandler.removeCallbacks(UpdateSongTime);
    }

    private void updatePlayPauseButton() {
        if (musicPlaybackState == MusicPlaybackState.PAUSED) {
            mPlayPause.setImageDrawable(mPlayDrawable);
            onRollingPlayerListener.onRollingPlayer(false);
        } else if (musicPlaybackState == MusicPlaybackState.PLAYING) {
            mPlayPause.setImageDrawable(mPauseDrawable);
            onRollingPlayerListener.onRollingPlayer(true);
        }
    }

    private void updateRepeatButton() {
        if (isRepeat == true) {
            mRepeat.setImageDrawable(mRepeatDrawable);
        } else {
            mRepeat.setImageDrawable(mUnRepeatDrawable);
        }
    }

    public void doAlbumArtStuff(Bitmap loadedImage) {
        setBlurredAlbumArt blurredAlbumArt = new setBlurredAlbumArt();
        blurredAlbumArt.execute(loadedImage);
    }

    public void updateBlurredImage(String urlImage) {
        if (mBlurredArt != null) {
            ImageLoader.getInstance().displayImage(urlImage, mBlurredArt,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.img_error)
                            .build(), new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            doAlbumArtStuff(loadedImage);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Bitmap failedBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.img_error);
                            doAlbumArtStuff(failedBitmap);
                        }

                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityCompat.finishAfterTransition(this);
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MusicPlayerService.class));
        myHandler.removeCallbacks(UpdateSongTime);
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
        super.onDestroy();
    }

    @Override
    public void onDataLoadedWithoutParams(JSONObject jsonObject) {
        Gson gson = new Gson();
        ViewDetail jsonHome = gson.fromJson(jsonObject.toString(), ViewDetail.class);
        if (jsonHome.getError() == Utils.OLD_REQUEST_SUCCESS) {
            infoVideoArrayModels = jsonHome.getActors();
            ContentModel contentModel = jsonHome.getContent();
            linkMusic = contentModel.getLink();
            title = contentModel.getName();
            isLive = contentModel.getIsLive();
            if (isLive == true) {
                mMusicLive.setVisibility(View.VISIBLE);
                mMusicTopController.setVisibility(View.INVISIBLE);

            } else {
                mMusicLive.setVisibility(View.INVISIBLE);
                mMusicTopController.setVisibility(View.VISIBLE);
            }
            if (infoVideoArrayModels.size() > 0) {
                int infoSize = infoVideoArrayModels.size();
                for (int i = 0; i < infoSize; i++) {
                    if (i == 0) {
                        mMusicDescriptonText = mMusicDescriptonText + infoVideoArrayModels.get(i).getName();
                    } else {
                        mMusicDescriptonText = mMusicDescriptonText + " - " + infoVideoArrayModels.get(i).getName();
                    }

                }

            } else {
                mMusicDescriptionToolbar.setVisibility(View.GONE);
            }
            imageLink = contentModel.getImage();

            relateds = jsonHome.getRelated();
            relatedSize = relateds.size() - 1;
            mMusicPagerAdapter = new MusicPagerAdapter(this, getSupportFragmentManager(), title, mMusicDescriptonText, imageLink, relateds);
            musicViewPager.setAdapter(mMusicPagerAdapter);
            mIndicator.setViewPager(musicViewPager);
            startMusic(linkMusic);
        }
    }

    private void updateTitleToolbar() {
        mMusicTitleToolbar.setText(title);
        mMusicDescriptionToolbar.setText(mMusicDescriptonText);
    }

    public void musicLoading(boolean isLoading) {
        if (isLoading) {
            mMusicLoading.setVisibility(View.GONE);
            mMusicControllers.setVisibility(View.INVISIBLE);
        } else {
            mMusicLoading.setVisibility(View.GONE);
            mMusicControllers.setVisibility(View.VISIBLE);
        }

    }

    public enum MusicPlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    public interface OnRollingPlayerListener {
        void onRollingPlayer(boolean isRolling);
    }

    public interface OnShowPlayerListener {
        void onShowPlayer();
    }

    private class setBlurredAlbumArt extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            Drawable drawable = null;
            try {
                drawable = ImageUtils.createBlurredImageFromBitmap(loadedImage[0], MusicPlayerActivity.this, 6);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null) {
                if (mBlurredArt.getDrawable() != null) {
                    final TransitionDrawable td =
                            new TransitionDrawable(new Drawable[]{
                                    mBlurredArt.getDrawable(),
                                    result
                            });
                    mBlurredArt.setImageDrawable(td);
                    td.startTransition(200);

                } else {
                    mBlurredArt.setImageDrawable(result);
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

}
