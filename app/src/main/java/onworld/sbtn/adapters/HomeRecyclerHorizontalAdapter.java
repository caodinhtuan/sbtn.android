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
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.enums.HomeType;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.Utils;

/**
 * Created by linhnguyen on 12/26/15.
 */
public class HomeRecyclerHorizontalAdapter extends RecyclerView.Adapter<HomeRecyclerHorizontalAdapter.HorizontalViewHolder> {
    public static final int HOME_VIDEO_TYPE = 0;
    public static final int HOME_AUDIO_TYPE = 1;
    private Context context;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ArrayList<DataDetailItem> dataDetailItems;
    private HomeType homeType;

    public HomeRecyclerHorizontalAdapter(Context context, ArrayList<DataDetailItem> dataDetailItems, HomeType homeType) {
        this.context = context;
        this.dataDetailItems = dataDetailItems;
        this.homeType = homeType;
        if (homeType == HomeType.Audio) {
            options = ImageUtils.getOptionsImageSquare();
        } else {
            options = ImageUtils.getOptionsImageRectangle();
        }
        mInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == HOME_VIDEO_TYPE) {
            view = mInflater.inflate(R.layout.list_home_horizontal_item, parent, false);
        } else {
            view = mInflater.inflate(R.layout.list_home_horizontal_item_square, parent, false);
        }

        HorizontalViewHolder horizontalViewHolder = new HorizontalViewHolder(view);
        Utils.setRobotoConsiderBoldFont(context, horizontalViewHolder.title);
        return horizontalViewHolder;

    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder, int position) {
        DataDetailItem dataDetailItem = dataDetailItems.get(position);
        holder.title.setText(dataDetailItem.getName());
        imageLoader.displayImage(dataDetailItem.getImage(), holder.thumbnail, options);
        holder.packageType.setVisibility(View.VISIBLE);
        if (dataDetailItem.getPackage_type() == 0) {
            holder.packageType.setText("FREE");
        } else {
            holder.packageType.setText("PACKAGE");
            holder.packageType.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (homeType == HomeType.Audio) {
            return HOME_AUDIO_TYPE;
        } else {
            return HOME_VIDEO_TYPE;
        }

    }

    @Override
    public int getItemCount() {
        if (dataDetailItems != null) {
            return dataDetailItems.size();
        }
        return 0;
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView thumbnail;
        TextView title, packageType;


        public HorizontalViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail_home_item);
            title = (TextView) itemView.findViewById(R.id.title_home_item);
            packageType = (TextView) itemView.findViewById(R.id.package_home_type);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
