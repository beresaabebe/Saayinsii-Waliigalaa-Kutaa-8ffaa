package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.MoreAppsModel;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.util.List;

public class MoreAppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_APP = 0;
    private static final int ITEM_AD = 1;
    private static final int AD_INTERVAL = 5; // Ad after every 4 items (at 5th, 10th, etc.)

    private final List<MoreAppsModel> list;
    private final OnAppClicked onAppClicked;
    private final Context context;

    public MoreAppsAdapter(List<MoreAppsModel> list, OnAppClicked onAppClicked, Context context) {
        this.list = list;
        this.onAppClicked = onAppClicked;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        // If ads are DISABLED by the user, never return TYPE_AD
        if (!AdManager.getInstance().areAdsEnabled(context)) {
            return ITEM_APP;
        }

        if (position > 0 && (position + 1) % AD_INTERVAL == 0) {
            return ITEM_AD;
        }
        return ITEM_APP;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_AD) {
            // Create a simple container for the MREC Ad
            View adView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_mrec_container, parent, false);
            return new AdHolder(adView);
        } else {
            View appView = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_app_list, parent, false);
            return new MoreAppHolder(appView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == ITEM_AD) {
            AdHolder adHolder = (AdHolder) holder;
            adHolder.bindAd(context);
        } else {
            // Calculate the actual index in the list (skipping the ads)
            int appIndex = position - (position / AD_INTERVAL);
            if (appIndex < list.size()) {
                MoreAppHolder appHolder = (MoreAppHolder) holder;
                MoreAppsModel model = list.get(appIndex);

                appHolder.appName.setText(model.getAppName());
                appHolder.appImage.setImageResource(model.getAppImage());
                appHolder.bgColor.setBackgroundColor(ContextCompat.getColor(context, model.getColor()));
                appHolder.itemView.setOnClickListener(view -> onAppClicked.clickedApp(model));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (list.isEmpty()) return 0;
        // Total items = Original list size + (number of ads based on interval)
        return list.size() + (list.size() / (AD_INTERVAL - 1));
    }

    public interface OnAppClicked {
        void clickedApp(MoreAppsModel model);
    }

    // --- VIEW HOLDERS ---

    public static class MoreAppHolder extends RecyclerView.ViewHolder {
        private final TextView appName;
        private final ImageView appImage;
        private final RelativeLayout bgColor;

        public MoreAppHolder(@NonNull View itemView) {
            super(itemView);
            appImage = itemView.findViewById(R.id.image_more);
            appName = itemView.findViewById(R.id.app_name);
            bgColor = itemView.findViewById(R.id.relative_bg);
        }
    }

    public static class AdHolder extends RecyclerView.ViewHolder {
        private final LinearLayout adContainer;

        public AdHolder(@NonNull View itemView) {
            super(itemView);
            adContainer = itemView.findViewById(R.id.mrec_container);
        }

        public void bindAd(Context context) {
            if (context instanceof android.app.Activity) {
                AdManager.getInstance().initAdChain((android.app.Activity) context, adContainer);
            }
        }
    }
}