package com.beckytech.saayinsiiwaliigalaakutaa8ffaa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity.AboutActivity;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity.BookDetailActivity;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity.MoreAppsActivity;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity.PrivacyActivity;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter.Adapter;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.ContentEndPage;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.ContentStartPage;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.SubTitleContents;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.TitleContents;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.Model;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickedListener{
    InterstitialAd interstitialAd;
    String TAG = MainActivity.class.getSimpleName();
    private List<Model> modelList;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);

        AudienceNetworkAds.initialize(this);
        callAds();

        AppRate.app_launched(this);

        ToolbarAndNavigationBar();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        getData();
        Adapter adapter = new Adapter(modelList, this);
        recyclerView.setAdapter(adapter);
    }

    private void ToolbarAndNavigationBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();
        drawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(drawerToggle);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            MenuOptions(item);
            return true;
        });

        View view = navigationView.getHeaderView(0);
        ImageButton back_image_btn = view.findViewById(R.id.back_image_btn);
        back_image_btn.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        ImageButton share_img_btn = view.findViewById(R.id.share_img_btn);
        share_img_btn.setOnClickListener(v -> shareBtn());
    }

    private void shareBtn() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, "I've been waiting for you to click on this for a long time. Please share it now!\n"+url);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void getData() {
        modelList = new ArrayList<>();
        for (int i =0; i < TitleContents.title.length; i++) {
            modelList.add(new Model(TitleContents.title[i],
                    SubTitleContents.subTitle[i],
                    ContentStartPage.pageStart[i],
                    ContentEndPage.pageEnd[i]));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void MenuOptions(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (item.getItemId() == R.id.privacy_action) startActivity(new Intent(this, PrivacyActivity.class));
        if (item.getItemId() == R.id.action_about_us) {
            showAdWithDelay();
            startActivity(new Intent(this, AboutActivity.class));
        }

        if (item.getItemId() == R.id.action_rate) {
            String pkg = getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + pkg)));
        }

        if (item.getItemId() == R.id.action_more_apps) {
            showAdWithDelay();
            startActivity(new Intent(this, MoreAppsActivity.class));
        }

        if (item.getItemId() == R.id.action_share) {
            shareBtn();
            showAdWithDelay();
        }

        if (item.getItemId() == R.id.action_update) {
            showAdWithDelay();
            SharedPreferences pref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            int lastVersion = pref.getInt("lastVersion", BuildConfig.VERSION_CODE);
            String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
            if (lastVersion < BuildConfig.VERSION_CODE) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                Toast.makeText(this, "New update is available download it from play store!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No update available!", Toast.LENGTH_SHORT).show();
            }
        }
        if (item.getItemId() == R.id.action_exit) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.MyAlertDialog);
            builder.setTitle("Exit")
                    .setMessage("Do you want to close?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        System.exit(0);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setBackground(getResources().getDrawable(R.drawable.nav_header_bg, null))
                    .show();
        }
    }

    @Override
    public void onItemClicked(Model model) {
        showAdWithDelay();
        startActivity(new Intent(this, BookDetailActivity.class).putExtra("data", model));
    }

    private void callAds() {
        LinearLayout banner_container_rectangle = findViewById(R.id.banner_container_rectangle);
        AdView rectangle = new AdView(this, getString(R.string.facebook_bottom_rectangle_more_apps), AdSize.RECTANGLE_HEIGHT_250);
        banner_container_rectangle.addView(rectangle);
        rectangle.loadAd(rectangle.buildLoadAdConfig().withAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(MainActivity.class.getSimpleName(), "onError"+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MainActivity.class.getSimpleName(), "onAdLoaded "+ad.getPlacementId());
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MainActivity.class.getSimpleName(), "onAdClicked "+ad.getPlacementId());
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(MainActivity.class.getSimpleName(), "onLoggingImpression "+ad.getPlacementId());
            }
        }).build());

//        513372960928869_513374324262066
        AdView adView = new AdView(this, "840876307206130_840877500539344", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(MainActivity.class.getSimpleName(), "onError "+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MainActivity.class.getSimpleName(), "onAdLoaded "+ad.getPlacementId());
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MainActivity.class.getSimpleName(), "onAdClicked "+ad.getPlacementId());
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(MainActivity.class.getSimpleName(), "onLoggingImpression "+ad.getPlacementId());
            }
        }).build());

        interstitialAd = new InterstitialAd(this, "840876307206130_840877617205999");
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    private void showAdWithDelay() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Check if interstitialAd has been loaded successfully
            if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
                return;
            }
            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
            if(interstitialAd.isAdInvalidated()) {
                return;
            }
            // Show the ad
            interstitialAd.show();
        }, 1000 * 60 * 2); // Show the ad after 15 minutes
    }
}