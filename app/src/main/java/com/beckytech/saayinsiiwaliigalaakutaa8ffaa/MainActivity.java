package com.beckytech.saayinsiiwaliigalaakutaa8ffaa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickedListener {

    private List<Model> modelList;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);

        // Initialize Facebook SDK
        AudienceNetworkAds.initialize(this);

        // Setup UI
        ToolbarAndNavigationBar();
        setupRecyclerView();

        // Logic
        AppRate.app_launched(this);
        checkForUpdate();

        // Handle Ads via centralized AdManager
        setupAds();
    }

    private void checkForUpdate() {
        com.google.android.play.core.appupdate.AppUpdateManager appUpdateManager = com.google.android.play.core.appupdate.AppUpdateManagerFactory.create(this);
        com.google.android.gms.tasks.Task<com.google.android.play.core.appupdate.AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE, this, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupAds() {
        if (AdManager.getInstance().areAdsEnabled(this)) {
            // 1. Load Banner
            LinearLayout adContainer = findViewById(R.id.banner_container);
            AdManager.getInstance().initBanner(this, adContainer, getString(R.string.google_banner_ads_unit_id));

            // 2. Load Rectangle
            LinearLayout rectContainer = findViewById(R.id.banner_container_rectangle);
            AdManager.getInstance().initRectangle(this, rectContainer, getString(R.string.google_native_ads_unit_id));

            // 3. Load Interstitial
            AdManager.getInstance().loadInterstitial(this, getString(R.string.google_interstitial_ads_unit_id));
        } else {
            // Hide containers if user recently turned off ads
            findViewById(R.id.banner_container).setVisibility(View.GONE);
            findViewById(R.id.banner_container_rectangle).setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        getData();
        Adapter adapter = new Adapter(this, modelList, this);
        recyclerView.setAdapter(adapter);
    }

    private void ToolbarAndNavigationBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        toolbar.setTitleTextColor(Color.WHITE);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            MenuOptions(item);
            return true;
        });

        View header = navigationView.getHeaderView(0);
        header.findViewById(R.id.back_image_btn).setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        header.findViewById(R.id.share_img_btn).setOnClickListener(v -> shareBtn());
    }

    private void shareBtn() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String text = "Baradhu! Saayinsii Walii galaa Kutaa 8ffaa: https://play.google.com/store/apps/details?id=" + getPackageName();
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void getData() {
        modelList = new ArrayList<>();
        for (int i = 0; i < TitleContents.title.length; i++) {
            modelList.add(new Model(TitleContents.title[i], SubTitleContents.subTitle[i], ContentStartPage.pageStart[i], ContentEndPage.pageEnd[i]));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void MenuOptions(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();

        if (id == R.id.privacy_action) {
            startActivity(new Intent(this, PrivacyActivity.class));
        } else if (id == R.id.action_about_us) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.action_rate) {
            AppRate.showRateDialog(this, null);
        } else if (id == R.id.action_more_apps) {
            startActivity(new Intent(this, MoreAppsActivity.class));
        } else if (id == R.id.action_share) {
            shareBtn();
        } else if (id == R.id.action_exit) {
            showExitDialog();
        } else if (id == R.id.action_turn_off_ads) {
            // This triggers the 4-minute timer and refreshes UI
            AdManager.getInstance().turnOffAds(this);
            setupAds(); // Call this to hide current ads immediately
        }
    }

    private void showExitDialog() {
        new MaterialAlertDialogBuilder(this, R.style.MyAlertDialog)
                .setTitle("Ba'uu")
                .setMessage("Dhuguma cufuu barbaaduu?")
                .setPositiveButton("Eeyyee", (dialog, which) -> finishAffinity())
                .setNegativeButton("Lakki", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onItemClicked(Model model) {
        // Find index of clicked model
        int index = modelList.indexOf(model);
        
        // Show interstitial before navigating to details
        AdManager.getInstance().showInterstitial(this);

        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh ad visibility when returning to the app
        setupAds();
    }
}