package core.base.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.util.HashSet;
import java.util.Set;

import core.base.BuildConfig;
import core.base.cache.CacheManager;
import core.base.exception.ABCrashHandler;
import core.base.log.L;
import core.base.utils.ABPrefsUtil;

/**
 * 基础的application类
 */
public class ABApplication extends Application {

    //状态栏颜色
    int statusColor;

    @SuppressLint("StaticFieldLeak")
    private static ABApplication instance;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    public static ABApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        instance = this;
        L.init(BuildConfig.DEBUG);
        CacheManager.getInstance().initCacheDir();
        // initCrashHandler(); // 初始化程序崩溃捕捉处理
        // 初始化SharedPreference
        initPrefs();
        initFresco();
        //友盟统计日志加密
        //MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //MobclickAgent.enableEncrypt(false);
    }


    private void initFresco() {
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        Set<RequestListener> listeners = new HashSet<>();
        listeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setRequestListeners(listeners)
                .build();
        Fresco.initialize(this, config);
    }


    public void saveMd5Pwd(String md5Pwd) {
        ABPrefsUtil.getPrefsUtil("encrypt_prefs").putString("md5Pwd", md5Pwd).commit();
    }

    public String getMd5Pwd() {
        return ABPrefsUtil.getPrefsUtil("encrypt_prefs").getString("md5Pwd", "");
    }


    /**
     * 初始化程序崩溃捕捉处理
     */
    protected void initCrashHandler() {
        ABCrashHandler.init(getApplicationContext());
    }

    /**
     * 初始化SharedPreference
     */
    protected void initPrefs() {
        //SP.init(getApplicationContext());
        ABPrefsUtil.init(this, "encrypt_prefs", MODE_PRIVATE);
    }

    /**
     * 配置状态栏颜色
     */
    public int getStatusColor() {
        return statusColor;
    }
}
