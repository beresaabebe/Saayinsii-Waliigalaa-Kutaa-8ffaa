package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.Model;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MainViewHolder> {

    private final List<Model> modelList;
    private final OnItemClickedListener itemClickedListener;

    public Adapter(List<Model> modelList, OnItemClickedListener itemClickedListener) {
        this.modelList = modelList;
        this.itemClickedListener = itemClickedListener;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item_list, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Model model = modelList.get(position);
        holder.title.setText(model.getTitle());
        holder.subTitle.setText(model.getSubtitle());
        holder.itemView.setOnClickListener(v -> itemClickedListener.onItemClicked(model));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public interface OnItemClickedListener {
        void onItemClicked(Model model);
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        TextView title, subTitle;
        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_chapter);
            subTitle = itemView.findViewById(R.id.subTitle);
        }
    }

}
