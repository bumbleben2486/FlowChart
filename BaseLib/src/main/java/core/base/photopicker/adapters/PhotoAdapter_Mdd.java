package core.base.photopicker.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.List;

import core.base.R;
import core.base.log.T;
import core.base.photopicker.PhotoPickerAty_Mdd;
import core.base.photopicker.beans.MediaBean;
import core.base.photopicker.utils.MediaManager;
import core.base.photopicker.utils.OtherUtils;
import core.base.utils.image.GlideDisplay;

/**
 * Created by Nowy on 2017/9/18.
 */

public class PhotoAdapter_Mdd extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_PHOTO = 1;
    private static final String TAG = "PhotoRcyAdapter";

    private List<MediaBean> mDatas;
    private String floderName;
    private String photoFlag;
    private PhotoSelectListener mPhotoSelectListener;
    private int mWidth;
    //是否显示相机，默认不显示
    private boolean mIsShowCamera = false;
    //是否需要裁剪
    private boolean mIsCrop = false;
    //图片选择数量
    private int mMaxNum = PhotoPickerAty_Mdd.DEFAULT_NUM;
    private int pageTag;
    private int selectedPosition = -1;

    public PhotoAdapter_Mdd(Context context, List<MediaBean> data) {
        this.mDatas = data;
        int screenWidth = OtherUtils.getWidthInPx(context);
        mWidth = (screenWidth - OtherUtils.dip2px(context, 5) * 4) / 3;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_camera_layout, parent, false);
            //设置高度等于宽度
            GridLayoutManager.LayoutParams layoutParams = new GridLayoutManager.LayoutParams(mWidth, mWidth);
            view.setLayoutParams(layoutParams);
            return new CameraHolder(view);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(ItemViewHolder.RES_LAYOUT, parent, false);
            GridLayoutManager.LayoutParams layoutParams = new GridLayoutManager.LayoutParams(mWidth, mWidth);
            itemView.setLayoutParams(layoutParams);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_CAMERA:
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPhotoSelectListener != null) {
                            mPhotoSelectListener.gotoCamera();
                        }
                    }
                });
                break;
            case TYPE_PHOTO:
                final ItemViewHolder photoHolder = (ItemViewHolder) holder;
                final int index = getRealPosition(position);

                photoHolder.cbSelected.setChecked(mDatas.get(index).isSelected());//设置显示选择切换
                photoHolder.rlMain.setBackgroundResource(mDatas.get(index).isSelected() ? R.drawable.bg_shape_white_stroke_mdd : R.color.white);
                //设置选中切换
                photoHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      /*  if (mDatas.get(index).isSelected()) {//原来选中，现在是取消选中
                            photoHolder.cbSelected.setSelected(false);
                            mDatas.get(index).setIsSelected(false);//更改数据为未选中
                            MediaManager.getSelectMediaBeans().remove(mDatas.get(index));
                        } else {//选中
                            if (MediaManager.getSelectMediaBeans().size() >= mMaxNum) {//已达上限
                                T.s(photoHolder.cbSelected.getContext().getApplicationContext(), "只能选取" + mMaxNum + "张");
                                return;
                            } else {
                                photoHolder.cbSelected.setSelected(true);
                                //先移除，保证不重复
                                MediaManager.getSelectMediaBeans().remove(mDatas.get(index));
                                MediaManager.getSelectMediaBeans().add(mDatas.get(index));
                                mDatas.get(index).setIsSelected(true);//更改数据为选中
                            }
                        }
                        notifyItemChanged(position);
                        if (mPhotoSelectListener != null) {
                            mPhotoSelectListener.photoSelected(index, mDatas.get(index).getId(), mDatas.get(index).isSelected());
                        }*/
                        try {
                            if (mDatas.get(index).isSelected()) {//原来选中，现在是取消选中
                                photoHolder.cbSelected.setSelected(false);
                                mDatas.get(index).setIsSelected(false);//更改数据为未选中
                                MediaManager.getSelectMediaBeans().remove(mDatas.get(index));
                            } else {//选中
                                List<MediaBean> selectMediaBeans = MediaManager.getSelectMediaBeans();
                                if (selectMediaBeans != null) {
                                    if (selectMediaBeans.size() >= mMaxNum) {//已达上限
                                        T.s(photoHolder.cbSelected.getContext().getApplicationContext(), "只能选取" + mMaxNum + "张");
                                        return;
                                    } else {
                                        photoHolder.cbSelected.setSelected(true);
                                        //先移除，保证不重复
                                        MediaManager.getSelectMediaBeans().remove(mDatas.get(index));
                                        MediaManager.getSelectMediaBeans().add(mDatas.get(index));
                                        mDatas.get(index).setIsSelected(true);//更改数据为选中
                                    }
                                }
                            }
                            notifyItemChanged(position);
                            if (mPhotoSelectListener != null) {
                                mPhotoSelectListener.photoSelected(index, mDatas.get(index).getId(), mDatas.get(index).isSelected());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                //预览
//                photoHolder.photoImageView.setAspectRatio(1);
//                photoHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        Toast.makeText(mContext, "去预览", Toast.LENGTH_LONG).show();
////                        PhotoPreviewActivity_2.startPreviewPhoto(mContext, floderName, index, pageTag,photoFlag);
//                    }
//                });
                photoHolder.ivPhoto.getLayoutParams().width = photoHolder.ivPhoto.getLayoutParams().height = mWidth;
                String photoTag = (String) photoHolder.ivPhoto.getTag();
                if (photoTag == null || !mDatas.get(index).getRealPath().equals(photoTag)) {
                    GlideDisplay.display(photoHolder.ivPhoto, new File(mDatas.get(index).getRealPath()));
                    photoHolder.ivPhoto.setTag(mDatas.get(index).getRealPath());
                }
                break;
        }
    }


    @Override
    public int getItemCount() {
        if (mIsShowCamera) {
            return mDatas == null ? 1 : mDatas.size() + 1;
        }
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mIsShowCamera) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    public Integer getRealPosition(int position) {
        if (mIsShowCamera) {
            if (position == 0) {
                return null;
            }
            return position - 1;
        } else {
            return position;
        }
    }


    public MediaBean getItemById(int id) {
        for (MediaBean bean : mDatas) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
    }

    public void setData(List<MediaBean> data) {
        this.mDatas = data;
    }

    public List<MediaBean> getData() {
        return mDatas;
    }

    public void setIsShowCamera(boolean isShowCamera) {
        this.mIsShowCamera = isShowCamera;
    }

    public boolean isShowCamera() {
        return mIsShowCamera;
    }

    public void setMaxNum(int maxNum) {
        this.mMaxNum = maxNum;
    }

    public void setHasCrop(boolean isCrop) {
        this.mIsCrop = isCrop;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        static final int RES_LAYOUT = R.layout.item_photo_picker;
        RelativeLayout rlMain;
        ImageView ivPhoto;
        CheckBox cbSelected;

        public ItemViewHolder(View itemView) {
            super(itemView);
            rlMain = (RelativeLayout) itemView.findViewById(R.id.item_photo_picker_RlMain);
            ivPhoto = (ImageView) itemView.findViewById(R.id.item_photo_picker_IvPhoto);
            cbSelected = (CheckBox) itemView.findViewById(R.id.item_photo_picker_CbSelected);
        }
    }

    static class CameraHolder extends RecyclerView.ViewHolder {
        public CameraHolder(View itemView) {
            super(itemView);
        }
    }


    public interface PhotoSelectListener {
        void photoSelected(int index, int ImageId, boolean isSelect);

        void gotoCamera();
    }

    public void setPhotoSelectListener(PhotoSelectListener mPhotoSelectListener) {
        this.mPhotoSelectListener = mPhotoSelectListener;
    }
}
