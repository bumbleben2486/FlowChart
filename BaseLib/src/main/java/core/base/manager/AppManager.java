package core.base.manager;

import android.app.Activity;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import core.base.log.L;

/**
 * @类名称：AppManager
 * @类描述： 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
public class AppManager {

    private final static String TAG = AppManager.class.getSimpleName();
    private static List<Activity> activityStack = new LinkedList<Activity>();
    private static AppManager instance = new AppManager();

    private AppManager() {
    }

    public static List<Activity> getActivityStack() {
        return activityStack;
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = null;
        try {
            L.i("当前Activity总数量=" + activityStack.size());
            if (activityStack.size() > 0) {
                activity = activityStack.get(activityStack.size() - 1);
                L.i("当前的acitivity=" + activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack.size() > 0) {
            Activity activity = activityStack.get(activityStack.size() - 1);
            if (activity != null) {
                removeActivity(activity);
                activity.finish();
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            removeActivity(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }


    public void finishActivityForClearTop(Class<?> cls) {

        boolean isOpt = false;

        for (Activity aty : activityStack) {
            if (aty.getClass().equals(cls)) {
                isOpt = true;
            }
        }

        if (isOpt) {
            for (int i = activityStack.size() - 1; i >= 0; i--) {
                Activity activity = activityStack.get(i);
                if (!activity.getClass().equals(cls)) {
                    finishActivity(activity);
                } else {
                    finishActivity(activity);
                    return;
                }
            }
        }
    }


    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);

            finishActivity(activity);
        }
    }

    /**
     * @Title: finishOtherActivity
     * @说 明:结束除传如的Activity外其他的Activity
     * @参 数: @param mActivity 不结束的activity
     */
    public void finishOtherActivity(Activity mActivity) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            if (!activityStack.get(i).getClass().equals(mActivity.getClass())) {
                finishActivity(activityStack.get(i));
            }
        }
    }

    /**
     * 结束除指定类名()的Activity
     *
     * @param targetClass 不结束的Activity页面
     */
    public void finishOtherActivity(Class<?> targetClass) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (!activity.getClass().equals(targetClass)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 从列表中移除插件activity，并没有finish
     *
     * @param pluginActivity
     */
    public void removeActivity(Activity pluginActivity) {
        boolean isremove = activityStack.remove(pluginActivity);
        L.i(TAG, "removeActivity:" + pluginActivity + " --> " + isremove);
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "AppManager [activityStack.size()=" + activityStack.size();
    }

}