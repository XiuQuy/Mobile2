package com.example.appxemphim.ui.activity;

import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appxemphim.R;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private ImageView backButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        backButton = findViewById(R.id.back_button);
        String[] languages = getResources().getStringArray(R.array.simple_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setSelection(0);


        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedLanguage = (String) parent.getItemAtPosition(position);
            if(selectedLanguage.equals("English")) {
                setLocal(SettingActivity.this, "en");
                recreate();

            } else {
                setLocal(SettingActivity.this, "vi");
                recreate();

            }
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

