package core.base.photopicker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Locale;

import core.base.R;
import core.base.photopicker.beans.MediaFloder;
import core.base.utils.image.GlideDisplay;
import core.base.views.grid.GridLayoutAdapter;

/**
 * Created by Nowy on 2017/9/18.
 */

public class PhotoClazzAdapter extends GridLayoutAdapter {
    List<MediaFloder> mData;
    public PhotoClazzAdapter(List<MediaFloder> data){
        this.mData = data;
    }

    @Override
    protected int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    protected View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = ItemViewHolder.createView(parent);
            new ItemViewHolder(convertView);
        }

        ItemViewHolder holder = (ItemViewHolder) convertView.getTag();
        holder.bingData(mData.get(position));
        return convertView;
    }

    static class ItemViewHolder{
        static final int RES_LAYOUT = R.layout.item_photo_clazz;
        private final ImageView mIvImg;
        private final TextView mTvTitle;

        static View createView(ViewGroup parent){
            return LayoutInflater.from(parent.getContext())
                    .inflate(RES_LAYOUT,parent,false);
        }

        ItemViewHolder(View itemView){
            mIvImg = (ImageView) itemView.findViewById(R.id.item_photo_clazz_IvImg);
            mTvTitle = (TextView) itemView.findViewById(R.id.item_photo_clazz_TvTitle);
            itemView.setTag(this);
        }

        public void bingData(MediaFloder item){
            if(item == null) return;
            mIvImg.setImageResource(R.drawable.photo_ic_loading);

            mTvTitle.setText(item.getName());
            mTvTitle.append(String.format(Locale.CHINA,"(%1$d)",item.getMediaBeanList().size()));
            if(item.getMediaBeanList().size() > 0){
                GlideDisplay.display(mIvImg,
                        new File(item.getMediaBeanList().get(0).getRealPath()),
                        R.drawable.photo_ic_loading);
            }
        }
    }
}
