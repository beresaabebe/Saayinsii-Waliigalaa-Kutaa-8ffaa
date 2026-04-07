package com.beckytech.saayinsiiwaliigalaakutaa8ffaa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AppRate {

    // Use R.string.app_name to keep it dynamic for your 65 apps
    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 3;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Logic check for showing the dialog
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        String appTitle = mContext.getString(R.string.app_name);
        String packageName = mContext.getPackageName();

        new MaterialAlertDialogBuilder(mContext)
                .setTitle("Nuun madaalaa!") // Afaan Oromoo: Rate us!
                .setMessage("Yoo " + appTitle + " jaallatte, maaloo daqiiqaa tokko gadi nuf madaali. Gargaarsa keetiif galatoomi!")
                .setCancelable(false)
                .setPositiveButton("Amma Madaali", (dialog, which) -> {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                    if (editor != null) {
                        editor.putBoolean("dontshowagain", true);
                        editor.apply();
                    }
                    dialog.dismiss();
                })
                .setNeutralButton("Booda natti hami", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton("Lakki, galatoomi", (dialog, which) -> {
                    if (editor != null) {
                        editor.putBoolean("dontshowagain", true);
                        editor.apply();
                    }
                    dialog.dismiss();
                })
                .show();
    }
}