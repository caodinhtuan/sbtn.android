package onworld.sbtn.josonmodel;

/**
 * Created by linhnguyen on 11/22/15.
 */
public class VotingUser {
    private int isActive;
    private int isVote;
    private int isDownLoad;
    private int isPay;

    private int totalVote = 0;
    private int remainVote = 0;

    public int getRemainVote() {
        return remainVote;
    }

    public int getTotalVoteFromUser() {
        return totalVote;
    }

    public int getIsActive() {
        return isActive;
    }

    public int getIsDownLoad() {
        return isDownLoad;
    }

    public int getIsPay() {
        return isPay;
    }

    public int getIsVote() {
        return isVote;
    }
}
