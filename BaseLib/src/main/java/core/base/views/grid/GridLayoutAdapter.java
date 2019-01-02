package core.base.views.grid;

import android.support.v7.widget.GridLayout;
import android.view.View;
import android.view.ViewGroup;


public abstract class GridLayoutAdapter {

    private static final int DEFAULT_COUNT = 1;

    private GridLayoutList gridLayoutList;

    protected abstract int getCount();

    protected abstract View getView(int position, View convertView, ViewGroup parent);

    protected Integer getViewType(int position) {
        return null;
    }

    public void bindGridLayoutList(GridLayoutList gridLayoutList) {
        this.gridLayoutList = gridLayoutList;
    }

    public void notifyDataSetChanged(boolean isViewTypeChange) {
        gridLayoutList.updateView(isViewTypeChange);
    }

    public GridLayoutList getGridLayoutList() {
        return gridLayoutList;
    }

    public void setGridLayoutList(GridLayoutList gridLayoutList) {
        this.gridLayoutList = gridLayoutList;
    }


    /**
     * 设置行列间距
     *
     * @param child
     * @param position
     * @param columnDividerWidth
     * @param rowDividerHeight
     */
    public void addViewWithMargin(View child, int position, int columnDividerWidth, int rowDividerHeight) {
        int columnCount = gridLayoutList.getColumnCount();
        GridLayout.LayoutParams gl = (GridLayout.LayoutParams) child.getLayoutParams();
//        GridLayout.LayoutParams gl = new GridLayout.LayoutParams(ll);
        int left = columnDividerWidth;
        int top = rowDividerHeight;

        if (columnCount == DEFAULT_COUNT) {//单列的左右不需要间隔，因为可以直接通过itemView设置
            left = 0;
            if (position == 0) {
                top = 0;
            }
        } else if (columnCount > DEFAULT_COUNT) {//多列
            if (position % columnCount == 0) {//第一列，左边不需要间距
                left = 0;
            }
            if (position < columnCount) {//第一行，不显示上边间距
                top = 0;
            }
        }
        gl.leftMargin = left;
        gl.topMargin = top;
        child.setLayoutParams(gl);
    }
}
