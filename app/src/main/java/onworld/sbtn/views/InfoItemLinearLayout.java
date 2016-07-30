package onworld.sbtn.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import onworld.sbtn.R;

/**
 * Created by onworldtv on 1/28/16.
 */
public abstract class InfoItemLinearLayout extends LinearLayout {
    TextView title;

    public InfoItemLinearLayout(Context context) {
        super(context);
        initView();
    }

    public InfoItemLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public InfoItemLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InfoItemLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    protected void initView() {
        View.inflate(getContext(), R.layout.info_item, this);
        this.title = (TextView) this.findViewById(R.id.video_info_genre);
    }

    @Override
    protected void onAttachedToWindow() {
        this.initData();
        super.onAttachedToWindow();
    }

    protected void initData() {
        this.initViewAction();
    }

    protected abstract void initViewAction();

    public void setInfoTitle(String title) {
        this.title.setText(title);
    }
}
