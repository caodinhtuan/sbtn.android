package onworld.sbtn.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.Shows;

/**
 * Created by onworldtv on 12/25/15.
 */
public class CategoryRecyclerHorizontalHolder extends CategoryViewHolder {
    private RecyclerView horizontalRecyclerView;
    private CategoryRecyclerHorizontalAdapter homeRecyclerHorizontalAdapter;
    private Context context;
    private int type;

    public CategoryRecyclerHorizontalHolder(Context context, View itemView, int type) {
        super(itemView);
        this.context = context;
        this.type = type;
        horizontalRecyclerView = (RecyclerView) itemView.findViewById(R.id.home_horizontal_recyclerView);

    }

    @Override
    public void bindView(Shows shows, boolean isShowMore) {
        horizontalRecyclerView.setHasFixedSize(true);
        if (type == CategoryRecyclerVerticalApdater.HOME_HORIZONTAL_LIST) {
            horizontalRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 3, LinearLayoutManager.HORIZONTAL, false));
        } else if (type == CategoryRecyclerVerticalApdater.HOME_HORIZONTAL_LIST_SINGLE_LINE) {
            horizontalRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1, LinearLayoutManager.HORIZONTAL, false));
        } else if (type == CategoryRecyclerVerticalApdater.HOME_HORIZONTAL_LIST_DOUBLE_LINE) {
            horizontalRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2, LinearLayoutManager.HORIZONTAL, false));
        } else {
            horizontalRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2));
        }

        ArrayList<DataDetailItem> showsDetails = shows.getShowsDetails();
        int showMode = shows.getMode();
        homeRecyclerHorizontalAdapter = new CategoryRecyclerHorizontalAdapter(context, showMode, showsDetails);
        horizontalRecyclerView.setAdapter(homeRecyclerHorizontalAdapter);
    }

    public int getChildItemPosition(float x, float y) {
        View child = horizontalRecyclerView.findChildViewUnder(x, y);
        if (child != null) {
            return horizontalRecyclerView.getChildAdapterPosition(child);
        }
        return -1;
    }

}
