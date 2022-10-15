package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.ModelViewPager;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;

import java.util.List;

public class AdapterViewPager extends RecyclerView.Adapter<AdapterViewPager.ViewPagerHolder> {

    private List<ModelViewPager> modelViewPagers;
    private pagerClick pagerClick;

    public AdapterViewPager(List<ModelViewPager> modelViewPagers, AdapterViewPager.pagerClick pagerClick) {
        this.modelViewPagers = modelViewPagers;
        this.pagerClick = pagerClick;
    }

    public interface pagerClick {
        void clickedPager(ModelViewPager modelViewPager);
    }

    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_page_list_item, parent, false);
        return new ViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        ModelViewPager model = modelViewPagers.get(position);
        holder.title.setText(model.getTitle());
        String txt = "Download from Play store";
        holder.link.setText(txt);
        holder.imageView.setImageResource(model.getImage());
        holder.itemView.setOnClickListener(v -> pagerClick.clickedPager(model));
    }

    @Override
    public int getItemCount() {
        return modelViewPagers.size();
    }

    public static class ViewPagerHolder extends RecyclerView.ViewHolder {
        TextView title, link;
        ImageView imageView;

        public ViewPagerHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_viewpager_item_text);
            link = itemView.findViewById(R.id.subtitle_viewpager_item_text);
            imageView = itemView.findViewById(R.id.imageview_viewpager_item);
        }
    }
}
