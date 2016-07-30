package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import onworld.sbtn.josonmodel.enums.HomeDataType;
import onworld.sbtn.josonmodel.enums.HomeType;

/**
 * Created by onworldtv on 1/15/16.
 */
public class ASHomeData implements Parcelable{
    HomeType type = HomeType.Video;
    HomeDataType homeDataType = HomeDataType.BodyVideo;

    public HomeDataType getHomeDataType() {
        return homeDataType;
    }

    public void setHomeDataType(HomeDataType homeDataType) {
        this.homeDataType = homeDataType;
    }

    private String title;

    protected ASHomeData(Parcel in) {
        title = in.readString();
        dataDetailItems = in.createTypedArrayList(DataDetailItem.CREATOR);
    }

    public static final Creator<ASHomeData> CREATOR = new Creator<ASHomeData>() {
        @Override
        public ASHomeData createFromParcel(Parcel in) {
            return new ASHomeData(in);
        }

        @Override
        public ASHomeData[] newArray(int size) {
            return new ASHomeData[size];
        }
    };

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private ArrayList<DataDetailItem> dataDetailItems;

    public ASHomeData(ArrayList<DataDetailItem> dataDetailItems) {
        this.dataDetailItems = dataDetailItems;
    }

    public ASHomeData() {

    }

    public ArrayList<DataDetailItem> getDataDetailItems() {
        return dataDetailItems;
    }

    public void setDataDetailItems(ArrayList<DataDetailItem> dataDetailItems) {
        this.dataDetailItems = dataDetailItems;
    }
    public HomeType getType() {
        return type;
    }

    public void setType(HomeType type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(dataDetailItems);
    }
}
