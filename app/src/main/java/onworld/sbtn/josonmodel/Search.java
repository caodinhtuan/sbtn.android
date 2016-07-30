package onworld.sbtn.josonmodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by onworldtv on 10/6/15.
 */
public class Search {
    @SerializedName("error")
    private int error;
    @SerializedName("message")
    private String message;



    ArrayList<Related> view;
    ArrayList<Related> listen;

    public ArrayList<Related> getListen() {
        return listen;
    }

    public ArrayList<Related> getView() {
        return view;
    }
}
