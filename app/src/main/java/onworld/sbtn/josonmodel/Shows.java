package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Steve on 9/6/2015.
 */
public class Shows implements Serializable, Parcelable {


    public static final Creator<Shows> CREATOR = new Creator<Shows>() {
        @Override
        public Shows createFromParcel(Parcel in) {
            return new Shows(in);
        }

        @Override
        public Shows[] newArray(int size) {
            return new Shows[size];
        }
    };
    private ArrayList<DataDetailItem> items;
    @SerializedName("id")
    private int showsId;
    @SerializedName("name")
    private String showsName;
    private int mode;
    private int karaoke;
    private boolean isHeader;

    protected Shows(Parcel in) {
        items = in.createTypedArrayList(DataDetailItem.CREATOR);
        showsId = in.readInt();
        showsName = in.readString();
        mode = in.readInt();
        karaoke = in.readInt();
        isHeader = in.readByte() != 0;
    }

    public Shows(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public Shows(ArrayList<DataDetailItem> items, int showsId, String showsName, int mode, int karaoke, boolean isHeader) {
        this.items = items;
        this.showsId = showsId;
        this.showsName = showsName;
        this.mode = mode;
        this.karaoke = karaoke;
        this.isHeader = isHeader;
    }

    public Shows() {
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public int getKaraoke() {
        return karaoke;
    }

    public int getMode() {
        return mode;
    }

    public int getShowsId() {
        return showsId;
    }

    public ArrayList<DataDetailItem> getShowsDetails() {
        return items;
    }

    public String getShowsName() {
        return showsName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(items);
        dest.writeInt(showsId);
        dest.writeString(showsName);
        dest.writeInt(mode);
        dest.writeInt(karaoke);
        dest.writeByte((byte) (isHeader ? 1 : 0));
    }
}
