package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by onworldtv on 11/19/15.
 */
public class VotingProgramDetailData implements Parcelable {

    private String title;
    @SerializedName("image")
    private String banner;
    @SerializedName("description")
    private String description;
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("enDate")
    private String endDate;
    private int status;
    private String owner;
    private String examinee;
    private String rule;
    private String[] images;
    private int isMultilRound;

    public int getIsMultilRound() {
        return isMultilRound;
    }

    protected VotingProgramDetailData(Parcel in) {
        title = in.readString();
        banner = in.readString();
        description = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        status = in.readInt();
        isMultilRound = in.readInt();
        owner = in.readString();
        examinee = in.readString();
        rule = in.readString();
        images = in.createStringArray();
        round = in.createTypedArrayList(VotingRoundItemData.CREATOR);
    }

    public static final Creator<VotingProgramDetailData> CREATOR = new Creator<VotingProgramDetailData>() {
        @Override
        public VotingProgramDetailData createFromParcel(Parcel in) {
            return new VotingProgramDetailData(in);
        }

        @Override
        public VotingProgramDetailData[] newArray(int size) {
            return new VotingProgramDetailData[size];
        }
    };

    public String[] getImages() {
        return images;
    }

    //private ArrayList<ImageBanner> images;
    private ArrayList<VotingRoundItemData> round;

    public VotingProgramDetailData() {

    }



    /*public ArrayList<ImageBanner> getImages() {
        return images;
    }*/

    public ArrayList<VotingRoundItemData> getRound() {
        return round;
    }

    public int getStatus() {
        return status;
    }

    public String getBanner() {
        return banner;
    }

    public String getDescription() {
        return description;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getExaminee() {
        return examinee;
    }

    public String getOwner() {
        return owner;
    }

    public String getRule() {
        return rule;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(banner);
        dest.writeString(description);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeInt(status);
        dest.writeString(owner);
        dest.writeString(examinee);
        dest.writeString(rule);
        dest.writeStringArray(images);
        dest.writeTypedList(round);
        dest.writeInt(isMultilRound);
    }
}
