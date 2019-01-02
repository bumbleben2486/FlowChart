/**
 * @dec
 */
package core.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Title HtmlUtils
 * @dec textview 显示html内容工具
 * @Author YANGQIYUN
 * @date 2015-11-21
 */
public class HtmlUtils {

    private Context context;
    private TextView txtView;
    /**
     * 单线程列队执行
     */
    private static ExecutorService singleExecutor = null;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            txtView.invalidate();
            txtView.setText(txtView.getText()); // 解决图文重叠
        }

    };

    public HtmlUtils(Context context) {
        this.context = context;
    }

    public void ShowHtml(final TextView txtView, final String html) {
        this.txtView = txtView;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Spanned text = Html.fromHtml(html, imgGetter, null);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtView.setText(text);
                    }
                });
            }
        }).start();
    }

   /* public interface WebViewActionCallBack {

        boolean actionCallback(WebView webView, String url);
    }

    private void loadHtmlUrl(WebView webView, final WebViewActionCallBack actionCallBack) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return actionCallBack.actionCallback(view, url);
            }
        });
    }*/

    //拦截url方式
    public String getParamByUrl(String url, String filt, String param) {
        if (url != null && url.contains(filt)) {
            String params = Uri.parse(url).getQueryParameter(param);
            return params;
        }
        return "";
    }

    private ImageGetter imgGetter = new ImageGetter() //格式语句不一定相同，只要进行网络加载图片即可
    {
        public Drawable getDrawable(String source) {

            final URLDrawable urlDrawable = new URLDrawable();

            onDownLoad(context, urlDrawable, source);

            return urlDrawable;
        }
    };

    @SuppressWarnings("deprecation")
    public class URLDrawable extends BitmapDrawable {
        protected Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            if (bitmap != null) {
                int screenWidth = ABAppUtil.getDeviceWidth(context);
                int draWidth = ABAppUtil.getDeviceWidth(context) - ABAppUtil.dip2px(context, 24);
                int draHeight = screenWidth * bitmap.getHeight() / bitmap.getWidth();
                Rect dst = new Rect();
                dst.left = 0;
                dst.top = 0;
                dst.right = draWidth;
                dst.bottom = draHeight;
                canvas.drawBitmap(bitmap, null, dst, null);
                dst = null;
            }
        }
    }

    /**
     * @param context 上下文
     * @return
     * @dec 获取屏幕宽
     */
    @SuppressWarnings("deprecation")
    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * @param context  上下文
     * @param dipValue dp大小
     * @return
     * @dec dp转px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 执行单线程列队执行
     */
    public void runOnQueue(Runnable runnable) {
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.submit(runnable);
    }

    interface ImageDownLoadCallBack {
        void onDownLoadSuccess(Bitmap bmp);

        void onDownLoadFailed();
    }

    /**
     * 启动图片下载线程
     */
    private void onDownLoad(final Context context, final URLDrawable urlDrawable, String url) {
        DownLoadImageService service = new DownLoadImageService(context, url,
                new ImageDownLoadCallBack() {
                    @Override
                    public void onDownLoadSuccess(Bitmap bmp) {
                        // 在这里执行图片保存方法
                        urlDrawable.bitmap = bmp;
                        int screenWidth = ABAppUtil.getDeviceWidth(context);
                        int draWidth = screenWidth;
                        int draHeight = screenWidth * bmp.getHeight() / bmp.getWidth();
                        urlDrawable.setBounds(0, 0, draWidth, draHeight);
                        mHandler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                    }
                });
        //启动图片下载线程
        runOnQueue(service);
    }


    public class DownLoadImageService implements Runnable {

        private String url;
        private Context context;
        private ImageDownLoadCallBack callBack;

        public DownLoadImageService(Context context, String url, ImageDownLoadCallBack callBack) {
            this.url = url;
            this.callBack = callBack;
            this.context = context;
        }

        @Override
        public void run() {
            Bitmap bmp = null;
            try {
                bmp = Glide.with(context)
                        .load(url)
                        .asBitmap()
//						.downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bmp != null) {
                    callBack.onDownLoadSuccess(bmp);
                } else {
                    callBack.onDownLoadFailed();
                }
            }
        }

    }
}
