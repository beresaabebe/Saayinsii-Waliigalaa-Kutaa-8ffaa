package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity;

import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.ContentEndPage;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.ContentStartPage;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.SubTitleContents;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.contents.TitleContents;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.fragments.ChapterFragment;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.Model;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.utils.AdManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {
    private PdfRenderer renderer;
    private ParcelFileDescriptor pfd;
    private ViewPager2 viewPager;
    private List<Model> chapters;
    private TextView titleTv, subTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail_pager);

        initData();
        setupToolbar();
        initPdfRenderer();

        viewPager = findViewById(R.id.viewPager);
        ChapterPagerAdapter adapter = new ChapterPagerAdapter(this);
        viewPager.setAdapter(adapter);

        int startIndex = getIntent().getIntExtra("index", 0);
        viewPager.setCurrentItem(startIndex, false);
        updateToolbar(startIndex);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateToolbar(position);
                AdManager.getInstance().showRewardedAd(BookDetailActivity.this);
            }
        });

        // Load ads
        AdManager.getInstance().loadInterstitial(this, getString(R.string.fb_interstitial_ads_detail));
        AdManager.getInstance().loadRewardedAd(this);
        
        loadBottomBanner();
    }

    private void loadBottomBanner() {
        android.widget.LinearLayout bannerContainer = findViewById(R.id.banner_container);
        AdManager.getInstance().initAdChain(this, bannerContainer);
    }

    private void initData() {
        chapters = new ArrayList<>();
        for (int i = 0; i < TitleContents.title.length; i++) {
            chapters.add(new Model(TitleContents.title[i], SubTitleContents.subTitle[i], ContentStartPage.pageStart[i], ContentEndPage.pageEnd[i]));
        }
    }

    private void setupToolbar() {
        titleTv = findViewById(R.id.title_book_detail);
        subTitleTv = findViewById(R.id.sub_title_book_detail);
        findViewById(R.id.back_book_detail).setOnClickListener(v -> finish());
    }

    private void updateToolbar(int position) {
        if (position < chapters.size()) {
            Model model = chapters.get(position);
            titleTv.setText(model.getTitle());
            subTitleTv.setText(model.getSubtitle());
        }
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

    public PdfRenderer getRenderer() {
        return renderer;
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

    private class ChapterPagerAdapter extends FragmentStateAdapter {
        public ChapterPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Model model = chapters.get(position);
            return ChapterFragment.newInstance(model.getPageStart(), model.getPageEnd());
        }

        @Override
        public int getItemCount() {
            return chapters.size();
        }
    }
}
