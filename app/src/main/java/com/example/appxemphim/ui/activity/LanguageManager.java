package com.example.appxemphim.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LanguageManager {

    public static final String SELECTED_LANGUAGE_KEY = "selected_language";
    public static final String DEFAULT_LANGUAGE = "en"; // English là ngôn ngữ mặc định
    public static void initLanguage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedLanguage = sharedPreferences.getString(SELECTED_LANGUAGE_KEY, "");
        setLanguage(context, selectedLanguage);
    }
    public static String getSelectedLanguage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SELECTED_LANGUAGE_KEY, DEFAULT_LANGUAGE);
    }
    public static void setLanguage(Context context, String selectedLanguage) {
        Locale locale = new Locale(selectedLanguage);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        saveLanguagePreference(context, selectedLanguage);
    }

    public static void saveLanguagePreference(Context context, String selectedLanguage) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SELECTED_LANGUAGE_KEY, selectedLanguage);
        editor.apply();
    }
}