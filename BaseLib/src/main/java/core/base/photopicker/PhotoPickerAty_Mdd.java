package core.base.photopicker;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import core.base.R;
import core.base.application.BaseActivity;
import core.base.log.L;
import core.base.photopicker.adapters.PhotoAdapter_Mdd;
import core.base.photopicker.adapters.SelectedPhotoAdapter;
import core.base.photopicker.beans.MediaBean;
import core.base.photopicker.beans.MediaFloder;
import core.base.photopicker.beans.SelectImageEvent;
import core.base.photopicker.beans.SelectPhotoEvent;
import core.base.photopicker.beans.SelectStatusEvent;
import core.base.photopicker.utils.MediaManager;
import core.base.photopicker.utils.OtherUtils;
import core.base.utils.ABAppUtil;
import core.base.views.grid.GridLayoutAdapter;
import core.base.views.recyclerview.GridSpacingItemDecoration;
import core.base.views.recyclerview.SpaceItemDecoration;

/**
 * 相册选择页面
 * Created by Nowy on 2017/9/11.
 */

public class PhotoPickerAty_Mdd extends BaseActivity implements PhotoAdapter_Mdd.PhotoSelectListener, View.OnClickListener {

    public final static String TAG = "PhotoPickerActivity";
    public final static String KEY_RESULT = "picker_result";
    public final static int REQUEST_CAMERA = 1;

    /**
     * 是否显示相机
     */
    public final static String EXTRA_SHOW_CAMERA = "is_show_camera";
    /**
     * 照片选择标志
     */
    public final static String FLAG = "flag";
    /**
     * 最大选择数量
     */
    public final static String EXTRA_MAX_MUN = "max_num";
    /**
     * 单选
     */
    public final static int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public final static int MODE_MULTI = 1;

    private int mMode = MODE_MULTI;

    private int mFileId = 1;//拍照自定义id

    /**
     * 默认最大选择数量
     */
    public final static int DEFAULT_NUM = 9;


    /**
     * 最大选择数量，仅多选模式有用
     */
    public static int mMaxNum = DEFAULT_NUM;


    /**
     * 默认为0 - 表示从编辑页面过去的，1 - 预览发表页面过去照片相册页面,2 - 我的上传头像
     */
    public final static String EXTRA_PAGE_TAG = "page_tag";

    /**
     * 标识编辑页面的点击封面图片过来还是内容图片过来
     */
    public final static String EXTRA_PHOTO_CONTENT = "photo_tag";


    /**
     * 是否显示底部选中栏
     */
    private static final String EXTRA_IS_SHOW_BOTTOM = "isShowBottom";

    /**
     * 图片选择模式，单选和多选
     */
    private static final String EXTRA_MODE = "mode";

    /**
     * 标题
     */
    private static final String EXTRA_TITLE = "title";

    /**
     * 是否裁剪
     */
    private static final String EXTRA_CROP = "crop_photo";


    /**
     * 是否显示相机，默认不显示
     */
    private boolean mIsShowCamera = true;
    /**
     * 照片选择标示
     */
    private String flag;

    private int pageTag;

    private String orderId;
    private List<MediaBean> mMediaBeanLists = new ArrayList<>();
    private PhotoAdapter_Mdd mPhotoAdapter;
    private ProgressDialog mProgressDialog;
    private ListView mFloderListView;
    private TextView previewBtn;
    private Button mCommitBtn;
    /**
     * 文件夹列表是否处于显示状态
     */
    boolean mIsFloderViewShow = false;
    /**
     * 文件夹列表是否被初始化，确保只被初始化一次
     */
    boolean mIsFloderViewInit = false;

    /**
     * 拍照时存储拍照结果的临时文件
     */
    private File mTmpFile;

    MediaManager mediaManager;
    private SelectPhotoEvent selectPhotoEvent;

