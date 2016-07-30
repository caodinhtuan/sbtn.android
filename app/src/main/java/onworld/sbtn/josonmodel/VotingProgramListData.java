package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by onworldtv on 11/19/15.
 */
public class VotingProgramListData {
    private int code;
    private String message;
    private ArrayList<VotingProgramListDetailData> data;

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public ArrayList<VotingProgramListDetailData> getData() {
        return data;
    }

}
