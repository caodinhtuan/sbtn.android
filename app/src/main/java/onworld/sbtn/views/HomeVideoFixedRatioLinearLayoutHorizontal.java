package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import onworld.sbtn.helper.PreferenceHelper;

/**
 * Created by onworldtv on 1/12/16.
 */
public class HomeVideoFixedRatioLinearLayoutHorizontal extends RelativeLayout {

    public HomeVideoFixedRatioLinearLayoutHorizontal(Context context) {
        super(context);
    }

    public HomeVideoFixedRatioLinearLayoutHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);

        Init(context, attrs);
    }

    public HomeVideoFixedRatioLinearLayoutHorizontal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Init(context, attrs);
    }

    private void Init(Context context, AttributeSet attrs) {
    }
    // **overrides**

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalWidth, finalHeight;
        int originalWidth = PreferenceHelper.sharedInstance().getScreenWidth();
        int imageWidth = originalWidth / 7;
        finalWidth = imageWidth * 3;

        int calculatedHeight = finalWidth * 9 / 16;

        finalHeight = calculatedHeight + 88;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}
