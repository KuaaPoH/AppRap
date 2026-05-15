package com.example.rapapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        RelativeLayout rlLanguage = findViewById(R.id.rlLanguage);
        if (rlLanguage != null) {
            rlLanguage.setOnClickListener(v -> {
                startActivity(new Intent(SettingsActivity.this, LanguageActivity.class));
            });
        }
    }
}
