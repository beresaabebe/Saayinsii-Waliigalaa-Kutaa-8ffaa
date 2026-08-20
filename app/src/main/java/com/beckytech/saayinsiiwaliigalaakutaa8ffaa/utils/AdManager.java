package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.vungle.ads.AdConfig;
import com.vungle.ads.BannerAd;
import com.vungle.ads.BannerAdListener;
import com.vungle.ads.BannerAdSize;
import com.vungle.ads.BaseAd;
import com.vungle.ads.InitializationListener;
import com.vungle.ads.InterstitialAd;
import com.vungle.ads.InterstitialAdListener;
import com.vungle.ads.NativeAd;
import com.vungle.ads.NativeAdListener;
import com.vungle.ads.RewardedAd;
import com.vungle.ads.RewardedAdListener;
import com.vungle.ads.VungleAds;
import com.vungle.ads.VungleError;

public class AdManager {
    private static final String TAG = "AdManager";
    private static final long AD_FREE_DURATION = 4 * 60 * 1000;
    private static final long COOLDOWN_DURATION = 30 * 60 * 1000;
    private static AdManager instance;

    // Facebook Ads
    private com.facebook.ads.InterstitialAd fbInterstitial;
    private com.facebook.ads.RewardedVideoAd fbRewarded;

    // Vungle (Liftoff) Ads
    private InterstitialAd vungleInterstitial;
    private RewardedAd vungleRewarded;

    private boolean isFbInterstitialLoading = false;
    private boolean isVungleInterstitialLoading = false;
    private boolean isUnityInterstitialLoading = false;

    private boolean isFbRewardedLoading = false;
    private boolean isVungleRewardedLoading = false;
    private boolean isUnityRewardedLoading = false;

    private RewardedResponse pendingRewardedResponse;

    public interface RewardedResponse {
        void onAdCompleted();
        void onAdFailed();
    }

    public static synchronized AdManager getInstance() {
        if (instance == null) instance = new AdManager();
        return instance;
    }

