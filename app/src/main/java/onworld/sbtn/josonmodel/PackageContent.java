package onworld.sbtn.josonmodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by onworldtv on 5/27/16.
 */
public class PackageContent {
    private int id;
    private String name;
    private int mode;
    @SerializedName("is_buy")
    private boolean isBuy;
    private ArrayList<DataDetailItem> items;

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public String getName() {
        return name;
    }

    public ArrayList<DataDetailItem> getItems() {
        return items;
    }

    public int getId() {
        return id;
    }

    public int getMode() {
        return mode;
    }

}
