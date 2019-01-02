package core.base.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import core.base.R;
import core.base.application.BaseActivity;
import core.base.log.L;
import core.base.log.T;
import core.base.manager.AppManager;
import core.base.photopicker.beans.MediaBean;
import core.base.photopicker.beans.SelectPhotoEvent;
import core.base.photopicker.beans.SelectStatusEvent;
import core.base.photopicker.utils.MediaManager;
import core.base.utils.ABAppUtil;
import core.base.utils.image.ABBitmapUtil;
import core.base.views.viewpager.MultiTouchViewPager;
import uk.co.senab.photoview.PhotoView;

/**
 * 预览界面，包括拍照预览
 */
public class PhotoPreviewActivity_2 extends BaseActivity {

    public static final String SAMPLE_CROPPED_IMAGE_NAME = "mdd_crop_img";

    public static final String TAG_PHOTO_CROP = "crop_photo";

    public final static int TYPE_CAMERA = 0;//照相预览
    public final static int TYPE_PHOTO = 1;//预览
    public final static int TYPE_SELECT_PHOTO = 2;//预览选中的照片
    private static final String TAG = "PhotoPreviewActivity";
    private int type;
    private int currentPosition;
    private int pageTag;
    private String cameraPhotoPath;
    private String floderName;
    private MultiTouchViewPager viewPager;
    private ViewPagerAdapter adapter;
    ImageView ivSelect;
    Button commitBtn;
    TextView tv_num;
    TextView tvEditPhoto;
    View rl_show_select;
    //    private List<MediaBean> selectMediaBeans=null;
    private long maxSize = 0L;
    private String photoFlag;

