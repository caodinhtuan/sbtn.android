package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by onworldtv on 10/7/15.
 */
public class Language {
    private int error;
    private String message;
    private ArrayList<LanguageItem> items;

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<LanguageItem> getLanguageItems() {
        return items;
    }
}
