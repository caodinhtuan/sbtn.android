package onworld.sbtn.utils;


import onworld.sbtn.BuildConfig;

/**
 * Created by onworldtv on 9/12/15.
 */
public class URL {
    //    public static final String VERSION_CODE = "v1.7/";
//    public static final String APP_NAME = "sbtn/";
    //Domain Dev
    //public static final String DOMAIN_NAME = "http://dev.ottapi.com/" + VERSION_CODE + APP_NAME;
    //Domain Product
//    public static final String DOMAIN_NAME = "http://ottapi.com/" + VERSION_CODE + APP_NAME;
    public static final String DOMAIN_NAME = BuildConfig.BASE_SERVICE_URL + BuildConfig.SERVICE_URL_VERSION_CODE + BuildConfig.SERVICE_URL_APP_NAME;

    public static final String SEARCH = DOMAIN_NAME + "search&keyword=";
    public static final String HOME = DOMAIN_NAME + "home";
    public static final String DETAIL = DOMAIN_NAME + "detail/index/";
    public static final String LANGUAGE = DOMAIN_NAME + "language";
    public static final String MENU = DOMAIN_NAME + "home/menu";
    public static final String PROVIDER = DOMAIN_NAME + "contentgroup&lang_id=1&provider_id=";
    public static final String CATEGORY = DOMAIN_NAME + "contentgroup&lang_id=1&category_id=";
    public static final String VOTING_PROGRAM_LIST = DOMAIN_NAME + "voting";
    public static final String VOTING_ROUND_DETAIL = DOMAIN_NAME + "examinee&r_id=";
    public static final String VOTING_PROGRAM_DETAIL = DOMAIN_NAME + "voting/detail/";
    public static final String VOTING_CANDIDATE_DETAIL = DOMAIN_NAME + "examinee/detail&id=";
    public static final String SIGN_UP = DOMAIN_NAME + "member/register";
    public static final String SIGN_IN = DOMAIN_NAME + "login";
    public static final String VOTING = DOMAIN_NAME + "vote";
    public static final String DOWNLOAD = DOMAIN_NAME + "download";
    public static final String WEB_FORGOT_PASSWORD = "http://dev.sbtnapp.com/en/user/forgot_password";

    public static final String TRACKING_START = DOMAIN_NAME + "tracking/start";
    public static final String TRACKING_END = DOMAIN_NAME + "tracking/end";
    public static final String LOGIN_FACEBOOK = DOMAIN_NAME + "fblogin";
    public static final String UPDATE_PAYMENT = DOMAIN_NAME + "payment/success";
    public static final String PACKAGE_PROMOTION = DOMAIN_NAME + "payment/promotion";
    public static final String PACKAGE_LIST = DOMAIN_NAME + "package/listall";
    public static final String PACKAGE_LIST_DETAIL = DOMAIN_NAME + "package/listcontentbypackage?pk_id=";
    public static final String PACKAGE_LIST_PURCHASED = DOMAIN_NAME + "package/listpayment";
    public static final String PACKAGE_LIST_CONTENT = DOMAIN_NAME + "package/mycontents  ";

    public static final String MEMBER_END_VIEW = DOMAIN_NAME + "member/endview";
    public static final String PING_SERVER = DOMAIN_NAME + "index/ping";

    public static final String GOOGLE_PLAY_APP_LINK = "https://play.google.com/store/apps/details?id=onworld.sbtn";
}
