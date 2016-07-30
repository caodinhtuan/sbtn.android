package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by onworldtv on 1/25/16.
 */
public class VideoInfoData implements Parcelable {
    String videoTitle;
    String videoDescription;
    String videoNation;
    String videoReleaseYear;
    String videoDuration;
    ArrayList<InfoVideoArrayModel> directors;
    ArrayList<InfoVideoArrayModel> actors;
    ArrayList<InfoVideoArrayModel> tags;
    ArrayList<InfoVideoArrayModel> genre;
    ArrayList<InfoVideoArrayModel> nations;

    public VideoInfoData() {

    }

    protected VideoInfoData(Parcel in) {
        videoTitle = in.readString();
        videoDescription = in.readString();
        videoNation = in.readString();
        videoReleaseYear = in.readString();
        videoDuration = in.readString();
    }

    public static final Creator<VideoInfoData> CREATOR = new Creator<VideoInfoData>() {
        @Override
        public VideoInfoData createFromParcel(Parcel in) {
            return new VideoInfoData(in);
        }

        @Override
        public VideoInfoData[] newArray(int size) {
            return new VideoInfoData[size];
        }
    };

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoNation() {
        return videoNation;
    }

    public void setVideoNation(String videoNation) {
        this.videoNation = videoNation;
    }

    public String getVideoReleaseYear() {
        return videoReleaseYear;
    }

    public void setVideoReleaseYear(String videoReleaseYear) {
        this.videoReleaseYear = videoReleaseYear;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoTitle);
        dest.writeString(videoDescription);
        dest.writeString(videoNation);
        dest.writeString(videoReleaseYear);
        dest.writeString(videoDuration);
    }

    public ArrayList<InfoVideoArrayModel> getActors() {
        return actors;
    }

    public ArrayList<InfoVideoArrayModel> getDirectors() {
        return directors;
    }

    public ArrayList<InfoVideoArrayModel> getGenre() {
        return genre;
    }

    public ArrayList<InfoVideoArrayModel> getNations() {
        return nations;
    }

    public ArrayList<InfoVideoArrayModel> getTags() {
        return tags;
    }

    public void setTags(ArrayList<InfoVideoArrayModel> tags) {
        this.tags = tags;
    }

    public void setGenre(ArrayList<InfoVideoArrayModel> genre) {
        this.genre = genre;
    }

    public void setActors(ArrayList<InfoVideoArrayModel> actors) {
        this.actors = actors;
    }

    public void setDirectors(ArrayList<InfoVideoArrayModel> directors) {
        this.directors = directors;
    }

    public void setNations(ArrayList<InfoVideoArrayModel> nations) {
        this.nations = nations;
    }
}
