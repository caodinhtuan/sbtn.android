package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by onworldtv on 1/12/16.
 */
public class FixedAspectRatioLinearLayout extends RelativeLayout {

    public FixedAspectRatioLinearLayout(Context context) {
        super(context);
    }

    public FixedAspectRatioLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        Init(context, attrs);
    }

    public FixedAspectRatioLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Init(context, attrs);
    }

    private void Init(Context context, AttributeSet attrs) {
    }
    // **overrides**

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //int originalWidth = PreferenceHelper.sharedInstance().getScreenWidth();
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int calculatedHeight = originalWidth * 7 / 16;

        int finalWidth, finalHeight;
        finalWidth = originalWidth;
        finalHeight = calculatedHeight;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));

    }
}
