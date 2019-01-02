package core.base.photopicker;

import android.app.Activity;
import android.view.View;
import android.widget.PopupWindow;

import java.util.List;

import core.base.R;
import core.base.photopicker.adapters.PhotoClazzAdapter;
import core.base.photopicker.beans.MediaFloder;
import core.base.utils.AnimationUtil;
import core.base.utils.PopUtil;
import core.base.views.grid.GridLayoutAdapter;
import core.base.views.grid.GridLayoutList;
import core.base.views.scroll.MaxHeightScrollView;


public class PhotoClazzPop {

    public static void showPop(final Activity activity, View view, final View maskView, final List<MediaFloder> data,
                               final OnItemClickListener listener) {
        final PopupWindow popupWindow = PopUtil.createPop_MatchW(activity, R.layout.pop_photo_clazz, view);
        MaxHeightScrollView parent = (MaxHeightScrollView) popupWindow.getContentView();
        final GridLayoutList glData = parent.findViewById(R.id.pop_photo_clazz_GllData);
        glData.setAdapter(new PhotoClazzAdapter(data));
        glData.setOnItemClickListener(new GridLayoutList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int posi) {
                if (listener != null) {
                    listener.itemClick(glData.getAdapter(), posi, data);
                }
                if (popupWindow != null && popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });

        maskAnim(popupWindow, maskView);
        popupWindow.showAsDropDown(view);
    }


    private static void maskAnim(PopupWindow popupWindow, final View maskView) {
        maskView.setVisibility(View.VISIBLE);
        maskView.startAnimation(AnimationUtil.createInAnimation_Alpha());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (maskView != null) {
                    maskView.startAnimation(AnimationUtil.createOutAnimation_Alpha());
                    maskView.setVisibility(View.GONE);
                }
            }
        });
    }


    public interface OnItemClickListener {
        void itemClick(GridLayoutAdapter adapter, int position, List<MediaFloder> data);
    }

}
