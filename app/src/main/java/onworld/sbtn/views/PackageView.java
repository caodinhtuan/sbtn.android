package onworld.sbtn.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import onworld.sbtn.R;

/**
 * Created by onworldtv on 5/16/16.
 */
public abstract class PackageView extends LinearLayout {
    public LinearLayout mRootView;
    TextView packageNameView, packageDescriptionView;
    Button packageBuyView;
    ImageView packageThumb;
    EditText inputCode;
    private RecyclerView packageChildList;

    public PackageView(Context context) {
        super(context);
        this.initView();
    }

    public PackageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public PackageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PackageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.package_item_layout, this);
        this.mRootView = (LinearLayout) this.findViewById(R.id.package_layout_item_root);
        packageNameView = (TextView) this.findViewById(R.id.package_name);
        packageThumb = (ImageView) this.findViewById(R.id.package_thumb);
        packageBuyView = (Button) this.findViewById(R.id.package_buy);
        packageChildList = (RecyclerView) this.findViewById(R.id.package_item_child_list);
        packageDescriptionView = (TextView) this.findViewById(R.id.package_description);
        inputCode = (EditText) this.findViewById(R.id.package_input_code);
        inputCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

    }

    @Override
    protected void onAttachedToWindow() {
        this.initData();
        super.onAttachedToWindow();
    }

    private void initData() {
        this.initViewAction();
        this.setupData();
    }

    protected abstract void initViewAction();

    protected abstract void setupData();
}
