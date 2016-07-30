package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import onworld.sbtn.utils.Utils;

/**
 * Created by onworldtv on 11/19/15.
 */
public class MyTextRobotoCondensedBold extends TextView {
    public MyTextRobotoCondensedBold(Context context) {
        super(context);
        init();
    }

    public MyTextRobotoCondensedBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextRobotoCondensedBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        Utils.setRobotoConsiderBoldFont(getContext(), MyTextRobotoCondensedBold.this);
       /* post(new Runnable() {
            @Override
            public void run() {
                Utils.setRobotoConsiderBoldFont(getContext(), MyTextRobotoCondensedBold.this);
            }
        });*/
    }
}
