package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Steve on 8/26/2015.
 */
public class Related implements Parcelable {
    public static final Creator<Related> CREATOR = new Creator<Related>() {
        @Override
        public Related createFromParcel(Parcel in) {
            return new Related(in);
        }

        @Override
        public Related[] newArray(int size) {
            return new Related[size];
        }
    };
    int package_type;
    @SerializedName("id")
    private int relatedId;
    @SerializedName("name")
    private String relatedName;
    @SerializedName("link")
    private String relatedLink;
    @SerializedName("image")
    private String relatedImage;

    protected Related(Parcel in) {
        package_type = in.readInt();
        relatedId = in.readInt();
        relatedName = in.readString();
        relatedLink = in.readString();
        relatedImage = in.readString();
    }

    public int getPackage_type() {
        return package_type;
    }

    public int getRelatedId() {
        return relatedId;
    }

    public String getRelatedImage() {
        return relatedImage;
    }

    public String getRelatedLink() {
        return relatedLink;
    }

    public String getRelatedName() {
        return relatedName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(package_type);
        dest.writeInt(relatedId);
        dest.writeString(relatedName);
        dest.writeString(relatedLink);
        dest.writeString(relatedImage);
    }
}
