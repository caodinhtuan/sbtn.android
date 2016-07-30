package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by Steve on 9/6/2015.
 */
public class ShowsHome {
    private int error;
    private String message;
    private boolean is_buy;


    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    ArrayList<Shows> groups;

    public ArrayList<Shows> getShowses() {
        return groups;
    }

    public boolean getIsBuy() {
        return is_buy;
    }
}
