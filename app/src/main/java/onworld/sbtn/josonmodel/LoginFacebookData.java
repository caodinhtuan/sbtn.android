package onworld.sbtn.josonmodel;

/**
 * Created by onworldtv on 12/14/15.
 */
public class LoginFacebookData {
    String token;
    int mem_id;
    String avatar;
    String fullname;

    public int getMem_id() {
        return mem_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFullname() {
        return fullname;
    }


    public String getAccessToken() {
        return token;
    }
}
