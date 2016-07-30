package onworld.sbtn.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import onworld.sbtn.josonmodel.Shows;

/**
 * Created by onworldtv on 12/25/15.
 */
public abstract class CategoryViewHolder extends RecyclerView.ViewHolder {
    Context context;

    public CategoryViewHolder(View itemView) {
        super(itemView);

    }

    public abstract void bindView(Shows shows, boolean isShowMore);
}
