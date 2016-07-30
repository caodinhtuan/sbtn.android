package onworld.sbtn.josonmodel;

import onworld.sbtn.items.Item;

/**
 * Created by linhnguyen on 10/8/15.
 */
public class DrawerItem implements Item{
    private int id;
    private String name;
    private int navImage;
    boolean isProvider;

    public void setIsProvider(boolean isProvider) {
        this.isProvider = isProvider;
    }
    public boolean getIsProvider(){
        return isProvider;
    }

    public int getNavImage() {
        return navImage;
    }

    public void setNavImage(int navImage) {
        this.navImage = navImage;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
