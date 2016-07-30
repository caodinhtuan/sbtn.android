package onworld.sbtn.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.josonmodel.Shows;

/**
 * Created by onworldtv on 12/25/15.
 */
public abstract class PackageDetailViewHolder extends RecyclerView.ViewHolder {
    Context context;

    public PackageDetailViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
    }

    public PackageDetailViewHolder(View itemView) {
        super(itemView);

    }

    public abstract void bindView(String parentTitle, String parentDes, PackageDetail packageDetail, Shows shows, boolean isShowMore);
}
