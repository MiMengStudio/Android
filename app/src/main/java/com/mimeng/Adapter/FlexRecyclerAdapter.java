package com.mimeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mimeng.R;

import java.util.ArrayList;

public class FlexRecyclerAdapter extends RecyclerView.Adapter<FlexRecyclerAdapter.mViewHolder> {
    private final Context context;
    private ArrayList<String> data;
    private setItemChangeListener itemChangeListener = keyWord -> {
    };

    public FlexRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public void setOnChangeListener(setItemChangeListener itemChangeListener) {
        this.itemChangeListener = itemChangeListener;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(context).inflate(R.layout.history_block_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        String content = data.get(position);
        holder.content.setText(content);
        holder.itemView.setOnClickListener(v -> itemChangeListener.onClickItemPosition(content));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public interface setItemChangeListener {
        void onClickItemPosition(String keyWord);
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final TextView content;

        public mViewHolder(@NonNull View v) {
            super(v);
            content = v.findViewById(R.id.hb_content);
        }
    }
}
