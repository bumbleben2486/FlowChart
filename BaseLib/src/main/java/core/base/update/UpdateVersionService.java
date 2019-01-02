package core.base.update;

/**
 * 启动下载的后台服务，通过广播接收下载完成的通知
 */

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

import core.base.log.L;
import core.base.log.T;
import core.base.manager.AppManager;

import static core.base.BuildConfig.APPLICATION_ID;

public class UpdateVersionService extends Service {
    public static final String DOWNLOAD_APK_NAME = "mdd.apk";
    public static final int NOTIFICATION_DOWNLOAD_ID = 1453;
    private String DOWNLOADPATH = "/apk/";//下载路径，如果不定义自己的路径，6.0的手机不自动安装
    private String url;
    /**
     * 安卓系统下载类
     **/
    DownloadManager manager;
    /**
     * 接收下载完的广播
     **/
    DownloadCompleteReceiver receiver;

    /**
     * 初始化下载器
     **/
    private void initDownManager() {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(url));
        L.i("转换过的：" + Uri.parse(url));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载路径和文件名
        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DOWNLOAD_APK_NAME);
        down.setDescription("美嘀嘀更新");
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        down.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        down.allowScanningByMediaScanner();
        // 将下载请求放入队列
        manager.enqueue(down);
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra("url");
        L.e("未转换:" + url);
        //url = "http://app.mdd88.cn/index.php/Mddapk/download";
        if (!TextUtils.isEmpty(url)) {
            // 调用下载
            initDownManager();
        }
        //意外杀死后，不在重启服务
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    //获取下载的文件id
                    long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    Uri downloadFileUri = manager.getUriForDownloadedFile(downId);
                    if (downloadFileUri != null) {
                        //自动安装apk
                        installAPK(context, downloadFileUri);
                        //install(context,(File)msg.obj);
                        //停止服务并关闭广播
                        UpdateVersionService.this.stopSelf();
                        AppManager.getAppManager().finishAllActivity();
                    } else {
                        T.s(context, "下载失败");
                    }
                }
            }
        }

        /**
         * 安装apk文件
         */
        private void installAPK(Context context, Uri apk) {
            if (apk != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);//动作
                intent.addCategory(Intent.CATEGORY_DEFAULT);//类型
                intent.setDataAndType(apk, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        }
    }

    //---------------------- 解决Android6.0安装APK闪退问题 ---------
   /* private void installAPK(Uri apk, Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(apk);
            intents.setDataAndType(apk, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intents);
        } else {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +DOWNLOADPATH+ "mdd.apk");
            if (file.exists()) {
                openFile(file, context);
            }
        }
    }

    public void openFile(File file, Context context) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }

    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }*/
    //---------------------- Android 6.0 end ---------------------

    /**
     * 通过隐式意图调用系统安装程序安装APK
     */
    public static void install(Context context, File apkfile) {
        File file = apkfile;
//                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                , "myApp.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, APPLICATION_ID + ".FileProvider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
