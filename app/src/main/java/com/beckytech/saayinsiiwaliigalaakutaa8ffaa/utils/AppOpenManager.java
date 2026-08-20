package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.vungle.ads.AdConfig;
import com.vungle.ads.BaseAd;
import com.vungle.ads.InterstitialAd;
import com.vungle.ads.InterstitialAdListener;
import com.vungle.ads.VungleError;

import java.util.Date;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private AppOpenAd googleAppOpenAd = null;
    private InterstitialAd vungleAppOpenFallback = null;
    
    private final Application myApplication;
    private Activity currentActivity;
    private static boolean isShowingAd = false;
    private long loadTime = 0;

    public AppOpenManager(Application myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void fetchAd() {
        if (isAdAvailable()) {
            return;
        }

        AppOpenAd.load(
                myApplication, myApplication.getString(R.string.google_app_open_ads_unit_id), new AdRequest.Builder().build(),
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        AppOpenManager.this.googleAppOpenAd = ad;
                        AppOpenManager.this.loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        fetchVungleFallback();
                    }
                });
    }

    private void fetchVungleFallback() {
        vungleAppOpenFallback = new InterstitialAd(myApplication, myApplication.getString(R.string.vungle_app_open_id), new AdConfig());
        vungleAppOpenFallback.setAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {
                AppOpenManager.this.loadTime = (new Date()).getTime();
            }

            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                vungleAppOpenFallback = null;
            }

            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                vungleAppOpenFallback = null;
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
        vungleAppOpenFallback.load();
    }

    private boolean wasLoadTimeLessThanNHoursAgo() {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * 4));
    }

    public boolean isAdAvailable() {
        return (googleAppOpenAd != null || (vungleAppOpenFallback != null && vungleAppOpenFallback.canPlayAd())) && wasLoadTimeLessThanNHoursAgo();
    }

    public void showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            if (googleAppOpenAd != null) {
                showGoogleAppOpen();
            } else if (vungleAppOpenFallback != null) {
                showVungleFallback();
            }
        } else {
            fetchAd();
        }
    }

    private void showGoogleAppOpen() {
        googleAppOpenAd.setFullScreenContentCallback(new com.google.android.gms.ads.FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                googleAppOpenAd = null;
                isShowingAd = false;
                fetchAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                isShowingAd = false;
                fetchAd();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                isShowingAd = true;
            }
        });
        googleAppOpenAd.show(currentActivity);
    }

    private void showVungleFallback() {
        vungleAppOpenFallback.setAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {}
            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                isShowingAd = false;
                vungleAppOpenFallback = null;
                fetchAd();
            }
            @Override
            public void onAdClicked(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdImpression(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdStart(@NonNull BaseAd baseAd) {
                isShowingAd = true;
            }
            @Override
            public void onAdEnd(@NonNull BaseAd baseAd) {
                vungleAppOpenFallback = null;
                isShowingAd = false;
                fetchAd();
            }
        });
        vungleAppOpenFallback.play(currentActivity);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        showAdIfAvailable();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
    }
}
