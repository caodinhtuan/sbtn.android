package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import onworld.sbtn.utils.Utils;

/**
 * Created by onworldtv on 11/19/15.
 */
public class MyTextRobotoCondensed extends TextView {
    public MyTextRobotoCondensed(Context context) {
        super(context);
        init();
    }

    public MyTextRobotoCondensed(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextRobotoCondensed(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Utils.setRobotoConsiderRegularFont(getContext(), MyTextRobotoCondensed.this);
        /*post(new Runnable() {
            @Override
            public void run() {
                Utils.setRobotoConsiderRegularFont(getContext(), MyTextRobotoCondensed.this);
            }
        });*/
    }
}
