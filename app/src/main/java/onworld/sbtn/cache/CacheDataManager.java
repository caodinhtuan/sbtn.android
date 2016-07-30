package onworld.sbtn.cache;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onworld.sbtn.josonmodel.ASHomeDataObject;
import onworld.sbtn.josonmodel.CategoryIdList;
import onworld.sbtn.josonmodel.LanguageItem;
import onworld.sbtn.josonmodel.PackagesData;
import onworld.sbtn.josonmodel.ShowsHome;

/**
 * Created by onworldtv on 5/30/16.
 */
public class CacheDataManager {
    public static final String CATEGORY_SHOWES_TITLE = "Shows";
    public static final String CATEGORY_SERIES_TITLE = "Series";
    public static final String CATEGORY_LIVETV_TITLE = "Live tv";
    public static final String CATEGORY_KARAOKE_TITLE = "KARAOKE";
    public static final String CATEGORY_MUSIC_TITLE = "MUSIC";
    public static final String CATEGORY_MUSIC_VIDEO_TITLE = "MUSIC VIDEO";
    public static final String CATEGORY_RADIO_TITLE = "RADIO";
    private static final Object mLock = new Object();
    private static CacheDataManager instance;
    private String deviceId;
    private Context mContext;
    private ASHomeDataObject mASHomeDataObject;
    private PackagesData mPackagesData;
    private ShowsHome mYourPackageContent;
    private CategoryIdList mCategoryIdList;
    private ArrayList<LanguageItem> language;
    private Map<Integer, ShowsHome> mapsCategoryData;
    private Map<Integer, ShowsHome> mapsProviderData;

    public CacheDataManager(Context context) {
        this.mContext = context;
    }

    public static CacheDataManager getInstance(Context pContext) {
        synchronized (mLock) {
            if (instance == null) {
                instance = new CacheDataManager(pContext);
            }
            return instance;
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void cacheDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void cacheHomeData(ASHomeDataObject asHomeDataObject) {
        this.mASHomeDataObject = asHomeDataObject;
    }

    public ASHomeDataObject getCacheHomeData() {
        return mASHomeDataObject;
    }

    public PackagesData getCachePackageData() {
        if (mPackagesData == null) return null;
        return mPackagesData;
    }

    public void cachePackageData(PackagesData packagesData) {
        this.mPackagesData = packagesData;
    }

    public void clearCachePackageData() {
        if (mPackagesData != null) {
            this.mPackagesData = null;
        }

    }

    public ShowsHome getCacheYourPackageContent() {
        if (mYourPackageContent == null) return null;
        return mYourPackageContent;
    }

    public void cacheYourPackageContent(ShowsHome yourPackageContent) {
        this.mYourPackageContent = yourPackageContent;
    }

    public void clearCacheYourContent() {
        if (mYourPackageContent != null) {
            mYourPackageContent = null;
        }
    }

    public ShowsHome getCacheCategoryData(int categoryId) {
        if (mapsCategoryData == null) return null;
        return mapsCategoryData.get(categoryId);
    }

    public void cacheCategoryData(int categoryId, ShowsHome showsHome) {
        if (mapsCategoryData == null) {
            mapsCategoryData = new HashMap<>();
        }
        mapsCategoryData.put(categoryId, showsHome);
    }

    public void cacheProviderData(int providerId, ShowsHome showsHome) {
        if (mapsProviderData == null) {
            mapsProviderData = new HashMap<>();
        }
        mapsProviderData.put(providerId, showsHome);
    }

    public ShowsHome getCacheProviderData(int providerId) {
        if (mapsProviderData == null) return null;
        return mapsProviderData.get(providerId);
    }

    public CategoryIdList getCategoryIdList() {
        return mCategoryIdList;
    }

    public void cacheCategoryIdList(CategoryIdList categoryIdList) {
        this.mCategoryIdList = categoryIdList;
    }

    public void cacheLanguageOfContent(ArrayList<LanguageItem> language) {
        this.language = language;
    }

    public ArrayList<LanguageItem> getCacheLanguageOfContent() {
        return language;
    }

}
