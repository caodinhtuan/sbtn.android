package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by onworldtv on 5/12/16.
 */
public class PackageDetail implements Parcelable, Serializable {


    public static final Creator<PackageDetail> CREATOR = new Creator<PackageDetail>() {
        @Override
        public PackageDetail createFromParcel(Parcel in) {
            return new PackageDetail(in);
        }

        @Override
        public PackageDetail[] newArray(int size) {
            return new PackageDetail[size];
        }
    };
    @SerializedName("group_name")
    private String groupName;
    @SerializedName("max_viewer")
    private int maxViewer;
    private boolean isHeader = false;
    private int promotion;
    private int pk_id;
    private int pkd_id;
    private String name;
    private String price;
    private String description;
    private String duration;
    private String product_id;
    private int type;
    private String image;
    @SerializedName("is_buy")
    private boolean isBuy;

    protected PackageDetail(Parcel in) {
        groupName = in.readString();
        maxViewer = in.readInt();
        isHeader = in.readByte() != 0;
        promotion = in.readInt();
        pk_id = in.readInt();
        pkd_id = in.readInt();
        name = in.readString();
        price = in.readString();
        description = in.readString();
        duration = in.readString();
        product_id = in.readString();
        type = in.readInt();
        image = in.readString();
        isBuy = in.readByte() != 0;
    }

    public PackageDetail(String groupName, int maxViewer, boolean isHeader, String description, String image) {
        this.groupName = groupName;
        this.maxViewer = maxViewer;
        this.isHeader = isHeader;
        this.description = description;
        this.image = image;
    }

    public PackageDetail(String groupName, int maxViewer, boolean isHeader) {
        this.groupName = groupName;
        this.maxViewer = maxViewer;
        this.isHeader = isHeader;
    }

    public PackageDetail(String name, String price, String description, boolean isHeader, boolean isBuy) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.isHeader = isHeader;
        this.isBuy = isBuy;
    }
    public PackageDetail(String name, String price, String description, boolean isHeader, boolean isBuy,String image) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.isHeader = isHeader;
        this.isBuy = isBuy;
        this.image = image;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getMaxViewer() {
        return maxViewer;
    }

    public void setMaxViewer(int maxViewer) {
        this.maxViewer = maxViewer;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public int getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return pk_id;
    }

    public int getPromotion() {
        return promotion;
    }

    public String getProductId() {
        return product_id;
    }

    public String getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public int getPk_id() {
        return pkd_id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public boolean getIsBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        dest.writeInt(maxViewer);
        dest.writeByte((byte) (isHeader ? 1 : 0));
        dest.writeInt(promotion);
        dest.writeInt(pk_id);
        dest.writeInt(pkd_id);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(duration);
        dest.writeString(product_id);
        dest.writeInt(type);
        dest.writeString(image);
        dest.writeByte((byte) (isBuy ? 1 : 0));
    }
}
