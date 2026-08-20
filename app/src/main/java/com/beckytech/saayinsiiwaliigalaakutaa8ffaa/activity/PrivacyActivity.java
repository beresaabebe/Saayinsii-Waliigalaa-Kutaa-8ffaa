package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;
import com.facebook.ads.AudienceNetworkAds;

public class PrivacyActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        initUI();
        setupWebView();
        setupModernBackNavigation();

        // Use AdManager to respect the "Turn off Ads" timer
        setupAds();

        webView.loadUrl("https://yoosaad.com/privacy/");
    }

    private void initUI() {
        progressBar = findViewById(R.id.progress_horizontal);
        TextView tv_title = findViewById(R.id.tv_title);
        webView = findViewById(R.id.webView_privacy);

        tv_title.setText(R.string.privacy_title);
        findViewById(R.id.ib_back).setOnClickListener(v -> finish());
    }

    private void setupAds() {
        if (AdManager.getInstance().areAdsEnabled(this)) {
            // Main Ad Chain
            LinearLayout bannerContainer = findViewById(R.id.banner_container);
            AdManager.getInstance().initAdChain(this, bannerContainer);
        } else {
            // Hide containers if ads are disabled
            findViewById(R.id.banner_container).setVisibility(View.GONE);
        }
    }

    private void setupModernBackNavigation() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    webView.loadUrl("file:///android_asset/error.html");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    // Ad cleanup is handled inside the AdManager or by the Activity lifecycle
    // since we aren't holding local references to AdViews here anymore.
}