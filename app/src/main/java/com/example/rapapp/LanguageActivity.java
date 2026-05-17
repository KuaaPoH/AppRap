package com.example.rapapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class LanguageActivity extends AppCompatActivity {

    private ImageView ivCheckEnglish, ivCheckVietnamese;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        RelativeLayout rlEnglish = findViewById(R.id.rlEnglish);
        RelativeLayout rlVietnamese = findViewById(R.id.rlVietnamese);
        ivCheckEnglish = findViewById(R.id.ivCheckEnglish);
        ivCheckVietnamese = findViewById(R.id.ivCheckVietnamese);

        if (rlEnglish != null) {
            rlEnglish.setOnClickListener(v -> {
                ivCheckEnglish.setVisibility(View.VISIBLE);
                ivCheckVietnamese.setVisibility(View.GONE);
            });
        }

        if (rlVietnamese != null) {
            rlVietnamese.setOnClickListener(v -> {
                ivCheckVietnamese.setVisibility(View.VISIBLE);
                ivCheckEnglish.setVisibility(View.GONE);
            });
        }
    }
}
