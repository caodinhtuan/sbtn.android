package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by onworldtv on 12/25/15.
 */
public class ASDataHomeItem implements Parcelable {
    private String name;
    private ArrayList<ASDataDetailItem> items;
    public ASDataHomeItem(){

    }

    protected ASDataHomeItem(Parcel in) {
        name = in.readString();
    }

    public static final Creator<ASDataHomeItem> CREATOR = new Creator<ASDataHomeItem>() {
        @Override
        public ASDataHomeItem createFromParcel(Parcel in) {
            return new ASDataHomeItem(in);
        }

        @Override
        public ASDataHomeItem[] newArray(int size) {
            return new ASDataHomeItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public ArrayList<ASDataDetailItem> getItems() {
        return items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
