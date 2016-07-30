package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by onworldtv on 6/21/16.
 */
public class GroupPackage implements Parcelable {

    public static final Creator<GroupPackage> CREATOR = new Creator<GroupPackage>() {
        @Override
        public GroupPackage createFromParcel(Parcel in) {
            return new GroupPackage(in);
        }

        @Override
        public GroupPackage[] newArray(int size) {
            return new GroupPackage[size];
        }
    };
    @SerializedName("max_viewer")
    int maxViewer;
    @SerializedName("group_name")
    private String groupName;
    private ArrayList<PackageDetail> items;
    private String description;
    private String image;

    protected GroupPackage(Parcel in) {
        maxViewer = in.readInt();
        groupName = in.readString();
        items = in.createTypedArrayList(PackageDetail.CREATOR);
        description = in.readString();
        image = in.readString();
    }

    public int getMaxViewer() {
        return maxViewer;
    }

    public void setMaxViewer(int maxViewer) {
        this.maxViewer = maxViewer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<PackageDetail> getItems() {
        return items;
    }

    public void setItems(ArrayList<PackageDetail> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(maxViewer);
        dest.writeString(groupName);
        dest.writeTypedList(items);
        dest.writeString(description);
        dest.writeString(image);
    }
}
