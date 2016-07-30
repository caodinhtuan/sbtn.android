package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

/**
 */
public class DataDetailItem implements Serializable, Parcelable {
    public static final Creator<DataDetailItem> CREATOR = new Creator<DataDetailItem>() {
        @Override
        public DataDetailItem createFromParcel(Parcel in) {
            return new DataDetailItem(in);
        }

        @Override
        public DataDetailItem[] newArray(int size) {
            return new DataDetailItem[size];
        }
    };
    int id = 0;
    String name = null;
    String description = null;
    String image = null;
    String category = null;
    int karaoke = 0;
    int package_type;

    protected DataDetailItem(Parcel in) {
        package_type = in.readInt();
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        image = in.readString();
        category = in.readString();
        karaoke = in.readInt();
    }

    public int getPackage_type() {
        return package_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKaraoke() {
        return karaoke;
    }

    public void setKaraoke(int karaoke) {
        this.karaoke = karaoke;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public void setImage(String img) {
        this.image = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateImageUrl(String newImageSize) {
        if (TextUtils.isEmpty(this.image) == false) {
            this.image = this.image.replace("wxh", newImageSize);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(package_type);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeString(category);
        dest.writeInt(karaoke);
    }
}
