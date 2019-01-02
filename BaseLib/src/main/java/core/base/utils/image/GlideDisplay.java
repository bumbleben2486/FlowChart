package core.base.utils.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import core.base.R;
import core.base.application.ABApplication;
import core.base.log.L;
import core.base.views.imageview.CircleImageView;
import core.base.views.imageview.roundedimageview.RoundedImageView;


/**
 * Created by Nowy on 2016/4/8.
 */
public class GlideDisplay {

    public static int BG_DEF = R.color.gray;
    public static int USER_EEF = R.color.gray;

    public static boolean isCircle(View v) {
        return v instanceof CircleImageView || v instanceof RoundedImageView;
    }


   /* public static int getImageHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        if (bmp == null) {
            L.e("bitmap is null.");
        }
        return options.outHeight;
    }*/

    public static int getImageViewHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        if (bmp == null) {
            L.e("bitmap is null.");
        }
        return options.outHeight;
    }

    /**
     * 适配第三方自定义ImageView
     *
     * @param builder
     * @param iv
     */
    private static void fitThirdImageView(DrawableRequestBuilder builder, ImageView iv) {
        RequestListener listener = null;
        if (isCircle(iv)) {
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            listener = new AgRequestListener(builder);
            builder.listener(listener);
        }
        builder.into(new MyImageViewTarget(iv));
    }

    public static void display(ImageView iv, String url) {
        DrawableRequestBuilder builder = Glide.with(iv.getContext()).load(url).centerCrop()
                .error(BG_DEF)
                .crossFade(300);
        fitThirdImageView(builder, iv);
    }


    /**
     * 加载原图
     *
     * @param iv  要显示的图片视图控件
     * @param url 要被加载的远程Url
     */
    public static void displayOriginalImage(ImageView iv, String url, int resDef) {
        DrawableRequestBuilder builder = Glide.with(iv.getContext())
                .load(url)
                .placeholder(resDef)
                .error(BG_DEF)
                .crossFade(300);
        fitThirdImageView(builder, iv);
    }

    public static void displayOriginalImage(ImageView iv, String url) {
        DrawableRequestBuilder builder = Glide.with(iv.getContext())
                .load(url)
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade(300);
        fitThirdImageView(builder, iv);
    }

    public static void displayUser(ImageView imageView, String url) {
        display(imageView, url, USER_EEF);
    }

    public static void displayFitCenter(ImageView iv, String url) {
        DrawableRequestBuilder builder = Glide.with(iv.getContext()).load(url).fitCenter()
                .error(BG_DEF)
                .dontAnimate();
        fitThirdImageView(builder, iv);
    }

    public static void display(ImageView iv, File file) {
        DrawableRequestBuilder<File> builder = Glide.with(iv.getContext()).load(file).centerCrop()
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade();

        fitThirdImageView(builder, iv);
    }

    public static void display(ImageView iv, File file, int resDef) {
        DrawableRequestBuilder<File> builder = Glide.with(iv.getContext()).load(file).centerCrop()
                .placeholder(BG_DEF)
                .error(resDef)
                .crossFade();

        fitThirdImageView(builder, iv);
    }

    public static void display(ImageView iv, Uri uri) {
        DrawableRequestBuilder<Uri> builder = Glide.with(iv.getContext()).load(uri).centerCrop()
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .dontAnimate()
                .crossFade();

        fitThirdImageView(builder, iv);
    }

    /**
     * 高度不限制的加载方式
     */
    public static void displayLifeShowImage(ImageView iv, String uri, int resDef) {
        DrawableRequestBuilder<String> builder = Glide.with(iv.getContext()).load(uri)
                .placeholder(resDef)
                .error(resDef)
                .crossFade();
        fitThirdImageView(builder, iv);
    }

    public static void display(ImageView iv, String uri, int resDef) {
        Context context = iv.getContext();
        if (context == null) {
            context = ABApplication.getInstance().getApplicationContext();
        }
        DrawableRequestBuilder<String> builder = Glide.with(context).load(uri).centerCrop()
                .placeholder(resDef)
                .error(resDef)
                .crossFade();
        fitThirdImageView(builder, iv);
    }

    public static void display(ImageView iv, int resId) {
        Context context = iv.getContext();
        if (context == null) {
            context = ABApplication.getInstance().getApplicationContext();
        }
        DrawableRequestBuilder<Integer> builder = Glide.with(context).load(resId).centerCrop()
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade();
        fitThirdImageView(builder, iv);
    }

    public static void display(ImageView iv, int resId, int scaleType) {
        DrawableRequestBuilder<Integer> builder;
        switch (scaleType) {
            case 6:
                builder = Glide.with(iv.getContext()).load(resId).centerCrop()
                        .placeholder(BG_DEF)
                        .error(BG_DEF)
                        .crossFade();
                fitThirdImageView(builder, iv);
                break;
            default:
                builder = Glide.with(iv.getContext()).load(resId)
                        .placeholder(BG_DEF)
                        .error(BG_DEF)
                        .crossFade();
                fitThirdImageView(builder, iv);
                break;
        }
    }

    public static void dispalyWithNoFade(ImageView iv, String url) {
        DrawableRequestBuilder<String> builder = Glide.with(iv.getContext()).load(url).centerCrop()
                .placeholder(BG_DEF)
                .error(BG_DEF);
        fitThirdImageView(builder, iv);
    }

    public static void displayGif(ImageView iv, String url) {
        GifRequestBuilder<String> stringGifRequestBuilder = Glide.with(iv.getContext()).load(url).asGif().centerCrop()
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade();
        stringGifRequestBuilder.into(buildTarget(iv, GifDrawable.class));
    }


    public static void dispalyWithCenterCrop(final ImageView imageView, String url) {
        DrawableRequestBuilder<String> builder = Glide.with(imageView.getContext()).load(url).centerCrop()
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade();
        fitThirdImageView(builder, imageView);
    }

    public static void dispalyWithCenterCrop(ImageView iv, File file) {
        DrawableRequestBuilder<File> builder = Glide.with(iv.getContext()).load(file).centerCrop()
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade();
        fitThirdImageView(builder, iv);
    }


    public static void dispalyWithCenterCrop(ImageView iv, File file, int placeholderId, int width, int height) {
        DrawableRequestBuilder<File> builder = Glide.with(iv.getContext()).load(file).centerCrop()
                .placeholder(placeholderId)
                .error(placeholderId)
                .override(width, height)
                .crossFade();
        fitThirdImageView(builder, iv);
    }


    public static void dispalyWithCenterCrop(ImageView iv, File file, int placeholderId) {
        DrawableRequestBuilder<File> builder = Glide.with(iv.getContext()).load(file).centerCrop()
                .placeholder(placeholderId)
                .crossFade();
        fitThirdImageView(builder, iv);
    }

    public static void dispalyWithFitCenter(ImageView iv, String url) {

        DrawableRequestBuilder<String> builder = Glide.with(iv.getContext()).load(url).fitCenter()
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade();
        fitThirdImageView(builder, iv);
    }


    public static void dispalyWithFitCenterDef(ImageView iv, String url, int resImg) {

        DrawableRequestBuilder<String> builder = Glide.with(iv.getContext()).load(url).fitCenter()
                .placeholder(resImg)
                .error(resImg)
                .crossFade();
        fitThirdImageView(builder, iv);
    }


    public static void display(ImageView iv, String url, int width, int height) {
        DrawableRequestBuilder<String> builder = Glide.with(iv.getContext()).load(url)
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade()
                .override(width, height)
                .centerCrop();
        fitThirdImageView(builder, iv);
    }

    /**
     * load and display a circular image.
     *
     * @param iv          ImageView showing the target image
     * @param uri         target image loading address
     * @param borderWidth the border value of a circular image
     * @param borderColor border color value of a circular image
     * @param def         round picture default placeholder
     */
    public static void displayCircleBitmap(ImageView iv, String uri, float borderWidth, int borderColor, int def) {
        Glide.with(iv.getContext()).load(uri).centerCrop()
                .placeholder(def)
                .error(def)
                .dontAnimate()
                .crossFade()
                .bitmapTransform(new GlideCircleTransform(iv.getContext(), borderWidth, borderColor))
                .into(iv);
    }

    /**
     * load and display rounded corners.
     *
     * @param imageView ImageView showing the target image
     * @param imgUrl    target image loading address
     * @param radius    set the fillet value of the fillet image
     */
    public static void loadRoundedCornersImage(ImageView imageView, String imgUrl, int radius) {
        loadRoundedCornersImage(imageView, imgUrl, radius, BG_DEF);
    }


    /**
     * load and display rounded corners.
     *
     * @param imageView ImageView showing the target image
     * @param imgUrl    target image loading address
     * @param radius    set the fillet value of the fillet image
     * @param def       set a placeholder for a rounded picture
     */
    public static void loadRoundedCornersImage(ImageView imageView, String imgUrl, int radius, int def) {
        Glide.with(imageView.getContext())
                .load(imgUrl)
                .transform(new CenterCrop(imageView.getContext()), new GlideRoundTransform(imageView.getContext(), radius))
                .placeholder(def)
                .error(def)
                .dontAnimate()
                .crossFade()
                .into(imageView);
       /* Glide.with(imageView.getContext())
                .load(imgUrl)
                .bitmapTransform(new GlideRoundTransform(imageView.getContext(), radius))
                .placeholder(def)
                .error(def)
                .dontAnimate()
                .crossFade()
                .into(imageView);*/
    }


    /**
     * Show round picture.
     */
    public static class GlideCircleTransform extends BitmapTransformation {

        private Paint mBorderPaint;
        private float mBorderWidth;

        public GlideCircleTransform(Context context) {
            super(context);
        }

        public GlideCircleTransform(Context context, float borderWidth, int borderColor) {
            super(context);
            mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;

            mBorderPaint = new Paint();
            mBorderPaint.setDither(true);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }


        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            if (mBorderPaint != null) {
                float borderRadius = r - mBorderWidth / 2;
                canvas.drawCircle(r, r, borderRadius, mBorderPaint);
            }
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

    /**
     * Show rounded picture.
     */
    public static class GlideRoundTransform extends BitmapTransformation {

        private static float radius = 0f;

        public GlideRoundTransform(Context context) {
            this(context, 4);
        }

        public GlideRoundTransform(Context context, int dp) {
            super(context);
            radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }


        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }


        @Override
        public String getId() {
            return getClass().getName() + Math.round(radius);
        }
    }

    public static class AgRequestListener implements RequestListener {

        private DrawableRequestBuilder builder;

        AgRequestListener(DrawableRequestBuilder builder) {
            this.builder = builder;
        }

        @Override
        public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            builder.listener(null);//去掉监听，防止死循环
            builder.load(model).crossFade().centerCrop().into(target);
            return false;
        }
    }

    public static class MyImageViewTarget extends ImageViewTarget<GlideDrawable> {

        public MyImageViewTarget(ImageView view) {
            super(view);
        }

        @Override
        protected void setResource(GlideDrawable resource) {
            view.setImageDrawable(resource);
        }

        @Override
        public void setRequest(Request request) {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            view.setTag(glide_tag_id, request);
        }

        @Override
        public Request getRequest() {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            return (Request) view.getTag(glide_tag_id);
        }
    }

    public static class MyGifTarget extends ImageViewTarget<GifDrawable> {

        public MyGifTarget(ImageView view) {
            super(view);
        }

        @Override
        protected void setResource(GifDrawable resource) {
            view.setImageDrawable(resource);
        }

        @Override
        public void setRequest(Request request) {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            view.setTag(glide_tag_id, request);
        }

        @Override
        public Request getRequest() {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            return (Request) view.getTag(glide_tag_id);
        }
    }

    public static class MyGifImageViewTarget extends GlideDrawableImageViewTarget {


        public MyGifImageViewTarget(ImageView view) {
            super(view);
        }

        @Override
        public void setRequest(Request request) {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            view.setTag(glide_tag_id, request);
        }

        @Override
        public Request getRequest() {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            return (Request) view.getTag(glide_tag_id);
        }
    }

    @SuppressWarnings("unchecked")
    public static <Z> Target<Z> buildTarget(ImageView view, Class<Z> clazz) {
        if (GlideDrawable.class.isAssignableFrom(clazz)) {
            return (Target<Z>) new MyGifImageViewTarget(view);
        } else if (Bitmap.class.equals(clazz)) {
            return (Target<Z>) new BitmapImageViewTarget(view);
        } else if (Drawable.class.isAssignableFrom(clazz)) {
            return (Target<Z>) new DrawableImageViewTarget(view);
        } else {
            throw new IllegalArgumentException("Unhandled class: " + clazz
                    + ", try .as*(Class).transcode(ResourceTranscoder)");
        }
    }
}
