package core.base.utils.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class XPermissionUtils {

    private static int mRequestCode = -1;
    private static OnPermissionListener mOnPermissionListener;

    public interface OnPermissionListener {

        void onPermissionGranted();

        void onPermissionDenied(String[] deniedPermissions, boolean alwaysDenied);
    }

    @TargetApi(Build.VERSION_CODES.M)
    static void requestPermissionsAgain(@NonNull Context context,
                                        @NonNull String[] permissions,
                                        int requestCode) {
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) context, permissions, requestCode);
            //((AppCompatActivity) context).requestPermissions(permissions, requestCode);
        } else {
            throw new IllegalArgumentException("Context must be an Activity");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    static void requestPermissions(@NonNull Context context, int requestCode,
                                   @NonNull String[] permissions, OnPermissionListener listener) {
        mRequestCode = requestCode;
        mOnPermissionListener = listener;
        String[] deniedPermissions = getDeniedPermissions(context, permissions);
        if (deniedPermissions.length > 0) {
            requestPermissionsAgain(context, permissions, requestCode);
        } else {
            if (mOnPermissionListener != null) mOnPermissionListener.onPermissionGranted();
        }
    }

    /**
     * 请求权限结果，对应Activity中onRequestPermissionsResult()方法。
     */
    public static void onRequestPermissionsResult(@NonNull AppCompatActivity context, int requestCode,
                                                  @NonNull String[] permissions, int[] grantResults) {
        if (mRequestCode != -1 && requestCode == mRequestCode) {
            if (mOnPermissionListener != null) {
                String[] deniedPermissions = getDeniedPermissions(context, permissions);
                if (deniedPermissions.length > 0) {
                    boolean alwaysDenied = hasAlwaysDeniedPermission(context, permissions);
                    mOnPermissionListener.onPermissionDenied(deniedPermissions, alwaysDenied);
                } else {
                    mOnPermissionListener.onPermissionGranted();
                }
            }
        }
    }

    /**
     * 获取请求权限中需要授权的权限
     */
    private static String[] getDeniedPermissions(@NonNull Context context, @NonNull String[] permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    /**
     * 是否彻底拒绝了某项权限
     */
    private static boolean hasAlwaysDeniedPermission(@NonNull Context context, @NonNull String... deniedPermissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        boolean rationale;
        for (String permission : deniedPermissions) {
            rationale = ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, permission);
            if (!rationale) return true;
        }
        return false;
    }


    /**
     * 6.0以下判断是否开启相机权限
     */
    static boolean isCameraEnable() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }
}