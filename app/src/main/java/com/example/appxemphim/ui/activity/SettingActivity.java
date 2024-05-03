package com.example.appxemphim.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.appxemphim.R;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private MaterialSwitch themeSwitch;
    private ImageView backButton;
    private String selectedLanguage; // Thêm biến instance

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        backButton = findViewById(R.id.back_button);

        String selectedLanguageCode = LanguageManager.getSelectedLanguage(this);
        String selectedLanguageName = "";
        switch (selectedLanguageCode) {
            case "vi":
                selectedLanguageName = "Tiếng Việt";
                break;
            case "en":
                selectedLanguageName = "English";
                break;
            // Thêm các trường hợp cho các ngôn ngữ khác nếu cần
            default:
                selectedLanguageName = selectedLanguageCode; // Sử dụng mã ngôn ngữ nếu không khớp với bất kỳ trường hợp nào
                break;
        }
        autoCompleteTextView.setText(selectedLanguageName);

        String[] languages = getResources().getStringArray(R.array.simple_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
        autoCompleteTextView.setAdapter(adapter);


        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String newSelectedLanguage = (String) parent.getItemAtPosition(position);
            if (!newSelectedLanguage.equals(selectedLanguage)) {
                selectedLanguage = newSelectedLanguage;
                if (selectedLanguage.equals("English")) {
                    setLocal(SettingActivity.this, "en");
                } else {
                    setLocal(SettingActivity.this, "vi");
                }
                recreate();
            }
        });
        themeSwitch = findViewById(R.id.themeswitch);
        int currentNightMode = ThemeManager.loadNightMode(this);
        themeSwitch.setChecked(currentNightMode == AppCompatDelegate.MODE_NIGHT_YES);
        int nightMode = ThemeManager.getNightModeConfiguration(this);
        AppCompatDelegate.setDefaultNightMode(nightMode);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int selectedTheme = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            ThemeManager.saveNightMode(this, selectedTheme);
            AppCompatDelegate.setDefaultNightMode(selectedTheme);
            ThemeManager.setNightModeConfiguration(this, selectedTheme);
            recreate();
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("languageChanged", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        LanguageManager.initLanguage(this);
    }

    public void setLocal(Context context, String selectedLanguage) {
        Locale locale = new Locale(selectedLanguage);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        LanguageManager.saveLanguagePreference(context, selectedLanguage);
    }


}

