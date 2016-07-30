package onworld.sbtn.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.josonmodel.Shows;
import onworld.sbtn.utils.Utils;


/**
 * Created by onworldtv on 12/28/15.
 */
public class PackageHorizontalHeaderHolder extends PackageDetailViewHolder {
    private TextView title, more;
    private Context context;
    private boolean isShowMOre;

    public PackageHorizontalHeaderHolder(Context context, View itemView, boolean isShowMore) {
        super(itemView);
        this.context = context;
        title = (TextView) itemView.findViewById(R.id.home_row_title);
        more = (TextView) itemView.findViewById(R.id.home_more);
    }

    @Override
    public void bindView(String parentTitle, String parentDes, PackageDetail packageDetail, Shows dataHomeItem, boolean isShowMOre) {
        title.setText(dataHomeItem.getShowsName());
        Utils.setRobotoConsiderBoldFont(context, title);
        if (isShowMOre) {
            more.setVisibility(View.VISIBLE);
        } else {
            more.setVisibility(View.GONE);
        }
    }
}
