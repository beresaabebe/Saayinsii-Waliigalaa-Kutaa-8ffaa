package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Random;

public class AdManager {
    private static final long AD_FREE_DURATION = 4 * 60 * 1000; // 4 Minutes
    private static final long COOLDOWN_DURATION = 30 * 60 * 1000; // 30 Minutes
    private static AdManager instance;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private boolean isAdLoading = false;

    public static synchronized AdManager getInstance() {
        if (instance == null) instance = new AdManager();
        return instance;
    }

    public boolean areAdsEnabled(Context context) {
        if (context == null) return true;
        SharedPreferences prefs = context.getSharedPreferences("AdPrefs", Context.MODE_PRIVATE);
        long turnOffTime = prefs.getLong("turn_off_timestamp", 0);
        long currentTime = System.currentTimeMillis();
        return currentTime >= (turnOffTime + AD_FREE_DURATION);
    }

    public void turnOffAds(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("AdPrefs", Context.MODE_PRIVATE);
        long lastTurnOff = prefs.getLong("turn_off_timestamp", 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime < (lastTurnOff + COOLDOWN_DURATION)) {
            long minutesLeft = ((lastTurnOff + COOLDOWN_DURATION) - currentTime) / 60000;
            Toast.makeText(activity, "You can use this again in " + minutesLeft + " minutes.", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.edit().putLong("turn_off_timestamp", currentTime).apply();

        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.ads_off_title)
                .setMessage(R.string.ads_off_msg)
                .setPositiveButton("OK", (d, w) -> activity.recreate())
                .show();
    }

    public void initBanner(Activity activity, ViewGroup container, String adUnitId) {
        container.removeAllViews();
        if (!areAdsEnabled(activity)) {
            container.setVisibility(View.GONE);
            return;
        }
        container.setVisibility(View.VISIBLE);
        AdView adView = new AdView(activity);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(AdSize.BANNER);
        container.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    public void initCollapsibleBanner(Activity activity, ViewGroup container, String adUnitId) {
        container.removeAllViews();
        if (!areAdsEnabled(activity)) {
            container.setVisibility(View.GONE);
            return;
        }
        container.setVisibility(View.VISIBLE);
        AdView adView = new AdView(activity);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(AdSize.BANNER);
        
        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom");
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter.class, extras)
                .build();
        
        container.addView(adView);
        adView.loadAd(adRequest);
    }

    public void initRectangle(Activity activity, ViewGroup container, String adUnitId) {
        container.removeAllViews();
        if (!areAdsEnabled(activity)) {
            container.setVisibility(View.GONE);
            return;
        }
        container.setVisibility(View.VISIBLE);
        AdView adView = new AdView(activity);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        container.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    public void loadInterstitial(Context context, String adUnitId) {
        if (!areAdsEnabled(context) || isAdLoading) return;
        isAdLoading = true;
        InterstitialAd.load(context, adUnitId, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                        isAdLoading = false;
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                interstitialAd = null;
                                loadInterstitial(context, adUnitId);
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitialAd = null;
                        isAdLoading = false;
                    }
                });
    }

    public void showInterstitial(Activity activity) {
        if (interstitialAd != null) {
            interstitialAd.show(activity);
        }
    }

    public void loadRewardedAd(Context context) {
        if (!areAdsEnabled(context)) return;
        RewardedAd.load(context, context.getString(R.string.google_rewarded_ads_unit_id),
                new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedAd = null;
                    }
                });
    }

    public void loadRewardedInterstitialAd(Context context) {
        if (!areAdsEnabled(context)) return;
        RewardedInterstitialAd.load(context, context.getString(R.string.google_rewarded_interstitial_ads_unit_id),
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedInterstitialAd = null;
                    }
                });
    }

    public void showRandomRewardedAd(Activity activity) {
        if (!areAdsEnabled(activity)) return;
        
        Random random = new Random();
        int choice = random.nextInt(2);

        if (choice == 0 && rewardedAd != null) {
            rewardedAd.show(activity, rewardItem -> loadRewardedAd(activity));
        } else if (choice == 1 && rewardedInterstitialAd != null) {
            rewardedInterstitialAd.show(activity, rewardItem -> loadRewardedInterstitialAd(activity));
        } else {
            // Fallback to interstitial
            showInterstitial(activity);
        }
    }
}
