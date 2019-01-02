package core.base.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;

public class


FontColorUtils {
    /**
     * 给字体加上颜色
     *
     * @param colorCode
     * @param text
     * @return
     */
    public static CharSequence addColor(String colorCode, String text) {
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor(colorCode)), 0, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    /**
     * 给字体加上颜色
     *
     * @param colorCode
     * @param text
     * @return
     */
    public static CharSequence addColor(int color, String text) {
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }


    /**
     * @param context
     * @param colorCode color.xml
     * @param text
     * @return
     */
    public static CharSequence addColor(Context context, @ColorRes int colorCode, String text) {
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorCode)), 0, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static CharSequence addColorWithStr(Context context, @ColorRes int colorCode, String text, String spanStr) {
        if (TextUtils.isEmpty(spanStr)) return text;

        int index = text.indexOf(spanStr);
        if (index == -1) return text;


        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorCode)), index, index + spanStr.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static CharSequence addDeatil(final Context context, @DrawableRes final int colorCode, String text, String spanStr) {
        if (TextUtils.isEmpty(spanStr)) return text;
        int index = text.indexOf(spanStr);
        if (index == -1) return text;

        DynamicDrawableSpan drawableSpan = new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BASELINE) {
                    @Override
                    public Drawable getDrawable() {
                        Drawable d = context.getResources().getDrawable(colorCode);
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight() - 5);
                        return d;
                    }
                };
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(drawableSpan, index, index + spanStr.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }


    /**
     * 调整字体大小
     *
     * @param colorCode
     * @param text
     * @return
     */
    public static CharSequence resize(float relativeSice, String text) {
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new RelativeSizeSpan(relativeSice), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    /**
     * 调整字体大小
     *
     * @param colorCode
     * @param text
     * @return
     */
    public static CharSequence scaleX(float relativeSice, String text) {
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new ScaleXSpan(relativeSice), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    /**
     * 字体转换为默认红色-----#F04877
     *
     * @param colorCode
     * @param text
     * @return <color name="one_color_black">#333333</color>
     * <color name="one_color_orienge">#F64C3B</color>
     * <color name="one_color_red">#F04877</color>
     * <color name="two_color">#666666</color>
     * <color name="three_color">#999999</color>
     * <color name="three_color_66">#66999999</color>
     * <color name="four_color">#FFFFFF</color>
     */
    public static CharSequence addFontRedColor(String text) {
        return addColor("#F04877", text);
    }

    /**
     * 给字体加上默认橙色
     *
     * @param colorCode
     * @param text
     * @return
     */
    public static CharSequence addFontOriengeColor(String text) {
        return addColor("#F04877", text);
    }

    /**
     * 给字体加上默认黑色
     *
     * @param colorCode
     * @param text
     * @return
     */
    public static CharSequence addFontOneColor(String text) {
        return addColor("#333333", text);
    }

    /**
     * 给字体加上灰色颜色
     *
     * @param colorCode
     * @param text
     * @return
     */
    public static CharSequence addFontTwoColor(String text) {
        return addColor("#666666", text);
    }

    /**
     * 给字体加上浅灰色颜色
     *
     * @param colorCode
     * @param text
     * @return
     */
    public static CharSequence addFontThreeColor(String text) {
        return addColor("#999999", text);
    }

    /**
     * 给字体加上白色颜色
     *
     * @param colorCode
     * @param text
     * @return
     */
    public static CharSequence addFontFourColor(String text) {
        return addColor("#FFFFFF", text);
    }
}
