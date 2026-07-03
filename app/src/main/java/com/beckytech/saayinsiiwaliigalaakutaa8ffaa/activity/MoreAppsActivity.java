package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;
import com.facebook.ads.AudienceNetworkAds;

import java.util.ArrayList;
import java.util.List;

public class MoreAppsActivity extends AppCompatActivity implements MoreAppsAdapter.OnAppClicked {

    private final MoreAppsImage image = new MoreAppsImage();
    private final MoreAppTitle title = new MoreAppTitle();
    private final MoreAppUrl url = new MoreAppUrl();
    private final MoreAppsBgColor color = new MoreAppsBgColor();
    private List<MoreAppsModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps);

        AudienceNetworkAds.initialize(this);

        setupToolbar();
        setupRecyclerView();
        setupAds();
    }

    private void setupAds() {
        if (AdManager.getInstance().areAdsEnabled(this)) {
            // Upper Rectangle Ad
            LinearLayout banner_container_rectangle = findViewById(R.id.banner_container_rectangle);
            AdManager.getInstance().initRectangle(this, banner_container_rectangle, getString(R.string.facebook_rectangle_upper_more_apps));

            // Bottom Banner
            LinearLayout banner_container = findViewById(R.id.banner_container);
            AdManager.getInstance().initBanner(this, banner_container, getString(R.string.facebook_banner_height_50_more_apps));

            // Pre-load Interstitial
            AdManager.getInstance().loadInterstitial(this, getString(R.string.facebook_interstitial_more_apps));
        } else {
            // Silence all ad containers
            findViewById(R.id.banner_container).setVisibility(View.GONE);
            findViewById(R.id.banner_container_rectangle).setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        RecyclerView moreRecyclerView = findViewById(R.id.more_app_recyclerView);
        getData();
        // Note: Check if your adapter needs to handle ads internally too!
        MoreAppsAdapter moreAppsAdapter = new MoreAppsAdapter(list, this, this);
        moreRecyclerView.setAdapter(moreAppsAdapter);
    }

    private void getData() {
        list = new ArrayList<>();
        for (int i = 0; i < title.title.length; i++) {
            list.add(new MoreAppsModel(title.title[i], url.url[i], image.images[i], color.color[i]));
        }
    }

    private void setupToolbar() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.more_apps_for_grade_8th);
        tv_title.setTextColor(ContextCompat.getColor(this, R.color.white));

        ImageButton back_btn = findViewById(R.id.ib_back);
        back_btn.setColorFilter(ContextCompat.getColor(this, R.color.white));
        back_btn.setOnClickListener(view -> finish());
    }

    @Override
    public void clickedApp(MoreAppsModel model) {
        String pkg = model.getUrl();
        String devUrl = "https://play.google.com/store/apps/dev?id=6669279757479011928";
        String playStoreUrl = "http://play.google.com/store/apps/details?id=";

        Intent intent;
        if (pkg == null || pkg.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(devUrl));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl + pkg));
        }

        // Show interstitial immediately on click for better conversion
        AdManager.getInstance().showInterstitial(this);
        startActivity(intent);
    }
}