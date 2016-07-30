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
import onworld.sbtn.josonmodel.Related;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.views.FlexibleDividerDecoration;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * Created by onworldtv on 12/29/15.
 */
public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.HomeGridViewHolder> implements FlexibleDividerDecoration.PaintProvider,
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
    private ArrayList<Related> relateds;

    public MusicListAdapter(final Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        options = ImageUtils.getOptionsImageRectangle();
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));


    }

    @Override
    public HomeGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = mInflater.inflate(R.layout.music_list_item_layout, parent, false);

        HomeGridViewHolder homeGridViewHolder = new HomeGridViewHolder(view);
        return homeGridViewHolder;
    }

    public void setData(ArrayList<Related> data) {
        this.relateds = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(HomeGridViewHolder holder, int position) {
        if (relateds != null) {
            int order = position + 1;
            Related related = relateds.get(position);
            holder.title.setText(related.getRelatedName());
            holder.order.setText("" + order);
            //imageLoader.getInstance().displayImage(related.getRelatedImage(), holder.thumbnail, options);
        }

    }

    @Override
    public int getItemCount() {
        if (relateds != null) {
            return relateds.size();
        }
        return 0;

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
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
        paint.setColor(context.getResources().getColor(R.color.music_list_divider));
        paint.setStrokeWidth(1);
        return paint;
    }

    @Override
    public boolean shouldHideDivider(int position, RecyclerView parent) {
        return false;
    }

    class HomeGridViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView description;
        TextView order;

        public HomeGridViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail_music_list_item);
            title = (TextView) itemView.findViewById(R.id.title_music_list_item);
            description = (TextView) itemView.findViewById(R.id.music_description);
            order = (TextView) itemView.findViewById(R.id.music_order_number);
        }
    }
}
