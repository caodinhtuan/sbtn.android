package onworld.sbtn.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.adapters.HomeRecyclerHorizontalAdapter;
import onworld.sbtn.callbacks.LoginListener;
import onworld.sbtn.fragments.homes.HomeFragment;
import onworld.sbtn.helper.PreferenceHelper;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.enums.HomeType;

/**
 * Created by onworldtv on 4/27/16.
 */
public class HomeViewItem extends HomeView implements RecyclerView.OnItemTouchListener {

    LoginListener loginListener;
    private Context context;
    private ArrayList<DataDetailItem> dataDetailItems = new ArrayList<>();
    private HomeRecyclerHorizontalAdapter homeRecyclerHorizontalAdapter;
    private GestureDetector gestureDetector;
    private HomeType homeType;
    private String titleHeader;
    private MoreHomeViewClickListener mMoreHomeViewClickListener;
    private HomeViewItemClickListener homeViewItemClickListener;

    public HomeViewItem(Context context) {
        super(context);
        this.context = context;
    }

    public HomeViewItem(Context context, String titleHeader, ArrayList<DataDetailItem> dataDetailItems, HomeType homeType) {
        super(context);
        this.context = context;
        this.dataDetailItems = dataDetailItems;
        this.homeType = homeType;
        this.titleHeader = titleHeader;
    }

    @Override
    protected int getListItemHeight() {
        if (dataDetailItems.size() > 0) {
            int deviceWidth = PreferenceHelper.sharedInstance().getScreenWidth();
            int imageWidthPart = deviceWidth / 7;
            int imageWidth = 0;
            int calculatedHeight = 0;
            int finalHeight;
            if (homeType == HomeType.Audio) {
                imageWidth = imageWidthPart * 2;
                calculatedHeight = imageWidth;
                finalHeight = calculatedHeight + context.getResources().getInteger(R.integer.home_list_item_height_audio);
            } else {
                imageWidth = imageWidthPart * 3;
                calculatedHeight = imageWidth * 9 / 16;
                finalHeight = calculatedHeight + context.getResources().getInteger(R.integer.home_list_item_height_video);
            }

            return finalHeight;
        }
        return 0;
    }

    @Override
    protected void initViewAction() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        this.mListView.addOnItemTouchListener(this);
        this.homeHeader.setText(titleHeader);
        this.homeHeaderLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int type;
                if (homeType == HomeType.Audio) {
                    type = HomeFragment.AUDIO_TYPE;
                } else {
                    type = HomeFragment.VIDEO_TYPE;
                }

                if (homeMore.getVisibility() == VISIBLE) {
                    mMoreHomeViewClickListener.onMoreHomeClickListener(HomeViewItem.this, type, titleHeader, dataDetailItems);
                    /**/
                }

            }
        });
    }

    @Override
    protected void setUpRecyclerView() {
        homeRecyclerHorizontalAdapter = new HomeRecyclerHorizontalAdapter(getContext(), dataDetailItems, homeType);
        this.mListView.setAdapter(homeRecyclerHorizontalAdapter);
    }

    @Override
    protected void setUpHeaderView() {
        if (homeType == HomeType.Audio) {
            if (dataDetailItems.size() > 3) {
                this.homeMore.setVisibility(VISIBLE);
            } else {
                this.homeMore.setVisibility(GONE);
            }
        } else {
            if (dataDetailItems.size() > 2) {
                this.homeMore.setVisibility(VISIBLE);
            } else {
                this.homeMore.setVisibility(GONE);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = this.mListView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = this.mListView.getChildAdapterPosition(child);
            DataDetailItem dataDetailItem = dataDetailItems.get(position);
            homeViewItemClickListener.onHomeViewItemClickListener(dataDetailItem,homeType);

        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public void setMoreHomeViewClickListener(MoreHomeViewClickListener moreHomeViewClickListener) {
        mMoreHomeViewClickListener = moreHomeViewClickListener;
    }

    public void setHomeViewItemClickListener(HomeViewItemClickListener listener) {
        this.homeViewItemClickListener = listener;
    }

    public interface MoreHomeViewClickListener {
        void onMoreHomeClickListener(HomeViewItem homeViewItem, int type, String titleHeader, ArrayList<DataDetailItem> dataDetailItems);
    }

    public interface HomeViewItemClickListener {
        void onHomeViewItemClickListener(DataDetailItem dataDetailItem, HomeType homeType);
    }
}
