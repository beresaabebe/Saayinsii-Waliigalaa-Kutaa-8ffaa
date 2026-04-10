package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;
import com.github.chrisbanes.photoview.PhotoView;

public class PdfRenderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PAGE = 0;
    private static final int TYPE_AD = 1;
    private static final int AD_EVERY_N_PAGES = 5; // Shows ad after every 5 pages
    private final PdfRenderer renderer;
    private final int startPage, endPage;
    private final Activity activity;

    public PdfRenderAdapter(Activity activity, PdfRenderer renderer, int startPage, int endPage) {
        this.activity = activity;
        this.renderer = renderer;
        this.startPage = startPage;
        this.endPage = endPage;
    }

    @Override
    public int getItemViewType(int position) {
        // If ads are off (4-min timer), everything is a PAGE
        if (!AdManager.getInstance().areAdsEnabled(activity)) return TYPE_PAGE;

        // Logic: Every 6th item (index 5, 11, 17...) is an Ad
        if (position > 0 && (position + 1) % (AD_EVERY_N_PAGES + 1) == 0) {
            return TYPE_AD;
        }
        return TYPE_PAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_AD) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_mrec_container, parent, false);
            return new AdViewHolder(v);
        }
        PhotoView photoView = new PhotoView(parent.getContext());
        photoView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        photoView.setAdjustViewBounds(true);
        return new PageViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PAGE) {
            int adsBefore = (position + 1) / (AD_EVERY_N_PAGES + 1);
            int pdfIndex = startPage + position - adsBefore;

            if (pdfIndex <= endPage && pdfIndex < renderer.getPageCount()) {
                renderPdfPage((PageViewHolder) holder, pdfIndex);
            }
        } else {
            AdViewHolder adHolder = (AdViewHolder) holder;
            // 🛑 CRITICAL: Only load the ad if the container is empty!
            // This prevents flickering and re-loading during scroll.
            if (adHolder.container.getChildCount() == 0) {
                adHolder.container.setVisibility(View.GONE); // Hide until loaded
                adHolder.bindAd(activity);
            }
        }
    }

    private void renderPdfPage(PageViewHolder holder, int index) {
        // Check if renderer is still valid (crucial for stability)
        if (renderer == null) return;
        try (PdfRenderer.Page page = renderer.openPage(index)) {
            // Use a standard multiplier or calculate based on display metrics
            int width = page.getWidth() * 2;
            int height = page.getHeight() * 2;

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            PhotoView pv = (PhotoView) holder.itemView;
            pv.setImageBitmap(bitmap);
        } catch (Exception e) {
            // Log the error so you can see it in Play Console
            android.util.Log.e("PDF_RENDER", "Error rendering page " + index, e);
        }
    }

    @Override
    public int getItemCount() {
        int totalPages = (endPage - startPage) + 1;
        if (!AdManager.getInstance().areAdsEnabled(activity)) return totalPages;

        // Adds space for the ads
        return totalPages + (totalPages / AD_EVERY_N_PAGES);
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.mrec_container);
        }

        public void bindAd(Activity activity) {
            AdManager.getInstance().initRectangle(activity, container,
                    activity.getString(R.string.facebook_rectangle_upper_more_apps));
        }
    }
}