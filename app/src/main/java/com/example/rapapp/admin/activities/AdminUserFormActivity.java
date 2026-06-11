package com.example.rapapp.admin.activities;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminUserFormActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etStars;
    private RadioGroup rgRole;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_form);

        db = FirebaseFirestore.getInstance();
        initViews();
        loadIntentData();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etStars = findViewById(R.id.etStars);
        rgRole = findViewById(R.id.rgRole);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveUser());
    }

    private void loadIntentData() {
        user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
            etStars.setText(String.valueOf(user.getStars()));

            if ("admin".equals(user.getRole())) {
                rgRole.check(R.id.rbAdmin);
            } else {
                rgRole.check(R.id.rbUser);
            }
        }
    }

    private void saveUser() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String starsStr = etStars.getText().toString().trim();
        String role = rgRole.getCheckedRadioButtonId() == R.id.rbAdmin ? "admin" : "user";

        if (name.isEmpty() || starsStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và số sao", Toast.LENGTH_SHORT).show();
            return;
        }

        int stars = 0;
        try {
            stars = Integer.parseInt(starsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số sao không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null) {
            user.setName(name);
            user.setPhone(phone);
            user.setStars(stars);
            user.setRole(role);

            db.collection("users").document(user.getUid()).set(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
