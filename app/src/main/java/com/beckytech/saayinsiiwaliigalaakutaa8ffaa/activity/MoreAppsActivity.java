package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter.MoreAppsAdapter;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.MoreAppTitle;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.MoreAppUrl;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.MoreAppsBgColor;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.MoreAppsImage;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.MoreAppsModel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MoreAppsActivity extends AppCompatActivity implements MoreAppsAdapter.OnAppClicked {

    private final MoreAppsImage image = new MoreAppsImage();
    private final MoreAppTitle title = new MoreAppTitle();
    private final MoreAppUrl url = new MoreAppUrl();
    private final MoreAppsBgColor color = new MoreAppsBgColor();
    private List<MoreAppsModel> list;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps);

        toolBar();
        recyclerView();
        facebookAds();
    }

    private void facebookAds() {
        AudienceNetworkAds.initialize(this);
        Random random = new Random();
        int rand = random.nextInt(100) + 1;
        LinearLayout banner_container = findViewById(R.id.banner_container);
        AdView adView;
        if (rand % 2 == 0)
            adView = new AdView(this, getString(R.string.facebook_banner_height_50_more_apps), AdSize.BANNER_HEIGHT_50);
        else
            adView = new AdView(this, getString(R.string.facebook_bottom_rectangle_more_apps), AdSize.RECTANGLE_HEIGHT_250);

        banner_container.addView(adView);
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(MoreAppsActivity.this.getLocalClassName(),"onError");
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MoreAppsActivity.this.getLocalClassName(),"onAdLoaded");
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MoreAppsActivity.this.getLocalClassName(),"onAdClicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(MoreAppsActivity.this.getLocalClassName(),"onLoggingImpression");            }
        }).build());

        LinearLayout banner_container_rectangle = findViewById(R.id.banner_container_rectangle);
        AdView rectangle = new AdView(this, getString(R.string.facebook_rectangle_upper_more_apps), AdSize.RECTANGLE_HEIGHT_250);
        banner_container_rectangle.addView(rectangle);
        rectangle.loadAd();

        interstitialAd = new InterstitialAd(this, getString(R.string.facebook_interstitial_more_apps));
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Log.d(MoreAppsActivity.this.getLocalClassName(), "onInterstitialDisplayed");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.d(MoreAppsActivity.this.getLocalClassName(), "onInterstitialDismissed");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(MoreAppsActivity.this.getLocalClassName(), "onError");

            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MoreAppsActivity.this.getLocalClassName(), "onAdLoaded");
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MoreAppsActivity.this.getLocalClassName(), "onAdClicked");

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(MoreAppsActivity.this.getLocalClassName(), "onLoggingImpression");
            }
        }).build());
    }
    private void showAdWithDelay() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
                return;
            }
            if(interstitialAd.isAdInvalidated()) {
                return;
            }
            interstitialAd.show();
        }, 1000 * 60 * 3);// Show the ad after 15 minutes
    }
    private void recyclerView() {
        RecyclerView moreRecyclerView = findViewById(R.id.more_app_recyclerView);
        getData();
        MoreAppsAdapter moreAppsAdapter = new MoreAppsAdapter(list, this, this);
        moreRecyclerView.setAdapter(moreAppsAdapter);
    }

    private void getData() {
        list = new ArrayList<>();
        for (int i = 0; i < title.title.length; i++) {
            list.add(new MoreAppsModel(title.title[i],
                    url.url[i],
                    image.images[i],
                    color.color[i]));
        }
    }

    private void toolBar() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.more_apps_for_grade_8th);
        tv_title.setTextColor(ContextCompat.getColor(this, R.color.white));
        ImageButton back_btn = findViewById(R.id.ib_back);
        back_btn.setColorFilter(ContextCompat.getColor(this, R.color.white));
        back_btn.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    public void clickedApp(MoreAppsModel model) {
        String pkg = model.getUrl();
        Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
        String url = "http://play.google.com/store/apps/details?id=";
        String dev = "https://play.google.com/store/apps/dev?id=6669279757479011928";
        if (intent == null) {
            if (Objects.equals(pkg,""))
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dev));
            else
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + model.getUrl()));
        }
        showAdWithDelay();
        startActivity(intent);
    }
}