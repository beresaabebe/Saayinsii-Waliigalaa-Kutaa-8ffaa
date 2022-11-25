package com.beckytech.saayinsiiwaliigalaakutaa8ffaa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity.AboutActivity;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity.BooKDetailActivity;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter.Adapter;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter.ImageSliderOneAdapter;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.ContentEndPage;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.ContentStartPage;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.SliderImages;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.SliderUrl;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.SubTitleContents;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.TitleContents;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.Model;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.SliderModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickedListener, ImageSliderOneAdapter.onImageClickListener {
    private InterstitialAd mInterstitialAd;
    private DrawerLayout drawerLayout;
    private List<Model> modelList;
    private final TitleContents titleContents = new TitleContents();
    private final SubTitleContents subTitleContents = new SubTitleContents();
    private final ContentStartPage startPage = new ContentStartPage();
    private final ContentEndPage endPage = new ContentEndPage();

    private final SliderImages images = new SliderImages();
    private final SliderUrl url = new SliderUrl();
    private List<SliderModel> sliderModels;
    private SliderView sliderViewOne;
    private SliderView sliderViewTwo;
    private SliderView sliderViewThree;
    private SliderView sliderViewFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);

        AppRate.app_launched(this);

        MobileAds.initialize(this, initializationStatus -> {
        });

        setAds();

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {MenuOptions(item); return true;});

        RecyclerView recyclerView = findViewById(R.id.recyclerView_main_item);
        getData();
        Adapter adapter = new Adapter(modelList, this);
        recyclerView.setAdapter(adapter);


//      Slider one initialization
        sliderViewOne = findViewById(R.id.imageSlider);
        sliderOneView();

        sliderViewTwo = findViewById(R.id.imageSliderTwo);
        sliderTwoView();

        sliderViewThree = findViewById(R.id.imageSliderThree);
        sliderThreeView();

        sliderViewFour = findViewById(R.id.imageSliderFour);
        sliderViewFour();
    }

    private void sliderViewFour() {
        sliderModels = new ArrayList<>();

        for (int i = 21; i < images.images.length; i++) {
            sliderModels.add(new SliderModel(images.images[i], url.url[i]));
        }
        sliderContainer(sliderViewFour);
    }

    private void sliderThreeView() {
        sliderModels = new ArrayList<>();

        for (int i = 17; i <= 20; i++) {
            sliderModels.add(new SliderModel(images.images[i], url.url[i]));
        }
        sliderContainer(sliderViewThree);
    }

    private void sliderTwoView() {
        sliderModels = new ArrayList<>();
        for (int i = 9; i < 17; i++) {
            sliderModels.add(new SliderModel(images.images[i], url.url[i]));
        }
        sliderContainer(sliderViewTwo);
    }

    private void sliderOneView() {
        sliderModels = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            sliderModels.add(new SliderModel(images.images[i], url.url[i]));
        }
        sliderContainer(sliderViewOne);
    }

    private void sliderContainer(SliderView sliderView) {
        ImageSliderOneAdapter sliderOneAdapter = new ImageSliderOneAdapter(MainActivity.this, sliderModels, this);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP);
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderView.setSliderAdapter(sliderOneAdapter);
        sliderView.setAutoCycle(true);
        sliderView.setSelected(true);
    }

    private void getData() {
        modelList = new ArrayList<>();
        for (int i = 0; i < titleContents.title.length; i++) {
            modelList.add(new Model(titleContents.title[i].substring(0,1).toUpperCase()+""+
                    titleContents.title[i].substring(1).toLowerCase(),
                    subTitleContents.subTitle[i],
                    R.drawable.icon,
                    startPage.pageStart[i],
                    endPage.pageEnd[i]));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void MenuOptions(MenuItem item) {
        if (item.getItemId() == R.id.action_about_us) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        mInterstitialAd = null;
                        setAds();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            } else {
                startActivity(new Intent(this, AboutActivity.class));
            }
        }

        if (item.getItemId() == R.id.action_rate) {
            String pkg = getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + pkg)));
        }

        if (item.getItemId() == R.id.action_more_apps) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/dev?id=6669279757479011928")));
                        mInterstitialAd = null;
                        setAds();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/dev?id=6669279757479011928")));
            }
        }

        if (item.getItemId() == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, "Download this app from Play store \n" + url);
            startActivity(Intent.createChooser(intent, "Choose to send"));
        }

        if (item.getItemId() == R.id.action_update) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            int lastVersion = pref.getInt("lastVersion", 0);
            String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
            if (lastVersion < BuildConfig.VERSION_CODE) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                Toast.makeText(this, "New update is available download it from play store!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No update available!", Toast.LENGTH_SHORT).show();
            }
        }
        if (item.getItemId() == R.id.action_exit) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Exit")
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        System.exit(0);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setBackground(getResources().getDrawable(R.drawable.nav_header_bg, null))
                    .show();
        }
    }

    private void setAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.test_interstitial_ads_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Close")
                    .setMessage("Do you want to close?")
                    .setBackground(AppCompatResources.getDrawable(this,R.drawable.nav_header_bg))
                    .setPositiveButton("Close", (dialog, which) -> {
                        System.exit(0);
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    @Override
    public void onItemClicked(Model model) {
        int rand = (int) (Math.random() * 100);
        if (rand % 2 == 0) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        startActivity(new Intent(MainActivity.this, BooKDetailActivity.class).putExtra("data", model));
                        mInterstitialAd = null;
                        setAds();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            } else {
                startActivity(new Intent(this, BooKDetailActivity.class).putExtra("data", model));
            }
        }
        else {
            startActivity(new Intent(this, BooKDetailActivity.class).putExtra("data", model));
        }
    }

    @Override
    public void imageClick(SliderModel model) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(model.getUrl()));
        int rand = (int) (Math.random() * 100);
        if (rand % 2 == 0) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        startActivity(intent);
                        mInterstitialAd = null;
                        setAds();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }
            else {
                startActivity(intent);
            }
        }
        else {
            startActivity(intent);
        }
    }
}