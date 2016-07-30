package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import onworld.sbtn.helper.PreferenceHelper;

/**
 * Created by onworldtv on 1/12/16.
 */
public class FixedTwoItemLinearLayout extends RelativeLayout {

    public FixedTwoItemLinearLayout(Context context) {
        super(context);
    }

    public FixedTwoItemLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        Init(context, attrs);
    }

    public FixedTwoItemLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Init(context, attrs);
    }

    private void Init(Context context, AttributeSet attrs) {
    }
    // **overrides**

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = PreferenceHelper.sharedInstance().getScreenWidth();
        int imageWidth = originalWidth / 2;

        int calculatedHeight = imageWidth * 9 / 16 + 150;

        int finalWidth, finalHeight;

        /*if (calculatedHeight > originalHeight) {
            finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight;
            finalHeight = originalHeight;
        } else {
            finalWidth = originalWidth;
            finalHeight = calculatedHeight;
        }*/
        finalWidth = imageWidth - 20;
        finalHeight = calculatedHeight;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}
