package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by onworldtv on 12/25/15.
 */
public class DataMenu {
    private String type;
    private ArrayList<DataMenuItem> items;
    private boolean encrypt;

    public boolean getEncrypt() {
        return encrypt;
    }

    public ArrayList<DataMenuItem> getItems() {
        return items;
    }

    public String getType() {
        return type;
    }
}
