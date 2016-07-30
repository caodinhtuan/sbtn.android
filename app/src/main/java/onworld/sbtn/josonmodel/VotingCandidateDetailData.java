package onworld.sbtn.josonmodel;

/**
 * Created by linhnguyen on 11/22/15.
 */
public class VotingCandidateDetailData {
    private VotingUser users;
    private VotingFile file;
    private String firstName;
    private String lastName;
    private String gender ;
    private String dob;
    private String phone;
    private String address;
    private String email;
    private String description;
    private String avatarUrl;
    private  int examineeId;
    private double totalVote;
    private float votePercent;
    private boolean showResult;
    public double getTotalVote() {
        return totalVote;
    }

    public float getVotePercent() {
        return votePercent;
    }
    public boolean getShowResult(){
        return showResult;
    }

    public int getExamineeId() {
        return examineeId;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public VotingFile getFile() {
        return file;
    }

    public VotingUser getUsers() {
        return users;
    }

    public String getAddress() {
        return address;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
