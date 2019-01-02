package core.base.cache;

import com.google.gson.Gson;

/**
 * 缓存工具类
 * Created by Administrator on 2017/12/5.
 */

public final class CacheUtil {

    public static final String KEY_HOME_BANNER = "homeBanner";
    public static final String KEY_HOME_INDEX_MODEL = "homeIndexModel";


    private CacheUtil() {
    }

    public static boolean isExist(String key) {
        return CacheManager.getInstance().contains(key);
    }

    public static void addDataToCache(String key, Object data) {
        if (!isExist(key)) {
            CacheManager.getInstance().putFileCache(key, new Gson().toJson(data), CacheItem.CACHE_VALID_TIME);
        }
    }

    public static String getCacheData(String key) {
        return CacheManager.getInstance().getFileCache(key);
    }

    public static void clearAllApplicationCacheData() {
        CacheManager.getInstance().clearAllCacheData();
    }

}
