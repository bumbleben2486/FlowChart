package core.base.views.recyclerview;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import core.base.R;
import core.base.log.L;


/**
 * This class is from the v7 samples of the Android SDK. It's not by me!
 * <p/>
 * See the license above for details.
 */
public class DividerItemDecorationWithHeaderFooter extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDivider;

    private int mOrientation;

    public DividerItemDecorationWithHeaderFooter(Context context, int orientation) {
        //使用自定义的分隔线
        mDivider = context.getResources().getDrawable(R.drawable.list_divider_bg);
        setOrientation(orientation);
    }


    public DividerItemDecorationWithHeaderFooter(Context context, int orientation, int drawableId) {
        //使用自定义的分隔线
        mDivider = context.getResources().getDrawable(drawableId);
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        Log.v("itemdecoration", "onDraw()");

        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * 画横边
     * 与Grid有区别，因为Grid是以每个item为参照物，而;Linear是以整个 RecyclerView为参照物，竖排List
     * 就打横边
     *
     * @param c
     * @param parent
     */
    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 1; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView v = new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * 画竖边
     * 与Grid有区别，因为Grid是以每个item为参照物，而;Linear是以整个 RecyclerView为参照物，横排List
     * 就打竖边
     *
     * @param c
     * @param parent 由于有HeaderView，所以这里画线从i = 1开始
     */
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            int intrinsicHeight = mDivider.getIntrinsicHeight();
            //加上Header和Footer总共有多少个items(因为HeaderView与 FooterView是算做item数)
            int totalCount = parent.getLayoutManager().getItemCount();
            //headerView下不加item，footerView上(即上面一个条目)不加item，footerView下不加item(逗号“，”对应“||”)
            if (itemPosition == 0 || itemPosition == totalCount - 1 || itemPosition == totalCount - 2) {
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, 0, intrinsicHeight);
            }
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}