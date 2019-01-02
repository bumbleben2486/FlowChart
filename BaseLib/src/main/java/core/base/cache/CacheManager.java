package core.base.cache;

import android.os.Environment;


import java.io.File;

import core.base.utils.BaseUtils;


public final class CacheManager {

    /**
     * 文件缓存的目录
     */
    public static final String APP_CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/mdd/data/";

    /**
     * 文件缓存的最小大小
     */
    public static final long SDCARD_MIN_SPACE = 1024 * 1024 * 10;

    private static CacheManager cacheManager;

    private CacheManager() {

    }

    public static synchronized CacheManager getInstance() {
        if (CacheManager.cacheManager == null) {
            CacheManager.cacheManager = new CacheManager();
        }
        return CacheManager.cacheManager;
    }

    public String getFileCache(final String key) {
        String md5Key = BaseUtils.getMd5(key);
        if (contains(md5Key)) {
            final CacheItem cacheItem = getFromDiskCache(md5Key);
            if (cacheItem != null) {
                return cacheItem.getData();
            }
        }
        return null;
    }

    public void putFileCache(final String key, final String data, long expiredTime) {
        String md5Key = BaseUtils.getMd5(key);
        final CacheItem cacheItem = new CacheItem(md5Key, data, expiredTime);
        putIntoCache(cacheItem);
    }

    public boolean contains(final String key) {
        final File file = new File(APP_CACHE_PATH + key);
        return file.exists();
    }


    public void initCacheDir() {
        try {
            // sdcard已经挂载并且空间不小于10M，可以写入文件,小于10M时，清除缓存
            if (BaseUtils.sdcardMounted()) {
                if (BaseUtils.getSDSize() < SDCARD_MIN_SPACE) {
                    clearAllCacheData();
                } else {
                    final File dir = new File(APP_CACHE_PATH);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将CacheItem对象从磁盘中读取出来
     *
     * @param key 根据key读取
     * @return 读取出来的对象
     */
    synchronized CacheItem getFromDiskCache(final String key) {
        CacheItem cacheItem = null;
        Object findItem = BaseUtils.restoreObject(APP_CACHE_PATH + key);
        if (findItem != null) {
            cacheItem = (CacheItem) findItem;
        }
        if (cacheItem == null) {
            return null;
        }
        long expiresTime = System.currentTimeMillis();
        if (expiresTime > cacheItem.getTimeStamp()) {
            clearAllCacheData();
            return null;
        }
        return cacheItem;
    }

    /**
     * 将CacheItem对象缓存到磁盘
     *
     * @param item 要缓存的对象
     * @return 是否缓存 true代表缓存成功 false代表缓存失败
     */
    synchronized boolean putIntoCache(final CacheItem item) {
        if (BaseUtils.getSDSize() > SDCARD_MIN_SPACE) {
            BaseUtils.saveObject(APP_CACHE_PATH + item.getKey(), item);
            return true;
        }
        return false;
    }

    public void clearAllCacheData() {
        File file = null;
        File[] files = null;
        if (BaseUtils.sdcardMounted()) {
            file = new File(APP_CACHE_PATH);
            files = file.listFiles();
            if (files != null) {
                for (File file2 : files) {
                    file2.delete();
                }
            }
        }
    }
}
