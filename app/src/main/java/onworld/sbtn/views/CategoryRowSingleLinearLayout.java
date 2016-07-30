package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by onworldtv on 1/12/16.
 */
public class CategoryRowSingleLinearLayout extends LinearLayout {

    public CategoryRowSingleLinearLayout(Context context) {
        super(context);
    }

    public CategoryRowSingleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        Init(context, attrs);
    }

    public CategoryRowSingleLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Init(context, attrs);
    }

    private void Init(Context context, AttributeSet attrs) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int imageWidth = originalWidth;

        int calculatedHeight = imageWidth * 9 / 32 + 100;

        int finalWidth, finalHeight;

        finalWidth = imageWidth;
        finalHeight = calculatedHeight;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}
