package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class PdfRenderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PAGE = 0;
    private static final int TYPE_AD = 1;
    private static final int AD_EVERY_N_ITEMS = 3; 
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
        if (!AdManager.getInstance().areAdsEnabled(activity)) return TYPE_PAGE;
        if (position > 0 && (position + 1) % (AD_EVERY_N_ITEMS + 1) == 0) {
            return TYPE_AD;
        }
        return TYPE_PAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_AD) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_hybrid_container, parent, false);
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
            int adsBefore = (position + 1) / (AD_EVERY_N_ITEMS + 1);
            int pdfIndex = startPage + position - adsBefore;

            if (pdfIndex <= endPage && pdfIndex < renderer.getPageCount()) {
                renderPdfPage((PageViewHolder) holder, pdfIndex);
            }
        } else {
            AdViewHolder adHolder = (AdViewHolder) holder;
            if (adHolder.container.getChildCount() == 0) {
                adHolder.bindAd(activity);
            }
        }
    }

    private void renderPdfPage(PageViewHolder holder, int index) {
        if (renderer == null) return;
        try (PdfRenderer.Page page = renderer.openPage(index)) {
            int width = page.getWidth() * 2;
            int height = page.getHeight() * 2;

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            PhotoView pv = (PhotoView) holder.itemView;
            pv.setImageBitmap(bitmap);
        } catch (Exception e) {
            android.util.Log.e("PDF_RENDER", "Error rendering page " + index, e);
        }
    }

    @Override
    public int getItemCount() {
        int totalPages = (endPage - startPage) + 1;
        if (!AdManager.getInstance().areAdsEnabled(activity)) return totalPages;
        return totalPages + (totalPages / AD_EVERY_N_ITEMS);
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        FrameLayout container;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.ad_container);
        }

        public void bindAd(Activity activity) {
            AdLoader adLoader = new AdLoader.Builder(activity, activity.getString(R.string.google_native_ads_unit_id))
                    .forNativeAd(nativeAd -> {
                        NativeAdView adView = (NativeAdView) LayoutInflater.from(activity).inflate(R.layout.native_ad_layout, null);
                        populateNativeAdView(nativeAd, adView);
                        container.removeAllViews();
                        container.addView(adView);
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            AdManager.getInstance().initRectangle(activity, container, activity.getString(R.string.google_banner_ads_unit_id));
                        }
                    })
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }

        private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setMediaView(adView.findViewById(R.id.ad_media));

            ((android.widget.TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((android.widget.TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((android.widget.Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((android.widget.ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            adView.setNativeAd(nativeAd);
        }
    }
}
