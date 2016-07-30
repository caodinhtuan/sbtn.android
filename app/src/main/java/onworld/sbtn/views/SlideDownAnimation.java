package onworld.sbtn.views;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import onworld.sbtn.R;

/**
 * Created by linhnguyen on 11/14/15.
 */
public class SlideDownAnimation {
    public static void slideDownAnimation(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        if (animation != null) {
            animation.reset();
            if (view != null) {
                view.clearAnimation();
                view.startAnimation(animation);
            }
        }
    }
}