package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by onworldtv on 12/25/15.
 */
public class ASDataDetailItem implements Parcelable {
    public static final Creator<ASDataDetailItem> CREATOR = new Creator<ASDataDetailItem>() {
        @Override
        public ASDataDetailItem createFromParcel(Parcel in) {
            return new ASDataDetailItem(in);
        }

        @Override
        public ASDataDetailItem[] newArray(int size) {
            return new ASDataDetailItem[size];
        }
    };
    private String name;
    private String link;
    private String thumbnail;
    public ASDataDetailItem(){

    }

    protected ASDataDetailItem(Parcel in) {
        name = in.readString();
        link = in.readString();
        thumbnail = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(link);
        dest.writeString(thumbnail);
    }
}
