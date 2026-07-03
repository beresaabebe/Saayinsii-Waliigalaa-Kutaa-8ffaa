package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.BuildConfig;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter.AboutAdapter;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.AboutImages;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.AboutName;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.AboutUrlContents;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.AboutModel;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AboutActivity extends AppCompatActivity implements AboutAdapter.OnLinkClicked {

    private final AboutImages images = new AboutImages();
    private final AboutName name = new AboutName();
    private final AboutUrlContents urlContents = new AboutUrlContents();
    private List<AboutModel> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // 1. Initialize Ads using your Centralized AdManager
        setupAds();

        // UI Setup
        findViewById(R.id.ib_back).setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.tv_title);
        title.setText("About us");

        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/about.html");

        TextView version = findViewById(R.id.version_tv);
        version.setText(String.format(Locale.ENGLISH, " %s", BuildConfig.VERSION_NAME));

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(view -> {
            Toast.makeText(this, "Share me, let others know!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareText = getString(R.string.app_name) + "\nDownload here: https://play.google.com/store/apps/details?id=" + getPackageName();
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(intent, "Share via"));

            // Show Interstitial when user interacts
            AdManager.getInstance().showInterstitial(AboutActivity.this);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_about);
        getData();
        AboutAdapter adapter = new AboutAdapter(modelList, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupAds() {
        // Check if ads should be visible
        if (AdManager.getInstance().areAdsEnabled(this)) {
            // Load Bottom Banner
            LinearLayout bannerContainer = findViewById(R.id.banner_container);
            AdManager.getInstance().initBanner(this, bannerContainer, getString(R.string.fb_banner_ads_main));

            // Load Rectangle Ad
            LinearLayout rectContainer = findViewById(R.id.banner_container_rectangle);
            AdManager.getInstance().initRectangle(this, rectContainer, getString(R.string.facebook_rectangle_upper_more_apps));

            // Load Interstitial for later use
            AdManager.getInstance().loadInterstitial(this, getString(R.string.fb_interstitial_ads_main));
        } else {
            // Hide containers if ads are disabled (4-minute timer active)
            findViewById(R.id.banner_container).setVisibility(android.view.View.GONE);
            findViewById(R.id.banner_container_rectangle).setVisibility(android.view.View.GONE);
        }
    }

    private void getData() {
        modelList = new ArrayList<>();
        for (int i = 0; i < name.name.length; i++) {
            modelList.add(new AboutModel(images.images[i], name.name[i], urlContents.url[i]));
        }
    }

    @Override
    public void linkClicked(AboutModel model) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(model.getUrl()));
        startActivity(intent);

        // Show Interstitial when clicking external links
        AdManager.getInstance().showInterstitial(this);
    }

    // Note: We don't need onDestroy() ad cleanup here anymore
    // if your AdManager handles the instance correctly.
    // But if AdManager uses local AdView variables, keep them.
}