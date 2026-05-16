package com.example.rapapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rapapp.models.News;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageView btnBack, btnShare, imgFeatured;
    private TextView tvTitle, tvNewsInfo;
    private android.widget.LinearLayout layoutContentBlocks;
    private View btnBuyTicket;
    private String newsId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initViews();
        db = FirebaseFirestore.getInstance();

        newsId = getIntent().getStringExtra("newsId");
        if (newsId != null) {
            loadNewsDetail();
        } else {
            Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "Chia sẻ bài viết", Toast.LENGTH_SHORT).show();
        });
        btnBuyTicket.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển sang trang đặt vé", Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        imgFeatured = findViewById(R.id.imgFeatured);
        tvTitle = findViewById(R.id.tvTitle);
        tvNewsInfo = findViewById(R.id.tvNewsInfo);
        layoutContentBlocks = findViewById(R.id.layoutContentBlocks);
        btnBuyTicket = findViewById(R.id.btnBuyTicket);
    }

    private void loadNewsDetail() {
        db.collection("news").document(newsId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                News news = documentSnapshot.toObject(News.class);
                if (news != null) {
                    displayNews(news);
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("NewsDetailActivity", "Error loading news", e);
            Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayNews(News news) {
        tvTitle.setText(news.getTitle());

        // Định dạng ngày đăng và chuyên mục
        String categoryName = "Tin tức";
        if ("Review".equals(news.getCategory())) categoryName = "Bình Luận";
        else if ("Character".equals(news.getCategory())) categoryName = "Nhân Vật";

        String dateStr = "";
        if (news.getPublishedDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            dateStr = sdf.format(news.getPublishedDate().toDate());
        }

        tvNewsInfo.setText(dateStr + " | " + categoryName);
        
        Glide.with(this)
                .load(news.getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .into(imgFeatured);

        // Xóa các khối nội dung cũ trước khi thêm mới (quan quan trọng)
        layoutContentBlocks.removeAllViews();
        layoutContentBlocks.addView(tvTitle);
        layoutContentBlocks.addView(tvNewsInfo);

        if (news.getContentBlocks() != null) {
            for (News.ContentBlock block : news.getContentBlocks()) {
                if ("text".equals(block.getType())) {
                    addTextView(block.getValue());
                } else if ("image".equals(block.getType())) {
                    addImageView(block.getValue());
                }
            }
        }
    }

    private void addTextView(String text) {
        TextView textView = new TextView(this);
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = (int) (16 * getResources().getDisplayMetrics().density);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextColor(android.graphics.Color.parseColor("#333333"));
        textView.setTextSize(14);
        textView.setLineSpacing(4 * getResources().getDisplayMetrics().density, 1.0f);
        
        // Căn lề đều hai bên (Justify) cho Android 8.0 trở lên
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            textView.setJustificationMode(android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        
        layoutContentBlocks.addView(textView);
    }

    private void addImageView(String url) {
        ImageView imageView = new ImageView(this);
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (200 * getResources().getDisplayMetrics().density)
        );
        params.topMargin = (int) (16 * getResources().getDisplayMetrics().density);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.bg_placeholder)
                .into(imageView);
        
        layoutContentBlocks.addView(imageView);
    }
}