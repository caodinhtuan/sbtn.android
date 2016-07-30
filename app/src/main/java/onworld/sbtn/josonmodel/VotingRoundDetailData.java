package onworld.sbtn.josonmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by onworldtv on 11/19/15.
 */
public class VotingRoundDetailData {
    public VotingRoundDetailData(){

    }
    private int id;
    private String name;
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("endDate")
    private String endDate;
    private String rule;
    private VotingCandidateData exminees;
    private boolean showResult;
    private int roundStatus;

    public int getRoundStatus() {
        return roundStatus;
    }

    public boolean getShowResult(){
        return showResult;
    }


    public int getId() {
        return id;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getName() {
        return name;
    }

    public String getRule() {
        return rule;
    }

    public String getStartDate() {
        return startDate;
    }

    public VotingCandidateData getExminees() {
        return exminees;
    }
}
