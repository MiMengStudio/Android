package com.mimeng.Adapter;

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
import com.google.android.material.imageview.ShapeableImageView;
import com.mimeng.EntityClass.ArticleEntity;
import com.mimeng.R;
import com.mimeng.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ArticleRecAdapter extends RecyclerView.Adapter<ArticleRecAdapter.mViewHolder> {
    private final Context context;
    private ArrayList<ArticleEntity> data;

    public ArticleRecAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<ArticleEntity> data) {
        this.data = data;
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

        // 移除现有的标签视图（如果存在）
        holder.tags.removeAllViews();

        // 为每个标签创建一个新的MaterialCardView和TextView
        for (String tag : tags) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View tagView = inflater.inflate(R.layout.module_tag, null); // 假设您的标签布局文件名为tag_layout.xml

            TextView tagTextView = tagView.findViewById(R.id.tag_text); // 假设您的TextView ID是tag_text
            tagTextView.setText("#" + tag);

            // 添加到ar_tags布局中
            holder.tags.addView(tagView);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView head_icon;
        private final TextView username;
        private final CardView badgeOfficial;
        private final CardView badgeSelected;
        private final CardView badgeTop;
        private final LinearLayout tags;
        private final TextView upTime;
        private final TextView title;
        private final TextView content;
        private final TextView likeCount;
        private final TextView commentCount;
        private final TextView shareCount;

        public mViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.ar_title);
            upTime = v.findViewById(R.id.ar_up_time);
            tags = v.findViewById(R.id.ar_tags);
            head_icon = v.findViewById(R.id.ar_heard);
            content = v.findViewById(R.id.ar_content);
            username = v.findViewById(R.id.ar_user_name);
            badgeOfficial = v.findViewById(R.id.ar_badge_official);
            badgeSelected = v.findViewById(R.id.ar_badge_selected);
            badgeTop = v.findViewById(R.id.ar_badge_top);
            likeCount = v.findViewById(R.id.ar_like_count);
            shareCount = v.findViewById(R.id.ar_shares_count);
            commentCount = v.findViewById(R.id.ar_comment_count);
        }
    }
}
