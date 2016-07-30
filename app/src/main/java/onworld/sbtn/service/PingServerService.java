package onworld.sbtn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;

import java.util.Timer;
import java.util.TimerTask;

import onworld.sbtn.callbacks.EndUserViewDataLoadedListener;
import onworld.sbtn.tasks.TaskRequestEndUserView;
import onworld.sbtn.utils.URL;

/**
 * Created by onworldtv on 6/29/16.
 */
public class PingServerService extends Service implements EndUserViewDataLoadedListener {
    private static final String TAG = "PingServerService";
    private final Handler handlerTimerPing = new Handler();
    private IBinder mIBinder = new MyPingBinder();
    private Timer timerPing;
    private TimerTask timerTaskPing;
    private VideoCastManager mCastManager;

    @Override
    public void onCreate() {
        mCastManager = VideoCastManager.getInstance();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        servicePingserver();
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onEndUserDataLoaded(String jsonObject) {

    }

    private void startTimerPing() {
        stopTimerPing();
        timerPing = new Timer();
        initializeTimerPing();
        timerPing.schedule(timerTaskPing, 30000, 30000);


    }

    private void stopTimerPing() {
        stopUserViewing();
        mCastManager.isPing = false;
        if (timerPing != null) {
            timerPing.cancel();
            timerPing = null;
        }
        stopSelf();
    }

    private void initializeTimerPing() {
        timerTaskPing = new TimerTask() {
            @Override
            public void run() {
                handlerTimerPing.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!mCastManager.isPing && mCastManager.isConnected()) {
                                if (mCastManager.isRemoteMediaPlaying() || mCastManager.isRemoteMediaPaused()) {
                                    Log.e(TAG, "Ping Server");
                                    new TaskRequestEndUserView(PingServerService.this, URL.PING_SERVER).execute();
                                } else {
                                    stopTimerPing();
                                }
                            } else {
                                stopTimerPing();
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

    private void stopUserViewing() {
        new TaskRequestEndUserView(this, URL.MEMBER_END_VIEW).execute();

    }

    public void servicePingserver() {
        startTimerPing();
    }

    public void castStopPingServer() {
        stopTimerPing();
    }

    public class MyPingBinder extends Binder {
        public PingServerService getService() {
            return PingServerService.this;
        }
    }
}
