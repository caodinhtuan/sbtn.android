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
import onworld.sbtn.josonmodel.Related;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.Utils;

/**
 * Created by onworldtv on 12/29/15.
 */
public class HomeGridAdapter extends RecyclerView.Adapter<HomeGridAdapter.HomeGridViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ArrayList<Related> relateds;

    public HomeGridAdapter(final Context context, RecyclerView recyclerView) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        options = ImageUtils.getOptionsImageRectangle();
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));


    }

    @Override
    public HomeGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.as_grid_item_layout, parent, false);
        HomeGridViewHolder homeGridViewHolder = new HomeGridViewHolder(view);
        return homeGridViewHolder;
    }

    public void setData(ArrayList<Related> data) {
        this.relateds = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(HomeGridViewHolder holder, int position) {
        Related related = relateds.get(position);
        holder.title.setText(related.getRelatedName());
        Utils.setRobotoConsiderBoldFont(context, holder.title);
        imageLoader.getInstance().displayImage(related.getRelatedImage(), holder.thumbnail, options);
        holder.packageRelatedType.setVisibility(View.VISIBLE);
        if (related.getPackage_type() == 0) {
            holder.packageRelatedType.setText("FREE");
        } else {
            holder.packageRelatedType.setText("PACKAGE");
            holder.packageRelatedType.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (relateds != null) {
            return relateds.size();
        }
        return 0;

    }

    class HomeGridViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, packageRelatedType;

        public HomeGridViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail_home_grid_item);
            title = (TextView) itemView.findViewById(R.id.title_home_grid_item);
            packageRelatedType = (TextView) itemView.findViewById(R.id.package_detail_type);
        }
    }
}
