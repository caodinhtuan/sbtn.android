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
import onworld.sbtn.utils.Utils;

/**
 * Created by linhnguyen on 12/26/15.
 */
public class CategoryRecyclerHorizontalAdapter extends RecyclerView.Adapter<CategoryRecyclerHorizontalAdapter.HorizontalViewHolder> {
    public static final int CATEGORY_VIDEO_TYPE = 1;
    public static final int CATEGORY_AUDIO_TYPE = 0;
    private Context context;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ArrayList<DataDetailItem> showsDetails;
    private int showMode;


    public CategoryRecyclerHorizontalAdapter(Context context, int showMode, ArrayList<DataDetailItem> showsDetails) {
        this.context = context;
        this.showsDetails = showsDetails;
        this.showMode = showMode;
        mInflater = LayoutInflater.from(context);
        if (showMode == 0) {
            options = ImageUtils.getOptionsImageSquare();
        } else {
            options = ImageUtils.getOptionsImageRectangle();
        }

        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == CATEGORY_AUDIO_TYPE) {
            view = mInflater.inflate(R.layout.as_category_audio_item_layout, parent, false);
            HorizontalViewHolder horizontalViewHolder = new HorizontalViewHolder(view);
            return horizontalViewHolder;
        } else {
            view = mInflater.inflate(R.layout.as_category_item_layout, parent, false);
            HorizontalViewHolder horizontalViewHolder = new HorizontalViewHolder(view);
            return horizontalViewHolder;
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (showMode == 0) {
            return CATEGORY_AUDIO_TYPE;
        } else {
            return CATEGORY_VIDEO_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder, int position) {
        DataDetailItem showsDetail = showsDetails.get(position);
        holder.title.setText(showsDetail.getName());
        Utils.setRobotoConsiderBoldFont(context, holder.title);
        imageLoader.getInstance().displayImage(showsDetail.getImage(), holder.thumbnail, options);
        holder.packageCategoryType.setVisibility(View.VISIBLE);
        if (showsDetail.getPackage_type() == 0) {
            holder.packageCategoryType.setText("FREE");
        } else {
            holder.packageCategoryType.setText("PACKAGE");
            holder.packageCategoryType.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        if (showsDetails != null) {
            int count = showsDetails.size();
            return count;
        }
        return 0;
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView thumbnail;
        TextView title, packageCategoryType;


        public HorizontalViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail_home_grid_item);
            title = (TextView) itemView.findViewById(R.id.title_home_grid_item);
            packageCategoryType = (TextView) itemView.findViewById(R.id.package_category_type);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