    //titleBar
    private TextView mTvTitle;
    private TextView mTvRight;
    //图片视图
    private RecyclerView mRvPhoto;
    //底部选中图片视图和提交按钮，只有多选的时候会出现
    private RecyclerView mRvSelectedPhoto;
    private Button mBtnConfirm;
    private GridLayoutManager mManager;
    private View mViewMask;
    private SelectedPhotoAdapter mSelectedPhotoAdapter;
    private String mTitleName;
    private boolean mIsShowBottom = true;
    private boolean mIsCrop;


    public static void start(Context context, String title, boolean isShowCamera, String flag, int num, int mode, boolean isShowBottom, int tag) {
        start(context, title, flag, num, mode, isShowBottom, tag, isShowCamera, false);
    }


    /**
     * 相册选择页面
     *
     * @param context      上下文
     * @param title        相册选取页面标题
     * @param flag         照片选择标示
     * @param num          选择照片最大数量
     * @param mode         选取模式（单选还是多选）
     * @param isShowBottom 是否显示底部菜单
     * @param tag          默认为0 - 表示从编辑页面过去的，1 - 预览发表页面过去照片相册页面，2 - 我的上传头像
     * @param isShowCamera 是否显示相机
     * @param isCrop       是否需要裁剪
     */
    public static void start(Context context, String title, String flag, int num, int mode, boolean isShowBottom, int tag, boolean isShowCamera, boolean isCrop) {
        Intent starter = new Intent(context, PhotoPickerAty_Mdd.class);
        starter.putExtra(FLAG, flag);
        //新增是编辑页面过来的还是预览发布过来的
        starter.putExtra(EXTRA_PAGE_TAG, tag);
        starter.putExtra(EXTRA_IS_SHOW_BOTTOM, isShowBottom);
        starter.putExtra(EXTRA_SHOW_CAMERA, isShowCamera);
        starter.putExtra(EXTRA_MAX_MUN, num);
        starter.putExtra(EXTRA_MODE, mode);
        starter.putExtra(EXTRA_TITLE, title);
        starter.putExtra(EXTRA_CROP, isCrop);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_photo_picker_w);
        EventBus.getDefault().register(this);
        selectPhotoEvent = new SelectPhotoEvent();
        selectPhotoEvent.flag = PhotoPreviewActivity_2.TAG_PHOTO_CROP;
        initVariables();
        initView();
        new ReadPhotosTask().execute();
    }


    private void initVariables() {
        Intent intent = getIntent();
        mIsShowBottom = intent.getBooleanExtra(EXTRA_IS_SHOW_BOTTOM, false);
        mIsShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        mIsCrop = intent.getBooleanExtra(EXTRA_CROP, false);
        mMaxNum = intent.getIntExtra(EXTRA_MAX_MUN, DEFAULT_NUM);
        mMode = intent.getIntExtra(EXTRA_MODE, MODE_MULTI);
        mTitleName = intent.getStringExtra(EXTRA_TITLE);
        flag = intent.getStringExtra(FLAG);
        pageTag = intent.getIntExtra(EXTRA_PAGE_TAG, 0);
        mediaManager = MediaManager.getInstance();
        mediaManager.init(getApplicationContext(), flag, pageTag);
    }


    private void initView() {
        findViewById(R.id.photo_picker_w_IvLeft).setOnClickListener(this);
        mTvTitle = (TextView) findViewById(R.id.photo_picker_w_TvTitle);
        mTvRight = (TextView) findViewById(R.id.photo_picker_w_TvRight);
        mTvTitle.setText(mTitleName);
        mViewMask = findViewById(R.id.photo_picker_w_Mask);
        mTvRight.setOnClickListener(this);
        mRvPhoto = (RecyclerView) findViewById(R.id.photo_picker_w_RvPhoto);
        View bottom = findViewById(R.id.photo_picker_w_VBottom);
        initBottom(bottom);
        initRv();
    }

    private void initRv() {
        mManager = new GridLayoutManager(this, 3);
        mManager.setOrientation(GridLayoutManager.VERTICAL);
        mRvPhoto.addItemDecoration(new GridSpacingItemDecoration(3, ABAppUtil.dip2px(this, 5.0f), false));
        ((SimpleItemAnimator) mRvPhoto.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvPhoto.setLayoutManager(mManager);
    }


    private void initBottom(View bottom) {
        if (bottom == null) return;
        bottom.setVisibility(mIsShowBottom ? View.VISIBLE : View.GONE);
        mRvSelectedPhoto = (RecyclerView) bottom.findViewById(R.id.photo_picker_bottom_RvSelectedPhoto);
        mBtnConfirm = (Button) bottom.findViewById(R.id.photo_picker_bottom_BtnConfirm);
        mBtnConfirm.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvSelectedPhoto.setLayoutManager(layoutManager);
        mRvSelectedPhoto.addItemDecoration(new SpaceItemDecoration(ABAppUtil.dip2px(this, 10.0f), LinearLayoutManager.HORIZONTAL));
        mSelectedPhotoAdapter = new SelectedPhotoAdapter(MediaManager.getSelectMediaBeans());
        mSelectedPhotoAdapter.setOnDelListener(getDelListener());
        mRvSelectedPhoto.setAdapter(mSelectedPhotoAdapter);
    }


    private void getPhotosSuccess() {
        mProgressDialog.dismiss();
        mMediaBeanLists.clear();
        mMediaBeanLists.addAll(mediaManager.getMediaFloder(MediaManager.ALL_PHOTO).getMediaBeanList());

        mPhotoAdapter = new PhotoAdapter_Mdd(this, mMediaBeanLists);
        mPhotoAdapter.setPhotoSelectListener(this);
        mPhotoAdapter.setIsShowCamera(mIsShowCamera);
        mPhotoAdapter.setMaxNum(mMaxNum);
        mRvPhoto.setAdapter(mPhotoAdapter);
//        Set<String> keys = mediaManager.getMediaMap().keySet();
//        final List<MediaFloder> floders = new ArrayList<MediaFloder>();
//        for (String key : keys) {
//            if (MediaManager.ALL_PHOTO.equals(key)) {
//                MediaFloder floder = mediaManager.getMediaMap().get(key);
//                floder.setIsSelected(true);
//                floders.add(0, floder);
//            } else {
//                floders.add(mediaManager.getMediaMap().get(key));
//            }
//        }

    }


    private SelectedPhotoAdapter.OnDelListener getDelListener() {
        return new SelectedPhotoAdapter.OnDelListener() {
            @Override
            public void onDel(int position, MediaBean bean) {
                if (mPhotoAdapter != null) {
                    MediaBean selectedBean = mPhotoAdapter.getItemById(bean.getId());
                    if (selectedBean != null) {
                        selectedBean.setIsSelected(false);
                    }
                    mPhotoAdapter.notifyDataSetChanged();
                }

                updateBtnTxt();
            }
        };
    }


    //裁剪相关
    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = System.currentTimeMillis() + PhotoPreviewActivity_2.SAMPLE_CROPPED_IMAGE_NAME;
        //这里默认用.jpg的形式
        destinationFileName += ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);
        uCrop.start(PhotoPickerAty_Mdd.this);
    }

    /**
     * 裁剪相关
     * In most cases you need only to set crop aspect ration and max size for resulting image.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop basisConfig(@NonNull UCrop uCrop) {
        if (pageTag == 0 || pageTag == 1) {//0-标识为生活秀编辑文章过来的
            return uCrop.withAspectRatio(25, 12);//16:9 - 25:12
        } else {
            return uCrop.withAspectRatio(1, 1);//2-标识为我的上传头像过来的
        }
        // return uCrop.withAspectRatio(1, 1);//uCrop.useSourceImageAspectRatio();
    }

    /**
     * 裁剪相关
     * Sometimes you want to adjust more options, it's done via {@link com.yalantis.ucrop.UCrop.Options} class.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        //jpg格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.c_333333));
        options.setStatusBarColor(ContextCompat.getColor(this, core.base.R.color.c_333333));// core.base.R.color.status_bg_color
        if (pageTag == 2) {//我的页面-上传头像
            options.setToolbarTitle("编辑头像");
        } else {
            options.setToolbarTitle("编辑图片");
        }
        options.setHideBottomControls(true);
        return uCrop.withOptions(options);
    }


    @Override
    public void photoSelected(int index, int ImageId, boolean isSelect) {
        if (mMode == MODE_SINGLE) {//单选
            if (mIsCrop) {//去裁剪页面
                if (isSelect) {
                    MediaBean selectedBean = mPhotoAdapter.getItemById(ImageId);
                    String photoPath = selectedBean.getRealPath();
                    startCropActivity(Uri.fromFile(new File(photoPath)));
                }
            } else {//不去裁剪就直接返回图片
                MediaManager.selectOK();
                finish();
            }
        } else {
            updateBtnTxt();
        }
    }

    private void updateBtnTxt() {
        if (MediaManager.getSelectMediaBeans() != null) {
            if (mSelectedPhotoAdapter != null) {
                mSelectedPhotoAdapter.notifyDataSetChanged();
            }
            mBtnConfirm.setText(String.format(Locale.CHINA, "确定(%1$d)", MediaManager.getSelectMediaBeans().size()));
        } else {
            mBtnConfirm.setText(String.format(Locale.CHINA, "确定(%1$d)", 0));
        }
    }


    @Override
    public void gotoCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // 创建临时文件
            mTmpFile = OtherUtils.createFile(getApplicationContext());
            //Android 7.0系统相机访问需要判断
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".FileProvider", mTmpFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            } else {
                // 设置系统相机拍照后的输出路径
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            }
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(getApplicationContext(), R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    //=======
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // 读取下载到手机的文件
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // 读取媒体文件
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // 读取普通媒体文件或者一般的文件
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Android 4.4以下版本自动使用该方法
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {MediaStore.Images.Media.DATA
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    //=======

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photo_picker_w_IvLeft) {
            finish();
        } else if (v.getId() == R.id.photo_picker_w_TvRight) {//分类筛选
            PhotoClazzPop.showPop(this, v, mViewMask, getPhotoData(), new PhotoClazzPop.OnItemClickListener() {
                @Override
                public void itemClick(GridLayoutAdapter adapter, int position, List<MediaFloder> data) {
                    for (MediaFloder floder : data) {
                        floder.setIsSelected(false);
                    }
                    MediaFloder floder = data.get(position);
                    floder.setIsSelected(true);
                    adapter.notifyDataSetChanged(false);

                    mMediaBeanLists.clear();
                    mMediaBeanLists.addAll(floder.getMediaBeanList());


                    if (MediaManager.ALL_PHOTO.equals(floder.getName())) {
                        mPhotoAdapter.setIsShowCamera(mIsShowCamera);
                    } else {
                        mPhotoAdapter.setIsShowCamera(false);
                    }
                    //这里重新设置adapter而不是直接notifyDataSetChanged，是让GridView返回顶部
                    mRvPhoto.setAdapter(mPhotoAdapter);
//                    previewBtn.setText(OtherUtils.formatResourceString(getApplicationContext(),
//                            R.string.photos_num, mMediaBeanLists.size()));
                    mTvRight.setText(floder.getName());

                }
            });
        } else if (R.id.photo_picker_bottom_BtnConfirm == v.getId()) {//确定
            MediaManager.selectOK();
            finish();
        }
    }


    private List<MediaFloder> getPhotoData() {
        Set<String> keys = mediaManager.getMediaMap().keySet();
        final List<MediaFloder> floders = new ArrayList<>();
        for (String key : keys) {
            if (MediaManager.ALL_PHOTO.equals(key)) {
                MediaFloder floder = mediaManager.getMediaMap().get(key);
                floder.setIsSelected(true);
                floders.add(0, floder);
            } else {
                floders.add(mediaManager.getMediaMap().get(key));
            }
        }
        return floders;
    }


    /**
     * 接收图片预览提交选择ok的请求，关闭本页面
     *
     * @param event
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SelectImageEvent event) {
        Log.d("helloTAG", "cover->" + "onSelectphotoevent");
        finish();
    }

    /**
     * 接受相册预览的选中改动
     *
     * @param event
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SelectStatusEvent event) {
        Log.d("helloTAG", "cover->" + "onSelect-Status-event");
        int firstVisibleItemPosition = mManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = mManager.findLastVisibleItemPosition();
        Integer realFirstPosition = mPhotoAdapter.getRealPosition(firstVisibleItemPosition);
        Integer realLastPosition = mPhotoAdapter.getRealPosition(lastVisibleItemPosition);
        if (realFirstPosition == null) {//起始点是null，说明有个照相机在
            realFirstPosition = 0;
        }

        if (realLastPosition != null && realFirstPosition >= 0 && realLastPosition >= 0 && mPhotoAdapter.getData() != null) {
            for (int index = realFirstPosition; index <= realLastPosition; index++) {
                if (mPhotoAdapter.getData().get(index).getId() == event.imageId) {//找到了更新
                    //有照相机的要加上照相机1个
                    mPhotoAdapter.getData().get(index).setIsSelected(event.isSelect);
                    mPhotoAdapter.notifyItemChanged(mPhotoAdapter.isShowCamera() ? index + 1 : index);
                    photoSelected(index, event.imageId, event.isSelect);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    //file:///data/user/0/com.mdd.platform/cache/SampleCropImage.jpg
                    selectPhotoEvent.status = SelectPhotoEvent.STATUS_OK;
                    selectPhotoEvent.imagePath = resultUri.getPath();
                    if (pageTag == 0) {
                        selectPhotoEvent.actTag = 0;//0-标识为生活秀编辑文章过来的
                    } else if (pageTag == 1) {
                        selectPhotoEvent.actTag = 1;//1-标识为我的发布文章页面
                    } else {
                        selectPhotoEvent.actTag = 2;//2-标识为我的上传头像过来的
                    }
                    EventBus.getDefault().post(selectPhotoEvent);
                    finish();
                    // AppManager.getAppManager().finishActivity(PhotoPickerActivity.class);
                    break;
                case REQUEST_CAMERA:
                    if (mTmpFile != null) {
//                    PhotoPreviewActivity_2.startFromCamera(this, mTmpFile.getAbsolutePath());
                        String dirPath = mTmpFile.getAbsolutePath();
                        long length = mTmpFile.length();
                        String size = Formatter.formatFileSize(this, length);
                        long currentTime = System.currentTimeMillis();
                        String absolutePath = mTmpFile.getAbsolutePath();
                        L.printForDebug("PhotoPickerAty-length", length + ",size=" + size);
                        MediaBean mediaBean = new MediaBean(mFileId, true, false, currentTime, length, size, dirPath, absolutePath, mTmpFile.getName());
                        mediaManager.getMediaFloder(MediaManager.ALL_PHOTO).getMediaBeanList().add(0, mediaBean);
                        getPhotosSuccess();
                        mFileId++;
                    }
                    break;
                default:
                    break;
            }
        } else {
            if (mTmpFile != null && mTmpFile.exists()) {
                mTmpFile.delete();
            }
        }
       /* // 相机拍照完成后，返回图片路径
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    PhotoPreviewActivity_2.startFromCamera(this, mTmpFile.getAbsolutePath());
                    String dirPath = mTmpFile.getAbsolutePath();

                    MediaBean mediaBean = new MediaBean(mFileId, true, false, System.currentTimeMillis(),
                            Formatter.formatFileSize(this, mTmpFile.length()), dirPath,
                            mTmpFile.getAbsolutePath(), mTmpFile.getName());
                    mediaManager.getMediaFloder(MediaManager.ALL_PHOTO).getMediaBeanList().add(0, mediaBean);
                    getPhotosSuccess();
                    mFileId++;
                }

            } else {
                if (mTmpFile != null && mTmpFile.exists()) {
                    mTmpFile.delete();
                }
            }
        } else if () {
        }*/
    }


    /**
     * 获取照片的异步任务
     */
    private class ReadPhotosTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(PhotoPickerAty_Mdd.this, null, "loading...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            mediaManager.initPhotos(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getPhotosSuccess();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
