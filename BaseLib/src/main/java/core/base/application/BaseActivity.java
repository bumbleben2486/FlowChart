package core.base.application;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import core.base.log.L;
import core.base.log.T;
import core.base.manager.AppManager;
import core.base.utils.permission.XPermissionUtils;

/**
 * @version 1.0
 * @#作者:XQ
 * @#创建时间：15-9-18 上午11:46
 * @#类 说 明:基础的Activity.
 */
public class BaseActivity extends AppCompatActivity {

    protected String TAG = getClass().getName();
    protected Context mContext;
    protected Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this.mActivity = this;
        AppManager.getAppManager().addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //加入了友盟时长统计
        //MobclickAgent.onResume(this);
    }


    public void onPause() {
        super.onPause();
        //加入了友盟时长统计
        //MobclickAgent.onPause(this);
    }


    protected void le(String msg) {
        L.e(TAG, msg);
    }


    /**
     * 用于eventbus 回调
     *
     * @param t 接收的参数类型l
     */
    public void onEventMainThread(T t) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        XPermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
