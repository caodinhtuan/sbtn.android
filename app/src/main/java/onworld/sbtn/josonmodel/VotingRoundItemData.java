package onworld.sbtn.josonmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by onworldtv on 11/19/15.
 */
public class VotingRoundItemData implements Parcelable{
    private int id;
    private String name;
    private int status;

    protected VotingRoundItemData(Parcel in) {
        id = in.readInt();
        name = in.readString();
        status = in.readInt();
    }

    public VotingRoundItemData(){

    }
    public static final Creator<VotingRoundItemData> CREATOR = new Creator<VotingRoundItemData>() {
        @Override
        public VotingRoundItemData createFromParcel(Parcel in) {
            return new VotingRoundItemData(in);
        }

        @Override
        public VotingRoundItemData[] newArray(int size) {
            return new VotingRoundItemData[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(status);
    }
}
