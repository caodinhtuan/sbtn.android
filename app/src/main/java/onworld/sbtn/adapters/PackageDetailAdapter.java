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
import onworld.sbtn.utils.ImageUtils;

/**
 * Created by linhnguyen on 5/27/16.
 */
public class PackageDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER_TYPE = 0;
    private static final int BODY_TYPE = 1;
    private LayoutInflater mInflater;
    private ArrayList<DataDetailItem> mDataDetailItems = new ArrayList<>();
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public PackageDetailAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        options = ImageUtils.getOptionsImageRectangle();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER_TYPE) {
            view = mInflater.inflate(R.layout.package_detail_header_layout, parent, false);
            return new PackageDetailHeaderHolder(view);
        } else {
            view = mInflater.inflate(R.layout.as_category_item_layout, parent, false);
            return new PackageDetailBodyHolder(view);
        }
    }

    public void setPackageDetailData(ArrayList<DataDetailItem> data) {
        this.mDataDetailItems = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PackageDetailHeaderHolder) {

        } else if (holder instanceof PackageDetailBodyHolder) {
            if (mDataDetailItems != null && mDataDetailItems.size() > 0) {
                PackageDetailBodyHolder packageDetailBodyHolder = (PackageDetailBodyHolder) holder;
                DataDetailItem dataDetailItem = mDataDetailItems.get(position);
                imageLoader.displayImage(dataDetailItem.getImage(), packageDetailBodyHolder.packageContentThumb, options);
                packageDetailBodyHolder.packageContentTitle.setText(dataDetailItem.getName());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mDataDetailItems != null && mDataDetailItems.size() > 0) {
            return mDataDetailItems.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TYPE;
        } else {
            return BODY_TYPE;
        }
    }

    public class PackageDetailHeaderHolder extends RecyclerView.ViewHolder {

        public PackageDetailHeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public class PackageDetailBodyHolder extends RecyclerView.ViewHolder {
        ImageView packageContentThumb;
        TextView packageContentTitle;

        public PackageDetailBodyHolder(View itemView) {
            super(itemView);
            packageContentThumb = (ImageView) itemView.findViewById(R.id.thumbnail_home_grid_item);
            packageContentTitle = (TextView) itemView.findViewById(R.id.title_home_grid_item);
        }
    }
}
