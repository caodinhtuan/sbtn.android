package onworld.sbtn.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.utils.ImageUtils;

/**
 * Created by onworldtv on 6/24/16.
 */
public class PackagePurchasedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER_TYPE = 0;
    private static final int BODY_TYPE = 1;
    private Context context;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ArrayList<PackageDetail> mPackageDetails = new ArrayList<>();

    public PackagePurchasedListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        options = ImageUtils.getOptionsImageCircle(context);
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER_TYPE) {
            view = mInflater.inflate(R.layout.purchased_package_header_layout, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == BODY_TYPE) {
            view = mInflater.inflate(R.layout.purchased_package_extras_layout, parent, false);
            return new ChildBodyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PackageDetail packageDetail = mPackageDetails.get(position);
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.title.setText(packageDetail.getName());
            headerViewHolder.childDesription.setText(packageDetail.getDescription());
            imageLoader.displayImage(packageDetail.getImage(), headerViewHolder.thumb, options);
        } else if (holder instanceof ChildBodyViewHolder) {
            ChildBodyViewHolder childBodyViewHolder = (ChildBodyViewHolder) holder;
            childBodyViewHolder.childExtrasDescription.setText(packageDetail.getDescription());
            childBodyViewHolder.extrasNumber.setText("$" + packageDetail.getPrice());
            if (packageDetail.getIsBuy()) {
                childBodyViewHolder.extrasNumber.setBackgroundResource(R.drawable.package_purchase_shape_grey);
                childBodyViewHolder.extrasNumber.setClickable(false);
            } else {

            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mPackageDetails.get(position).isHeader()) {
            return HEADER_TYPE;
        } else {
            return BODY_TYPE;
        }

    }

    @Override
    public int getItemCount() {
        if (mPackageDetails != null && mPackageDetails.size() > 0) return mPackageDetails.size();
        return 0;
    }

    public void setData(ArrayList<PackageDetail> packageDetails) {
        this.mPackageDetails = packageDetails;
        notifyDataSetChanged();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView title, childDesription;
        private ImageView thumb;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.purchased_package_header_child_title);
            childDesription = (TextView) itemView.findViewById(R.id.purchased_package_header_child_description);
            thumb = (ImageView) itemView.findViewById(R.id.purchased_package_header_child_thumb);
        }
    }

    public class ChildBodyViewHolder extends RecyclerView.ViewHolder {
        private TextView childExtrasDescription;
        private ImageView thumb;
        private Button extrasNumber;

        public ChildBodyViewHolder(View itemView) {
            super(itemView);
            extrasNumber = (Button) itemView.findViewById(R.id.purchased_package_extras_number);
            childExtrasDescription = (TextView) itemView.findViewById(R.id.purchased_package_extras_description);

        }
    }
}
