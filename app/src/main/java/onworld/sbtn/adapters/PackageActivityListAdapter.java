package onworld.sbtn.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.Utils;

/**
 * Created by onworldtv on 6/24/16.
 */
public class PackageActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER_TYPE = 0;
    private static final int BODY_TYPE = 1;
    private Context context;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ArrayList<PackageDetail> mPackageDetails = new ArrayList<>();
    private PackageActivityBuyPackageListener mBuyPackageListener;

    public PackageActivityListAdapter(Context context) {
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
            view = mInflater.inflate(R.layout.package_activity_list_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == BODY_TYPE) {
            view = mInflater.inflate(R.layout.package_child_item_layout, parent, false);
            return new ChildBodyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PackageDetail packageDetail = mPackageDetails.get(position);
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.title.setText(packageDetail.getGroupName());
            headerViewHolder.description.setText(packageDetail.getDescription());
            if(position == 0){
                headerViewHolder.divider.setVisibility(View.GONE);
            }else {
                headerViewHolder.divider.setVisibility(View.VISIBLE);
            }
        } else if (holder instanceof ChildBodyViewHolder) {
            final ChildBodyViewHolder childBodyViewHolder = (ChildBodyViewHolder) holder;

            childBodyViewHolder.title.setText(packageDetail.getName());
            childBodyViewHolder.childeDescription.setText(packageDetail.getDescription());
            imageLoader.displayImage(packageDetail.getImage(), childBodyViewHolder.thumb, options);
            childBodyViewHolder.buyButton.setText(packageDetail.getPrice() + " USD");
            childBodyViewHolder.buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBuyPackageListener.onPackageActivityBuyPackageListener(packageDetail, childBodyViewHolder.inputCode.getText().toString().trim());
                }
            });
            if (packageDetail.getPromotion() == 0) {
                if (packageDetail.getIsBuy() == true) {
                    childBodyViewHolder.buyButton.setText(context.getResources().getString(R.string.package_title_button_bought));
                    childBodyViewHolder.buyButton.setBackgroundColor(context.getResources().getColor(R.color.border_grey));
                    childBodyViewHolder.buyButton.setClickable(false);
                    childBodyViewHolder.buyButton.setVisibility(View.GONE);
                } else {
                    childBodyViewHolder.buyButton.setText(packageDetail.getPrice() + " USD");
                    childBodyViewHolder.inputCode.setVisibility(View.GONE);
                }

            } else {
                if (packageDetail.getIsBuy() == true) {
                    childBodyViewHolder.buyButton.setText(context.getResources().getString(R.string.package_title_button_addedcode));
                    childBodyViewHolder.buyButton.setBackgroundColor(context.getResources().getColor(R.color.border_grey));
                    childBodyViewHolder.buyButton.setClickable(false);
                    childBodyViewHolder.inputCode.setVisibility(View.VISIBLE);
                    Utils.hideSoftKeyboard((Activity) context);
                } else {
                    childBodyViewHolder.buyButton.setText(context.getResources().getString(R.string.package_title_button_addcode));
                    childBodyViewHolder.inputCode.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    public void setPackageActivityBuyPackageListener(PackageActivityBuyPackageListener buyPackageListener) {
        mBuyPackageListener = buyPackageListener;
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

    public interface PackageActivityBuyPackageListener {
        void onPackageActivityBuyPackageListener(PackageDetail packageDetail, String inputCode);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView title, description;
        private View divider;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            divider = itemView.findViewById(R.id.package_activity_header_divider);
            title = (TextView) itemView.findViewById(R.id.package_activity_header_title);
            description = (TextView) itemView.findViewById(R.id.package_activity_header_description);
        }
    }

    public class ChildBodyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, childeDescription;
        private ImageView thumb;
        private Button buyButton;
        private EditText inputCode;

        public ChildBodyViewHolder(View itemView) {
            super(itemView);
            childeDescription = (TextView) itemView.findViewById(R.id.package_detail_header_child_description);
            title = (TextView) itemView.findViewById(R.id.package_detail_header_child_name);
            thumb = (ImageView) itemView.findViewById(R.id.package_detail_header_child_thumb);
            buyButton = (Button) itemView.findViewById(R.id.package_detail_header_child_buy);
            inputCode = (EditText) itemView.findViewById(R.id.package_detail_header_child_code);
            inputCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        }
    }
}
