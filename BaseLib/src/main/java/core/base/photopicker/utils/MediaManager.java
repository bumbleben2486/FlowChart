package core.base.photopicker.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import core.base.log.L;
import core.base.photopicker.beans.MediaBean;
import core.base.photopicker.beans.MediaFloder;
import core.base.photopicker.beans.SelectImageEvent;


/**
 * @Class: MediaManager
 * @Description:
 * @author: lling(www.liuling123.com)
 * @Date: 2015/11/4
 */
public class MediaManager {
    private Uri imageUri;
    private Uri thumbnailImageUri;
    private Uri albumsImageUri;
    private Uri videoUri;
    private static final String TAG = "MediaManager";
    public final static String ALL_PHOTO = "所有图片";
    private static Map<String, MediaFloder> mediaMap;
    //存放已选中的Photo数据
    private static List<MediaBean> selectMediaBeans;//快速删除和添加;
    private static MediaManager instance;
    /**
     * 照片选择标示
     */
    private static String mFlag;
    private static int pageTag;

    public static synchronized MediaManager getInstance() {
        if (instance == null) {
            instance = new MediaManager();
        }
        return instance;
    }

    private MediaManager() {
    }

    /**
     * 初始化各种uri
     */
    public void init(Context context, String flag, int tag) {
        mediaMap = new HashMap<>();
        mFlag = flag;
        pageTag = tag;
        selectMediaBeans = new LinkedList<>();//快速删除和添加;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            L.e(TAG, "无外部存储!!!");
            imageUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            albumsImageUri = MediaStore.Audio.Albums.INTERNAL_CONTENT_URI;
            videoUri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
        } else {
            imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            albumsImageUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
    }

    public static List<MediaBean> getSelectMediaBeans() {
        return selectMediaBeans;
    }


    public static MediaBean findBeadById(int id) {
        for (MediaBean b : selectMediaBeans) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    public static Map<String, MediaFloder> getMediaMap() {
        return mediaMap;
    }

    public static MediaFloder getMediaFloder(String floderName) {
        return mediaMap.get(floderName);
    }

    public void initPhotos(Context context) {
        try {
            String allPhotosKey = "所有图片";
            MediaFloder allFloder = new MediaFloder();
            allFloder.setName(allPhotosKey);
            allFloder.setDirPath(allPhotosKey);
            allFloder.setMediaBeanList(new ArrayList<MediaBean>());
            mediaMap.put(allPhotosKey, allFloder);

            ContentResolver mContentResolver = context.getContentResolver();
            String[] projection = {MediaStore.Images.Media._ID, //图片id
                    MediaStore.Images.Thumbnails.DATA,//图片的真实路径
                    MediaStore.Images.Media.DISPLAY_NAME, //图片的名字
                    MediaStore.Images.Media.DATE_MODIFIED, //图片最后修改日期
                    MediaStore.Images.Media.SIZE         //图片大小

            };
            // 只查询jpeg和png的图片，按修改时间的降序排序
            Cursor mCursor = mContentResolver.query(imageUri, projection,
                    MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DATE_MODIFIED + " desc");

            while (mCursor.moveToNext()) {
                //获取图片的id
                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
                // 获取图片的路径
                String realPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                // 获取图片的名字
                String name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                // 获取图片的最后修改时间
                Long date = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                // 获取图片的最后修改时间
                String size = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                // 获取该图片的父路径名
                File parentFile = new File(realPath).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();
                long photoSize = Long.valueOf(size);
                //L.printForDebug("MediaManager-size", photoSize);
                MediaBean mediaBean = new MediaBean(id, true, false, date, photoSize, size, dirPath, realPath, name);
                if (mediaMap.containsKey(dirPath)) {

                    MediaFloder mediaFloder = mediaMap.get(dirPath);
                    mediaFloder.getMediaBeanList().add(mediaBean);
                    mediaMap.get(allPhotosKey).getMediaBeanList().add(mediaBean);
                    continue;
                } else {
                    // 初始化imageFloder
                    MediaFloder mediaFloder = new MediaFloder();
                    List<MediaBean> mediaBeanList = new ArrayList<MediaBean>();
                    mediaBeanList.add(mediaBean);
                    mediaFloder.setMediaBeanList(mediaBeanList);
                    mediaFloder.setDirPath(dirPath);
                    mediaFloder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                    mediaMap.put(dirPath, mediaFloder);
                    mediaMap.get(allPhotosKey).getMediaBeanList().add(mediaBean);
                }
            }
            mCursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void selectOK() {
        L.e("mFlag" + mFlag);
        EventBus.getDefault().post(new SelectImageEvent(mFlag, pageTag, SelectImageEvent.STATUS_OK, selectMediaBeans));
        selectMediaBeans = null;
        instance = null;
        mediaMap = null;
    }
}
