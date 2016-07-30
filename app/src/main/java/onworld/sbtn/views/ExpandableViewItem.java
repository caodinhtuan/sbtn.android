package onworld.sbtn.views;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by onworldtv on 11/13/15.
 */
public class ExpandableViewItem extends ExpandableView {
    FragmentActivity activity;

    public ExpandableViewItem(Context context) {
        super(context);
    }

    public ExpandableViewItem(FragmentActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void initViewAction() {
        this.moreExpandable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ExpandableViewItem.this.descriptionExpandable.setVisibility(ExpandableViewItem.this.descriptionExpandable.isShown()
                        ? View.GONE
                        : View.VISIBLE);*/
                if (ExpandableViewItem.this.descriptionExpandable.isShown()) {
                    SlideDownAnimation.slideDownAnimation(getContext(), ExpandableViewItem.this.descriptionExpandable);
                    ExpandableViewItem.this.descriptionExpandable.setVisibility(GONE);
                    moreExpandable.setText("SHOW");

                } else {
                    ExpandableViewItem.this.descriptionExpandable.setVisibility(VISIBLE);
                    SlideDownAnimation.slideDownAnimation(getContext(), ExpandableViewItem.this.descriptionExpandable);
                    moreExpandable.setText("HIDE");
                }
            }
        });
        this.titleExpandable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExpandableViewItem.this.descriptionExpandable.isShown()) {
                    SlideDownAnimation.slideDownAnimation(getContext(), ExpandableViewItem.this.descriptionExpandable);
                    ExpandableViewItem.this.descriptionExpandable.setVisibility(GONE);
                    moreExpandable.setText("SHOW");
                } else {
                    ExpandableViewItem.this.descriptionExpandable.setVisibility(VISIBLE);
                    SlideDownAnimation.slideDownAnimation(getContext(), ExpandableViewItem.this.descriptionExpandable);
                    moreExpandable.setText("HIDE");
                }
            }
        });
    }
}
