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


/**
 * @author 陈俊彬
 *         这个分割线，是一定有addHeaerView的，不信你看类名
 */
public class DividerGridItemDecorationWithHeader extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private boolean isTopLine;
    private boolean hasFootView;

    public DividerGridItemDecorationWithHeader(Context context) {
        //使用自定义的分隔线
        mDivider = context.getResources().getDrawable(R.drawable.list_divider_bg);
    }

    public DividerGridItemDecorationWithHeader(Context context, int layoutId) {
        this.mDivider = context.getResources().getDrawable(layoutId);
    }

    public DividerGridItemDecorationWithHeader(Context context, boolean isTopLine) {
        //使用自定义的分隔线
        mDivider = context.getResources().getDrawable(R.drawable.list_divider_bg);
        this.isTopLine = isTopLine;
    }


    public DividerGridItemDecorationWithHeader(Context context, int layoutId, boolean isTopLine) {
        this.mDivider = context.getResources().getDrawable(layoutId);
        this.isTopLine = isTopLine;
    }

    public DividerGridItemDecorationWithHeader(Context context, int layoutId,
                                               boolean isTopLine, boolean hasFootView) {
        this.mDivider = context.getResources().getDrawable(layoutId);
        this.isTopLine = isTopLine;
        this.hasFootView = hasFootView;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state) {

        drawHorizontal(c, parent);
        drawVertical(c, parent);

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
     * 画横边
     *
     * @param c
     * @param parent 由于有HeaderView，所以这里画线从i = 1开始
     */
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 1; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            int top;
            int bottom;
            if (isTopLine) {
                //这里画出来的线，就是在item的上面的
                bottom = child.getTop() + params.topMargin;
                top = bottom - mDivider.getIntrinsicWidth();
            } else {
                // 这里画出来的线，其实是在item的下面
                top = child.getBottom() + params.bottomMargin;
                bottom = top + mDivider.getIntrinsicHeight();
            }
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * 画竖边
     *
     * @param c
     * @param parent 由于有HeaderView，所以这里画线从i = 1开始
     */
    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 1; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();

            int top;
            int bottom;
            if (isTopLine) {
                //对应于画在item上面的线
                top = child.getTop() - params.topMargin - mDivider.getIntrinsicWidth();
                bottom = child.getBottom() + params.bottomMargin;
            } else {
                //对应于画在item下面的线
                top = child.getTop() - params.topMargin;
                bottom = child.getBottom() + params.bottomMargin + mDivider.getIntrinsicHeight();
            }

            final int right = child.getLeft() - params.leftMargin;
            final int left = right - mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
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


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildAdapterPosition(view);
        //判断是否有footview和headerView,最后要确定最后一行是不是footview，要不要去掉itemDecoration
//        int outerViewCount = 0;
//        boolean hasFootView = false;
//        if (parent.getAdapter() instanceof BaseQuickAdapter) {
//            BaseQuickAdapter adapter = (BaseQuickAdapter) parent.getAdapter();
//            outerViewCount += adapter.getHeaderLayoutCount();
//            if (adapter.getFooterLayoutCount() > 0) {
//                hasFootView = true;
//                outerViewCount += adapter.getFooterLayoutCount();
//            }
//        }
//        L.d("hasFootview->" + hasFootView + ",itemPostion->" + itemPosition + ",outerViewcount->" + outerViewCount + ",childcount->" + childCount);
        //第一个item，头部的item
        if (itemPosition == 0) {
            outRect.set(0, 0, 0, 0);
        } else if (hasFootView && itemPosition == childCount - 1) {
            outRect.set(0, 0, 0, 0);
        } else if (isFirstColum(parent, itemPosition, spanCount, childCount)) {
            outRect.set(mDivider.getIntrinsicWidth() / 2, 0, 0, 0);
        } else if (isLastColum(parent, itemPosition, spanCount, childCount)) {
            outRect.set(0, 0, mDivider.getIntrinsicWidth() / 2, 0);
        }
        //从第二个item开始
        else {
            outRect.set(mDivider.getIntrinsicWidth(), mDivider.getIntrinsicWidth(), 0, 0);
        }
    }
}
