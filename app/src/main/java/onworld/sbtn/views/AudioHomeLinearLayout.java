package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import onworld.sbtn.helper.PreferenceHelper;

/**
 * Created by onworldtv on 1/12/16.
 */
public class AudioHomeLinearLayout extends RelativeLayout {

    public AudioHomeLinearLayout(Context context) {
        super(context);
    }

    public AudioHomeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        Init(context, attrs);
    }

    public AudioHomeLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Init(context, attrs);
    }

    private void Init(Context context, AttributeSet attrs) {
    }
    // **overrides**

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = PreferenceHelper.sharedInstance().getScreenWidth();
        int widthPart = originalWidth / 11;
        int width = widthPart*3;

        int calculatedHeight = width;

        int finalWidth, finalHeight;

        /*if (calculatedHeight > originalHeight) {
            finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight;
            finalHeight = originalHeight;
        } else {
            finalWidth = originalWidth;
            finalHeight = calculatedHeight;
        }*/
        finalWidth = width;
        finalHeight = calculatedHeight + 150;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}
