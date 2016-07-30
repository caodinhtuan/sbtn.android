package onworld.sbtn.josonmodel;

/**
 * Created by Steve on 8/26/2015.
 */
public class ContentModel {
    String img;
    String link;
    String name;
    private boolean isLive;
    private int id;
    private int mode;//mode = 1: video; mode = 2: audio
    private long duration;
    private int karaoke;
    private String image;
    private int type;//type = 0: single (like live tv)
    //type = 1: serries ( like phim)
    //type == 2: episode ( episode phim)
    private String description;
    private String year;

    public boolean getIsLive() {
        return isLive;
    }

    public String getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getLink() {
        return link;
    }

    public long getDuration() {
        return duration;
    }

    public String getImage() {
        return image;
    }

    public int getMode() {
        return mode;
    }

    public int getId() {
        return id;
    }

    public int getKaraoke() {
        return karaoke;
    }
}

