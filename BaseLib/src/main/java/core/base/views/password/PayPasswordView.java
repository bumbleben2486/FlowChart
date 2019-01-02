package core.base.views.password;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * author : Administrator
 * time   : 2018/08/06
 * desc   : 密码输入框
 */
public class PayPasswordView extends RelativeLayout {

    //文本编辑框
    private EditText mEditText;
    //所需的上下文
    private Context mContext;
    //文本密码的文本
    private LinearLayout mPasswordLienar;
    //文本数组
    private TextView[] mTextViews;
    //默认密码长度
    private int mPasswordLength = 6;

    private PasswordListener onPasswordListener;

    public PayPasswordView(Context context) {
        super(context);
    }

    public PayPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PayPasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //this.mContext = context;
    }

    /**
     * 默认初始化密码框样式
     *
     * @param bgDrawable     背景Drawable
     * @param passwordLength 密码长度
     * @param splitLineWidth 分割线宽度
     * @param splitLineColor 分割线颜色
     * @param passwordColor  密码字体颜色
     * @param passwordSize   密码字体大小
     */
    public void initStyle(Context context, int bgDrawable, int passwordLength, float splitLineWidth,
                          int splitLineColor, int passwordColor, int passwordSize) {
        this.mPasswordLength = passwordLength;
        this.mContext = context;
        initEdit(context, bgDrawable);
        initShowInput(bgDrawable, passwordLength, splitLineWidth, splitLineColor, passwordColor, passwordSize);
    }

    /**
     * 初始化编辑框
     */
    private void initEdit(Context context, int bgColor) {
        this.mEditText = new EditText(context);
        this.mEditText.setBackgroundResource(bgColor);
        this.mEditText.setCursorVisible(false);
        this.mEditText.setTextSize(0);
        this.mEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
        this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mPasswordLength)});
        this.mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Editable editable = mEditText.getText();
                Selection.setSelection(editable, editable.length());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showPasswordNumber(s);
                if (s.length() == mPasswordLength) {
                    if (onPasswordListener != null) {
                        onPasswordListener.onFinish(s.toString().trim(),PayPasswordView.this);
                    }
                }
            }
        });
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        addView(mEditText, lp);
    }

    /**
     * @param bgColor        背景drawable
     * @param passwordLength 密码长度
     * @param splitLineWidth 分割线宽度
     * @param splitLineColor 分割线颜色
     * @param passwordColor  密码字体颜色
     * @param passwordSize   密码字体大小
     */
    public void initShowInput(int bgColor, int passwordLength, float splitLineWidth, int splitLineColor, int passwordColor, int passwordSize) {

        //添加密码框父布局
        mPasswordLienar = new LinearLayout(mContext);
        mPasswordLienar.setBackgroundResource(bgColor);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mPasswordLienar.setLayoutParams(layoutParams);
        mPasswordLienar.setOrientation(LinearLayout.HORIZONTAL);
        addView(mPasswordLienar);

        //添加密码框
        mTextViews = new TextView[passwordLength];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(dip2px(mContext, splitLineWidth), LayoutParams.MATCH_PARENT);
        for (int i = 0; i < mTextViews.length; i++) {
            final int index = i;
            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            mTextViews[i] = textView;
            mTextViews[i].setTextSize(passwordSize);
            mTextViews[i].setTextColor(mContext.getResources().getColor(passwordColor));
            mTextViews[i].setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
            mPasswordLienar.addView(textView, params);

            if (i < mTextViews.length - 1) {
                View view = new View(mContext);
                view.setBackgroundColor(mContext.getResources().getColor(splitLineColor));
                mPasswordLienar.addView(view, params2);
            }
        }
    }


    /**
     * 是否显示明文
     */
    public void setShowPwd(boolean showPwd) {
        int length = mTextViews.length;
        for (int i = 0; i < length; i++) {
            if (showPwd) {
                mTextViews[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                mTextViews[i].setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        }
    }


    /**
     * 设置显示类型
     *
     * @param type
     */
    public void setInputType(int type) {
        int length = mTextViews.length;
        for (int i = 0; i < length; i++) {
            mTextViews[i].setInputType(type);
        }
    }


    /**
     * 清除文本框
     */
    public void clearText() {
        mEditText.setText("");
        for (int i = 0; i < mPasswordLength; i++) {
            mTextViews[i].setText("");
        }
    }


    public void setPasswordListener(PasswordListener onPasswordListener) {
        this.onPasswordListener = onPasswordListener;
    }

    /**
     * 根据输入字符显示密码个数
     */
    public void showPasswordNumber(Editable editable) {
        if (editable.length() > 0) {
            int length = editable.length();
            for (int i = 0; i < mPasswordLength; i++) {
                if (i < length) {
                    for (int j = 0; j < length; j++) {
                        char ch = editable.charAt(j);
                        mTextViews[j].setText(String.valueOf(ch));
                    }
                } else {
                    mTextViews[i].setText("");
                }
            }
        } else {
            for (int i = 0; i < mPasswordLength; i++) {
                mTextViews[i].setText("");
            }
        }
    }

    public String getPasswordText() {
        if (mEditText != null) {
            return mEditText.getText().toString().trim();
        }
        return "";
    }

    public interface PasswordListener {

        void onFinish(String str, PayPasswordView passwordView);
    }

    public void setFocus() {
        mEditText.requestFocus();
        mEditText.setFocusable(true);
        showKeyBord(mEditText);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 显示键盘
     */
    public void showKeyBord(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
}
