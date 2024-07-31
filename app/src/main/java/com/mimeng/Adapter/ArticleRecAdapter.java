package com.mimeng.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.mimeng.EntityClass.ArticleEntity;
import com.mimeng.R;

import java.util.ArrayList;

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
        holder.username.setText(indexData.getAuthor());
        holder.content.setText(indexData.getOutline());
        holder.likeCount.setText(indexData.getLikes() + "");
        holder.shareCount.setText(indexData.getShares() + "");
        holder.upTime.setText(indexData.getPublishDate() + "");
        holder.commentCount.setText(indexData.getCommentCounter() + "");
        Glide.with(context).load(indexData.getHead()).into(holder.heard_icon);

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView heard_icon;
        private final TextView username;
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
            heard_icon = v.findViewById(R.id.ar_heard);
            content = v.findViewById(R.id.ar_content);
            username = v.findViewById(R.id.ar_user_name);
            likeCount = v.findViewById(R.id.ar_like_count);
            shareCount = v.findViewById(R.id.ar_shares_count);
            commentCount = v.findViewById(R.id.ar_comment_count);
        }
    }
}
