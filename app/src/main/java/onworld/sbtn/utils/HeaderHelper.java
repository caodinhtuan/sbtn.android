package onworld.sbtn.utils;


import onworld.sbtn.security.Crypto;

/**
 * Created by Peter on 6/4/15.
 */
public class HeaderHelper {
    public static String createAuthorizationValue(String httpVerb, String date, String accessToken, int contentLength) {
        String result = "";
        String message = String.format("%s\n%s\n%d", date, httpVerb, contentLength);
        String hash = Crypto.getEncryptedHMAC(accessToken, message);
        result = String.format("%s:%s", accessToken, hash);
        return result;
    }

    public static String createRequestTokenValue(String httpVerb, String date, String accessToken, int contentLength) {
        String message = String.format("%s\n%s\n%d", date, httpVerb, contentLength);
        String hash = Crypto.getEncryptedHMAC(accessToken, message);
        return hash;
    }
}
