package com.beckytech.saayinsiiwaliigalaakutaa8ffaa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class AppRate {

    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 3;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24L * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }
        editor.apply();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_rate, null);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        TextView ratingDesc = view.findViewById(R.id.rating_desc);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            switch ((int) rating) {
                case 1: ratingDesc.setText(R.string.rating_bad); break;
                case 2: ratingDesc.setText(R.string.rating_not_good); break;
                case 3: ratingDesc.setText(R.string.rating_somewhat_good); break;
                case 4: ratingDesc.setText(R.string.rating_very_good); break;
                case 5: ratingDesc.setText(R.string.rating_excellent); break;
                default: ratingDesc.setText(""); break;
            }
        });

        new MaterialAlertDialogBuilder(mContext)
                .setView(view)
                .setCancelable(true)
                .setPositiveButton(R.string.rate, (dialog, which) -> {
                    if (ratingBar.getRating() >= 4) {
                        launchNativeReview(mContext);
                    } else {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                    }
                    if (editor != null) {
                        editor.putBoolean("dontshowagain", true).apply();
                    }
                })
                .setNegativeButton(R.string.no_thanks, (dialog, which) -> {
                    if (editor != null) {
                        editor.putBoolean("dontshowagain", true).apply();
                    }
                })
                .show();
    }

    private static void launchNativeReview(Context context) {
        ReviewManager manager = ReviewManagerFactory.create(context);
        manager.requestReviewFlow().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                if (context instanceof Activity) {
                    manager.launchReviewFlow((Activity) context, reviewInfo);
                }
            } else {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
            }
        });
    }
}
