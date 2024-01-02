package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.ContentEndPage;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.ContentStartPage;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.SubTitleContents;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.TitleContents;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.Model;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {
    private final String TAG = BookDetailActivity.class.getSimpleName();
    private InterstitialAd interstitialAd;
    private AdView adView;
    private PDFView pdfView;
    private TextView subTitle;
    private TextView title;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        findViewById(R.id.back_book_detail).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        Intent intent = getIntent();
        Model model = (Model) intent.getSerializableExtra("data");

        title = findViewById(R.id.title_book_detail);
        subTitle = findViewById(R.id.subTitle_book_detail);
        pdfView = findViewById(R.id.pdfView);

        subTitle.setSelected(true);
        title.setSelected(true);

        shareContent();
        new Handler().postDelayed(this::callAds, 30000);

        assert model != null;
        currentIndex = getIndex(model.getTitle());
        allContents(currentIndex);

        ImageButton prevButton = findViewById(R.id.prevButton);
        prevButton.setVisibility(View.INVISIBLE);
        ImageButton nextButton = findViewById(R.id.nextButton);
        nextButton.setVisibility(View.INVISIBLE);

        prevButton.setOnClickListener(v -> {
            if (currentIndex < TitleContents.title.length && currentIndex > 0) {
                currentIndex = getIndex(TitleContents.title[currentIndex - 1]);
                allContents(currentIndex);
                if (nextButton.getVisibility() == View.INVISIBLE)
                    nextButton.setVisibility(View.VISIBLE);
            } else {
                if (prevButton.getVisibility() == View.VISIBLE)
                    prevButton.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Kun Boqonnaa jalqabaati!", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentIndex < TitleContents.title.length - 1 && currentIndex >= 0) {
                currentIndex = getIndex(TitleContents.title[currentIndex + 1]);
                allContents(currentIndex);
                if (prevButton.getVisibility() == View.INVISIBLE)
                    prevButton.setVisibility(View.VISIBLE);
            } else {
                if (nextButton.getVisibility() == View.VISIBLE)
                    nextButton.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Kun Boqonnaa xumuraati!", Toast.LENGTH_SHORT).show();
            }
        });

        new Handler().postDelayed(() -> {
            prevButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        }, 3000);
    }

    private int getIndex(String title) {
        for (int i = 0; i < TitleContents.title.length; i++) {
            if (TitleContents.title[i].equalsIgnoreCase(title)) return i;
        }
        return -1;
    }

    private void allContents(int currentIndex) {
        title.setText(String.valueOf(TitleContents.title[currentIndex]));
        subTitle.setText(String.valueOf(SubTitleContents.subTitle[currentIndex]));

        int start = ContentStartPage.pageStart[currentIndex];
        int end = ContentEndPage.pageEnd[currentIndex];

        List<Integer> list = new ArrayList<>();

        for (int i = start; i <= end; i++) {
            list.add(i);
        }

        int[] array = new int[list.size()];

        for (int j = 1; j < array.length; j++) {
            array[j] = list.get(j);
        }

        pdfView.fromAsset("sw8.pdf")
                .pages(array)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .spacing(10)
                .enableDoubletap(true)
                .fitEachPage(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    private void shareContent() {
        ImageButton share_img_btn = findViewById(R.id.share_img_btn);
        share_img_btn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND).setType("text/plain");
            String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, "Sharing is caring! \n" + url);
            startActivity(Intent.createChooser(intent, "Share via"));
        });
    }

    private void callAds() {
        AudienceNetworkAds.initialize(this);
        adView = new AdView(this, "840876307206130_840877100539384", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();

        interstitialAd = new InterstitialAd(this, "840876307206130_840877297206031");
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

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
}