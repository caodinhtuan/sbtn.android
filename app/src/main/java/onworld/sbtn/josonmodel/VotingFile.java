package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by linhnguyen on 11/22/15.
 */
public class VotingFile {
    public ArrayList<Advertisement> adv;
    private String roundName;
    private double price;
    private String subject;
    private int fileID;
    private int fileType;
    private int roundId;
    private String[] photos;
    private int voteNum;
    private int roundStatus;
    private int voteType;

    public int getVoteType() {
        return voteType;
    }

    private FileCandidate files;

    public int getRoundStatus() {
        return roundStatus;
    }

    public int getVoteNum() {
        return voteNum;
    }

    public ArrayList<Advertisement> getAdv() {
        return adv;
    }

    public String[] getPhotos() {
        return photos;
    }

    public FileCandidate getFiles() {
        return files;
    }

    public int getRoundId() {
        return roundId;
    }

    public double getPrice() {
        return price;
    }

    public int getFileID() {
        return fileID;
    }

    public int getFileType() {
        return fileType;
    }

    public String getRoundName() {
        return roundName;
    }

    public String getSubject() {
        return subject;
    }

}
