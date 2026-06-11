package com.example.rapapp.admin.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.Combo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminComboFormActivity extends AppCompatActivity {

    private TextInputEditText etName, etPrice, etImageUrl, etDescription;
    private FirebaseFirestore db;
    private String comboId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_combo_form);

        db = FirebaseFirestore.getInstance();
        initViews();
        loadIntentData();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etImageUrl = findViewById(R.id.etImageUrl);
        etDescription = findViewById(R.id.etDescription);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveCombo());
    }

    private void loadIntentData() {
        comboId = getIntent().getStringExtra("comboId");
        if (comboId != null) {
            TextView tvFormTitle = findViewById(R.id.tvFormTitle);
            tvFormTitle.setText("Chỉnh sửa Combo");

            etName.setText(getIntent().getStringExtra("comboName"));
            etPrice.setText(String.valueOf((int) getIntent().getDoubleExtra("comboPrice", 0)));
            etImageUrl.setText(getIntent().getStringExtra("comboImage"));
            etDescription.setText(getIntent().getStringExtra("comboDesc"));
        }
    }

    private void saveCombo() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        Combo combo = new Combo(name, description, price, imageUrl);

        if (comboId != null) {
            db.collection("combos").document(comboId).set(combo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            db.collection("combos").add(combo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi thêm mới: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
