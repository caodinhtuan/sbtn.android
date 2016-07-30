package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import onworld.sbtn.R;

/**
 * Created by onworldtv on 11/13/15.
 */
public abstract class ExpandableView extends LinearLayout {

    TextView titleExpandable;
    Button moreExpandable;
    TextView descriptionExpandable;

    public ExpandableView(Context context) {
        super(context);
        initView();
    }

    public ExpandableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ExpandableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        View.inflate(getContext(), R.layout.expandable_layout, this);
        this.titleExpandable = (TextView) this.findViewById(R.id.title_expandable);
        this.moreExpandable = (Button) this.findViewById(R.id.more_expandable);
        this.descriptionExpandable = (TextView) this.findViewById(R.id.description_expandable);
    }

    protected void initData() {
        this.initViewAction();
    }

    protected abstract void initViewAction();

    public void showExpandableDescription() {
        descriptionExpandable.setVisibility(VISIBLE);
        moreExpandable.setText("HIDE");
    }

    public boolean isHideExpandableDescription() {
        if (descriptionExpandable.getVisibility() == GONE) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        this.initData();
        super.onAttachedToWindow();
    }

    public void setTitleExpandable(String titleExpandable) {
        this.titleExpandable.setText(titleExpandable);
    }

    public void setDescriptionExpandable(String descriptionExpandable) {
        this.descriptionExpandable.setText(descriptionExpandable);
    }

    public void hideButton() {
        this.moreExpandable.setVisibility(GONE);
    }

    public void setMinLineDescription(int minline) {
        this.descriptionExpandable.setMinLines(10);
    }

}
