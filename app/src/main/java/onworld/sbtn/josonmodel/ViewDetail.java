package onworld.sbtn.josonmodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Steve on 8/26/2015.
 */
public class ViewDetail {
    public ArrayList<Advertisement> adv;
    private int error = 0;
    private String message;
    private ContentModel content;
    private int permission = 0;
    private String perm_reason;

    public int getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(int permissionType) {
        this.permissionType = permissionType;
    }

    @SerializedName("permission_type")
    private int permissionType;
    @SerializedName("view_count")
    private int numberUserView;
    private ArrayList<InfoVideoArrayModel> genres;
    private ArrayList<InfoVideoArrayModel> countries;
    private ArrayList<InfoVideoArrayModel> tags;
    private ArrayList<InfoVideoArrayModel> directors;
    private ArrayList<InfoVideoArrayModel> actors;
    private ArrayList<Episode> episodes;
    private ArrayList<Related> related;
    private ArrayList<TimeLines> timelines;
    @SerializedName("package")
    private ArrayList<GroupPackage> mPackages;

    public int getNumberUserView() {
        return numberUserView;
    }

    public void setNumberUserView(int numberUserView) {
        this.numberUserView = numberUserView;
    }

    public String getMessage() {
        return message;
    }

    public int getError() {
        return error;
    }

    public ArrayList<TimeLines> getTimelines() {
        return timelines;
    }

    public ArrayList<Advertisement> getAdvertisement() {
        return adv;
    }

    public ContentModel getContent() {
        return content;
    }


    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public ArrayList<Related> getRelated() {
        return related;
    }

    public ArrayList<InfoVideoArrayModel> getCountries() {
        return countries;
    }

    public ArrayList<InfoVideoArrayModel> getGenres() {
        return genres;
    }

    public ArrayList<InfoVideoArrayModel> getTags() {
        return tags;
    }

    public ArrayList<InfoVideoArrayModel> getDirectors() {
        return directors;
    }

    public ArrayList<InfoVideoArrayModel> getActors() {
        return actors;
    }

    public int getPermission() {
        return permission;
    }

    public ArrayList<GroupPackage> getPackages() {
        return mPackages;
    }
}
