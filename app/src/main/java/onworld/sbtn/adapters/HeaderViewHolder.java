package onworld.sbtn.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import onworld.sbtn.R;

/**
 * Created by onworldtv on 5/31/16.
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static final int PACKAGE_CONTENT = 1000;
    public static final int PACKAGE_PURCHASED = 1001;
    public static final int PACKAGE_INFO = 1002;
    Button btnLogin;
    TextView UserName;
    LinearLayout infoLayout;
    FrameLayout yourContentLayout, yourPurchasedPackage;

    private MenuHeaderClickListener mMenuHeaderClickListener;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        btnLogin = (Button) itemView.findViewById(R.id.btn_login);
        UserName = (TextView) itemView.findViewById(R.id.user_name);
        infoLayout = (LinearLayout) itemView.findViewById(R.id.menu_info_layout);
        yourContentLayout = (FrameLayout) itemView.findViewById(R.id.menu_your_content);
        yourPurchasedPackage = (FrameLayout) itemView.findViewById(R.id.menu_your_purchased_package);
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuHeaderClickListener.onMenuHeaderClickListener(PACKAGE_INFO);
            }
        });
        yourContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuHeaderClickListener.onMenuHeaderClickListener(PACKAGE_CONTENT);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuHeaderClickListener.onMenuHeaderClickListener(PACKAGE_INFO);
            }
        });
        yourPurchasedPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuHeaderClickListener.onMenuHeaderClickListener(PACKAGE_PURCHASED);
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    public boolean LoginVisible() {
        if (btnLogin.getVisibility() == View.VISIBLE) return true;
        return false;
    }

    public void setMenuHeaderClickListener(MenuHeaderClickListener menuHeaderClickListener) {
        mMenuHeaderClickListener = menuHeaderClickListener;
    }

    public interface MenuHeaderClickListener {
        void onMenuHeaderClickListener(int placeClick);
    }
}
