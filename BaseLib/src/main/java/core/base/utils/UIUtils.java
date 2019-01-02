package core.base.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import core.base.staticvalue.StaticValue;


/**
 * 一些ui 方面的工具类
 * Created by min on 2015/6/17.
 */
public class UIUtils {
    /**
     * 多个Edittet都要填写，这个button才能够被点击
     *
     * @param button    当前button
     * @param editTexts 当前要判断的所有edittext
     */
    public static void setButtonEnableByEdittext(final Button button, final EditText... editTexts) {
        button.setEnabled(false);
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isEveryEdittextNotNull(editTexts)) {
                        button.setEnabled(true);
                    } else {
                        button.setEnabled(false);
                    }
                }
            });
        }
    }

    /**
     * 是否所有的Edittext不为空
     *
     * @param editTexts 要判断的edittext
     * @return 是否所有的不为空
     */
    private static boolean isEveryEdittextNotNull(EditText... editTexts) {
        boolean flag = true;
        for (EditText editText : editTexts) {
            if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 抖动指定的view
     *
     * @param context
     * @param view    目标
     */
    public static void shake(Context context, View view) {
        AnimatorSet animator = new AnimatorSet();
        animator.setDuration(1000);
        animator.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1, 0.9f, 0.9f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1),
                ObjectAnimator.ofFloat(view, "scaleY", 1, 0.9f, 0.9f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1),
                ObjectAnimator.ofFloat(view, "rotation", 0, -3, -3, 3, -3, 3, -3, 3, -3, 0)
        );
        animator.start();
        return;
    }

    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        // DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5, xdpi=160.421, ydpi=159.497}
        // DisplayMetrics{density=2.0, width=720, height=1280, scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }

    public static int scale(Context context, float value) {
        DisplayMetrics mDisplayMetrics = getDisplayMetrics(context);
        return scale(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels, value);
    }

    /**
     * 描述：根据屏幕大小缩放.
     *
     * @param displayWidth  the display width
     * @param displayHeight the display height
     * @param pxValue       the px value
     * @return the int
     */
    public static int scale(int displayWidth, int displayHeight, float pxValue) {
        if (pxValue == 0) {
            return 0;
        }
        float scale = 1;
        try {
            float scaleWidth = (float) displayWidth / StaticValue.UI_WIDTH;
            float scaleHeight = (float) displayHeight / StaticValue.UI_HEIGHT;
            scale = Math.min(scaleWidth, scaleHeight);
        } catch (Exception e) {
        }
        return Math.round(pxValue * scale + 0.5f);
    }
}
