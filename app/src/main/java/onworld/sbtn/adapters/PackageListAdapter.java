package onworld.sbtn.adapters;

import android.content.Context;
import android.graphics.Paint;
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
import onworld.sbtn.josonmodel.GroupPackage;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.views.FlexibleDividerDecoration;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * Created by linhnguyen on 5/26/16.
 */
public class PackageListAdapter extends RecyclerView.Adapter<PackageListAdapter.PackageListViewHolder> implements FlexibleDividerDecoration.PaintProvider,
        FlexibleDividerDecoration.VisibilityProvider,
        HorizontalDividerItemDecoration.MarginProvider {

    private LayoutInflater mInflater;
    private ArrayList<GroupPackage> packageDetails = new ArrayList<>();
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public PackageListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        options = ImageUtils.getOptionsImageCircle(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public PackageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.package_list_item, parent, false);
        PackageListViewHolder packageViewHolder = new PackageListViewHolder(view);
        return packageViewHolder;
    }

    public void setPackageListData(ArrayList<GroupPackage> packageListData) {
        this.packageDetails = packageListData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(PackageListViewHolder holder, int position) {
        if (packageDetails != null && packageDetails.size() > 0) {
            GroupPackage groupPackage = packageDetails.get(position);
            holder.packageTitle.setText(groupPackage.getGroupName());
            holder.packageDescription.setText(groupPackage.getDescription());

            ArrayList<PackageDetail> packageDetails = groupPackage.getItems();
            PackageDetail packageDetail = packageDetails.get(0);

            imageLoader.displayImage(packageDetail.getImage(), holder.packageListThumb, options);
            if (packageDetail.getIsBuy() == true) {
                holder.packageButtonBuy.setVisibility(View.VISIBLE);
                holder.packageButtonBuy.setText(context.getResources().getString(R.string.package_title_button_bought));
            } else {
                holder.packageButtonBuy.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        if (packageDetails != null && packageDetails.size() > 0) {
            return packageDetails.size();
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
        return false;
    }

    public class PackageListViewHolder extends RecyclerView.ViewHolder {

        TextView packageTitle, packageDescription;
        ImageView packageListThumb;
        Button packageButtonBuy;

        public PackageListViewHolder(View itemView) {
            super(itemView);
            packageListThumb = (ImageView) itemView.findViewById(R.id.package_list_thumb);
            packageTitle = (TextView) itemView.findViewById(R.id.package_list_name);
            packageDescription = (TextView) itemView.findViewById(R.id.package_list_description);
            packageButtonBuy = (Button) itemView.findViewById(R.id.package_list_buy);
        }
    }
}
