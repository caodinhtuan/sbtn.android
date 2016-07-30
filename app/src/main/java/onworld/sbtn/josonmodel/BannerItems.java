package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by onworldtv on 10/23/15.
 */
public class BannerItems implements Parcelable {
    public static final Creator<BannerItems> CREATOR = new Creator<BannerItems>() {
        @Override
        public BannerItems createFromParcel(Parcel in) {
            return new BannerItems(in);
        }

        @Override
        public BannerItems[] newArray(int size) {
            return new BannerItems[size];
        }
    };
    private int id;
    private int type;
    private int objectID;
    @SerializedName("image")
    private String linkImageBanner;

    protected BannerItems(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        objectID = in.readInt();
        linkImageBanner = in.readString();
    }

    public BannerItems() {
    }

    public int getId() {
        return id;
    }

    public String getLinkImageBanner() {
        return linkImageBanner;
    }

    public int getObjectID() {
        return objectID;
    }

    public int getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(type);
        parcel.writeInt(objectID);
        parcel.writeString(linkImageBanner);
    }
}
