package onworld.sbtn.josonmodel;

/**
 * Created by onworldtv on 11/27/15.
 */
public class AccountInfoData {
    private String accessToken;
    private String avatarUrl;
    private String fullName;
    private int memberId;

    public int getMemberId() {
        return memberId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }
}
