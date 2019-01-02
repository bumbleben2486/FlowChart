package core.base.utils.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import java.lang.ref.WeakReference;

import core.base.R;
import core.base.application.BaseActivity;
import core.base.log.T;

/**
 * Created by Nowy on 2017/6/28.
 * 权限请求帮助类
 */

public class PermissionHelper {

    public static final int REQ_PERMISSION_NORMAL = 5;//通用权限
    public static final int REQ_PERMISSION_CAMERA = 6;//摄像头
    public static final int REQ_PERMISSION_CALL = 7;//打电话

    public interface PermissionListener {

        void onSuccess();

        void onFailure();

    }

    /**
     * 请求摄像头和SD卡
     *
     * @param context
     * @param listener
     */
    public static void reqCameraAndSDcard(BaseActivity context, final PermissionListener listener) {
        WeakReference<BaseActivity> baseActivity = new WeakReference<>(context);
        final BaseActivity activity = baseActivity.get();
        XPermissionUtils.requestPermissions(activity, REQ_PERMISSION_CAMERA, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (XPermissionUtils.isCameraEnable()) {
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        } else {
                            PermissionDialogUtil.showPermissionManagerDialog(activity, "相机、读写SD卡");
                            if (listener != null) {
                                listener.onFailure();
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                        T.s(activity.getApplicationContext(), "获取相机权限失败");
                        if (listener != null) {
                            listener.onFailure();
                        }
                        if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                            PermissionDialogUtil.showPermissionManagerDialog(activity, "相机、读写SD卡");
                        } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                            alert.setTitle("温馨提示")
                                    .setMessage("我们需要相机权限才能正常使用该功能")
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            XPermissionUtils.requestPermissionsAgain(activity, deniedPermissions, REQ_PERMISSION_CAMERA);
                                        }
                                    });
                            AlertDialog alertDialog = alert.create();
                            alertDialog.show();
                            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(ContextCompat.getColor(activity, R.color.c_f04877));
                            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(ContextCompat.getColor(activity, R.color.c_f04877));
                        }
                    }
                });
    }


    /**
     * 请求拨号权限
     */
    public static void reqCall(final BaseActivity activity, final PermissionListener listener) {
        XPermissionUtils.requestPermissions(activity, REQ_PERMISSION_CALL, new String[]{
                        Manifest.permission.CALL_PHONE},
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (XPermissionUtils.isCameraEnable()) {
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        } else {
                            PermissionDialogUtil.showPermissionManagerDialog(activity, "拨号");
                            if (listener != null) {
                                listener.onFailure();
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                        T.s(activity.getApplicationContext(), "获取拨号权限失败");
                        if (listener != null) {
                            listener.onFailure();
                        }
                        if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                            PermissionDialogUtil.showPermissionManagerDialog(activity, "拨号");
                        } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                            alert.setTitle("温馨提示")
                                    .setMessage("我们需要拨号权限才能正常使用该功能")
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            XPermissionUtils.requestPermissionsAgain(activity, deniedPermissions,
                                                    REQ_PERMISSION_CALL);
                                        }
                                    });
                            AlertDialog alertDialog = alert.create();
                            alertDialog.show();
                            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(ContextCompat.getColor(activity, R.color.c_f04877));
                            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(ContextCompat.getColor(activity, R.color.c_f04877));
                        }
                    }
                });
    }


    /**
     * 请求权限
     *
     * @param tips       对话框弹出提示："我们需要"+tips+"权限才能正常使用该功能"
     * @param permission 权限数组
     */
    public static void reqPermission(final BaseActivity activity,
                                     final PermissionListener listener,
                                     final String tips, String... permission) {
        XPermissionUtils.requestPermissions(activity, REQ_PERMISSION_NORMAL, permission,
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }

                    @Override
                    public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                        T.s(activity.getApplicationContext(), "获取" + tips + "权限失败");
                        if (listener != null) {
                            listener.onFailure();
                        }
                        if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                            PermissionDialogUtil.showPermissionManagerDialog(activity, tips);
                        } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                            alert.setTitle("温馨提示")
                                    .setMessage("我们需要" + tips + "权限才能正常使用该功能")
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            XPermissionUtils.requestPermissionsAgain(activity, deniedPermissions,
                                                    REQ_PERMISSION_NORMAL);
                                        }
                                    });
                            AlertDialog alertDialog = alert.create();
                            alertDialog.show();
                            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(ContextCompat.getColor(activity, R.color.c_f04877));
                            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(ContextCompat.getColor(activity, R.color.c_f04877));
                        }
                    }
                });
    }


    /**
     * 请求权限
     *
     * @param tips       对话框弹出提示："我们需要"+tips+"权限才能正常使用该功能"
     * @param permission 权限数组
     */
    public static void reqPermissionWithNotDialog(final BaseActivity activity,
                                                  final PermissionListener listener,
                                                  final String tips, String... permission) {
        XPermissionUtils.requestPermissions(activity, REQ_PERMISSION_NORMAL, permission,
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }

                    @Override
                    public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                        T.s(activity.getApplicationContext(), "获取" + tips + "权限失败");
                        if (listener != null) {
                            listener.onFailure();
                        }
                        if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                            PermissionDialogUtil.showPermissionManagerDialog(activity, tips);
                        }
                    }
                });
    }

}
