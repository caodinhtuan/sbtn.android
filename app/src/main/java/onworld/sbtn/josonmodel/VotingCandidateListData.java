package onworld.sbtn.josonmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by onworldtv on 11/26/15.
 */
public class VotingCandidateListData {
    private int id;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    private String avatar;
    private String status;
    private float votePercent;
    private double totalVote;
    private boolean showResult;
    private int roundStatus;

    public int getRoundStatus() {
        return roundStatus;
    }

    public void setRoundStatus(int roundStatus) {
        this.roundStatus = roundStatus;
    }

    public boolean getShowResult() {
        return showResult;
    }

    public void setShowResult(boolean showResult) {
        this.showResult = showResult;
    }

    public float getVotePercent() {
        return votePercent;
    }

    public double getTotalVote() {
        return totalVote;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getStatus() {
        return status;
    }

}
