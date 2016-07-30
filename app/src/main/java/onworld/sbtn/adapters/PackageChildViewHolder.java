package onworld.sbtn.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.josonmodel.Shows;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.views.PackageChildViewItem;

/**
 * Created by linhnguyen on 5/28/16.
 */
public class PackageChildViewHolder extends PackageDetailViewHolder {
    private TextView packageTitle;
    private TextView packageDescription;
    private EditText inputCode;
    private Button mButton;
    private ImageView thumb;
    private Context mContext;
    private int promotionCodeStatus;
    private LinearLayout packageChildLayout;
    private PackageChildViewItem mPackageChildViewItem;
    private PackageChildHolderBuyListener packageChildBuyListener;
    private PackageDetail mPackageDetail;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private boolean isBuy;

    public PackageChildViewHolder(Context context, final View itemView) {
        super(context, itemView);
        this.mContext = context;
        packageTitle = (TextView) itemView.findViewById(R.id.package_detail_header_child_name);
        packageDescription = (TextView) itemView.findViewById(R.id.package_detail_header_child_description);
        thumb = (ImageView) itemView.findViewById(R.id.package_detail_header_child_thumb);
        packageChildLayout = (LinearLayout) itemView.findViewById(R.id.package_detail_header_child_root);
        mButton = (Button) itemView.findViewById(R.id.package_detail_header_child_buy);
        inputCode = (EditText) itemView.findViewById(R.id.package_detail_header_child_code);
        inputCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("Kaka", "" + getAdapterPosition());
                packageChildBuyListener.onPackageChildHolderBuyListener(getAdapterPosition() - 1, mPackageDetail, mPackageDetail.getPromotion(), inputCode.getText().toString().trim());
            }
        });
        options = ImageUtils.getOptionsImageCircle(context);
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public void bindView(String parentTitle, String parentDes, PackageDetail packageDetail, Shows shows, boolean isShowMore) {
        mPackageDetail = packageDetail;
        packageTitle.setText(packageDetail.getName());
        packageDescription.setText(packageDetail.getDescription());
        //mButton.setText(packageDetail.getPrice() + " USD");
        imageLoader.displayImage(packageDetail.getImage(), thumb, options);
        if (mPackageDetail.getPromotion() == 0) {
            if (mPackageDetail.getIsBuy() == true ) {
                this.mButton.setText(mContext.getResources().getString(R.string.package_title_button_bought));
                this.mButton.setBackgroundResource(R.drawable.package_purchase_shape_grey);
                this.mButton.setClickable(false);
                inputCode.setVisibility(View.GONE);
                this.isBuy = false;
            } else {
                this.mButton.setText(mPackageDetail.getPrice() + " USD");
                inputCode.setVisibility(View.GONE);
            }

        } else {
            if (mPackageDetail.getIsBuy() == true) {
                this.mButton.setText(mContext.getResources().getString(R.string.package_title_button_addedcode));
                this.mButton.setBackgroundResource(R.drawable.package_purchase_shape_grey);
                this.mButton.setClickable(false);
                inputCode.setVisibility(View.VISIBLE);
                Utils.hideSoftKeyboard((Activity) mContext);
            } else {
                this.mButton.setText(mContext.getResources().getString(R.string.package_title_button_addcode));
                inputCode.setVisibility(View.VISIBLE);
            }

        }

    }

    public void setBuyToBoughtNoNofity() {
        this.mButton.setBackgroundResource(R.drawable.package_purchase_shape_grey);
        this.mButton.setClickable(false);
    }

    public void setPackageChildHolderBuyListener(PackageChildHolderBuyListener packageContentBuyListener) {
        this.packageChildBuyListener = packageContentBuyListener;
    }

    public interface PackageChildHolderBuyListener {
        void onPackageChildHolderBuyListener(int position, PackageDetail packageDetail, int promotionCodeStatus, String inputCode);
    }
}

