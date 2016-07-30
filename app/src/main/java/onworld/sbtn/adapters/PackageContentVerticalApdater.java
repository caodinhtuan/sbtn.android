package onworld.sbtn.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.PackageDetail;
import onworld.sbtn.josonmodel.Shows;
import onworld.sbtn.views.FlexibleDividerDecoration;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * Created by onworldtv on 12/25/15.
 */
public class PackageContentVerticalApdater extends RecyclerView.Adapter<PackageDetailViewHolder> implements FlexibleDividerDecoration.PaintProvider,
        FlexibleDividerDecoration.VisibilityProvider,
        HorizontalDividerItemDecoration.MarginProvider,
        PackageContentHeaderViewHolder.PackageContentHolderBuyListener, PackageChildViewHolder.PackageChildHolderBuyListener {
    public static final int HOME_HORIZONTAL_LIST = 2;
    public static final int HOME_HORIZONTAL_LIST_SINGLE_LINE = 3;
    public static final int HOME_HORIZONTAL_LIST_DOUBLE_LINE = 4;
    public static final int HOME_LIST_FULL = 5;
    public static final int PACKAGE_CONTENT_HEADER = 0;
    public static final int PACKAGE_CONTENT_TITLE = 1;
    public static final int PACKAGE_CHILD = 6;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Shows> showses = new ArrayList<>();
    private int showMode;
    private boolean isShowMore;
    private PackageDetail packageDetail;
    private ArrayList<PackageDetail> mPackageDetails = new ArrayList<>();
    private PackageContentAdapterBuyListener packageContentBuyListener;
    private PackageContentHeaderViewHolder packageContentHeaderViewHolder;
    private int sizePackageGroup = 0;
    private Shows myShows;
    private String parentTitle, parentDescription;
    private PackageChildAdapterBuyListener mPackageChildAdapterBuyListener;
    private PackageChildViewHolder packageChildViewHolder;

    public PackageContentVerticalApdater(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return PACKAGE_CONTENT_HEADER;
        } else if (position <= sizePackageGroup) {
            return PACKAGE_CHILD;
        } else {
            myShows = showses.get(position - 1 - sizePackageGroup);
            ArrayList<DataDetailItem> showsDetail = myShows.getShowsDetails();
            int gridSize = showsDetail.size();
            if (myShows.isHeader()) {

                if (gridSize <= 6) {
                    isShowMore = false;
                } else {
                    isShowMore = true;
                }
                return PACKAGE_CONTENT_TITLE;
            } else {
                showMode = myShows.getMode();
                if (showMode == CategoryRecyclerHorizontalAdapter.CATEGORY_AUDIO_TYPE) {
                    {
                        if (gridSize <= 3) {
                            return HOME_HORIZONTAL_LIST_SINGLE_LINE;
                        } else if (gridSize <= 6) {
                            return HOME_HORIZONTAL_LIST_DOUBLE_LINE;
                        } else {
                            return HOME_HORIZONTAL_LIST;
                        }
                    }

                } else {
                    {
                        if (gridSize <= 2) {
                            return HOME_HORIZONTAL_LIST_SINGLE_LINE;
                        } else if (gridSize <= 4) {
                            return HOME_HORIZONTAL_LIST_DOUBLE_LINE;
                        } else {
                            return HOME_HORIZONTAL_LIST;
                        }
                    }

                }
            }

        }
    }

    @Override
    public PackageDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == PACKAGE_CONTENT_HEADER) {
            view = inflater.inflate(R.layout.package_detail_header_layout, parent, false);
            packageContentHeaderViewHolder = new PackageContentHeaderViewHolder(context, view);
            packageContentHeaderViewHolder.setPackageContentHolderBuyListener(this);
            return packageContentHeaderViewHolder;
        } else if (viewType == PACKAGE_CHILD) {
            view = inflater.inflate(R.layout.package_child_item_layout, parent, false);
            packageChildViewHolder = new PackageChildViewHolder(context, view);
            packageChildViewHolder.setPackageChildHolderBuyListener(this);
            return packageChildViewHolder;
        } else if (viewType == PACKAGE_CONTENT_TITLE) {
            view = inflater.inflate(R.layout.list_category_horizontal_header, parent, false);
            PackageHorizontalHeaderHolder homeRecyclerHorizontalHeaderHolder = new PackageHorizontalHeaderHolder(context, view, isShowMore);
            return homeRecyclerHorizontalHeaderHolder;
        } else {
            if (showMode == CategoryRecyclerHorizontalAdapter.CATEGORY_AUDIO_TYPE) {
                if (viewType == HOME_HORIZONTAL_LIST) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_tripple, parent, false);
                } else if (viewType == HOME_HORIZONTAL_LIST_SINGLE_LINE) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_single, parent, false);
                } else if (viewType == HOME_HORIZONTAL_LIST_DOUBLE_LINE) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_double, parent, false);
                } else if (viewType == HOME_LIST_FULL) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_tripple, parent, false);
                }
            } else {
                if (viewType == HOME_HORIZONTAL_LIST) {
                    view = inflater.inflate(R.layout.list_category_horizontal, parent, false);
                } else if (viewType == HOME_HORIZONTAL_LIST_SINGLE_LINE) {
                    view = inflater.inflate(R.layout.list_category_horizontal_single, parent, false);
                } else if (viewType == HOME_HORIZONTAL_LIST_DOUBLE_LINE) {
                    view = inflater.inflate(R.layout.list_category_horizontal_double, parent, false);
                } else if (viewType == HOME_LIST_FULL) {
                    view = inflater.inflate(R.layout.list_audio_category_horizontal_tripple, parent, false);
                }
            }

            PackageContentHorizontalHolder homeRecyclerHorizontalHolder = new PackageContentHorizontalHolder(context, view, viewType);
            return homeRecyclerHorizontalHolder;
        }

    }

    public void setHomeVerticalData(String parentTitle, String parentDes, ArrayList<PackageDetail> packageDetails, ArrayList<Shows> showses) {
        this.showses = showses;
        this.mPackageDetails = packageDetails;
        this.parentTitle = parentTitle;
        this.parentDescription = parentDes;
        sizePackageGroup = mPackageDetails.size();
        this.packageDetail = packageDetails.get(0);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(PackageDetailViewHolder holder, int position) {
        if (showses != null && showses.size() > 0) {
            if (position == 0) {
                holder.bindView(parentTitle, parentDescription, packageDetail, null, isShowMore);
            } else if (position <= sizePackageGroup) {
                holder.bindView("", "", mPackageDetails.get(position - 1), null, isShowMore);
            } else {
                holder.bindView("", "", packageDetail, showses.get(position - 1 - sizePackageGroup), isShowMore);
            }

        }

    }

    @Override
    public int getItemCount() {
        if (showses != null && showses.size() > 0) {
            return showses.size() + 1 + sizePackageGroup;
        }
        return 0;
    }

    @Override
    public int dividerLeftMargin(int position, RecyclerView parent) {
        return 10;
    }

    @Override
    public int dividerRightMargin(int position, RecyclerView parent) {
        return 10;
    }

    @Override
    public Paint dividerPaint(int position, RecyclerView parent) {
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.drawer_divider_grey));
        paint.setStrokeWidth(2);
        return paint;
    }

    @Override
    public boolean shouldHideDivider(int position, RecyclerView parent) {
        if (position <= sizePackageGroup) {
            return false;
        } else {
            if (myShows.isHeader()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void setPackageContentAdapterBuyListener(PackageContentAdapterBuyListener packageContentBuyListener) {
        this.packageContentBuyListener = packageContentBuyListener;
    }

    @Override
    public void onPackageContentHolderBuyListener(int promotionCodeStatus, String proCode) {
        packageContentBuyListener.onPackageContentBuyListener(promotionCodeStatus, proCode);
    }

    public void setBuyToBought(int position) {
        //packageContentHeaderViewHolder.setBuyToBought();
        mPackageDetails.get(position - 1).setBuy(true);
        notifyItemChanged(position);
    }

    public void setBuyToBoughtNoNotify() {
        packageChildViewHolder.setBuyToBoughtNoNofity();
    }

    public void setAddToAdded() {
        packageContentHeaderViewHolder.setAddToAdded();
    }

    @Override
    public void onPackageChildHolderBuyListener(int position, PackageDetail packageDetail, int promotionCodeStatus, String inputCode) {
        mPackageChildAdapterBuyListener.onPackageChildAdapterBuyListener(position, packageDetail, promotionCodeStatus, inputCode);
    }

    public void setPackageChildAdapterBuyListener(PackageChildAdapterBuyListener packageChildAdapterBuyListener) {
        this.mPackageChildAdapterBuyListener = packageChildAdapterBuyListener;
    }

    public interface PackageContentAdapterBuyListener {
        void onPackageContentBuyListener(int promotionCode, String proCode);
    }

    public interface PackageChildAdapterBuyListener {
        void onPackageChildAdapterBuyListener(int position, PackageDetail packageDetail, int promotionCodeStatus, String inputCode);
    }

}
