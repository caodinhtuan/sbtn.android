package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import onworld.sbtn.josonmodel.enums.HomeType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 */
public class HomeItem implements Serializable,Parcelable {

    ArrayList<DataDetailItem> recommend;
    ArrayList<DataDetailItem> added;
    ArrayList<DataDetailItem> popular;

    HomeType type = HomeType.Video;

    protected HomeItem(Parcel in) {
    }
    public HomeItem(){

    }

    public static final Creator<HomeItem> CREATOR = new Creator<HomeItem>() {
        @Override
        public HomeItem createFromParcel(Parcel in) {
            return new HomeItem(in);
        }

        @Override
        public HomeItem[] newArray(int size) {
            return new HomeItem[size];
        }
    };

    public ArrayList<DataDetailItem> getRecommend() {
        return recommend;
    }
    public ArrayList<DataDetailItem> getAdded() {
        return added;
    }
    public ArrayList<DataDetailItem> getPopular() {
        return popular;
    }
    public HomeType getType() {
        return type;
    }

    public void setType(HomeType type) {
        this.type = type;
    }

    public void updateImageUrl(String newImageSize) {
        this.updateImageUrlInArray(this.recommend, newImageSize);
        this.updateImageUrlInArray(this.added, newImageSize);
        this.updateImageUrlInArray(this.popular, newImageSize);
    }

    private void updateImageUrlInArray(ArrayList<DataDetailItem> items, String newImageSize) {
        if (items == null) {
            return;
        }
        for (DataDetailItem item : items) {
            item.updateImageUrl(newImageSize);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
