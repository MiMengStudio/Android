package com.mimeng.adapters;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mimeng.R;
import com.mimeng.values.BannerEntity;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class ImageUrlBanner extends BannerAdapter<BannerEntity, ImageUrlBanner.BannerViewHolder> {
    private final Context context;

    public ImageUrlBanner(List<BannerEntity> mData, Context context) {
        super(mData);
        this.context = context;
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindView(BannerViewHolder holder, BannerEntity data, int position, int size) {
        Glide.with(context).load(data.getImage()).placeholder(R.mipmap.banner_loading).into(holder.img);
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;

        public BannerViewHolder(@NonNull ImageView v) {
            super(v);
            img = v;
        }
    }

}
