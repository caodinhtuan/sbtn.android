package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by onworldtv on 11/19/15.
 */
public class VotingCandidateData {
    public VotingCandidateData(){

    }

    private int totalPage;
    private int currentPage;
    ArrayList<VotingCandidateListData> data;

    public ArrayList<VotingCandidateListData> getData() {
        return data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }
}
