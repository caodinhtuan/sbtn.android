package onworld.sbtn.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import onworld.sbtn.R;
import onworld.sbtn.activities.MainActivity;
import onworld.sbtn.fragments.homes.OWLoginFragment;
import onworld.sbtn.josonmodel.NavDrawerRowItem;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.views.FlexibleDividerDecoration;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

public class NavDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FlexibleDividerDecoration.PaintProvider,
        FlexibleDividerDecoration.VisibilityProvider,
        HorizontalDividerItemDecoration.MarginProvider, HeaderViewHolder.MenuHeaderClickListener {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_CHILD = 2;
    public static final int TYPE_ITEM_NON_CHILD = 3;
    public NavDrawerRowItem mNavDrawerRowItem;
    public ItemViewHolder mItemViewHolder;
    private LayoutInflater inflater;
    private Context context;
    private List<NavDrawerRowItem> navDrawerRowItems = new ArrayList<>();
    private NavClickListener navClickListener;
    private boolean isExpand;
    private boolean statusLogin;
    private boolean isLoginVisible;

    public NavDrawerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = inflater.inflate(R.layout.nav_drawer_header, parent, false);
            HeaderViewHolder holder = new HeaderViewHolder(view);
            isLoginVisible = holder.LoginVisible();
            return holder;
        } else if (viewType == TYPE_ITEM) {
            view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        } else if (viewType == TYPE_ITEM_NON_CHILD) {
            view = inflater.inflate(R.layout.nav_drawer_row_non_child, parent, false);
            ItemNonChildViewHolder itemViewHolder = new ItemNonChildViewHolder(view);
            return itemViewHolder;
        } else {
            view = inflater.inflate(R.layout.nav_drawer_child, parent, false);
            ChildViewHolder childHolder = new ChildViewHolder(view);
            return childHolder;
        }
    }

    public void setDataDrawer(ArrayList<NavDrawerRowItem> navDrawerRowItems) {
        this.navDrawerRowItems = navDrawerRowItems;
        notifyDataSetChanged();
    }

    public void notifyChangeHeader(int position, boolean statusLogin) {
        this.statusLogin = statusLogin;
        notifyItemChanged(position);
    }

    public void setLoginVisible(boolean loginVisible) {
        isLoginVisible = loginVisible;
    }

    public boolean isLoginVisibleAdapter() {
        return isLoginVisible;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NavDrawerRowItem navDrawerRowItem = navDrawerRowItems.get(holder.getAdapterPosition());
        if (holder instanceof HeaderViewHolder) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.LOGIN_STATE, 0);
            boolean isLogin = sharedPreferences.getBoolean(OWLoginFragment.IS_LOGIN, false);
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.setMenuHeaderClickListener(this);

            if (statusLogin == true || isLogin == true) {
                headerViewHolder.yourContentLayout.setVisibility(View.VISIBLE);
                headerViewHolder.yourPurchasedPackage.setVisibility(View.VISIBLE);
                headerViewHolder.btnLogin.setVisibility(View.GONE);
                headerViewHolder.UserName.setText(sharedPreferences.getString(OWLoginFragment.EMAIL, "User"));
            } else {
                headerViewHolder.yourContentLayout.setVisibility(View.GONE);
                headerViewHolder.yourPurchasedPackage.setVisibility(View.GONE);
                headerViewHolder.btnLogin.setVisibility(View.VISIBLE);
                headerViewHolder.UserName.setText("User");
            }

        } else if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.title.setText(navDrawerRowItem.getTitle().toUpperCase());
            Utils.setRobotoConsiderRegularFont(context, itemViewHolder.title);
            itemViewHolder.icon.setImageResource(navDrawerRowItem.getIconId());

            itemViewHolder.referalNavDrawerRowItem = navDrawerRowItem;
            if (navDrawerRowItem.invisibleChildren == null) {
                itemViewHolder.expandChildToggle.setImageResource(R.mipmap.ic_keyboard_arrow_up_white_36dp);
            } else {
                itemViewHolder.expandChildToggle.setImageResource(R.mipmap.ic_keyboard_arrow_down_white_36dp);
            }
            this.mNavDrawerRowItem = navDrawerRowItem;
            this.mItemViewHolder = itemViewHolder;
            //set Nav Open
        } else if (holder instanceof ChildViewHolder) {
            ChildViewHolder childViewHolder = (ChildViewHolder) holder;
            childViewHolder.title.setText(navDrawerRowItems.get(position).getTitle());
            Utils.setRobotoConsiderRegularFont(context, childViewHolder.title);
        } else if (holder instanceof ItemNonChildViewHolder) {
            ItemNonChildViewHolder itemNonChildViewHolder = (ItemNonChildViewHolder) holder;
            itemNonChildViewHolder.title.setText(navDrawerRowItem.getTitle().toUpperCase());
            Utils.setRobotoConsiderRegularFont(context, itemNonChildViewHolder.title);
            itemNonChildViewHolder.icon.setImageResource(navDrawerRowItem.getIconId());
        }
    }

    public void expandClick(NavDrawerRowItem navDrawerRowItem, ItemViewHolder itemViewHolder) {
        if (navDrawerRowItem.invisibleChildren == null) {
            isExpand = false;
            navDrawerRowItem.invisibleChildren = new ArrayList<>();
            int count = 0;
            int pos = navDrawerRowItems.indexOf(itemViewHolder.referalNavDrawerRowItem);
            while (navDrawerRowItems.size() > pos + 1 && navDrawerRowItems.get(pos + 1).type == TYPE_CHILD) {
                navDrawerRowItem.invisibleChildren.add(navDrawerRowItems.remove(pos + 1));
                count++;
            }
            notifyItemRangeRemoved(pos + 1, count);
            itemViewHolder.expandChildToggle.setImageResource(R.mipmap.ic_keyboard_arrow_down_white_36dp);
        } else {
            isExpand = true;
            int pos = navDrawerRowItems.indexOf(itemViewHolder.referalNavDrawerRowItem);
            int index = pos + 1;
            for (NavDrawerRowItem i : navDrawerRowItem.invisibleChildren) {
                navDrawerRowItems.add(index, i);
                index++;
            }
            notifyItemRangeInserted(pos + 1, index - pos - 1);
            itemViewHolder.expandChildToggle.setImageResource(R.mipmap.ic_keyboard_arrow_up_white_36dp);
            navDrawerRowItem.invisibleChildren = null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return navDrawerRowItems.get(position).type;
        }
    }

    @Override
    public int getItemCount() {
        if (navDrawerRowItems != null) {
            return navDrawerRowItems.size();
        }
        return 0;
    }

    public void setNavClickListenter(NavClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    @Override
    public Paint dividerPaint(int position, RecyclerView parent) {
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.drawer_divider_white));
        paint.setStrokeWidth(1);
        return paint;
    }

    @Override
    public int dividerLeftMargin(int position, RecyclerView parent) {
        RecyclerView.ViewHolder child = parent.findViewHolderForAdapterPosition(position);
        if (child instanceof ChildViewHolder) {
            return 150;
        } else {
            return 20;
        }
    }

    @Override
    public int dividerRightMargin(int position, RecyclerView parent) {
        return 20;
    }

    @Override
    public boolean shouldHideDivider(int position, RecyclerView parent) {
        if (position == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void onMenuHeaderClickListener(int placeClick) {
        navClickListener.onNavClickListener(placeClick);
    }

    public interface NavClickListener {
        void onNavClickListener(int placeClick);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public NavDrawerRowItem referalNavDrawerRowItem;
        TextView title;
        ImageView icon;
        ImageView expandChildToggle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.id_navigationdrawer_title);
            icon = (ImageView) itemView.findViewById(R.id.id_navigationdrawer_icon);
            expandChildToggle = (ImageView) itemView.findViewById(R.id.id_expand_toggle);
        }
    }

    public class ItemNonChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView icon;

        public ItemNonChildViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.id_navigationdrawer_title);
            icon = (ImageView) itemView.findViewById(R.id.id_navigationdrawer_icon);
        }

        @Override
        public void onClick(View v) {
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;

        public ChildViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.id_child_title);
        }

        @Override
        public void onClick(View v) {
        }
    }

}