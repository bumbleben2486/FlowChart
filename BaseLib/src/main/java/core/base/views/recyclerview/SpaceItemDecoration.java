package core.base.views.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    int mSpace;
    int mOrientation = LinearLayoutManager.VERTICAL;

    /**
     * @param space 传入的值，其单位视为px
     */
    public SpaceItemDecoration(int space, int orientation) {
        this.mSpace = space;
        this.mOrientation = orientation;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int itemCount = parent.getAdapter().getItemCount();
        int pos = parent.getChildAdapterPosition(view);

        outRect.left = 0;
        outRect.top = 0;

        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            outRect.bottom = 0;
            if (pos != (itemCount - 1)) {
                outRect.right = mSpace;
            } else {
                outRect.right = 0;
            }
        } else {
            outRect.right = 0;
            if (pos != (itemCount - 1)) {
                outRect.bottom = mSpace;
            } else {
                outRect.bottom = 0;
            }
        }
    }
}