package com.example.appxemphim.ui.activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.appxemphim.R;

public class ThemeManager {

    public static final String SELECTED_THEME_KEY = "selected_theme";

    private static final String PREFS_NAME = "night_mode_prefs";
    private static final String NIGHT_MODE_KEY = "night_mode";

    public static void saveNightMode(Context context, int nightMode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NIGHT_MODE_KEY, nightMode);
        editor.apply();
    }

    public static int loadNightMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(NIGHT_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
    public static void setNightModeConfiguration(Context context, int nightMode) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        configuration.uiMode |= nightMode;
        Context updatedContext = context.createConfigurationContext(configuration);
        context.getResources().updateConfiguration(updatedContext.getResources().getConfiguration(), context.getResources().getDisplayMetrics());
    }
    public static int getNightModeConfiguration(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (prefs.contains(NIGHT_MODE_KEY)) {

            return prefs.getInt(NIGHT_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {

            return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }
}