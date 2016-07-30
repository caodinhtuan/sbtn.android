package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by onworldtv on 10/28/15.
 */
public class TimeLine implements Parcelable{
    private String name;
    private String image;
    private String link;
    private String start;
    private String end;
    private int id;
    private String dayTitle;


    protected TimeLine(Parcel in) {
        dayTitle = in.readString();
        name = in.readString();
        image = in.readString();
        link = in.readString();
        start = in.readString();
        end = in.readString();
        id = in.readInt();
    }

    public static final Creator<TimeLine> CREATOR = new Creator<TimeLine>() {
        @Override
        public TimeLine createFromParcel(Parcel in) {
            return new TimeLine(in);
        }

        @Override
        public TimeLine[] newArray(int size) {
            return new TimeLine[size];
        }
    };

    public TimeLine() {
    }

    public int getId() {
        return id;
    }

    public String getEnd() {
        return end;
    }

    public String getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public String getStart() {
        return start;
    }

    public String getDayTitle() {
        return dayTitle;
    }

    public void setDayTitle(String dayTitle) {
        this.dayTitle = dayTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dayTitle);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(link);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeInt(id);
    }
}
