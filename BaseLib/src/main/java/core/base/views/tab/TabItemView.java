package core.base.views.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import core.base.R;
import core.base.utils.ABAppUtil;


/**
 * 底部Item
 * Created by Benjen on 2017/7/13.
 */
public class TabItemView extends FrameLayout {

    // 代表选中状态的集合
    private static final int[] CHECK_STATE_SET = new int[]{android.R.attr.state_checked};

    private static final int DEFAULT_COLOR = Color.GRAY;

    private View mTabView;
    private ImageView mIvIcon;
    @Nullable
    private TextView mTvTitle;
    private ImageView mIvRedPoint;

    //private int mCurTextColor;
    //private Drawable mIcon;

    private boolean mChecked;
    private OnCheckedChangeListener mOnCheckChangedListener;
    private ColorStateList mTextColorList;
    private StateListDrawable mStateListDrawable;
    private int mIconHeight;//图片的宽高，暂时作为正方形显示
    private ViewStub tvStub;

//    public TabItemView(Context context) {
//        super(context);
//        init(context);
//        showRedPoint(false);
//    }

    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setupData(context, attrs);
    }


    public TabItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        setupData(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        mTabView = inflate(context, R.layout.tab_item, this);
        mIvIcon = mTabView.findViewById(R.id.tab_item_IvIcon);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabItemView);
        CharSequence title = a.getText(R.styleable.TabItemView_tabItem_title);
        if (title != null) {
            tvStub = mTabView.findViewById(R.id.stub_import);
            tvStub.setVisibility(VISIBLE);
            mTvTitle = mTabView.findViewById(R.id.stub_commnet_tv);
        }
        mIvRedPoint = new ImageView(context);
        mIvRedPoint.setImageResource(R.drawable.bg_shape_o_red_small);
        LayoutParams layoutParams = new LayoutParams(ABAppUtil.dip2px(context, 4.0f), ABAppUtil.dip2px(context, 4.0f));
        layoutParams.topMargin = ABAppUtil.dip2px(context, 6.0f);
        layoutParams.rightMargin = ABAppUtil.dip2px(context, 14.0f);
        layoutParams.gravity = Gravity.END;
        addView(mIvRedPoint, layoutParams);
    }


    private void setupData(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabItemView);
        Drawable d = a.getDrawable(R.styleable.TabItemView_tabItem_icon);
        setIcon(d);
        CharSequence title = a.getText(R.styleable.TabItemView_tabItem_title);
        if (title != null) {
            setText(title);
            //因为要有title的时候, 设置颜色才能起作用, 所以这两行代码移动到这里来
            mTextColorList = a.getColorStateList(R.styleable.TabItemView_tabItem_txtColor);
            setTextColor(mTextColorList != null ? mTextColorList : ColorStateList.valueOf(DEFAULT_COLOR));
        }
        boolean isSelected = a.getBoolean(R.styleable.TabItemView_tabItem_checked, false);
        setChecked(isSelected);
        boolean showRedPoint = a.getBoolean(R.styleable.TabItemView_tabItem_showRedPoint, false);
        showRedPoint(showRedPoint);
        a.recycle();
    }


    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        if (!mChecked) {
            // 如果未选中，直接返回父类的结果
            return super.onCreateDrawableState(extraSpace);
        } else {
            // 如果选中，将父类的结果和选中状态合并之后返回
            final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
            return mergeDrawableStates(drawableState, CHECK_STATE_SET);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        //根据状态修改控件的UI显示
        if (mStateListDrawable != null && mStateListDrawable.isStateful()) {
            mStateListDrawable.setState(getDrawableState());
            setIcon(mStateListDrawable.getCurrent()); //图标修改
        }
        if (mTextColorList != null && mTextColorList.isStateful()
                && tvStub.getVisibility() == VISIBLE) {
            int color = mTextColorList.getColorForState(getDrawableState(), DEFAULT_COLOR);
            mTvTitle.setTextColor(color);//文本颜色就改
        }
    }


    public void setIcon(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {//区分bmp
            BitmapDrawable bmp = (BitmapDrawable) drawable;
            mIvIcon.setImageDrawable(bmp);
        } else if (drawable instanceof StateListDrawable) {//多状态的drawable
            mStateListDrawable = (StateListDrawable) drawable;
            mStateListDrawable.mutate(); // make sure that we aren't sharing state anymore
            mIvIcon.setImageDrawable(mStateListDrawable);
        }
    }

    public void setIcon(@DrawableRes int imgRes) {
        Drawable d = ContextCompat.getDrawable(getContext(), imgRes);
        setIcon(d);
    }


    /**
     * 通过路径设置ImageView的图标。
     *
     * @param checkedImgPath 选中时的图片路径
     * @param defImgPath     默认时的图片路径
     * @param iconHeight     图片显示的宽高，暂时只支持正方形
     */
    public void setIconPath(final String checkedImgPath, final String defImgPath, int iconHeight) {

        final StateListDrawable stateListDrawable = new StateListDrawable();
        final int checked = android.R.attr.state_checked;
        mIconHeight = iconHeight != 0 ? iconHeight : Target.SIZE_ORIGINAL;

        Glide.with(getContext()).load(checkedImgPath).asBitmap().into(new SimpleTarget<Bitmap>(mIconHeight, mIconHeight) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Drawable drawable = new BitmapDrawable(getResources(), resource);
                stateListDrawable.addState(new int[]{checked}, drawable);
                drawableStateChanged();
                //mIvIcon.invalidate();
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
            }
        });

        Glide.with(getContext()).load(defImgPath).asBitmap().into(new SimpleTarget<Bitmap>(mIconHeight, mIconHeight) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Drawable drawable = new BitmapDrawable(getResources(), resource);
                stateListDrawable.addState(new int[]{-checked}, drawable);
                drawableStateChanged();
                //mIvIcon.invalidate();
                //stateListDrawable.addState(new int[]{},drawable);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
            }
        });
        setIcon(stateListDrawable);
    }


    public void setText(CharSequence txt) {
        if (tvStub.getVisibility() == VISIBLE) {
            mTvTitle.setText(txt);
        }
    }


    /**
     * 设置textView的字体颜色
     *
     * @param checkedColor 选中颜色
     * @param defColor     默认颜色
     */
    public void setTextColor(int checkedColor, int defColor) {
        int[] colors = new int[]{checkedColor, defColor};
        final int checked = android.R.attr.state_checked;
        int[][] states = new int[2][];
        states[0] = new int[]{checked};
        states[1] = new int[]{};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        setTextColor(colorStateList);
    }


    public void setTextColor(ColorStateList colorStateList) {
        this.mTextColorList = colorStateList != null ? colorStateList : ColorStateList.valueOf(DEFAULT_COLOR);
        if (tvStub.getVisibility() == VISIBLE) {
            mTvTitle.setTextColor(mTextColorList);
        }
    }


    /**
     * 设置选中状态，状态不一样的情况下刷新图片状态和文本颜色（根据{{@link View#getDrawableState()}的值改变）
     * 同时对外暴露OnCheckChangedListener监听器，提供选中监听
     *
     * @param isChecked 是否选中
     */
    public void setChecked(boolean isChecked) {
        if (mChecked != isChecked) {
            mChecked = isChecked;
            refreshDrawableState();
            // updateTextColors();
            if (mOnCheckChangedListener != null) {
                mOnCheckChangedListener.onCheckedChanged(this, mChecked);
            }
        }
    }


//    /**
//     * textView根据状态修改颜色
//     */
//    private void updateTextColors() {
//        boolean inval = false;
//        int color = mTextColorList.getColorForState(getDrawableState(), 0);
//        if (color != mCurTextColor) {
//            mCurTextColor = color;
//            inval = true;
//        }
//        if (inval) {
//            // Text needs to be redrawn with the new color
//            invalidate();
//        }
//    }


    /**
     * 是否选中
     *
     * @return 是否选中
     */
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * 是否显示小红点,暂时不支持显示数字
     *
     * @param isShow 显示为true
     */
    public void showRedPoint(boolean isShow) {
        mIvRedPoint.setVisibility(isShow ? VISIBLE : GONE);
    }


    /**
     * 隐藏小红点
     */
    public void hideShowPoint() {
        mIvRedPoint.setVisibility(GONE);
    }


    public void setOnCheckChangedListener(OnCheckedChangeListener onCheckChangedListener) {
        this.mOnCheckChangedListener = onCheckChangedListener;
    }


    public interface OnCheckedChangeListener {
        void onCheckedChanged(TabItemView buttonView, boolean isChecked);
    }
}
