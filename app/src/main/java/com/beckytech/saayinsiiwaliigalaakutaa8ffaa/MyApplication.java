package com.beckytech.saayinsiiwaliigalaakutaa8ffaa;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AppOpenManager;

public class MyApplication extends Application {

    private static AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, initializationStatus -> {});
        appOpenManager = new AppOpenManager(this);
    }
}
