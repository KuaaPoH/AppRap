package com.example.rapapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView btnClose = findViewById(R.id.btnClose);
        AppCompatButton btnGoToRegister = findViewById(R.id.btnGoToRegister);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }
        
        if (btnGoToRegister != null) {
            btnGoToRegister.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            });
        }
    }
}
