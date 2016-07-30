package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by onworldtv on 5/26/16.
 */
public class PackagesData {
    private int error;
    private String message;
    private ArrayList<GroupPackage> data;

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<GroupPackage> getData() {
        return data;
    }
}
