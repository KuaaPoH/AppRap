package com.example.rapapp.admin.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.Banner;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminBannerFormActivity extends AppCompatActivity {

    private TextInputEditText etImageUrl, etContent;
    private FirebaseFirestore db;
    private String bannerId = null;
    private Banner currentBanner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_banner_form);

        db = FirebaseFirestore.getInstance();

        etImageUrl = findViewById(R.id.etImageUrl);
        etContent = findViewById(R.id.etContent);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveBanner());

        setupFormatToolbar();

        if (getIntent().hasExtra("bannerId")) {
            bannerId = getIntent().getStringExtra("bannerId");
            TextView tvFormTitle = findViewById(R.id.tvFormTitle);
            tvFormTitle.setText("Sửa Banner");
            loadBannerData();
        }
    }

    private void setupFormatToolbar() {
        findViewById(R.id.btnBold).setOnClickListener(v -> insertFormat("<b>", "</b>"));
        findViewById(R.id.btnImage).setOnClickListener(v -> insertFormat("\n\nIMAGE: [Nhập_Link_Ảnh]\n\n", ""));
        findViewById(R.id.btnBullet).setOnClickListener(v -> insertFormat("\n\nBULLET: ", ""));
    }

    private void insertFormat(String prefix, String suffix) {
        int start = Math.max(etContent.getSelectionStart(), 0);
        int end = Math.max(etContent.getSelectionEnd(), 0);
        android.text.Editable editable = etContent.getText();
        if (editable != null) {
            String selectedText = editable.subSequence(start, end).toString();
            String replacement = prefix + selectedText + suffix;
            editable.replace(start, end, replacement);
            etContent.setSelection(start + prefix.length() + selectedText.length());
        }
    }

    private void loadBannerData() {
        db.collection("banners").document(bannerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentBanner = documentSnapshot.toObject(Banner.class);
                        if (currentBanner != null) {
                            etImageUrl.setText(currentBanner.getImageUrl());
                            
                            StringBuilder sb = new StringBuilder();
                            if (currentBanner.getContentBlocks() != null) {
                                for (Banner.ContentBlock block : currentBanner.getContentBlocks()) {
                                    sb.append(block.getType().toUpperCase()).append(": ").append(block.getValue()).append("\n\n");
                                }
                            }
                            etContent.setText(sb.toString().trim());
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
    }

    private void saveBanner() {
        String imageUrl = etImageUrl.getText().toString().trim();
        String contentRaw = etContent.getText().toString().trim();

        if (imageUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập link ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        Banner banner = new Banner(imageUrl);
        
        java.util.List<Banner.ContentBlock> blocks = new java.util.ArrayList<>();
        if (!contentRaw.isEmpty()) {
            String[] lines = contentRaw.split("\n\n");
            for (String line : lines) {
                if (line.startsWith("TEXT:")) {
                    blocks.add(new Banner.ContentBlock("text", line.substring(5).trim()));
                } else if (line.startsWith("IMAGE:")) {
                    blocks.add(new Banner.ContentBlock("image", line.substring(6).trim()));
                } else if (line.startsWith("BULLET:")) {
                    blocks.add(new Banner.ContentBlock("bullet", line.substring(7).trim()));
                } else {
                    blocks.add(new Banner.ContentBlock("text", line.trim()));
                }
            }
        }
        banner.setContentBlocks(blocks);

        if (bannerId == null) {
            db.collection("banners").add(banner)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Đã thêm banner", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi thêm", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("banners").document(bannerId)
                    .set(banner)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Đã cập nhật banner", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show());
        }
    }
}
