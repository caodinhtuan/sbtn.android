package onworld.sbtn.josonmodel;

import onworld.sbtn.items.Item;

/**
 * Created by Ravi on 29/07/15.
 */
public class NavDrawerItem implements Item {
    private int id;
    private boolean showNotify;
    private String title;
    private int navImage;


    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title, int navImage) {
        this.showNotify = showNotify;
        this.title = title;
        this.navImage = navImage;

    }

    public NavDrawerItem(String title, int navImage) {
        this.title = title;
        this.navImage = navImage;

    }

    public int getNavImage() {
        return navImage;
    }

    public void setNavImage(int navImage) {
        this.navImage = navImage;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
