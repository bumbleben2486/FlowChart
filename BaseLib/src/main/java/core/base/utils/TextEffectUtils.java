package core.base.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class TextEffectUtils {
    /**
     * 设置text显示的颜色
     *
     * @param text  要设定颜色的字符串
     * @param color 指定的颜色
     * @param begin 从字符串哪里开始
     * @param end   到哪里结束
     */
    public static SpannableStringBuilder setTextColor(@NonNull CharSequence text, int color, int begin, int end) {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(color), begin, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return style;
    }

    /**
     * 设置text加粗
     *
     * @param text  要设定加粗的字符串
     * @param begin 从字符串哪里开始
     * @param end   到哪里结束
     */
    public static SpannableStringBuilder setTextBold(@NonNull String text, int begin, int end) {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new StyleSpan(Typeface.BOLD), begin, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return style;
    }

    /**
     * 设置字体
     *
     * @param text
     * @param textSpSize
     * @param begin
     * @param end
     * @param context
     * @return
     */
    public static SpannableStringBuilder setTextSize(@NonNull CharSequence text, float textSpSize, int begin, int end, @NonNull Context context) {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(context, textSpSize)), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }
}
