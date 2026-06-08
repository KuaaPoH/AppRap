package com.example.rapapp.admin.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.Product;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminProductFormActivity extends AppCompatActivity {

    private TextInputEditText etName, etPrice, etImageUrl, etCategory, etDescription;
    private TextView tvFormTitle, btnSave;
    private FirebaseFirestore db;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_form);

        db = FirebaseFirestore.getInstance();

        initViews();
        productId = getIntent().getStringExtra("productId");
        if (productId != null) {
            tvFormTitle.setText("Chỉnh sửa sản phẩm");
            loadProductData();
        }

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        btnSave = findViewById(R.id.btnSave);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etImageUrl = findViewById(R.id.etImageUrl);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);
    }

    private void loadProductData() {
        db.collection("products").document(productId).get().addOnSuccessListener(documentSnapshot -> {
            Product product = documentSnapshot.toObject(Product.class);
            if (product != null) {
                etName.setText(product.getName());
                etPrice.setText(String.valueOf((int) product.getPrice()));
                etImageUrl.setText(product.getImageUrl());
                etCategory.setText(product.getCategory());
                etDescription.setText(product.getDescription());
            }
        });
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(Double.parseDouble(priceStr));
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setDescription(description);

        if (productId != null) {
            db.collection("products").document(productId).set(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            db.collection("products").add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}
