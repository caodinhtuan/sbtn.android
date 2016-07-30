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
 * Created by onworldtv on 6/23/16.
 */
public abstract class PackageChildView extends LinearLayout {
    public LinearLayout mRootView;
    public TextView packageChildValue, packageChildDescription;

    public PackageChildView(Context context) {
        super(context);
        initView();
    }

    public PackageChildView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PackageChildView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PackageChildView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void initView() {
        View.inflate(getContext(), R.layout.package_child_item_layout, this);
    }

    @Override
    protected void onAttachedToWindow() {
        initAction();
        super.onAttachedToWindow();
    }

    protected abstract void initAction();
}
