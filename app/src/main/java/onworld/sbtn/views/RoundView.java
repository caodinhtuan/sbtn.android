package onworld.sbtn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import onworld.sbtn.R;

/**
 * Created by onworldtv on 11/13/15.
 */
public abstract class RoundView extends RelativeLayout {
    public TextView roundTitle;
    public ImageButton roundDetail;
    public LinearLayout roundLayout;

    public RoundView(Context context) {
        super(context);
        this.initView();
    }

    public RoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public RoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    protected void initView() {
        View.inflate(getContext(), R.layout.round_layout, this);
        this.roundTitle = (TextView) this.findViewById(R.id.round_title);
        this.roundDetail = (ImageButton) this.findViewById(R.id.round_btn_next);
        this.roundLayout = (LinearLayout)this.findViewById(R.id.round_layout_id);
    }

    public void setTitle(String title) {
        this.roundTitle.setText(title);
    }
    public void setBackgroundRoundLayout(int color){
        roundLayout.setBackgroundResource(color);
    }
    protected void initData(){
        this.initViewAction();
    }

    @Override
    protected void onAttachedToWindow() {
        this.initData();
        super.onAttachedToWindow();
    }

    protected abstract void initViewAction();


}
