package onworld.sbtn.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.PackageDetail;

/**
 * Created by linhnguyen on 5/14/16.
 */
public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {
    //luc dau dinh lam list package sau do thay = add view

    private LayoutInflater mInflater;
    private ArrayList<PackageDetail> mPackages = new ArrayList<>();

    public PackageAdapter(Context context, ArrayList<PackageDetail> aPackages) {
        mInflater = LayoutInflater.from(context);
        this.mPackages = aPackages;
    }

    @Override
    public PackageAdapter.PackageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.package_item_layout, parent, false);

        PackageViewHolder packageViewHolder = new PackageViewHolder(view);
        return packageViewHolder;
    }

    public void setPackageData() {

    }

    @Override
    public void onBindViewHolder(PackageAdapter.PackageViewHolder holder, int position) {
        PackageDetail packageData = mPackages.get(position);
        holder.packageNameView.setText(packageData.getName());
        holder.packageDescriptionView.setText(packageData.getDescription());
        holder.packageBuyView.setText("Buy Now");
    }

    @Override
    public int getItemCount() {
        if (mPackages != null) {
            return mPackages.size();
        }
        return 0;
    }

    class PackageViewHolder extends RecyclerView.ViewHolder {
        TextView packageNameView, packageDescriptionView;
        Button packageBuyView;
        ImageView packageThumb;


        public PackageViewHolder(View itemView) {
            super(itemView);
            packageNameView = (TextView) itemView.findViewById(R.id.package_name);
            packageThumb = (ImageView) itemView.findViewById(R.id.package_thumb);
            packageBuyView = (Button) itemView.findViewById(R.id.package_buy);
            packageDescriptionView = (TextView) itemView.findViewById(R.id.package_description);

        }
    }
}
