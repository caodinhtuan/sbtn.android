package onworld.sbtn.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.cast.companionlibrary.cast.exceptions.CastException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import onworld.sbtn.R;

public class Utils {
    public static final int OLD_REQUEST_SUCCESS = 0;

    public static Utils Singleton = new Utils();
    private static Typeface typeFaceRobotoConsiderBoldFont;
    private static Typeface typeFaceRobotoConsiderRegularFont;
    private static Typeface typeFaceLobsterRegularFont;

    private static Object mLock = new Object();

    /*
     * Making sure public utility methods remain static
     */
    private Utils() {
    }

    public static void setLobsterRegularFont(Context context, TextView textView) {
        synchronized (mLock) {
            if (typeFaceLobsterRegularFont == null)
                typeFaceLobsterRegularFont = Typeface.createFromAsset(context.getAssets(), "Lobster-Regular.ttf");
            textView.setTypeface(typeFaceLobsterRegularFont);
        }
    }

    public static void setRobotoConsiderRegularFont(Context context, TextView textView) {
        synchronized (mLock) {
            if (typeFaceRobotoConsiderRegularFont == null)
                typeFaceRobotoConsiderRegularFont = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
            textView.setTypeface(typeFaceRobotoConsiderRegularFont);
        }
    }

    public static void setRobotoConsiderBoldFont(Context context, TextView textView) {
        synchronized (mLock) {
            if (typeFaceRobotoConsiderBoldFont == null)
                typeFaceRobotoConsiderBoldFont = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Bold.ttf");
            textView.setTypeface(typeFaceRobotoConsiderBoldFont);
        }
    }

    public static String getDate(String date) {
        Date dateTime = null;
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateTime = sdfDate.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sdfDate.format(dateTime);
    }

    public static long getFileSizeInBytes(String fileName) {
        long ret = 0;
        File f = new File(fileName);
        if (f.isFile()) {
            return f.length();
        } else if (f.isDirectory()) {
            File[] contents = f.listFiles();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i].isFile()) {
                    ret += contents[i].length();
                } else if (contents[i].isDirectory()) {
                    ret += getFileSizeInBytes(contents[i].getPath());
                }
            }
        }
        return ret;
    }

    public static Point getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        return new Point(width, height);
    }

    /**
     * Returns {@code true} if and only if the screen orientation is portrait.
     */
    public static boolean isOrientationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * Shows an error dialog with a given text message.
     */
    public static void showErrorDialog(Context context, String errorString) {
        new AlertDialog.Builder(context).setTitle(R.string.error)
                .setMessage(errorString)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    /**
     * Shows an "Oops" error dialog with a text provided by a resource ID
     */
    public static void showOopsDialog(Context context, int resourceId) {
        new AlertDialog.Builder(context).setTitle(R.string.oops)
                .setMessage(context.getString(resourceId))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_action_alerts_and_states_warning)
                .create()
                .show();
    }

    public static void showUpdateNewVersionDialog(final Context context) {
        new AlertDialog.Builder(context).setTitle(R.string.title_update_new_app_version)
                .setMessage(context.getString(R.string.msg_force_update_new_app_version))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL.GOOGLE_PLAY_APP_LINK));
                        context.startActivity(browserIntent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_action_alerts_and_states_warning)
                .create()
                .show();
    }

    public static void handleException(Context context, Exception e) {
        int resourceId;
        if (e instanceof TransientNetworkDisconnectionException) {
            // temporary loss of connectivity
            resourceId = R.string.connection_lost_retry;

        } else if (e instanceof NoConnectionException) {
            // connection gone
            resourceId = R.string.connection_lost;
        } else if (e instanceof RuntimeException ||
                e instanceof IOException ||
                e instanceof CastException) {
            // something more serious happened
            resourceId = R.string.failed_to_perform_action;
        } else {
            // well, who knows!
            resourceId = R.string.failed_to_perform_action;
        }
        onworld.sbtn.utils.Utils.showOopsDialog(context, resourceId);
    }

    /**
     * Gets the version of app.
     */
    public static String getAppVersionName(Context context) {
        String versionString = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0 /* basic info */);
            versionString = info.versionName;
        } catch (Exception e) {
            // do nothing
        }
        return versionString;
    }

    /**
     * Shows a (long) toast.
     */
    public static void showToast(Context context, int resourceId) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
    }

    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
        final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    public static SharedPreferences getSharedPreferences(String preferenceName) {
        SharedPreferences sharedPreferences = getSharedPreferences(preferenceName);
        return sharedPreferences;
    }

    public static void alert(Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context).create();

        //alertDialog.setTitle(message);

        alertDialog.setMessage(message);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        alertDialog.show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public int parseTime(String time) {
        try {
            if (time.equals("") || time.equals("00:00:00")) return 0;
            String[] arr_time = time.split(":");
            if (arr_time.length == 3) {
                int hours = Utils.Singleton.parseInteger(arr_time[0]);
                int minutes = Utils.Singleton.parseInteger(arr_time[1]);
                int seconds = Utils.Singleton.parseInteger(arr_time[2]);
                return hours * 60 * 60 + minutes * 60 + seconds;
            } else if (arr_time.length == 2) {
                int minutes = Utils.Singleton.parseInteger(arr_time[0]);
                int seconds = Utils.Singleton.parseInteger(arr_time[1]);
                return minutes * 60 + seconds;
            }
        } catch (Exception ex) {
        }
        return 0;
    }

    public int parseInteger(String data) {
        try {
            return Integer.parseInt(data);
        } catch (Exception ex) {
        }
        return 0;
    }
}
