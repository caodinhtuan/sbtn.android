package onworld.sbtn.josonmodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by onworldtv on 6/24/16.
 */
public class PurchasedPackageListDetailData {
    private int promotion;
    private int id;
    private int pk_id;
    private int gpk_id;
    private int pkd_id;
    private String duration;
    private String price;
    @SerializedName("max_player")
    private int maxPlayer;
    private String image;
    private String name;
    private String description;
    private int type;
    @SerializedName("pk_type")
    private int packageType;
    @SerializedName("product_id")
    private String productId;
    @SerializedName("is_buy")
    private boolean isBuy;
    @SerializedName("package")
    private ArrayList<PackageDetail> packageExtraList;

    public ArrayList<PackageDetail> getPackageExtraList() {
        return packageExtraList;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public String getProductId() {
        return productId;
    }

    public int getPackageType() {
        return packageType;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public String getPrice() {
        return price;
    }

    public String getDuration() {
        return duration;
    }

    public int getPkd_id() {
        return pkd_id;
    }

    public int getGpk_id() {
        return gpk_id;
    }

    public int getPk_id() {
        return pk_id;
    }

    public int getId() {
        return id;
    }

    public int getPromotion() {
        return promotion;
    }
}
