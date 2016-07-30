package onworld.sbtn.josonmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by onworldtv on 10/7/15.
 */
public class LanguageItem {
    private String code;
    @SerializedName("lang_id")
    private int languageId;
    @SerializedName("name")
    private String nameLanguage;

    public int getLanguageId() {
        return languageId;
    }

    public String getNameLanguage() {
        return nameLanguage;
    }
}