    SelectPhotoEvent selectPhotoEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview_2);
        type = getIntent().getIntExtra("type", 1);
        currentPosition = getIntent().getIntExtra("position", 0);
        cameraPhotoPath = getIntent().getStringExtra("cameraPhotoPath");
        floderName = getIntent().getStringExtra("floderName");
        pageTag = getIntent().getIntExtra(PhotoPickerActivity.EXTRA_PAGE_TAG, 0);
        photoFlag = getIntent().getStringExtra(PhotoPickerActivity.EXTRA_PHOTO_CONTENT);
        maxSize = ABAppUtil.getDeviceWidth(mContext) * ABAppUtil.getDeviceHeight(mContext);
        setupUI();
        selectPhotoEvent = new SelectPhotoEvent();
        selectPhotoEvent.flag = TAG_PHOTO_CROP;
    }

    private void setupUI() {
        ivSelect = (ImageView) findViewById(R.id.iv_select);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tvEditPhoto = (TextView) findViewById(R.id.tv_edit_photo);
        rl_show_select = findViewById(R.id.rl_show_select);
        commitBtn = (Button) findViewById(R.id.commit);
        Log.i("Result", "photoFlag---------->=" + photoFlag);
        if (!TextUtils.isEmpty(photoFlag)){
            if (photoFlag.equals("photo_content")) {
                tvEditPhoto.setVisibility(View.GONE);
            } else {
                tvEditPhoto.setVisibility(View.VISIBLE);
            }
        }else {
            tvEditPhoto.setVisibility(View.GONE);
        }

        //编辑照片
        tvEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/9/6 裁剪
                Log.i("Result", "裁剪---------->cameraPhotoPath=" + cameraPhotoPath);
                if (cameraPhotoPath != null) {
                    startCropActivity(Uri.parse(cameraPhotoPath));
                } else {
                    MediaBean item = adapter.getItem(currentPosition);
                    String realPath = item.getRealPath();
                    Log.i("Result", "裁剪---------->realPath=" + realPath);
                    startCropActivity(Uri.fromFile(new File(realPath)));
                }
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == TYPE_CAMERA) {//是相机预览
                    MediaManager.getSelectMediaBeans().clear();
                    MediaManager.getSelectMediaBeans().add(adapter.getItem(0));
                }
                if (rl_show_select.getVisibility() == View.VISIBLE) {
                    if (ivSelect.isSelected()) {
                        MediaManager.selectOK();
                        finish();
                    } else {
                        T.s(PhotoPreviewActivity_2.this, "请选择图片哦！");
                    }
                } else {
                    MediaManager.selectOK();
                    finish();
                }
            }
        });

        viewPager = (MultiTouchViewPager) findViewById(R.id.viewpager);
        if (type == TYPE_CAMERA) {
            rl_show_select.setVisibility(View.GONE);
            MediaBean bean = new MediaBean(cameraPhotoPath);
            bean.setIsPhoto(true);
            ArrayList<MediaBean> mediaBeans = new ArrayList<>();
            L.e(TAG, "cameraPhotoPath=" + cameraPhotoPath);
            mediaBeans.add(bean);
            adapter = new ViewPagerAdapter(mediaBeans);
        } else if (type == TYPE_SELECT_PHOTO) {
//            selectMediaBeans=new LinkedList<>();
//            selectMediaBeans.addAll(MediaManager.getSelectMediaBeans());
            adapter = new ViewPagerAdapter(MediaManager.getSelectMediaBeans()); //不能直接设置，否则更改选中时数据全局的会减少
//            adapter=new ViewPagerAdapter(selectMediaBeans);
            L.e(TAG, "选中列表预览：" + MediaManager.getSelectMediaBeans());
        } else if (type == TYPE_PHOTO) {
            adapter = new ViewPagerAdapter(MediaManager.getMediaFloder(floderName).getMediaBeanList());
        }
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition, false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ivSelect.setSelected(adapter.getItem(position).isSelected());
                tv_num.setText((position + 1) + "/" + adapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //第一次显示状态
        ivSelect.setSelected(adapter.getItem(currentPosition).isSelected());
        tv_num.setText((currentPosition + 1) + "/" + adapter.getCount());
        ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ivSelect.setSelected(!ivSelect.isSelected());
                boolean isSelected = ivSelect.isSelected();
                int currentItem = viewPager.getCurrentItem();
                MediaBean mediaBean = adapter.getItem(currentItem);

                L.e("接收到的图片: 将要选中的->" + mediaBean.getId() + " mediaBean.isSelected:" + mediaBean.isSelected());
                if (isSelected) {//已经选中的，取消选中
                    MediaManager.getSelectMediaBeans().remove(mediaBean);
                    ivSelect.setSelected(false);
                    EventBus.getDefault().post(new SelectStatusEvent(mediaBean.getId(), false));
                } else {//未选中的，选择
                    if (MediaManager.getSelectMediaBeans().size() >= PhotoPickerActivity.mMaxNum) {//已达上限
                        ivSelect.setSelected(false);
                        Toast.makeText(mContext, "只能选取" + PhotoPickerActivity.mMaxNum + "张", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        MediaManager.getSelectMediaBeans().remove(mediaBean);
                        ivSelect.setSelected(true);
                        if (ivSelect.isSelected()) {
                            mediaBean.setIsSelected(true);
                            MediaManager.getSelectMediaBeans().add(mediaBean);
                        }
                        //通知相册列表更新
                        EventBus.getDefault().post(new SelectStatusEvent(mediaBean.getId(), true));
                    }
                }
            }
        });
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = System.currentTimeMillis() + SAMPLE_CROPPED_IMAGE_NAME;
        //这里默认用.jpg的形式
        destinationFileName += ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);
        uCrop.withAspectRatio(16, 9);//宽高比
        uCrop.start(PhotoPreviewActivity_2.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            //file:///data/user/0/com.mdd.platform/cache/SampleCropImage.jpg
            Log.i("Result", "裁剪-------------resultUri=" + resultUri);
            Log.i("Result", "裁剪-------------resultUri-path=" + resultUri.getPath());
            Log.i("Result", "裁剪-------------pageTag=" + pageTag);
            //----------
            selectPhotoEvent.status = SelectPhotoEvent.STATUS_OK;
            selectPhotoEvent.imagePath = resultUri.getPath();
            selectPhotoEvent.actTag = pageTag;//0
            EventBus.getDefault().post(selectPhotoEvent);
            AppManager.getAppManager().finishActivity(PhotoPickerActivity.class);
            finish();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    /**
     * In most cases you need only to set crop aspect ration and max size for resulting image.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop basisConfig(@NonNull UCrop uCrop) {
        return uCrop.useSourceImageAspectRatio();
    }

    /**
     * Sometimes you want to adjust more options, it's done via {@link com.yalantis.ucrop.UCrop.Options} class.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop advancedConfig(@NonNull UCrop uCrop) {

        UCrop.Options options = new UCrop.Options();
        //jpg格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        //png格式
        //options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        /*
        If you want to configure how gestures work for all UCropActivity tabs

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        * */

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.

        options.setMaxBitmapSize(640);
        * */


       /*

        Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧

        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setDimmedLayerColor(Color.CYAN);
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);
        options.setToolbarCropDrawable(R.drawable.your_crop_icon);
        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon);

        // Color palette
        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));

        // Aspect ratio options
        options.setAspectRatioOptions(1,
            new AspectRatio("WOW", 1, 2),
            new AspectRatio("MUCH", 3, 4),
            new AspectRatio("RATIO", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
            new AspectRatio("SO", 16, 9),
            new AspectRatio("ASPECT", 1, 1));

       */

        options.setToolbarColor(ContextCompat.getColor(this, R.color.c_f04877));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bg_color));
        options.setToolbarTitle("编辑图片");
        return uCrop.withOptions(options);
    }


    //---------------------------------------------------

    /**
     * 预览相机拍过来的照片
     *
     * @param context
     * @param cameraPhotoPath
     */
    public static void startFromCamera(Context context, String cameraPhotoPath) {
        L.e(TAG, "cameraPhotoPath=" + cameraPhotoPath);
        Intent starter = new Intent(context, PhotoPreviewActivity_2.class);
        starter.putExtra("type", TYPE_CAMERA);
        starter.putExtra("cameraPhotoPath", cameraPhotoPath);
        context.startActivity(starter);
    }

    /**
     * 预览图片
     *
     * @param context
     * @param floderName
     * @param position
     */
    public static void startPreviewPhoto(Context context, String floderName, int position, int tag, String flag) {
        Intent starter = new Intent(context, PhotoPreviewActivity_2.class);
        starter.putExtra("type", TYPE_PHOTO);
        starter.putExtra("position", position);
        starter.putExtra("floderName", floderName);
        //添加页面来向
        starter.putExtra(PhotoPickerActivity.EXTRA_PAGE_TAG, tag);
        //是点击封面还是内容图片过来的
        starter.putExtra(PhotoPickerActivity.EXTRA_PHOTO_CONTENT, flag);
        context.startActivity(starter);
    }

    public static void startPreviewPhoto(Activity activity, String floderName, int position, int reqCode) {
        Intent starter = new Intent(activity, PhotoPreviewActivity_2.class);
        starter.putExtra("type", TYPE_PHOTO);
        starter.putExtra("position", position);
        starter.putExtra("floderName", floderName);
        activity.startActivityForResult(starter, reqCode);
    }

    /**
     * 预览选中的图片
     *
     * @param context
     */
    public static void startPreviewSelectPhoto(Context context, int tag, String flag) {
        Intent starter = new Intent(context, PhotoPreviewActivity_2.class);
        starter.putExtra("type", TYPE_SELECT_PHOTO);
        //添加页面来向
        starter.putExtra(PhotoPickerActivity.EXTRA_PAGE_TAG, tag);
        //是点击封面还是内容图片过来的
        starter.putExtra(PhotoPickerActivity.EXTRA_PHOTO_CONTENT, flag);
        context.startActivity(starter);
    }

    class ViewPagerAdapter extends PagerAdapter {
        private List<MediaBean> allPicFiles;// 所有图片
        private MediaBean mf;

        public ViewPagerAdapter(List<MediaBean> sysallPicFiles) {
            super();
            this.allPicFiles = sysallPicFiles;
        }


        @Override
        public int getCount() {
            return allPicFiles.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public MediaBean getItem(int position) {
            return allPicFiles.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            mf = allPicFiles.get(position);
            if (mf.isPhoto()) {
                PhotoView photoView = new PhotoView(container.getContext());
                photoView.setScaleType(ImageView.ScaleType.CENTER);
//                DrawableRequestBuilder builder =Glide.with(mContext)
//                        .load(new File(mf.getRealPath()));
//
//                builder.into(photoView);

                int[] size = ABBitmapUtil.getBitmapWidthAndHeight(mf.getRealPath());
                if (size[0] * size[1] > maxSize) {

                    Glide.with(mContext)
                            .load(new File(mf.getRealPath()))
                            .dontAnimate()
                            .dontTransform()
                            .override(ABAppUtil.getDeviceWidth(mContext) > size[0] ? size[0] : ABAppUtil.getDeviceWidth(mContext),
                                    ABAppUtil.getDeviceHeight(mContext) > size[1] ? size[1] : ABAppUtil.getDeviceHeight(mContext))
                            .into(photoView);
                } else {
                    Glide.with(mContext)
                            .load(new File(mf.getRealPath()))
                            .dontAnimate()
                            .dontTransform()
                            .into(photoView);
                }


                container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                return photoView;
            }

            return null;
        }
    }
}
