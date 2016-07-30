package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Steve on 8/26/2015.
 */
public class Episode implements Parcelable {
    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };
    private String startTime = "";
    private String endTime = "";
    @SerializedName("id")
    private int episodeId;
    @SerializedName("name")
    private String episodeName;
    @SerializedName("link")
    private String episodeLink;
    @SerializedName("image")
    private String episodeImage;
    private int duration;
    private int year;
    private String dayTimeLine = "";

    public Episode() {

    }

    protected Episode(Parcel in) {
        dayTimeLine = in.readString();
        episodeId = in.readInt();
        episodeName = in.readString();
        episodeLink = in.readString();
        episodeImage = in.readString();
        duration = in.readInt();
        year = in.readInt();
        endTime = in.readString();
        startTime = in.readString();
    }

    public String getDayTimeLine() {
        return dayTimeLine;
    }

    public void setDayTimeLine(String dayTimeLine) {
        this.dayTimeLine = dayTimeLine;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEpisodeImage() {
        return episodeImage;
    }

    public void setEpisodeImage(String episodeImage) {
        this.episodeImage = episodeImage;
    }

    public String getEpisodeLink() {
        return episodeLink;
    }

    public void setEpisodeLink(String episodeLink) {
        this.episodeLink = episodeLink;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dayTimeLine);
        parcel.writeInt(episodeId);
        parcel.writeString(episodeName);
        parcel.writeString(episodeLink);
        parcel.writeString(episodeImage);
        parcel.writeInt(duration);
        parcel.writeInt(year);
        parcel.writeString(endTime);
        parcel.writeString(startTime);
    }
}
