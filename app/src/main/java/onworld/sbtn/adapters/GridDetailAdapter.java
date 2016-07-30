package onworld.sbtn.adapters;

import android.content.Context;
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
import onworld.sbtn.fragments.homes.HomeFragment;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.Utils;

/**
 * Created by onworldtv on 12/29/15.
 */
public class GridDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TITLE_TYPE = 0;
    private static final int BODY_TYPE = 1;
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<DataDetailItem> dataDetailItems = new ArrayList<>();
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int type;
    private String title;

    public GridDetailAdapter(final Context context, RecyclerView recyclerView, String title, int type) {
        this.context = context;
        this.type = type;
        this.title = title;
        mInflater = LayoutInflater.from(context);


    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TITLE_TYPE) {
            view = mInflater.inflate(R.layout.detail_grid_header_layout, parent, false);
            return new TitleViewHolder(view);
        } else if (type == HomeFragment.AUDIO_TYPE) {
            view = mInflater.inflate(R.layout.as_grid_audio_item_layout, parent, false);
            return new HomeGridViewHolder(view);
        } else {
            view = mInflater.inflate(R.layout.as_grid_item_layout, parent, false);
            return new HomeGridViewHolder(view);
        }

    }

    public void setData(ArrayList<DataDetailItem> data, String title, int type) {
        this.dataDetailItems = data;
        this.type = type;
        this.title = title;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleViewHolder) {
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            titleViewHolder.title.setText(title);
        } else {
            DataDetailItem dataDetailItem = dataDetailItems.get(position - 1);
            HomeGridViewHolder homeGridViewHolder = (HomeGridViewHolder) holder;
            homeGridViewHolder.title.setText(dataDetailItem.getName());
            if (type == HomeFragment.AUDIO_TYPE) {
                homeGridViewHolder.title.setMaxLines(2);
                options = ImageUtils.getOptionsImageSquare();
            } else {
                options = ImageUtils.getOptionsImageRectangle();
            }
            homeGridViewHolder.packageDetailType.setVisibility(View.VISIBLE);
            if (dataDetailItem.getPackage_type() == 0) {
                homeGridViewHolder.packageDetailType.setText("FREE");
            } else {
                homeGridViewHolder.packageDetailType.setText("PACKAGE");
                homeGridViewHolder.packageDetailType.setVisibility(View.GONE);
            }
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            Utils.setRobotoConsiderBoldFont(context, homeGridViewHolder.title);
            String image = dataDetailItem.getImage();

            imageLoader.displayImage(image, homeGridViewHolder.thumbnail, options);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TITLE_TYPE;
        } else {
            return BODY_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if (dataDetailItems != null) {
            return dataDetailItems.size() + 1;
        }
        return 0;

    }

    class HomeGridViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, packageDetailType;

        public HomeGridViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail_home_grid_item);
            title = (TextView) itemView.findViewById(R.id.title_home_grid_item);
            packageDetailType = (TextView) itemView.findViewById(R.id.package_detail_type);
        }
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.detail_grid_title_id);
        }
    }
}
