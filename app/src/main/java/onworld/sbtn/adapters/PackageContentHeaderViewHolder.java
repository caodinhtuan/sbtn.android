package onworld.sbtn.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.josonmodel.Shows;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.views.PackageChildViewItem;

/**
 * Created by linhnguyen on 5/28/16.
 */
public class PackageContentHeaderViewHolder extends PackageDetailViewHolder {
    private TextView packageValue;
    private TextView packageTitle;
    private TextView packageDescription;
    private EditText packageCodeInput;
    private Button packageBuy;
    private Context context;
    private int promotionCodeStatus;
    private LinearLayout packageChildLayout;
    private PackageChildViewItem mPackageChildViewItem;
    private boolean isBuy;
    private PackageContentHolderBuyListener packageContentBuyListener;

    public PackageContentHeaderViewHolder(Context context, View itemView) {
        super(context, itemView);
        this.context = context;
        packageValue = (TextView) itemView.findViewById(R.id.package_detail_header_value);
        packageTitle = (TextView) itemView.findViewById(R.id.package_detail_header_name);
        packageDescription = (TextView) itemView.findViewById(R.id.package_detail_header_description);
        packageCodeInput = (EditText) itemView.findViewById(R.id.package_detail_header_code);
        packageChildLayout = (LinearLayout) itemView.findViewById(R.id.package_detail_header_child_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        packageCodeInput.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        packageBuy = (Button) itemView.findViewById(R.id.package_detail_header_buy);
        packageBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String proCode = packageCodeInput.getText().toString().trim();
                packageContentBuyListener.onPackageContentHolderBuyListener(promotionCodeStatus, proCode);

            }
        });

    }

    @Override
    public void bindView(String parentTitle, String parentDes, PackageDetail packageDetail, Shows shows, boolean isShowMore) {
        packageValue.setText(packageDetail.getPrice() + "\n USD");
        packageTitle.setText(parentTitle);
        packageDescription.setText(parentDes);

        promotionCodeStatus = packageDetail.getPromotion();
        isBuy = packageDetail.getIsBuy();

        if (packageDetail.getIsBuy() == true) {
            disableBuyPackage();
        } else if (promotionCodeStatus == 0) {
            packageCodeInput.setVisibility(View.GONE);
            packageBuy.setText(packageDetail.getPrice() + " USD");
        } else {
            //prevent code now
            packageCodeInput.setVisibility(View.GONE);
            packageBuy.setText(context.getResources().getString(R.string.package_title_button_addcode));
            packageBuy.setVisibility(View.GONE);
        }

    }

    public void disableBuyPackage() {
        packageBuy.setVisibility(View.GONE);
        packageCodeInput.setVisibility(View.GONE);
    }

    public void setBuyToBought() {
        packageBuy.setText(context.getResources().getString(R.string.package_title_button_bought));
        packageBuy.setBackgroundResource(R.drawable.package_purchase_shape_grey);
        packageBuy.setClickable(false);
    }

    public void setAddToAdded() {
        packageBuy.setText(context.getResources().getString(R.string.package_title_button_addedcode));
        packageBuy.setBackgroundResource(R.drawable.package_purchase_shape_grey);
        packageBuy.setClickable(false);
        packageCodeInput.setVisibility(View.GONE);
        Utils.hideSoftKeyboard((Activity) context);
    }

    public void setPackageContentHolderBuyListener(PackageContentHolderBuyListener packageContentBuyListener) {
        this.packageContentBuyListener = packageContentBuyListener;
    }

    public interface PackageContentHolderBuyListener {
        void onPackageContentHolderBuyListener(int promotionCodeStatus, String proCode);
    }
}

