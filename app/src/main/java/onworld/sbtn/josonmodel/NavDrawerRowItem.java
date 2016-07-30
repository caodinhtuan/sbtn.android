package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by onworldtv on 12/23/15.
 */
public class NavDrawerRowItem implements Parcelable {
    public List<NavDrawerRowItem> invisibleChildren;
    public int type;
    int iconId;
    String title;
    private int id;
    private boolean isProvider;

    public NavDrawerRowItem() {
    }

    public NavDrawerRowItem(int type, int iconId, String title, int id) {
        this.iconId = iconId;
        this.title = title;
        this.type = type;
        this.id = id;
    }

    protected NavDrawerRowItem(Parcel in) {
        invisibleChildren = in.createTypedArrayList(NavDrawerRowItem.CREATOR);
        type = in.readInt();
        iconId = in.readInt();
        title = in.readString();
        id = in.readInt();
        isProvider = in.readByte() != 0;
    }

    public static final Creator<NavDrawerRowItem> CREATOR = new Creator<NavDrawerRowItem>() {
        @Override
        public NavDrawerRowItem createFromParcel(Parcel in) {
            return new NavDrawerRowItem(in);
        }

        @Override
        public NavDrawerRowItem[] newArray(int size) {
            return new NavDrawerRowItem[size];
        }
    };

    public boolean getIsProvider() {
        return isProvider;
    }

    public void setProvider(boolean isProvider) {
        this.isProvider = isProvider;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(invisibleChildren);
        dest.writeInt(type);
        dest.writeInt(iconId);
        dest.writeString(title);
        dest.writeInt(id);
        dest.writeByte((byte) (isProvider ? 1 : 0));
    }
}
