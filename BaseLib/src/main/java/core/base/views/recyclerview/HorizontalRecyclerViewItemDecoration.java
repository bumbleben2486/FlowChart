package core.base.views.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 进驻模块 - 商家申请填写页面，图片显示一栏
 * Created by Administrator on 2018/1/10.
 */

public class HorizontalRecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public HorizontalRecyclerViewItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.bottom = space;
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            //outRect.top = space;
            outRect.right = (space * 2) + space;
        } else if (parent.getChildLayoutPosition(view) == 2) {
            outRect.right = (space * 2) + space;
        } else {
            outRect.top = 0;
            outRect.right = space;
        }
    }
}
