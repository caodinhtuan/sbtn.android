package onworld.sbtn.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.BannerItems;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.Shows;
import onworld.sbtn.views.FlexibleDividerDecoration;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * Created by onworldtv on 12/25/15.
 */
public class CategoryRecyclerVerticalApdater extends RecyclerView.Adapter<CategoryViewHolder> implements FlexibleDividerDecoration.PaintProvider,
        FlexibleDividerDecoration.VisibilityProvider,
        HorizontalDividerItemDecoration.MarginProvider {
    public static final int HOME_HORIZONTAL_LIST = 1;
    public static final int HOME_HORIZONTAL_LIST_SINGLE_LINE = 2;
    public static final int HOME_HORIZONTAL_LIST_DOUBLE_LINE = 3;
    public static final int HOME_LIST_FULL = 5;
    public static final int HOME_HORIZONTAL_HEADER = 0;
    private static final int HOME_HEADER_BANNER = 3;
    private String[] bannerLink;
    private ArrayList<BannerItems> bannerItemses = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Shows> showses = new ArrayList<>();
    private TextView rowTitle, more;
    private int showMode;
    private boolean isShowMore;


    public CategoryRecyclerVerticalApdater(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            Shows shows = showses.get(position);
            ArrayList<DataDetailItem> showsDetail = shows.getShowsDetails();
            int gridSize = showsDetail.size();
            if (gridSize <= 6) {
                isShowMore = false;
            } else {
                isShowMore = true;
            }
            return HOME_HORIZONTAL_HEADER;
        } else {
            Shows shows = showses.get(position);
            showMode = shows.getMode();
            ArrayList<DataDetailItem> showsDetail = shows.getShowsDetails();
            int gridSize = showsDetail.size();
            if (showMode == CategoryRecyclerHorizontalAdapter.CATEGORY_AUDIO_TYPE) {
                {
                    if (gridSize <= 3) {
                        return HOME_HORIZONTAL_LIST_SINGLE_LINE;
                    } else if (gridSize <= 6) {
                        return HOME_HORIZONTAL_LIST_DOUBLE_LINE;
                    } else {
                        return HOME_HORIZONTAL_LIST;
                    }
                }

            } else {
                {
                    if (gridSize <= 2) {
                        return HOME_HORIZONTAL_LIST_SINGLE_LINE;
                    } else if (gridSize <= 4) {
                        return HOME_HORIZONTAL_LIST_DOUBLE_LINE;
                    } else {
                        return HOME_HORIZONTAL_LIST;
                    }
                }

            }
        }
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == HOME_HORIZONTAL_HEADER) {
            view = inflater.inflate(R.layout.list_category_horizontal_header, parent, false);
            CategoryRecyclerHorizontalHeaderHolder homeRecyclerHorizontalHeaderHolder = new CategoryRecyclerHorizontalHeaderHolder(context, view, isShowMore);
            return homeRecyclerHorizontalHeaderHolder;
        } else {
            if (showMode == CategoryRecyclerHorizontalAdapter.CATEGORY_AUDIO_TYPE) {
                if (viewType == HOME_HORIZONTAL_LIST) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_tripple, parent, false);
                } else if (viewType == HOME_HORIZONTAL_LIST_SINGLE_LINE) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_single, parent, false);
                } else if (viewType == HOME_HORIZONTAL_LIST_DOUBLE_LINE) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_double, parent, false);
                } else if (viewType == HOME_LIST_FULL) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_tripple, parent, false);
                }
            } else {
                if (viewType == HOME_HORIZONTAL_LIST) {
                    view = inflater.inflate(R.layout.list_category_horizontal, parent, false);
                } else if (viewType == HOME_HORIZONTAL_LIST_SINGLE_LINE) {
                    view = inflater.inflate(R.layout.list_category_horizontal_single, parent, false);
                } else if (viewType == HOME_HORIZONTAL_LIST_DOUBLE_LINE) {
                    view = inflater.inflate(R.layout.list_category_horizontal_double, parent, false);
                } else if (viewType == HOME_LIST_FULL) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_tripple, parent, false);
                }
            }

            CategoryRecyclerHorizontalHolder homeRecyclerHorizontalHolder = new CategoryRecyclerHorizontalHolder(context, view, viewType);
            return homeRecyclerHorizontalHolder;
        }

    }

    public void setHomeVerticalData(ArrayList<Shows> showses) {
        this.showses = showses;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        if (showses == null && showses.size() == 0) {
            return;
        }
        holder.bindView(showses.get(position), isShowMore);

    }

    @Override
    public int getItemCount() {
        if (showses != null) {
            return showses.size();
        }
        return 0;
    }


    @Override
    public int dividerLeftMargin(int position, RecyclerView parent) {
        return 10;
    }

    @Override
    public int dividerRightMargin(int position, RecyclerView parent) {
        return 10;
    }

    @Override
    public Paint dividerPaint(int position, RecyclerView parent) {
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.drawer_divider_grey));
        paint.setStrokeWidth(2);
        return paint;
    }

    @Override
    public boolean shouldHideDivider(int position, RecyclerView parent) {
        if (position % 2 == 0) {
            return true;
        }
        return false;
    }
}
