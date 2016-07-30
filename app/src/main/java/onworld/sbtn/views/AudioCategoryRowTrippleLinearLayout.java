package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by onworldtv on 1/12/16.
 */
public class AudioCategoryRowTrippleLinearLayout extends LinearLayout {

    public AudioCategoryRowTrippleLinearLayout(Context context) {
        super(context);
    }

    public AudioCategoryRowTrippleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        Init(context, attrs);
    }

    public AudioCategoryRowTrippleLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Init(context, attrs);
    }

    private void Init(Context context, AttributeSet attrs) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int calculatedHeight = originalWidth / 3;

        int finalWidth, finalHeight;

        finalWidth = originalWidth;
        finalHeight = calculatedHeight * 3 + 450;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}
