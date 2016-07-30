package onworld.sbtn.josonmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by onworldtv on 9/12/15.
 */
public class Advertisement {
    private String link;
    private String type;
    @SerializedName("start")
    private int startTime;
    @SerializedName("duration")
    private int durationTime;
    @SerializedName("skip")
    private int skipTime;
    @SerializedName("skippable_time")
    private long skippableTime;

    public int getDurationTime() {
        return durationTime;
    }

    public long getSkippableTime() {
        return skippableTime;
    }

    public int getSkipTime() {
        return skipTime;
    }

    public String getLink() {
        return link;
    }

    public int getStartTime() {
        return startTime;
    }

    public String getType() {
        return type;
    }
}
