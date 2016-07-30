package onworld.sbtn.helper;

import android.content.Context;

import onworld.sbtn.security.PreferenceConnector;

/**
 */
public class PreferenceHelper {
    private static PreferenceHelper _sharedInstance;
    private Context _context;

    PreferenceHelper(Context context) {
        this._context = context;

        this.setScreenWidth(_context.getResources().getDisplayMetrics().widthPixels);
        this.setScreenHeight(_context.getResources().getDisplayMetrics().heightPixels);
    }

    public static PreferenceHelper sharedInstance() {
        return  _sharedInstance;
    }

    public static void instanceHelper(Context context) {
        if (_sharedInstance == null) {
            _sharedInstance = new PreferenceHelper(context);
        }
    }

    public void setScreenWidth(int width) {
        PreferenceConnector.writeInteger(_context, "ScreenWidth", width);
    }

    public int getScreenWidth() {
        return PreferenceConnector.readInteger(_context, "ScreenWidth", 320);
    }

    public void setScreenHeight(int height) {
        PreferenceConnector.writeInteger(_context, "ScreenHeight", height);
    }

    public int getScreenHeight() {
        return PreferenceConnector.readInteger(_context, "ScreenHeight", 800);
    }
}
