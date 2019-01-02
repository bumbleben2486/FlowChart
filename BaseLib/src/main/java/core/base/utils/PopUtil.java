package core.base.utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by Nowy on 2017/7/24.
 */
@Deprecated
public class PopUtil {

    /**
     * 显示pop在屏幕下方
     * @param activity
     * @param resLayout
     * @param view
     */
    public static void ShowPopAtBottom(final Activity activity, @LayoutRes int resLayout,
                                       View view,boolean isMatchWidth){
        PopupWindow mPopupWindow = isMatchWidth ?
                createPop_MatchW(activity,resLayout,view) : createPop_Wrap(activity,resLayout,view,false);

        mPopupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,0,
                ABAppUtil.dip2px(view.getContext(),6.0f));
    }


    /**
     * 显示Pop在控件下方
     * @param activity
     * @param resLayout
     * @param view
     * @param isMatchWidth 是否填充父容器宽度，true:填充（只填充0.9宽度）
     */
    public static void ShowPopAsDropDown(final Activity activity, @LayoutRes int resLayout,
                                         View view,boolean isMatchWidth){
        PopupWindow mPopupWindow = isMatchWidth ?
                createPop_MatchW(activity,resLayout,view) : createPop_Wrap(activity,resLayout,view,false);

        mPopupWindow.showAsDropDown(view);
    }


    /**
     * 构建填充宽度的pop
     * @param activity
     * @param resLayout
     * @param view
     * @return
     */
    public static PopupWindow createPop_MatchW(final Activity activity,
                                         @LayoutRes int resLayout, View view){
        View popView =  LayoutInflater.from(view.getContext())
                .inflate(resLayout,null);
        PopupWindow mPopupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        mPopupWindow.setWidth((int)(ABAppUtil.getDeviceWidth(view.getContext())*0.9));
        setupPop(activity,false,mPopupWindow);

        return mPopupWindow;
    }


    public static PopupWindow createPop_Match(final Activity activity,
                                               @LayoutRes int resLayout, View view){
        View popView =  LayoutInflater.from(view.getContext())
                .inflate(resLayout,null);
        PopupWindow mPopupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
//        mPopupWindow.setWidth((int)(ABAppUtil.getDeviceWidth(view.getContext())*0.9));
        setupPop(activity,false,mPopupWindow);

        return mPopupWindow;
    }


    public static PopupWindow createPop_Wrap(final Activity activity,@LayoutRes int resLayout,
                                             View view,boolean hasShadow){
        View popView =  LayoutInflater.from(view.getContext())
                .inflate(resLayout,null);
        PopupWindow mPopupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        setupPop(activity,hasShadow,mPopupWindow);
        return mPopupWindow;
    }


    /**
     * 设置Pop的部分通用参数
     * @param activity
     * @param popupWindow
     */
    private static void setupPop(final Activity activity,boolean hasShadow,PopupWindow popupWindow){
        // 设置外部可点击
        popupWindow.setOutsideTouchable(true);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
        // 设置弹出窗体的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        if(hasShadow){//是否需要屏幕变暗
            //变暗
            // 设置背景颜色变暗
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = 0.7f;
            activity.getWindow().setAttributes(lp);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {//回复到正常颜色
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.alpha = 1f;
                    activity.getWindow().setAttributes(lp);
                }
            });
        }
    }


}
