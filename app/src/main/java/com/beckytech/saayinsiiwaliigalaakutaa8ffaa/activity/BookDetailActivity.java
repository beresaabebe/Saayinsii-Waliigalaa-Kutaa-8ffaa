package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity;

import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter.PdfRenderAdapter;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.Model;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;
import com.facebook.ads.AudienceNetworkAds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class BookDetailActivity extends AppCompatActivity {
    private PdfRenderer renderer;
    private ParcelFileDescriptor pfd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Initialize Facebook SDK
        AudienceNetworkAds.initialize(this);

        // 1. Fixed Bottom Banner Management
        if (AdManager.getInstance().areAdsEnabled(this)) {
            loadBottomBanner();
            // Load Interstitial for when they eventually leave or finish a section
            AdManager.getInstance().loadInterstitial(this, getString(R.string.fb_interstitial_ads_detail));
        } else {
            findViewById(R.id.banner_container).setVisibility(View.GONE);
        }

        Model model = (Model) getIntent().getSerializableExtra("data");
        if (model == null) return;

        setupToolbar(model);
        initPdfRenderer();

        // 2. Setup High-Performance PDF List
        RecyclerView recyclerView = findViewById(R.id.pdfRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // The adapter handles the in-stream MREC ads internally
        PdfRenderAdapter adapter = new PdfRenderAdapter(this, renderer, model.getPageStart(), model.getPageEnd());
        recyclerView.setAdapter(adapter);
    }

    private void loadBottomBanner() {
        LinearLayout bannerContainer = findViewById(R.id.banner_container);
        // Use the AdManager helper to keep the code clean
        AdManager.getInstance().initBanner(this, bannerContainer, getString(R.string.fb_banner_ads_detail));
    }

    private void setupToolbar(Model model) {
        TextView title = findViewById(R.id.title_book_detail);
        TextView subTitle = findViewById(R.id.sub_title_book_detail);
        title.setText(model.getTitle());
        subTitle.setText(model.getSubtitle());
        findViewById(R.id.back_book_detail).setOnClickListener(v -> finish());
    }

    private void initPdfRenderer() {
        try {
            File file = new File(getCacheDir(), "sw8_temp.pdf");
            if (!file.exists()) {
                InputStream in = getAssets().open("sw8.pdf");
                FileOutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
                in.close();
                out.close();
            }
            pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            renderer = new PdfRenderer(pfd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (renderer != null) renderer.close();
            if (pfd != null) pfd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}