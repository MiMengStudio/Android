package com.mimeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.imageview.ShapeableImageView;
import com.mimeng.values.ArticleEntity;
import com.mimeng.R;
import com.mimeng.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ArticleRecAdapter extends RecyclerView.Adapter<ArticleRecAdapter.mViewHolder> {
    private final Context context;
    private ArrayList<ArticleEntity> data;
    private setItemChangeInterface mSetItemChangeInterface;

    public ArticleRecAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<ArticleEntity> data) {
        this.data = data;
    }

    public interface setItemChangeInterface{
        void articleId(int arId);
    }

    public void setItemChangeListener(setItemChangeInterface mSetItemChangeInterface){
        this.mSetItemChangeInterface = mSetItemChangeInterface;
    }

    @NonNull
    @Override
    public ArticleRecAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(context).inflate(R.layout.module_article, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ArticleRecAdapter.mViewHolder holder, int position) {
        ArticleEntity indexData = data.get(position);
        holder.title.setText(indexData.getTitle());
        holder.username.setText(indexData.getName());
        if (indexData.isOfficial()) {
            holder.badgeOfficial.setVisibility(View.VISIBLE);
        } else {
            holder.badgeOfficial.setVisibility(View.GONE);
        }
        if (indexData.isSelected()) {
            holder.badgeSelected.setVisibility(View.VISIBLE);
        } else {
            holder.badgeSelected.setVisibility(View.GONE);
        }
        holder.badgeTop.setVisibility(View.GONE); //暂时不显示置顶
        holder.content.setText(indexData.getOutline());
        holder.likeCount.setText(indexData.getLikes() + "");
        holder.shareCount.setText(indexData.getShares() + "");
        holder.upTime.setText(DateUtils.getTimeAgo(indexData.getPublishDate()));
        holder.commentCount.setText(indexData.getCommentCounter() + "");
        Glide.with(context).load(indexData.getHead()).into(holder.head_icon);

        // 获取标签列表
        List<String> tags = indexData.getTags();
        // 获取图片集合
        List<String> images = indexData.getImages();

        // 移除现有的标签视图（如果存在）
        holder.tags.removeAllViews();
        holder.ar_images.removeAllViews();

        // 为每个标签创建一个新的MaterialCardView和TextView
        for (String tag : tags) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View tagView = inflater.inflate(R.layout.module_tag, null); // 假设您的标签布局文件名为tag_layout.xml

            TextView tagTextView = tagView.findViewById(R.id.tag_text); // 假设您的TextView ID是tag_text
            tagTextView.setText("#" + tag);

            // 添加到ar_tags布局中
            holder.tags.addView(tagView);
        }

        if (!images.isEmpty()) {
            int maxSum = 0;
            for (String url : images) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View imageView = inflater.inflate(R.layout.ar_rec_img_item, holder.ar_images, false);
                ShapeableImageView shapeableImageView = imageView.findViewById(R.id.it_image);
                Glide.with(context).load(url).into(shapeableImageView);
                holder.ar_images.addView(imageView);
                if (maxSum >= 2) {
                    int count = images.size() - 3;
                    if (count > 0) {
                        TextView textView = imageView.findViewById(R.id.it_text);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("+" + (images.size() - 3));
                    }
                    break;
                }
                maxSum++;
            }

        }

        holder.itemView.setOnClickListener(v -> mSetItemChangeInterface.articleId(indexData.getId()));


    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView upTime;
        private final TextView content;
        private final TextView username;
        private final CardView badgeTop;
        private final TextView likeCount;
        private final FlexboxLayout tags;
        private final TextView shareCount;
        private final TextView commentCount;
        private final LinearLayout ar_images;
        private final CardView badgeSelected;
        private final CardView badgeOfficial;
        private final ShapeableImageView head_icon;

        public mViewHolder(@NonNull View v) {
            super(v);
            tags = v.findViewById(R.id.ar_tags);
            title = v.findViewById(R.id.ar_title);
            upTime = v.findViewById(R.id.ar_up_time);
            head_icon = v.findViewById(R.id.ar_heard);
            content = v.findViewById(R.id.ar_content);
            ar_images = v.findViewById(R.id.ar_images);
            badgeTop = v.findViewById(R.id.ar_badge_top);
            username = v.findViewById(R.id.ar_user_name);
            likeCount = v.findViewById(R.id.ar_like_count);
            shareCount = v.findViewById(R.id.ar_shares_count);
            commentCount = v.findViewById(R.id.ar_comment_count);
            badgeSelected = v.findViewById(R.id.ar_badge_selected);
            badgeOfficial = v.findViewById(R.id.ar_badge_official);
        }
    }
}
