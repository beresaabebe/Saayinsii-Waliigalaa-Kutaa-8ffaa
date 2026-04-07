package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.Model;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CHAPTER = 0;
    private static final int TYPE_AD = 1;
    private static final int AD_INTERVAL = 5; // Ad at positions 5, 10, 15...

    private final List<Model> modelList;
    private final OnItemClickedListener itemClickedListener;
    private final Context context;

    public Adapter(Context context, List<Model> modelList, OnItemClickedListener itemClickedListener) {
        this.context = context;
        this.modelList = modelList;
        this.itemClickedListener = itemClickedListener;
    }

    @Override
    public int getItemViewType(int position) {
        // If ads are DISABLED by the user, never return TYPE_AD
        if (!AdManager.getInstance().areAdsEnabled(context)) {
            return TYPE_CHAPTER;
        }

        if (position > 0 && (position + 1) % AD_INTERVAL == 0) {
            return TYPE_AD;
        }
        return TYPE_CHAPTER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_AD) {
            // Inflate the same MREC container we used for MoreApps
            View adView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ad_mrec_container, parent, false);
            return new AdViewHolder(adView);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_item_list, parent, false);
            return new MainViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_AD) {
            AdViewHolder adHolder = (AdViewHolder) holder;
            adHolder.bindAd();
        } else {
            // Logic to get the correct chapter index from the list
            int chapterIndex = position - (position / AD_INTERVAL);

            if (chapterIndex < modelList.size()) {
                MainViewHolder chapterHolder = (MainViewHolder) holder;
                Model model = modelList.get(chapterIndex);

                chapterHolder.title.setText(model.getTitle());
                chapterHolder.subTitle.setText(model.getSubtitle());
                chapterHolder.itemView.setOnClickListener(v ->
                        itemClickedListener.onItemClicked(model));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (modelList.isEmpty()) return 0;
        // Calculation: Original chapters + total possible ads
        return modelList.size() + (modelList.size() / (AD_INTERVAL - 1));
    }

    public interface OnItemClickedListener {
        void onItemClicked(Model model);
    }

    // --- VIEW HOLDERS ---

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        TextView title, subTitle;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_chapter);
            subTitle = itemView.findViewById(R.id.subTitle);
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout adContainer;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            adContainer = itemView.findViewById(R.id.mrec_ad_layout);
        }

        public void bindAd() {
            adContainer.removeAllViews();
            // Use a specific placement ID for Main Chapter list if you want to track it separately
            AdView adView = new AdView(itemView.getContext(),
                    itemView.getContext().getString(R.string.facebook_rectangle_upper_more_apps),
                    AdSize.RECTANGLE_HEIGHT_250);
            adContainer.addView(adView);
            adView.loadAd();
        }
    }
}