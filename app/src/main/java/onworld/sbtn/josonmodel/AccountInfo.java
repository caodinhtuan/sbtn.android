package onworld.sbtn.josonmodel;

/**
 * Created by onworldtv on 11/4/15.
 */
public class AccountInfo {
    private int code;
    private String message;
    private AccountInfoData data;

    public AccountInfoData getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
