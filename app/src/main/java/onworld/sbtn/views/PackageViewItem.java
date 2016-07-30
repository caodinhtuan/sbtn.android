package onworld.sbtn.views;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.Utils;

/**
 * Created by onworldtv on 5/16/16.
 */
public class PackageViewItem extends PackageView {
    private PackageDetail mPackageDetail;
    private BuyPackageListener mBuyPackageListener;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Context mContext;

    public PackageViewItem(Context context) {
        super(context);
    }

    public PackageViewItem(Context context, BuyPackageListener buyPackageListener, PackageDetail packageDetail) {
        super(context);
        this.mContext = context;
        this.mBuyPackageListener = buyPackageListener;
        this.mPackageDetail = packageDetail;
        options = ImageUtils.getOptionsImageCircle(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    protected void initViewAction() {
        this.packageBuyView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBuyPackageListener.onBuyPackageListener(mPackageDetail, inputCode.getText().toString().trim());
            }
        });
    }

    @Override
    protected void setupData() {
        if (mPackageDetail != null) {

            this.packageNameView.setText(mPackageDetail.getName());
            this.packageDescriptionView.setText(mPackageDetail.getDescription());
            imageLoader.displayImage(mPackageDetail.getImage(), this.packageThumb, options);

            if (mPackageDetail.getPromotion() == 0) {
                if (mPackageDetail.getIsBuy() == true) {
                    this.packageBuyView.setText(mContext.getResources().getString(R.string.package_title_button_bought));
                    this.packageBuyView.setBackgroundColor(mContext.getResources().getColor(R.color.border_grey));
                    this.packageBuyView.setClickable(false);
                    inputCode.setVisibility(GONE);
                } else {
                    this.packageBuyView.setText(mPackageDetail.getPrice() + " USD");
                    inputCode.setVisibility(GONE);
                }

            } else {
                if (mPackageDetail.getIsBuy() == true) {
                    this.packageBuyView.setText(mContext.getResources().getString(R.string.package_title_button_addedcode));
                    this.packageBuyView.setBackgroundColor(mContext.getResources().getColor(R.color.border_grey));
                    this.packageBuyView.setClickable(false);
                    inputCode.setVisibility(View.GONE);
                    Utils.hideSoftKeyboard((Activity) mContext);
                } else {
                    this.packageBuyView.setText(mContext.getResources().getString(R.string.package_title_button_addcode));
                    inputCode.setVisibility(VISIBLE);
                }

            }

        }

    }

    public String getInputCode() {
        return this.inputCode.getText().toString().trim();
    }

    public void setBuyPackageListener(BuyPackageListener buyPackageListener) {
        mBuyPackageListener = buyPackageListener;
    }

    public interface BuyPackageListener {
        void onBuyPackageListener(PackageDetail packageDetail, String inputCode);
    }
}
