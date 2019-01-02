package core.base.views.convenientbanner.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import core.base.utils.image.GlideDisplay;

/**
 * Created by Sai on 15/8/4.
 * 本地图片Holder例子
 */
public class NetWorkImageHolderView implements Holder<String> {

    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        GlideDisplay.display(imageView, data);
    }

}
