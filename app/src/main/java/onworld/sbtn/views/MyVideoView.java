package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import onworld.sbtn.MyApplication;

/**
 * Created by onworldtv on 1/12/16.
 */
public class MyVideoView extends VideoView {

    public MyVideoView(Context context) {
        super(MyApplication.getAppContext());
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(MyApplication.getAppContext(), attrs);

        Init(MyApplication.getAppContext(), attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(MyApplication.getAppContext(), attrs, defStyle);

        Init(MyApplication.getAppContext(), attrs);
    }

    private void Init(Context context, AttributeSet attrs) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int calculatedHeight = originalWidth * 9 / 16;

        int finalWidth, finalHeight;

        finalWidth = originalWidth;
        finalHeight = calculatedHeight;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}
