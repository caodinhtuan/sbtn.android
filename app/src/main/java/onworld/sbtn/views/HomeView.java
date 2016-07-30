package onworld.sbtn.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import onworld.sbtn.R;

/**
 * Created by onworldtv on 4/27/16.
 */
public abstract class HomeView extends LinearLayout {
    public LinearLayout mRootView, homeHeaderLayout;
    public RecyclerView mListView;
    public TextView homeHeader, homeMore;

    public HomeView(Context context) {
        super(context);
        this.initView();
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public HomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HomeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView();
    }

    protected void initView() {
        View.inflate(getContext(), R.layout.home_list_content_item, this);
        this.mRootView = (LinearLayout) this.findViewById(R.id.home_list_content_root);
        this.mListView = (RecyclerView) this.findViewById(R.id.home_list_content);
        this.homeHeader = (TextView) this.findViewById(R.id.home_header_title);
        this.homeMore = (TextView) this.findViewById(R.id.home_more);
        this.homeHeaderLayout = (LinearLayout) this.findViewById(R.id.home_header_layout);
        this.mListView.setHasFixedSize(true);
        this.mListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    }

    @Override
    protected void onAttachedToWindow() {
        this.initData();
        super.onAttachedToWindow();
    }

    protected abstract int getListItemHeight();

    private void initData() {
        int heightOfListItem = this.getListItemHeight();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, heightOfListItem);
        this.mRootView.updateViewLayout(mListView, layoutParams);
        this.initViewAction();
        this.setUpRecyclerView();
        this.setUpHeaderView();
    }

    public void setTextHeader(String textHeader) {
        this.homeHeader.setText(textHeader);
    }

    protected abstract void initViewAction();

    protected abstract void setUpRecyclerView();

    protected abstract void setUpHeaderView();
}
