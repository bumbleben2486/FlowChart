package core.base.photopicker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import core.base.R;
import core.base.photopicker.beans.MediaBean;
import core.base.photopicker.utils.MediaManager;
import core.base.utils.image.GlideDisplay;

/**
 * Created by Benjen April on 2018/9/18.
 */

public class SelectedPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MediaBean> mData;
    private OnDelListener mOnDelListener;

    public SelectedPhotoAdapter(List<MediaBean> data) {
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(ItemViewHolder.RES_LAYOUT, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).bingData(mData.get(position));
        ((ItemViewHolder) holder).setListener(this, mData, position, mOnDelListener);
    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    public OnDelListener getOnDelListener() {
        return mOnDelListener;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        static final int RES_LAYOUT = R.layout.item_selected_photo;
        ImageView ivImg;
        ImageView ivDel;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ivImg = (ImageView) itemView.findViewById(R.id.item_selected_photo_IvImg);
            ivDel = (ImageView) itemView.findViewById(R.id.item_selected_photo_IvDel);
        }

        private void bingData(MediaBean bean) {
            if (bean == null) return;
            GlideDisplay.display(ivImg, new File(bean.getRealPath()));
        }

        private void setListener(final SelectedPhotoAdapter adapter, final List<MediaBean> data,
                                 final int position, final OnDelListener listener) {
            ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaBean bean = data.get(position);
                    MediaBean sBean = MediaManager.findBeadById(bean.getId());
                    if (sBean != null) {
                        sBean.setIsSelected(false);
                        MediaManager.getSelectMediaBeans().remove(sBean);
                    }

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    if (listener != null) {
                        listener.onDel(position, bean);
                    }
                }
            });
        }
    }


    public void setOnDelListener(OnDelListener onDelListener) {
        this.mOnDelListener = onDelListener;
    }

    public interface OnDelListener {
        void onDel(int position, MediaBean bean);
    }
}
