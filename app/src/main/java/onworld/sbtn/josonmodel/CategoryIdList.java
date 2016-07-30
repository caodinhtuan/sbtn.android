package onworld.sbtn.josonmodel;

/**
 * Created by onworldtv on 6/2/16.
 */
public class CategoryIdList {
    private int categorySeries;
    private int categoryShows;
    private int categoryLiveTv;
    private int categoryKaraoke;
    private int categoryMusic;
    private int categoryRadio;
    private int categoryMusicVideo;
    private boolean isProvider;

    public CategoryIdList(int categorySeries, int categoryShows, int categoryLiveTv) {
        this.categorySeries = categorySeries;
        this.categoryShows = categoryShows;
        this.categoryLiveTv = categoryLiveTv;
    }

    public CategoryIdList() {
    }

    public boolean isProvider() {
        return isProvider;
    }

    public void setProvider(boolean provider) {
        isProvider = provider;
    }

    public int getCategoryMusicVideo() {
        return categoryMusicVideo;
    }

    public void setCategoryMusicVideo(int categoryMusicVideo) {
        this.categoryMusicVideo = categoryMusicVideo;
    }

    public int getCategoryRadio() {
        return categoryRadio;
    }

    public void setCategoryRadio(int categoryRadio) {
        this.categoryRadio = categoryRadio;
    }

    public int getCategoryMusic() {
        return categoryMusic;
    }

    public void setCategoryMusic(int categoryMusic) {
        this.categoryMusic = categoryMusic;
    }

    public int getCategoryKaraoke() {
        return categoryKaraoke;
    }

    public void setCategoryKaraoke(int categoryKaraoke) {
        this.categoryKaraoke = categoryKaraoke;
    }

    public int getCategoryLiveTv() {
        return categoryLiveTv;
    }

    public void setCategoryLiveTv(int categoryLiveTv) {
        this.categoryLiveTv = categoryLiveTv;
    }

    public int getCategoryShows() {
        return categoryShows;
    }

    public void setCategoryShows(int categoryShows) {
        this.categoryShows = categoryShows;
    }

    public int getCategorySeries() {
        return categorySeries;
    }

    public void setCategorySeries(int categorySeries) {
        this.categorySeries = categorySeries;
    }
}
