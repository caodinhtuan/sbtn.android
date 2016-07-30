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

package onworld.sbtn;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.player.VideoCastController;
import com.google.android.libraries.cast.companionlibrary.cast.player.VideoCastControllerActivity;

import onworld.sbtn.helper.PreferenceHelper;
import onworld.sbtn.utils.DeviceUtils;

/**
 * The {@link Application} for this demo application.
 */
public class MyApplication extends Application {

    public static final double VOLUME_INCREMENT = 0.05;
    public static final String TAG = MyApplication.class.getSimpleName();
    private static Context appContext;
    private static String userAgent = "";
    private static MyApplication mInstance;
    private RequestQueue mRequestQueue;

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public static String getUserAgent() {
        if (userAgent == null || userAgent.equals(""))
            userAgent = DeviceUtils.getUserAgent(appContext);

        return userAgent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        PreferenceHelper.instanceHelper(getApplicationContext());
        appContext = getApplicationContext();
        mInstance = this;
        //String applicationId = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;
        String applicationId = getResources().getString(R.string.cast_application_id);
        // initialize VideoCastManager
        VideoCastManager.
                initialize(this, applicationId, VideoCastControllerActivity.class, null).
                setVolumeStep(VOLUME_INCREMENT).
                enableFeatures(VideoCastManager.FEATURE_NOTIFICATION |
                        VideoCastManager.FEATURE_LOCKSCREEN |
                        VideoCastManager.FEATURE_WIFI_RECONNECT |
                        VideoCastManager.FEATURE_CAPTIONS_PREFERENCE |
                        VideoCastManager.FEATURE_DEBUGGING);

        // this is the default behavior but is mentioned to make it clear that it is configurable.
        VideoCastManager.getInstance().setNextPreviousVisibilityPolicy(
                VideoCastController.NEXT_PREV_VISIBILITY_POLICY_DISABLED);
    }

    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getmRequestQueue().add(req);
    }
}
