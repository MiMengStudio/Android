package com.mimeng.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mimeng.R;

import java.util.ArrayList;

public class FlexRecyclerAdapter extends RecyclerView.Adapter<FlexRecyclerAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<String> data;
    private ItemChangeListener itemChangeListener = null;

    public FlexRecyclerAdapter(Context context){
        this.context = context;
    }

    public void setData(ArrayList<String> data){
        this.data = data;
    }

    public void setOnChangeListener(ItemChangeListener itemChangeListener) {
        this.itemChangeListener = itemChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.history_block_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String content = data.get(position);
        holder.content.setText(content);
        holder.itemView.setOnClickListener(v -> itemChangeListener.onClickItemPosition(content));
    }

    public interface ItemChangeListener {
        void onClickItemPosition(String keyWord);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView content;

        public ViewHolder(@NonNull View v) {
            super(v);
            content = v.findViewById(R.id.hb_content);
        }
    }
}
