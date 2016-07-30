package onworld.sbtn.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.RequestQueue;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import onworld.sbtn.activities.MainActivity;
import onworld.sbtn.fragments.homes.OWLoginFragment;
import onworld.sbtn.volley.Requestor;

/**
 * Created by linhnguyen on 12/7/15.
 */
public class VotingUtils {
    public static final int REQUEST_SUCCESS = 50001;
    public static final int REQUEST_DEVICE_VOTED = 50024;
    public static final int REQUEST_TIMEOUT = 30000;
    public static ProgressDialog dialog;

    public static boolean checkLoginStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.LOGIN_STATE, context.MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean(OWLoginFragment.IS_LOGIN, false);
        String token = sharedPreferences.getString(OWLoginFragment.TOKEN, "");
        if (token.length() != 0 && isLogin == true) {
            return true;
        } else {
            return false;
        }
    }

    public static void showDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait..");
        dialog.setCancelable(false);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public static void hideDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static String getData(RequestQueue requestQueue, String url) {
        String response = Requestor.requestDataJson(requestQueue, url);
        return response;
    }

    public static String updateImageUrl(String oldUrl, String newImageSize) {
        if (TextUtils.isEmpty(oldUrl) == false) {
            oldUrl = oldUrl.replace("wxh", newImageSize);
        }
        return oldUrl;
    }

    public static String reformatDouble(double number) {
        NumberFormat numberFormat = new DecimalFormat("#.##");
        String output = numberFormat.format(number);
        return output;
    }

    public static void showAlertWithMessage(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("CLOSE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
