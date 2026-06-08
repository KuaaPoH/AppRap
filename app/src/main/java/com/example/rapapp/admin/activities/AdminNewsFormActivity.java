package com.example.rapapp.admin.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.News;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminNewsFormActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etImageUrl, etCategory, etContent;
    private FirebaseFirestore db;
    private String newsId = null;
    private News currentNews = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_news_form);

        db = FirebaseFirestore.getInstance();

        etTitle = findViewById(R.id.etTitle);
        etImageUrl = findViewById(R.id.etImageUrl);
        etCategory = findViewById(R.id.etCategory);
        etContent = findViewById(R.id.etContent);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveNews());

        setupFormatToolbar();

        if (getIntent().hasExtra("newsId")) {
            newsId = getIntent().getStringExtra("newsId");
            TextView tvFormTitle = findViewById(R.id.tvFormTitle);
            tvFormTitle.setText("Sửa Tin tức");
            loadNewsData();
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

    private void loadNewsData() {
        db.collection("news").document(newsId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentNews = documentSnapshot.toObject(News.class);
                        if (currentNews != null) {
                            etTitle.setText(currentNews.getTitle());
                            etImageUrl.setText(currentNews.getImageUrl());
                            etCategory.setText(currentNews.getCategory());
                            
                            StringBuilder sb = new StringBuilder();
                            if (currentNews.getContentBlocks() != null) {
                                for (News.ContentBlock block : currentNews.getContentBlocks()) {
                                    sb.append(block.getType().toUpperCase()).append(": ").append(block.getValue()).append("\n\n");
                                }
                            }
                            etContent.setText(sb.toString().trim());
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
    }

    private void saveNews() {
        String title = etTitle.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String contentRaw = etContent.getText().toString().trim();

        if (title.isEmpty() || imageUrl.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        News news = new News();
        news.setTitle(title);
        news.setImageUrl(imageUrl);
        news.setCategory(category);
        
        java.util.List<News.ContentBlock> blocks = new java.util.ArrayList<>();
        if (!contentRaw.isEmpty()) {
            String[] lines = contentRaw.split("\n\n");
            for (String line : lines) {
                if (line.startsWith("TEXT:")) {
                    blocks.add(new News.ContentBlock("text", line.substring(5).trim()));
                } else if (line.startsWith("IMAGE:")) {
                    blocks.add(new News.ContentBlock("image", line.substring(6).trim()));
                } else if (line.startsWith("BULLET:")) {
                    blocks.add(new News.ContentBlock("bullet", line.substring(7).trim()));
                } else {
                    blocks.add(new News.ContentBlock("text", line.trim()));
                }
            }
        }
        news.setContentBlocks(blocks);

        if (currentNews != null) {
            news.setPublishedDate(currentNews.getPublishedDate());
        } else {
            news.setPublishedDate(Timestamp.now());
        }

        if (newsId == null) {
            db.collection("news").add(news)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Đã thêm tin tức", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi thêm", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("news").document(newsId)
                    .set(news)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Đã cập nhật tin tức", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show());
        }
    }
}
