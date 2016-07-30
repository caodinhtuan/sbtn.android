package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by linhnguyen on 10/8/15.
 */
public class Drawer {
    int error;
    String message;

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    private ArrayList<DrawerItem> provider;
    private ArrayList<DrawerItem> category;

    public ArrayList<DrawerItem> getCategory() {
        return category;
    }

    public ArrayList<DrawerItem> getProvider() {
        return provider;
    }
}
