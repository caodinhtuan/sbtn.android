package onworld.sbtn.josonmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by onworldtv on 11/27/15.
 */
public class SignUpData {
    @SerializedName("token")
    private String signupToken;

    public String getSignupToken() {
        return signupToken;
    }
}
