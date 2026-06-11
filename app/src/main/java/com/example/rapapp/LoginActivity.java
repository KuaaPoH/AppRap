package com.example.rapapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private AppCompatButton btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
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

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etEmail.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void updateLoginButtonState() {
        boolean isEnabled = !etEmail.getText().toString().trim().isEmpty() &&
                !etPassword.getText().toString().trim().isEmpty();

        if (isEnabled) {
            btnLogin.setBackgroundResource(R.drawable.bg_btn_orange);
            btnLogin.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setBackgroundResource(R.drawable.bg_btn_disabled);
            btnLogin.setTextColor(ContextCompat.getColor(this, R.color.divider_gray));
            btnLogin.setEnabled(false);
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        btnLogin.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                    }
                });
    }
}

