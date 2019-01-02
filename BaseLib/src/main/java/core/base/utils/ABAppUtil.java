package core.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import core.base.application.ABApplication;
import core.base.log.L;
import core.base.system.SystemBarTintManager;
import rx.functions.Action1;

/**
 * 当前程序是否后台运行
 * 当前手机是否处于睡眠
 * 当前网络是否已连接
 * 当前网络是否wifi状态
 * 安装apk
 * 初始化view的高度
 * 初始化view的高度后不可见
 * 判断是否为手机
 * 获取屏幕宽度
 * 获取屏幕高度
 * 获取设备的IMEI
 * 获取设备的mac地址
 * 获取当前应用的版本号
 * 收集设备信息并以Properties返回
 * 收集设备信息并以String返回
 */
public class ABAppUtil {

    private static final String TAG = ABAppUtil.class.getSimpleName();


    /**
     * 创建一个唯一的快捷方式
     * <p>
     * <-创建快捷方式权限
     * <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
     * <uses-permission android:name="com.android.launcher.permission.UNINSTALL_SHORTCUT" />
     *
     * @param context
     * @param appNameId
     * @param ic_launcherId
     * @param launcherActivity
     */
    public static void createOnlyShortcut(Context context, int appNameId, int ic_launcherId, Class launcherActivity) {
        ABPrefsUtil.init(context, "shortcut", Context.MODE_PRIVATE);
        ABPrefsUtil abPrefsUtil = ABPrefsUtil.getPrefsUtil("shortcut");
        if (abPrefsUtil.getBoolean("isFirst", true)) { //是第一次启动应用
            deleteShortCut(context, appNameId); //防止应用被清空数据的判断标识位为true，可桌面已有图标的情况
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(appNameId));
            shortcut.putExtra("duplicate", false);//设置是否重复创建
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClass(context, launcherActivity);//设置第一个页面
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, ic_launcherId);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            context.sendBroadcast(shortcut);
            abPrefsUtil.putBoolean("isFirst", false).commit();
        }
    }

    /**
     * 删除快捷方式
     */
    public static void deleteShortCut(Context activity, int appNameId) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        //快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, activity.getString(appNameId));
        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        activity.sendBroadcast(shortcut);
    }


    /**
     * 判断当前应用程序是否后台运行
     * 在android5.0以上失效！请使用isApplicationBackground()方法代替！
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Deprecated
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    L.d(TAG, "后台程序: " + appProcess.processName);
                    return true;
                } else {
                    L.d(TAG, "前台程序: " + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断当前应用程序处于前台还是后台
     * 需要添加权限: <uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static boolean isApplicationBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                L.d(TAG, "isBackground: " + true);
                return true;
            }
        }
        L.d(TAG, "isBackground: " + false);
        return false;
    }

    /**
     * @return boolean 返回类型
     * @throws
     * @Title: isFirstRun
     * @说 明:判断程序是否第一次运行
     * @参 数: @param context
     * @参 数: @return
     */
    public static boolean isFirstRun(Context context) {
        boolean isFirstRun = false;
        SharedPreferences sp = context.getSharedPreferences("isFirst", Context.MODE_PRIVATE);

        int versionCode = sp.getInt("versionCode", 0);
        int newAppVersion = getAppVersionCode();
        if (versionCode != newAppVersion) {
            sp.edit().putInt("version", newAppVersion).commit();
            isFirstRun = true;
        }
        return isFirstRun;
    }

    /**
     * 判断手机是否处理睡眠
     *
     * @param context
     * @return
     */
    public static boolean isSleeping(Context context) {
        KeyguardManager kgMgr = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
        L.d(TAG, isSleeping ? "手机睡眠中.." : "手机未睡眠...");
        return isSleeping;
    }

    /**
     * 检查网络是否已连接
     *
     * @param context
     * @return
     * @author com.tiantian
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前是否是wifi状态
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 安装apk
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    /**
     * 初始化View的高宽
     *
     * @param view
     */
    @Deprecated
    public static void initViewWH(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                System.out.println(view + ", width: " + view.getWidth() + "; height: " + view.getHeight());
            }
        });

    }

    /**
     * 初始化View的高宽并显示不可见
     *
     * @param view
     */
    @Deprecated
    public static void initViewWHAndGone(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                view.setVisibility(View.GONE);
            }
        });

    }


    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";

    /**
     * 判断是否为手机
     *
     * @param context
     * @return
     * @author wangjie
     */
    public static boolean isPhone(Context context) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephony.getPhoneType();
        if (type == TelephonyManager.PHONE_TYPE_NONE) {
            L.i(TAG, "Current device is Tablet!");
            return false;
        } else {
            L.i(TAG, "Current device is phone!");
            return true;
        }
    }

    /**
     * 获得设备的屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getDeviceWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getWidth();
    }

    /**
     * 获得设备的屏幕高度
     *
     * @param context
     * @return
     */
    public static int getDeviceHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getHeight();
    }


    /**
     * 获取设备id（IMEI）
     *
     * @param appContext 请使用Application context
     * @return "" & String
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static String getDeviceIMEI(Activity appContext) {

        String deviceId = "";

        //未获取权限返回空字符串
        if (!getUserPermission(appContext, android.Manifest.permission.READ_PHONE_STATE))
            return deviceId;

        if (isPhone(appContext)) {
            TelephonyManager telephony = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephony.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        }
        L.d(TAG, "当前设备IMEI码: " + deviceId);
        return deviceId;
    }


    /**
     * 是否获得用户权限许可
     *
     * @param activity   当前Activity的上下文引用
     * @param permission 权限引用ID,edg:Manifest.permission.CAMERA
     * @return 权限是否可用
     */
    public static boolean getUserPermission(Activity activity, String... permission) {
        final boolean[] isAllow = new boolean[1];
        isAllow[0] = false;
        try {
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions.request(permission).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean permission) {
                    isAllow[0] = permission;
                }
            });
            /*RxPermissions.getInstance(appContext).request(permission).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean permission) {
                    isAlow[0] = permission;
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAllow[0];
    }

    /**
     * 获取设备mac地址
     *
     * @param context
     * @return
     * @author wangjie
     */
    public static String getMacAddress(Context context) {
        String macAddress;
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        macAddress = info.getMacAddress();
        L.d(TAG, "当前mac地址: " + (null == macAddress ? "null" : macAddress));
        if (null == macAddress) {
            return "";
        }
        macAddress = macAddress.replace(":", "");
        return macAddress;
    }

    /**
     * 获取当前应用程序的版本名
     *
     * @return
     * @author wangjie
     */
    public static String getAppVersionName() {
        String versionName = "1.0";
        try {
            Context context = ABApplication.getInstance();
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            L.e(TAG, "getAppVersion", e);
        }

        return versionName;
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @return
     * @author wangjie
     */
    public static int getAppVersionCode() {
        int version = 1;
        try {
            Context context = ABApplication.getInstance();
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            L.e(TAG, "该应用的版本号: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            L.e(TAG, "getAppVersion", e);
        }

        return version;
    }

    /**
     * 收集设备信息
     *
     * @param context
     */
    public static Properties collectDeviceInfo(Context context) {
        Properties mDeviceCrashInfo = new Properties();
        try {
            // Class for retrieving various kinds of information related to the
            // application packages that are currently installed on the device.
            // You can find this class through getPackageManager().
            PackageManager pm = context.getPackageManager();
            // getPackageInfo(String packageName, int flags)
            // Retrieve overall information about an application package that is installed on the system.
            // public static final int GET_ACTIVITIES
            // Since: API Level 1 PackageInfo flag: return information about activities in the package in activities.
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                // public String versionName The version name of this package,
                // as specified by the <manifest> tag's versionName attribute.
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
                // public int versionCode The version number of this package,
                // as specified by the <manifest> tag's versionCode attribute.
                mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            L.e(TAG, "Error while collect package info", e);
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        // 返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                // setAccessible(boolean flag)
                // 将此对象的 accessible 标志设置为指示的布尔值。
                // 通过设置Accessible属性为true,才能对私有变量进行访问，不然会得到一个IllegalAccessException的异常
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), field.get(null));
//                if (DEBUG) {
                L.d(TAG, field.getName() + " : " + field.get(null));
//                }
            } catch (Exception e) {
                L.e(TAG, "Error while collect crash info", e);
            }
        }

        return mDeviceCrashInfo;
    }

    /**
     * 收集设备信息
     *
     * @param context
     * @return
     */
    public static String collectDeviceInfoStr(Context context) {
        Properties prop = collectDeviceInfo(context);
        Set deviceInfos = prop.keySet();
        StringBuilder deviceInfoStr = new StringBuilder("{\n");
        for (Iterator iter = deviceInfos.iterator(); iter.hasNext(); ) {
            Object item = iter.next();
            deviceInfoStr.append("\t\t\t" + item + ":" + prop.get(item) + ", \n");
        }
        deviceInfoStr.append("}");
        return deviceInfoStr.toString();
    }

    /**
     * 是否有SDCard
     *
     * @return
     */
    public static boolean haveSDCard() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }


    /**
     * 检查是否是网络链接
     *
     * @param url
     * @return
     */
    public static boolean checkURL(String url) {
        if (isNull(url)) {
            return false;
        }
        String regular = "(http|https)://[\\S]*";
        return Pattern.matches(regular, url);
    }

    /**
     * 判断对象是否为空
     */
    public static boolean isNull(Object strObj) {
        String str = strObj + "";
        if (!"".equals(str) && !"null".equals(str)) {
            return false;
        }
        return true;
    }

    /**
     * 隐藏软键盘
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideSoftInput(Context context) {
        View view = ((Activity) context).getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 隐藏软键盘
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideSoftInput(Context context, EditText edit) {
        edit.clearFocus();
        InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    /**
     * 关闭键盘事件.
     *
     * @param context the context
     */
    public static void closeSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     *
     * @param context
     * @param edit
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void showSoftInput(Context context, EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(edit, 0);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void toggleSoftInput(Context context, EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void showSoftInput(Context context, View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    /**
     * 复制文本到手机中
     *
     * @param context
     * @param content
     */
    public static void copyToSystem(Context context, String content) {
        ClipboardManager cm = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(content);
    }

    /**
     * 获得栈顶activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    /**
     * 回到home，后台运行
     *
     * @param context
     */
    public static void goHome(Context context) {
        L.d(TAG, "返回键回到HOME，程序后台运行...");
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }

    /**
     * 获取状态栏高度＋标题栏高度
     *
     * @param activity
     * @return
     */
    public static int getTopBarHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    /**
     * 获取网络类型
     *
     * @return
     */
    public static int getNetworkType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkType();
    }

    /**
     * MCC+MNC代码 (SIM卡运营商国家代码和运营商网络代码)
     * 仅当用户已在网络注册时有效, CDMA 可能会无效
     *
     * @return （中国移动：46000 46002, 中国联通：46001,中国电信：46003）
     */
    public static String getNetworkOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperator();
    }

    /**
     * 返回移动网络运营商的名字(例：中国联通、中国移动、中国电信)
     * 仅当用户已在网络注册时有效, CDMA 可能会无效
     *
     * @return
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    //China Mobile(中国移动).China Unicom(中国联通).China Telecom(中国电信).unknown(未知)
    public static String getNetWorkOperatorCode(Context context) {
        String networkOperatorName = getNetworkOperatorName(context);
        switch (networkOperatorName) {
            case "中国移动":
                networkOperatorName = "China Mobile";
                break;
            case "中国联通":
                networkOperatorName = "China Unicom";
                break;
            case "中国电信":
                networkOperatorName = "China Telecom";
                break;
            default:
                networkOperatorName = "unknown";
                break;

        }
        return networkOperatorName;
    }


    /**
     * 返回移动终端类型
     * PHONE_TYPE_NONE :0手机制式未知
     * PHONE_TYPE_GSM :1手机制式为GSM，移动和联通
     * PHONE_TYPE_CDMA :2手机制式为CDMA，电信
     * PHONE_TYPE_SIP:3
     *
     * @return
     */
    public static int getPhoneType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType();
    }


    /**
     * 在中国，联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
     *
     * @return 2G、3G、4G、未知四种状态
     */
    public static int getNetWorkClass(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return Constants.NETWORK_CLASS_2_G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return Constants.NETWORK_CLASS_3_G;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return Constants.NETWORK_CLASS_4_G;

            default:
                return Constants.NETWORK_CLASS_UNKNOWN;
        }
    }


    /**
     * 获取当前的网络状态 ：没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
     */
    public static int getAPNType(Context context) {
        //结果返回值
        int netType = 0;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return netType;
        }
        //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 4;
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 3;
                //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                    || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 2;
            } else {
                netType = 2;
            }
        }
        return netType;
    }


    public static String getNetWorkStateName(Context context) {
        String networkInfo = "";
        int netWorkStatus = getAPNType(context);
        switch (netWorkStatus) {
            case 0:
                networkInfo = "没有网络";
                break;
            case 1:
                networkInfo = "WIFI";
                break;
            case 2:
                networkInfo = "2G";
                break;
            case 3:
                networkInfo = "3G";
                break;
            case 4:
                networkInfo = "4G";
                break;
            default:
                networkInfo = "未知网络";
                break;

        }
        return networkInfo;
    }

    /**
     * 返回状态：当前的网络链接状态
     *
     * @return 没有网络，2G，3G，4G，WIFI
     */
    public static int getNetWorkStatus(Context context) {
        int netWorkType = Constants.NETWORK_CLASS_UNKNOWN;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = Constants.NETWORK_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                netWorkType = getNetWorkClass(context);
            }
        }

        return netWorkType;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return x;
    }

    /**
     * 设置状态栏沉浸，包含顶部状态栏和底部状态栏
     *
     * @param activity
     * @param statusColor 顶部颜色
     */
    public static void setStatusImmerse(Activity activity, int statusColor, int navColor, boolean darkmode) {
        if ("m1".equals(android.os.Build.MODEL)) {
            return;
        }
        if (darkmode) {
            setMiuiStatusBarDarkMode(true, activity);
            setMeiZuStatusBarDarkIcon(activity.getWindow(), true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintColor(statusColor);
            tintManager.setStatusBarTintEnabled(darkmode);
            tintManager.setNavigationBarTintColor(navColor);
            tintManager.setNavigationBarTintEnabled(darkmode);
            setRootView(activity, true);
        }
    }

    /**
     * 设置根布局参数 是否需要fitsSystemWindows
     */
    public static void setRootView(Activity activity, boolean fitsSystemWindows) {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        rootView.setFitsSystemWindows(fitsSystemWindows);
        rootView.setClipToPadding(fitsSystemWindows);
    }

    /**
     * 设置miui状态栏黑色字体
     *
     * @param darkmode
     * @param activity
     */
    public static void setMiuiStatusBarDarkMode(boolean darkmode, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobile(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）、177
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
        String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }


    /**
     * 设置魅族状态栏黑色字体
     *
     * @param window
     * @param dark
     * @return
     */
    public static boolean setMeiZuStatusBarDarkIcon(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams e = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt((Object) null);
                int value = meizuFlags.getInt(e);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(e, value);
                window.setAttributes(e);
                result = true;
            } catch (Exception var8) {
                Log.e("StatusBar", "setStatusBarDarkIcon: failed");
            }
        }
        return result;
    }

    public static String getCacheSize(Context context) {
        long directorySize = ABFileUtil.getDirectorySize(context.getCacheDir());
        L.e(TAG, "cacheSize=" + directorySize);
        double kiloByte = directorySize / 1024.;
        if (kiloByte < 1) {
            return "没有缓存";
        }
        return ABFileUtil.formatFileSize(directorySize);
    }

    public static void clearCache(Context context) {
        ABFileUtil.clearDirectory(context.getCacheDir());
    }


    /**
     * 栈顶
     */
    public static final int STATUS_TASK_TOP = 1;
    /**
     * 栈中
     */
    public static final int STATUS_IN_TASK = 2;
    /**
     * 栈内不存在
     */
    public static final int STATUS_NO_IN_TASK = 3;

    /**
     * 获取App当前在activity栈的位置状态
     *
     * @param context
     * @param pageName
     * @return
     */
    public static int getAppStatus(Context context, String pageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);
        //判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName) || list.get(0).baseActivity.getPackageName().equals(pageName)) {
            return STATUS_TASK_TOP;
        } else {
            //判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName) || info.baseActivity.getPackageName().equals(pageName)) {
                    return STATUS_IN_TASK;
                }
            }
            return STATUS_NO_IN_TASK;//栈里找不到，返回3
        }
    }

    private static long mLastClickTime;

    /**
     * 防止view被连续多次点击
     *
     * @return
     */
    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - mLastClickTime;
        if (time > 0 && time <= 500) {
            mLastClickTime = currentTime;
            return true;
        }
        mLastClickTime = currentTime;
        return false;
    }


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            L.d("gps:" + gps + ",network:" + network);
            if (gps || network) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);

        } catch (ActivityNotFoundException ex) {

            // The Android SDK doc says that the location settings activity
            // may not be found. In that case show the general settings.

            // General settings activity
            intent.setAction(Settings.ACTION_SETTINGS);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
            }
        }
        // Intent GPSIntent = new Intent();
        // GPSIntent.setClassName("com.android.settings",
        // "com.android.settings.widget.SettingsAppWidgetProvider");
        // GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        // GPSIntent.setData(Uri.parse("custom:3"));
        // try {
        // PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        // } catch (CanceledException e) {
        // e.printStackTrace();
        // }
    }

    /**
     * Get Mobile Type * * @return
     */
    private static String getMobileType() {
        return Build.MANUFACTURER;
    }

    /**
     * GoTo Open Self Setting Layout * Compatible Mainstream Models 兼容市面主流机型 * * @param context
     */
    public static void jumpStartInterface(Context context) {
        Intent intent = new Intent();
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("HLQ_Struggle", "******************当前手机型号为：" + getMobileType());
            ComponentName componentName = null;
            if (getMobileType().equals("Xiaomi")) { // 红米Note4测试通过
                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter" +
                        ".autostart.AutoStartManagementActivity");
            } else if (getMobileType().equals("Letv")) { // 乐视2测试通过
                intent.setAction("com.letv.android.permissionautoboot");
            } else if (getMobileType().equals("samsung")) { // 三星Note5测试通过
                componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui" +
                        ".ram.AutoRunActivity");
            } else if (getMobileType().equals("HUAWEI")) { // 华为测试通过
                componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager" +
                        ".optimize.process.ProtectActivity");
            } else if (getMobileType().equals("vivo")) { // VIVO测试通过
                componentName = ComponentName.unflattenFromString("com.iqoo.secure/.safeguard" +
                        ".PurviewTabActivity");
            } else if (getMobileType().equals("Meizu")) { //万恶的魅族
                // 通过测试，发现魅族是真恶心，也是够了，之前版本还能查看到关于设置自启动这一界面，系统更新之后，完全找不到了，心里默默Fuck！
                // 针对魅族，我们只能通过魅族内置手机管家去设置自启动，所以我在这里直接跳转到魅族内置手机管家界面，具体结果请看图
                componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission" +
                        ".PermissionMainActivity");
            } else if (getMobileType().equals("OPPO")) { // OPPO R8205测试通过
                componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup" +
                        ".StartupAppListActivity");
            } else if (getMobileType().equals("ulong")) { // 360手机 未测试
                componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun" +
                        ".AutoRunListActivity");
            } else {
                // 以上只是市面上主流机型，由于公司你懂的，所以很不容易才凑齐以上设备
                // 针对于其他设备，我们只能调整当前系统app查看详情界面
                // 在此根据用户手机当前版本跳转系统设置界面
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            }
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {//抛出异常就直接打开设置页面
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

}
