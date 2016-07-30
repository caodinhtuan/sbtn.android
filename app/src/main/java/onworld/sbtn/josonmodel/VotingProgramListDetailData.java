package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by onworldtv on 11/19/15.
 */
public class VotingProgramListDetailData implements Parcelable {
    private int id;
    private String title;
    @SerializedName("image")
    private String banner;
    @SerializedName("description")
    private String shortDescription;
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("endDate")
    private String endDate;
    private int status;
    @SerializedName("multiRound")
    private int multiRound;

    protected VotingProgramListDetailData(Parcel in) {
        id = in.readInt();
        title = in.readString();
        banner = in.readString();
        shortDescription = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        status = in.readInt();
        multiRound = in.readInt();
    }
    public VotingProgramListDetailData(){

    }

    public static final Creator<VotingProgramListDetailData> CREATOR = new Creator<VotingProgramListDetailData>() {
        @Override
        public VotingProgramListDetailData createFromParcel(Parcel in) {
            return new VotingProgramListDetailData(in);
        }

        @Override
        public VotingProgramListDetailData[] newArray(int size) {
            return new VotingProgramListDetailData[size];
        }
    };

    public String getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getId() {
        return id;
    }

    public int getMultiRound() {
        return multiRound;
    }

    public int getStatus() {
        return status;
    }

    public String getBanner() {
        return banner;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(banner);
        dest.writeString(shortDescription);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeInt(status);
        dest.writeInt(multiRound);
    }
}
