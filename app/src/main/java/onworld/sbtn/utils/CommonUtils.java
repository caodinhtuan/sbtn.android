package onworld.sbtn.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import onworld.sbtn.activities.MainActivity;
import onworld.sbtn.fragments.homes.OWLoginFragment;
import onworld.sbtn.security.AESEncryptDecrypt;

/**
 * Created by sb4 on 4/20/15.
 */
public class CommonUtils {

    public static String getAppVersion(Context context) {
        String versionName = "1.4.3";
        try {
            versionName = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            0).versionName;
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
        return versionName;
    }

    public static String getAccessTokenSecu(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.LOGIN_STATE, 0);
        String accessToken = sharedPreferences.getString(OWLoginFragment.TOKEN, "");
        return accessToken;
    }

    public static String getClientAppID(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String outputString = sharedPreferences.getString("clientAppID", "");
        String getResultString = "";
        byte[] decrypted = null;
        if (!outputString.equals("")) {
            byte[] byteAccessToken = Base64
                    .decode(outputString, Base64.NO_WRAP);
            try {
                AESEncryptDecrypt encrypter = new AESEncryptDecrypt(Constant.KEY);
                decrypted = encrypter.decrypt(byteAccessToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (decrypted != null) {
                getResultString = Base64.encodeToString(decrypted,
                        Base64.NO_WRAP);
            }

        }
        return getResultString;
    }
}