    public void initSDKs(Context context) {
        // Initialize Facebook
        AudienceNetworkAds.initialize(context);

        // Initialize Vungle
        VungleAds.init(context, context.getString(R.string.vungle_app_id), new InitializationListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Vungle SDK Initialized");
                loadRewardedAd(context);
            }

            @Override
            public void onError(@NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle SDK Init Error: " + vungleError.getErrorMessage());
            }
        });

        // Initialize Unity
        UnityAds.initialize(context, context.getString(R.string.unity_game_id), false, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                Log.d(TAG, "Unity SDK Initialized");
                loadRewardedAd(context);
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                Log.e(TAG, "Unity SDK Init Error: " + message);
            }
        });

        // Start loading FB ads immediately
        loadRewardedAd(context);
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

        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.ads_off_title)
                .setMessage(R.string.watch_ad_to_disable)
                .setPositiveButton(R.string.watch_ad_yes, (d, w) -> {
                    showRewardedAd(activity, new RewardedResponse() {
                        @Override
                        public void onAdCompleted() {
                            prefs.edit().putLong("turn_off_timestamp", System.currentTimeMillis()).apply();
                            new MaterialAlertDialogBuilder(activity)
                                    .setTitle(R.string.ads_off_title)
                                    .setMessage(R.string.ads_off_msg)
                                    .setPositiveButton("OK", (d2, w2) -> activity.recreate())
                                    .show();
                        }

                        @Override
                        public void onAdFailed() {
                            Toast.makeText(activity, R.string.ad_not_ready, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(R.string.watch_ad_no, null)
                .show();
    }

    // --- Interstitial Fallback Logic ---

    public void loadInterstitial(Context context, String fbId) {
        if (!areAdsEnabled(context)) return;
        loadFbInterstitial(context, fbId);
    }

    private void loadFbInterstitial(Context context, String fbId) {
        Log.d(TAG, "Loading FB Interstitial...");
        if (isFbInterstitialLoading) return;
        isFbInterstitialLoading = true;

        fbInterstitial = new com.facebook.ads.InterstitialAd(context, fbId);
        fbInterstitial.loadAd(fbInterstitial.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.AbstractAdListener() {
                    @Override
                    public void onAdLoaded(Ad ad) {
                        Log.d(TAG, "FB Interstitial Loaded");
                        isFbInterstitialLoading = false;
                    }

                    @Override
                    public void onError(Ad ad, AdError error) {
                        Log.e(TAG, "FB Interstitial Error: " + error.getErrorMessage());
                        isFbInterstitialLoading = false;
                        fbInterstitial = null;
                        loadVungleInterstitial(context);
                    }
                }).build());
    }

    private void loadVungleInterstitial(Context context) {
        Log.d(TAG, "Loading Vungle Interstitial...");
        if (isVungleInterstitialLoading) return;
        isVungleInterstitialLoading = true;

        vungleInterstitial = new InterstitialAd(context, context.getString(R.string.vungle_interstitial_id), new AdConfig());
        vungleInterstitial.setAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle Interstitial Loaded");
                isVungleInterstitialLoading = false;
            }

            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle Interstitial Error: " + vungleError.getErrorMessage());
                isVungleInterstitialLoading = false;
                vungleInterstitial = null;
                loadUnityInterstitial(context);
            }

            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle Interstitial Play Error: " + vungleError.getErrorMessage());
                isVungleInterstitialLoading = false;
                vungleInterstitial = null;
                loadUnityInterstitial(context);
            }

            @Override
            public void onAdClicked(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdImpression(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdStart(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdEnd(@NonNull BaseAd baseAd) {}
        });
        vungleInterstitial.load();
    }

    private void loadUnityInterstitial(Context context) {
        Log.d(TAG, "Loading Unity Interstitial...");
        if (isUnityInterstitialLoading) return;
        isUnityInterstitialLoading = true;

        UnityAds.load(context.getString(R.string.unity_interstitial_ads_unit_id), new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.d(TAG, "Unity Interstitial Loaded");
                isUnityInterstitialLoading = false;
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Unity Interstitial Error: " + message);
                isUnityInterstitialLoading = false;
            }
        });
    }

    public void showInterstitial(Activity activity) {
        if (fbInterstitial != null && fbInterstitial.isAdLoaded()) {
            fbInterstitial.show();
        } else if (vungleInterstitial != null && vungleInterstitial.canPlayAd()) {
            vungleInterstitial.play(activity);
        } else {
            UnityAds.show(activity, activity.getString(R.string.unity_interstitial_ads_unit_id), new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {}
                @Override
                public void onUnityAdsShowStart(String placementId) {}
                @Override
                public void onUnityAdsShowClick(String placementId) {}
                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {}
            });
        }
    }

    // --- Rewarded Fallback Logic ---

    public void loadRewardedAd(Context context) {
        if (!areAdsEnabled(context)) return;
        loadFbRewarded(context);
    }

    private void loadFbRewarded(Context context) {
        Log.d(TAG, "Loading FB Rewarded...");
        if (isFbRewardedLoading) return;
        isFbRewardedLoading = true;

        fbRewarded = new com.facebook.ads.RewardedVideoAd(context, context.getString(R.string.fb_rewarded_interstitial_ads_id));
        fbRewarded.loadAd(fbRewarded.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoCompleted() {
                        if (pendingRewardedResponse != null) {
                            pendingRewardedResponse.onAdCompleted();
                            pendingRewardedResponse = null;
                        }
                    }
                    @Override
                    public void onRewardedVideoClosed() {
                        if (pendingRewardedResponse != null) {
                            pendingRewardedResponse.onAdFailed();
                            pendingRewardedResponse = null;
                        }
                    }
                    @Override
                    public void onAdLoaded(Ad ad) {
                        Log.d(TAG, "FB Rewarded Loaded");
                        isFbRewardedLoading = false;
                    }
                    @Override
                    public void onAdClicked(Ad ad) {}
                    @Override
                    public void onLoggingImpression(Ad ad) {}
                    @Override
                    public void onError(Ad ad, AdError error) {
                        Log.e(TAG, "FB Rewarded Error: " + error.getErrorMessage());
                        isFbRewardedLoading = false;
                        fbRewarded = null;
                        loadVungleRewarded(context);
                    }
                }).build());
    }

    private void loadVungleRewarded(Context context) {
        Log.d(TAG, "Loading Vungle Rewarded...");
        if (isVungleRewardedLoading) return;
        isVungleRewardedLoading = true;

        vungleRewarded = new RewardedAd(context, context.getString(R.string.vungle_rewarded_id), new AdConfig());
        vungleRewarded.setAdListener(new RewardedAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle Rewarded Loaded");
                isVungleRewardedLoading = false;
            }
            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle Rewarded Error: " + vungleError.getErrorMessage());
                isVungleRewardedLoading = false;
                vungleRewarded = null;
                loadUnityRewarded(context);
            }
            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle Rewarded Play Error: " + vungleError.getErrorMessage());
                isVungleRewardedLoading = false;
                vungleRewarded = null;
                loadUnityRewarded(context);
            }
            @Override
            public void onAdRewarded(@NonNull BaseAd baseAd) {
                if (pendingRewardedResponse != null) {
                    pendingRewardedResponse.onAdCompleted();
                    pendingRewardedResponse = null;
                }
            }
            @Override
            public void onAdClicked(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdImpression(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdStart(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdEnd(@NonNull BaseAd baseAd) {
                if (pendingRewardedResponse != null) {
                    pendingRewardedResponse.onAdFailed();
                    pendingRewardedResponse = null;
                }
            }
        });
        vungleRewarded.load();
    }

    private void loadUnityRewarded(Context context) {
        Log.d(TAG, "Loading Unity Rewarded...");
        if (isUnityRewardedLoading) return;
        isUnityRewardedLoading = true;

        UnityAds.load(context.getString(R.string.unity_rewarded_ads_unit_id), new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.d(TAG, "Unity Rewarded Loaded");
                isUnityRewardedLoading = false;
            }
            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Unity Rewarded Error: " + message);
                isUnityRewardedLoading = false;
            }
        });
    }

    public void showRewardedAd(Activity activity) {
        showRewardedAd(activity, null);
    }

    public void showRewardedAd(Activity activity, RewardedResponse response) {
        this.pendingRewardedResponse = response;
        if (fbRewarded != null && fbRewarded.isAdLoaded()) {
            fbRewarded.show();
        } else if (vungleRewarded != null && vungleRewarded.canPlayAd()) {
            vungleRewarded.play(activity);
        } else {
            UnityAds.show(activity, activity.getString(R.string.unity_rewarded_ads_unit_id), new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                    if (pendingRewardedResponse != null) {
                        pendingRewardedResponse.onAdFailed();
                        pendingRewardedResponse = null;
                    }
                }
                @Override
                public void onUnityAdsShowStart(String placementId) {}
                @Override
                public void onUnityAdsShowClick(String placementId) {}
                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                    if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                        if (pendingRewardedResponse != null) {
                            pendingRewardedResponse.onAdCompleted();
                            pendingRewardedResponse = null;
                        }
                    } else {
                        if (pendingRewardedResponse != null) {
                            pendingRewardedResponse.onAdFailed();
                            pendingRewardedResponse = null;
                        }
                    }
                }
            });
        }
    }

    // --- Banner/Native Fallback Chain ---

    public void initAdChain(Activity activity, ViewGroup container) {
        container.removeAllViews();
        if (!areAdsEnabled(activity)) {
            container.setVisibility(View.GONE);
            return;
        }
        container.setVisibility(View.VISIBLE);
        tryFbNative(activity, container);
    }

    private void tryFbNative(Activity activity, ViewGroup container) {
        Log.d(TAG, "Trying FB Native...");
        com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(activity, activity.getString(R.string.fb_native_ads_id));
        nativeAd.loadAd(nativeAd.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(Ad ad) {}
                    @Override
                    public void onError(Ad ad, AdError error) {
                        Log.e(TAG, "FB Native Error: " + error.getErrorMessage());
                        tryFbMrec(activity, container);
                    }
                    @Override
                    public void onAdLoaded(Ad ad) {
                        Log.d(TAG, "FB Native Loaded (Skipping to MREC for display)");
                        tryFbMrec(activity, container);
                    }
                    @Override
                    public void onAdClicked(Ad ad) {}
                    @Override
                    public void onLoggingImpression(Ad ad) {}
                }).build());
    }

    private void tryFbMrec(Activity activity, ViewGroup container) {
        Log.d(TAG, "Trying FB MREC...");
        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, activity.getString(R.string.facebook_rectangle_upper_more_apps), com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250);
        adView.loadAd(adView.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.AbstractAdListener() {
                    @Override
                    public void onError(Ad ad, AdError error) {
                        Log.e(TAG, "FB MREC Error: " + error.getErrorMessage());
                        tryVungleNative(activity, container);
                    }
                    @Override
                    public void onAdLoaded(Ad ad) {
                        Log.d(TAG, "FB MREC Loaded");
                        container.addView(adView);
                    }
                }).build());
    }

    private void tryVungleNative(Activity activity, ViewGroup container) {
        Log.d(TAG, "Trying Vungle Native...");
        NativeAd vungleNative = new NativeAd(activity, activity.getString(R.string.vungle_native_id));
        vungleNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle Native Loaded (Skipping to MREC for display)");
                tryVungleMrec(activity, container);
            }
            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle Native Error: " + vungleError.getErrorMessage());
                tryVungleMrec(activity, container);
            }
            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                tryVungleMrec(activity, container);
            }
            @Override
            public void onAdStart(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdEnd(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdClicked(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdImpression(@NonNull BaseAd baseAd) {}
        });
        vungleNative.load();
    }

    private void tryVungleMrec(Activity activity, ViewGroup container) {
        Log.d(TAG, "Trying Vungle MREC...");
        BannerAd vungleMrec = new BannerAd(activity, activity.getString(R.string.vungle_mrec_id), BannerAdSize.VUNGLE_MREC);
        vungleMrec.setAdListener(new BannerAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle MREC Loaded");
                container.addView(vungleMrec.getBannerView());
            }
            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle MREC Error: " + vungleError.getErrorMessage());
                tryFbBanner(activity, container);
            }
            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                tryFbBanner(activity, container);
            }
            @Override
            public void onAdStart(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdEnd(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdClicked(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdImpression(@NonNull BaseAd baseAd) {}
        });
        vungleMrec.load();
    }

    private void tryFbBanner(Activity activity, ViewGroup container) {
        Log.d(TAG, "Trying FB Banner...");
        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, activity.getString(R.string.fb_banner_ads_main), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        adView.loadAd(adView.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.AbstractAdListener() {
                    @Override
                    public void onError(Ad ad, AdError error) {
                        Log.e(TAG, "FB Banner Error: " + error.getErrorMessage());
                        tryVungleBanner(activity, container);
                    }
                    @Override
                    public void onAdLoaded(Ad ad) {
                        Log.d(TAG, "FB Banner Loaded");
                        container.addView(adView);
                    }
                }).build());
    }

    private void tryVungleBanner(Activity activity, ViewGroup container) {
        Log.d(TAG, "Trying Vungle Banner...");
        BannerAd vungleBanner = new BannerAd(activity, activity.getString(R.string.vungle_banner_id), BannerAdSize.BANNER);
        vungleBanner.setAdListener(new BannerAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle Banner Loaded");
                container.addView(vungleBanner.getBannerView());
            }
            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle Banner Error: " + vungleError.getErrorMessage());
                tryUnityBanner(activity, container);
            }
            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                tryUnityBanner(activity, container);
            }
            @Override
            public void onAdStart(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdEnd(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdClicked(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdImpression(@NonNull BaseAd baseAd) {}
        });
        vungleBanner.load();
    }

    private void tryUnityBanner(Activity activity, ViewGroup container) {
        Log.d(TAG, "Trying Unity Banner...");
        com.unity3d.services.banners.BannerView bannerView = new com.unity3d.services.banners.BannerView(activity, activity.getString(R.string.unity_banner_ads_unit_id), new com.unity3d.services.banners.UnityBannerSize(320, 50));
        bannerView.setListener(new com.unity3d.services.banners.BannerView.IListener() {
            @Override
            public void onBannerLoaded(com.unity3d.services.banners.BannerView bannerView) {
                Log.d(TAG, "Unity Banner Loaded");
                container.addView(bannerView);
            }
            @Override
            public void onBannerClick(com.unity3d.services.banners.BannerView bannerView) {}
            @Override
            public void onBannerFailedToLoad(com.unity3d.services.banners.BannerView bannerView, com.unity3d.services.banners.BannerErrorInfo errorInfo) {
                Log.e(TAG, "Unity Banner Error: " + errorInfo.errorMessage);
            }
            @Override
            public void onBannerLeftApplication(com.unity3d.services.banners.BannerView bannerView) {}
            @Override
            public void onBannerShown(com.unity3d.services.banners.BannerView bannerView) {}
        });
        bannerView.load();
    }
}
