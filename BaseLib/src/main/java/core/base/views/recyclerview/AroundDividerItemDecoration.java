package core.base.views.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import core.base.R;

public class AroundDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;

    public AroundDividerItemDecoration(Context context) {
        //使用自定义的分隔线
        mDivider = context.getResources().getDrawable(R.drawable.list_divider_bg);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state) {

        drawTopLine(c, parent);
        drawBottomLine(c, parent);
        drawLeftLine(c, parent);
        drawRightLine(c, parent);

    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    /**
     * 画下横边
     *
     * @param c
     * @param parent
     */
    public void drawBottomLine(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //分隔线各个顶点的偏移量
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * 画右竖边
     *
     * @param c
     * @param parent
     */
    private void drawRightLine(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();

            final int top1 = child.getTop() - params.topMargin - mDivider.getIntrinsicWidth();
            final int bottom1 = child.getBottom() + params.bottomMargin + mDivider.getIntrinsicWidth();

            final int left1 = child.getRight() + params.leftMargin;
            final int right1 = left1 + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left1, top1, right1, bottom1);
            mDivider.draw(c);
        }
    }

    /**
     * 画上横边
     *
     * @param c
     * @param parent
     */
    private void drawTopLine(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //分隔线各个顶点的偏移量
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getTop() - params.topMargin - mDivider.getIntrinsicHeight();
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }


    /**
     * 画左竖边
     *
     * @param c
     * @param parent
     */
    public void drawLeftLine(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();

            final int top1 = child.getTop() - params.topMargin - mDivider.getIntrinsicWidth();
            final int bottom1 = child.getBottom() + params.bottomMargin + mDivider.getIntrinsicWidth();

            final int right1 = child.getLeft() - params.leftMargin;
            final int left1 = right1 - mDivider.getIntrinsicWidth();

            mDivider.setBounds(left1, top1, right1, bottom1);
            mDivider.draw(c);
        }
    }

    private boolean isFirstColum(RecyclerView parent, int pos, int spanCount,
                                 int childCount) {
        pos -= 1;
        return pos % spanCount == 0;
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                int childCount) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            pos -= 1;
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            //九宫格没铺满
            if ((childCount - 1) % spanCount != 0) {
                childCount = childCount - (childCount - 1) % spanCount;
                if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                    return true;
            }
            //刚好九宫格铺满
            else {
                if (pos >= childCount - spanCount)// 如果是最后一行，则不需要绘制底部
                    return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildAdapterPosition(view);
        //最后一行，上下左右四条边都有线
        if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
            outRect.set(mDivider.getIntrinsicWidth(), mDivider.getIntrinsicWidth(), mDivider.getIntrinsicWidth(), mDivider.getIntrinsicWidth());
        }
        //除了最后一行，其他行只需要上左右三条线，形状如“冂”
        else {
            outRect.set(mDivider.getIntrinsicWidth(), mDivider.getIntrinsicWidth(), mDivider.getIntrinsicWidth(), 0);
        }
    }
}