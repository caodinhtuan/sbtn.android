package onworld.sbtn.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.ASDataDetailItem;
import onworld.sbtn.josonmodel.TimeLine;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.views.FlexibleDividerDecoration;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * Created by onworldtv on 12/29/15.
 */
public class TimeLinesAdapter extends RecyclerView.Adapter<TimeLinesAdapter.HomeGridViewHolder> implements FlexibleDividerDecoration.PaintProvider,
        FlexibleDividerDecoration.VisibilityProvider,
        HorizontalDividerItemDecoration.MarginProvider {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<ASDataDetailItem> dataDetailItems = new ArrayList<>();
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private ArrayList<TimeLine> episodes;
    private boolean isVertical;

    public TimeLinesAdapter(final Context context, boolean isVertical) {
        this.context = context;
        this.isVertical = isVertical;
        mInflater = LayoutInflater.from(context);
        options = ImageUtils.getOptionsImageRectangle();
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));


    }

    @Override
    public HomeGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (isVertical == true) {
            view = mInflater.inflate(R.layout.as_list_vertical_item_layout, parent, false);
        } else {
            view = mInflater.inflate(R.layout.as_list_item_layout, parent, false);
        }

        HomeGridViewHolder homeGridViewHolder = new HomeGridViewHolder(view);
        return homeGridViewHolder;
    }

    public void setData(ArrayList<TimeLine> data) {
        this.episodes = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(HomeGridViewHolder holder, int position) {
        TimeLine episode = episodes.get(position);
        holder.title.setText(episode.getName());
        holder.timelineTime.setVisibility(View.VISIBLE);
        holder.timelineTime.setText(episode.getStart() + " - " + episode.getEnd());
        holder.timelineDay.setVisibility(View.VISIBLE);
        holder.timelineDay.setText(episode.getDayTitle());
        Utils.setRobotoConsiderBoldFont(context, holder.title);
        imageLoader.getInstance().displayImage(episode.getImage(), holder.thumbnail, options);

    }

    @Override
    public int getItemCount() {
        if (episodes != null) {
            return episodes.size();
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
        paint.setStrokeWidth(1);
        return paint;
    }

    @Override
    public boolean shouldHideDivider(int position, RecyclerView parent) {
        return false;
    }

    class HomeGridViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, timelineTime, timelineDay;

        public HomeGridViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail_home_grid_item);
            title = (TextView) itemView.findViewById(R.id.title_home_grid_item);
            timelineTime = (TextView) itemView.findViewById(R.id.time_home_grid_item);
            timelineDay = (TextView) itemView.findViewById(R.id.day_home_grid_item);
        }
    }
}
