package onworld.sbtn.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by sb4 on 4/20/15.
 */
public class DeviceUtils {

    public static String getOSVersion() {
        String osversion = android.os.Build.VERSION.RELEASE;
        return osversion;
    }

    public static String getSDKVersion() {
        @SuppressWarnings("deprecation")
        String sdkversion = android.os.Build.VERSION.SDK;
        return sdkversion;
    }

    public static String getModel() {
        String model = android.os.Build.MODEL;
        return model;
    }


    public static String getProductName() {
        String productname = android.os.Build.PRODUCT;
        return productname;
    }

    public static String getDeviceName() {
        String deviceName = Build.MANUFACTURER;
        return deviceName;
    }

    private static String getSerial() {
        String serial = "";
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null)
                    .toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return serial;
    }

    public static String getLocalIpAddress() {
        String result = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        result = inetAddress.getHostAddress();
                        int index = result.indexOf("%");
                        try {
                            result = result.substring(0, index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return result;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getUuid(Context context) {
        String uuid = Settings.Secure.getString(context
                        .getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        return uuid;
    }

    public static String getImei(Context context) {
        String imei = "";
        TelephonyManager mngr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = mngr.getDeviceId();
        return imei;
    }

    public static String getDeviceId(Context context) {
        String deviceImei = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceImei;
    }

    public static boolean isAmazonDevice() {
        return android.os.Build.MANUFACTURER.equals(Constant.AMAZON_DEVICE);
    }

    public static String getSimSerial(Context context) {
        String result = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();
        switch (simState) {

            case (TelephonyManager.SIM_STATE_ABSENT):
            case (TelephonyManager.SIM_STATE_NETWORK_LOCKED):
            case (TelephonyManager.SIM_STATE_PIN_REQUIRED):
            case (TelephonyManager.SIM_STATE_PUK_REQUIRED):
            case (TelephonyManager.SIM_STATE_UNKNOWN):
                break;
            case (TelephonyManager.SIM_STATE_READY): {
                result = telephonyManager.getSimSerialNumber();
            }
        }
        return result;
    }

    @SuppressLint("NewApi")
    public static String getDeviceName(Context context) {
        String userOwner = "";
        try {
            Cursor c = context
                    .getApplicationContext()
                    .getContentResolver()
                    .query(ContactsContract.Profile.CONTENT_URI, null, null,
                            null, null);
            c.moveToFirst();
            userOwner = c.getString(c.getColumnIndex("display_name"));
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userOwner;
    }

    public static String getCarrier(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        carrierName = carrierName.replace(" ", "");
        if (carrierName.equals("VietnamMobileTelecomServicesCompany"))
            carrierName = "Mobifone";
        return carrierName;
    }

    public static String getPlatform() {
        String platform;
        if (isAmazonDevice()) {
            platform = Constant.AMAZON_PLATFORM;
        } else {
            platform = Constant.ANDROID_PLATFORM;
        }
        return platform;
    }

    public static String getPhoneNumber(Context context) {
        TelephonyManager tMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    public static String getUserAgent(Context context) {
        String userAgent = "OWTVMobile/" + CommonUtils.getAppVersion(context) + " (" + getPlatform()
                + ";" + getOSVersion() + ";" + getModel() + ";" + getCarrier(context)
                + ")";
        return userAgent;
    }

}
