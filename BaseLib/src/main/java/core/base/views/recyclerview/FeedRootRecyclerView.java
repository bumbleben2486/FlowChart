package core.base.views.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Nowy on 2017/7/20.
 */

public class FeedRootRecyclerView extends RecyclerView {
    public FeedRootRecyclerView(Context context) {
        super(context);
    }

    public FeedRootRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedRootRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    /* do nothing */
    }

}
