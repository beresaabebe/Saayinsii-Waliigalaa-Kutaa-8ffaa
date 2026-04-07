package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AdManager {
    private static final long AD_FREE_DURATION = 4 * 60 * 1000; // 4 Minutes
    private static final long COOLDOWN_DURATION = 30 * 60 * 1000; // 30 Minutes
    private static AdManager instance;
    private InterstitialAd interstitialAd;

    public static synchronized AdManager getInstance() {
        if (instance == null) instance = new AdManager();
        return instance;
    }

    public boolean areAdsEnabled(Context context) {
        if (context == null) return true;
        SharedPreferences prefs = context.getSharedPreferences("AdPrefs", Context.MODE_PRIVATE);
        long turnOffTime = prefs.getLong("turn_off_timestamp", 0);
        long currentTime = System.currentTimeMillis();

        // If currently in the 4-minute "Safe Zone"
        if (currentTime < (turnOffTime + AD_FREE_DURATION)) {
            return false;
        }
        return true;
    }

    public void turnOffAds(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("AdPrefs", Context.MODE_PRIVATE);
        long lastTurnOff = prefs.getLong("turn_off_timestamp", 0);
        long currentTime = System.currentTimeMillis();

        // Check if 30 minutes have passed since the LAST time they clicked it
        if (currentTime < (lastTurnOff + COOLDOWN_DURATION)) {
            long minutesLeft = ((lastTurnOff + COOLDOWN_DURATION) - currentTime) / 60000;
            Toast.makeText(activity, "You can use this again in " + minutesLeft + " minutes.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logic: Turn off now
        prefs.edit().putLong("turn_off_timestamp", currentTime).apply();

        new MaterialAlertDialogBuilder(activity)
                .setTitle("Ads Disabled")
                .setMessage("Ads are now off for 4 minutes. Enjoy your study session!")
                .setPositiveButton("OK", (d, w) -> activity.recreate())
                .show();
    }

    // --- Standard FAN Loaders ---

    public void initBanner(Activity activity, ViewGroup container, String placementId) {
        container.removeAllViews();
        if (!areAdsEnabled(activity)) {
            container.setVisibility(View.GONE);
            return;
        }
        container.setVisibility(View.VISIBLE);
        AdView adView = new AdView(activity, placementId, AdSize.BANNER_HEIGHT_50);
        container.addView(adView);
        adView.loadAd();
    }

    public void loadInterstitial(Context context, String placementId) {
        if (!areAdsEnabled(context)) return;
        interstitialAd = new InterstitialAd(context, placementId);
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(new AbstractAdListener() {
            @Override
            public void onInterstitialDismissed(Ad ad) {
                interstitialAd.loadAd();
            }
        }).build());
    }

    public void showInterstitial() {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        }
    }

    // Add this inside your AdManager class
    public void initRectangle(Activity activity, ViewGroup container, String placementId) {
        if (!areAdsEnabled(activity)) {
            container.setVisibility(View.GONE);
            return;
        }

        container.setVisibility(View.VISIBLE);
        container.removeAllViews(); // Clean up old ads

        AdView adView = new AdView(activity, placementId, AdSize.RECTANGLE_HEIGHT_250);
        container.addView(adView);
        adView.loadAd();
    }
}