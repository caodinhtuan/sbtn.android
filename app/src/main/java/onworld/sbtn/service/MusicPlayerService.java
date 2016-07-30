package onworld.sbtn.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import java.io.IOException;

/**
 * Created by linhnguyen on 11/15/15.
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final String IS_SEEKING = "is seeking";
    public static final String SEEKING_POSITION = "seeking position";
    public static final String IS_START = "is start";
    public static final String LINK_MP3 = "link mp3";
    public static OnMusicCompleteListener onMusicCompleteListener;
    public static OnMusicLoopingListener onMusicLoopingListener;
    public static OnMusicStartListener onMusicStartListener;
    private static String LOG_TAG = "MusicService";
    MediaPlayer player;
    boolean isStart;
    Uri myUri = null;
    private PlaybackState mPlaybackState;
    private IBinder mBinder = new MyBinder();
    private double currentTime = 0;
    private double durationTime = 0;
    private String dataPath;
    private int seekbarPosition;
    private boolean isRepeat;

    public static void setOnMusicCompleteListener(OnMusicCompleteListener listener) {
        onMusicCompleteListener = listener;
    }

    public static void setOnMusicLoopingListener(OnMusicLoopingListener listener) {
        onMusicLoopingListener = listener;
    }
    public static void setOnMusicStartListener(OnMusicStartListener listener){
        onMusicStartListener = listener;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        myUri = Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/Download/MotNha.mp3");
        mPlaybackState = PlaybackState.IDLE;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

            isStart = intent.getBooleanExtra(IS_START, false);
            dataPath = intent.getStringExtra(LINK_MP3);
            if (isStart == true) {
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }
                mPlaybackState = PlaybackState.IDLE;
            }
            boolean isSeeking = intent.getBooleanExtra(IS_SEEKING, false);
            if (isSeeking == true) {
                mPlaybackState = PlaybackState.SEEKING;
            }

            if (mPlaybackState == PlaybackState.IDLE) {
                player = new MediaPlayer();
                try {
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(dataPath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.setVolume(100, 100);
                player.setLooping(false);
                player.prepareAsync();
                player.setOnPreparedListener(this);
            } else if (mPlaybackState == PlaybackState.PLAYING) {
                player.pause();
                mPlaybackState = PlaybackState.PAUSED;
            } else if (mPlaybackState == PlaybackState.PAUSED) {
                player.start();
                mPlaybackState = PlaybackState.PLAYING;
            } else if (mPlaybackState == PlaybackState.SEEKING) {
                seekbarPosition = intent.getIntExtra(SEEKING_POSITION, 0);
                player.seekTo(seekbarPosition);
                player.start();
                mPlaybackState = PlaybackState.PLAYING;

            }
        }

        return 1;
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        stopSelf();
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onMusicCompleteListener.onMusicComplete();
                if (getLooping()) {
                    onMusicLoopingListener.onMusicLooping();
                }
            }
        });
        mPlaybackState = PlaybackState.PLAYING;
        durationTime = mp.getDuration();
        onMusicStartListener.onMusicStart();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public double getDuaration() {
        return durationTime;
    }

    public boolean getLooping() {
        return isRepeat;
    }

    public void setLooping(boolean isRepeat) {
        this.isRepeat = isRepeat;
        if (isRepeat) {
            player.setLooping(true);
        } else {
            player.setLooping(false);
        }
    }

    public double getCurrentTime() {
        if (player.isPlaying()) {
            currentTime = player.getCurrentPosition();
            return currentTime;
        }
        return 0;
    }

    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE, SEEKING
    }

    public interface OnMusicCompleteListener {
        void onMusicComplete();
    }

    public interface OnMusicLoopingListener {
        void onMusicLooping();
    }
    public interface OnMusicStartListener {
        void onMusicStart();
    }

    public class MyBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }


}
