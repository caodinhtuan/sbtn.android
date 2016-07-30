package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by onworldtv on 12/1/15.
 */
public class ImageBanner implements Parcelable{
    private String url;

    protected ImageBanner(Parcel in) {
        url = in.readString();
    }

    public static final Creator<ImageBanner> CREATOR = new Creator<ImageBanner>() {
        @Override
        public ImageBanner createFromParcel(Parcel in) {
            return new ImageBanner(in);
        }

        @Override
        public ImageBanner[] newArray(int size) {
            return new ImageBanner[size];
        }
    };

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }
}
