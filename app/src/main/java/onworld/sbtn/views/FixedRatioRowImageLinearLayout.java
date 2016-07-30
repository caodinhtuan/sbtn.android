package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by onworldtv on 1/12/16.
 */

public class FixedRatioRowImageLinearLayout extends LinearLayout {

    public FixedRatioRowImageLinearLayout(Context context) {
        super(context);
    }

    public FixedRatioRowImageLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        Init(context, attrs);
    }

    public FixedRatioRowImageLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Init(context, attrs);
    }

    private void Init(Context context, AttributeSet attrs) {
    }
    // **overrides**

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalWidth, finalHeight;


        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int imageWidth = originalWidth / 7;
        finalWidth = imageWidth * 3;
        int calculatedHeight = finalWidth * 9 / 16;


        finalHeight = calculatedHeight + 90;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}
