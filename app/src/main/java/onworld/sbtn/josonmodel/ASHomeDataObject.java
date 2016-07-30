package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by onworldtv on 2/1/16.
 */
public class ASHomeDataObject {
    private int error;
    private String message;
    private ArrayList<BannerItems> banners;
    private HomeItem view;
    private HomeItem listen;

    public ArrayList<BannerItems> getBanners() {
        return banners;
    }

    public HomeItem getListen() {
        return listen;
    }

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public HomeItem getView() {
        return view;
    }
}
