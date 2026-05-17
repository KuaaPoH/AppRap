package com.example.rapapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rapapp.models.Banner;
import com.google.firebase.firestore.FirebaseFirestore;

import android.text.Html;

public class PromoDetailActivity extends AppCompatActivity {

    private ImageView btnBack, btnShare, imgPromo;
    private LinearLayout layoutContent;
    private String bannerId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_detail);

        initViews();
        db = FirebaseFirestore.getInstance();

        bannerId = getIntent().getStringExtra("bannerId");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Luôn hiển thị ảnh banner trước
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.bg_placeholder)
                    .into(imgPromo);
        }

        if (bannerId != null) {
            loadPromoDetail();
        }

        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "Chia sẻ khuyến mãi", Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        imgPromo = findViewById(R.id.imgPromo);
        layoutContent = findViewById(R.id.layoutContent); // Cần thêm ID này vào XML
    }

    private void loadPromoDetail() {
        db.collection("banners").document(bannerId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Banner banner = documentSnapshot.toObject(Banner.class);
                if (banner != null && banner.getContentBlocks() != null) {
                    displayContent(banner);
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("PromoDetailActivity", "Error loading promo detail", e);
        });
    }

    private void displayContent(Banner banner) {
        layoutContent.removeAllViews();
        java.util.List<Banner.ContentBlock> blocks = banner.getContentBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            Banner.ContentBlock block = blocks.get(i);
            int fontSize = (i == 0) ? 14 : 12;

            switch (block.getType()) {
                case "title":
                    addTitleView(block.getValue(), fontSize);
                    break;
                case "text":
                    addTextView(block.getValue(), false, fontSize);
                    break;
                case "italic":
                    addTextView(block.getValue(), true, fontSize);
                    break;
                case "bullet":
                    addBulletView(block.getValue(), fontSize);
                    break;
                case "image":
                    addImageView(block.getValue());
                    break;
            }
        }
    }

    private void addTitleView(String text, int fontSize) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (24 * getResources().getDisplayMetrics().density);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(fontSize);
        textView.setTypeface(null, Typeface.BOLD);
        layoutContent.addView(textView);
    }

    private void addTextView(String text, boolean isItalic, int fontSize) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (12 * getResources().getDisplayMetrics().density);
        textView.setLayoutParams(params);
        
        // Hỗ trợ bôi đậm bằng thẻ <b> từ Database
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(text));
        }
        
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(fontSize);
        if (isItalic) {
            textView.setTypeface(null, Typeface.ITALIC);
        }
        textView.setLineSpacing(4 * getResources().getDisplayMetrics().density, 1.0f);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            textView.setJustificationMode(android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        layoutContent.addView(textView);
    }

    private void addBulletView(String text, int fontSize) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = (int) (8 * getResources().getDisplayMetrics().density);
        row.setLayoutParams(rowParams);

        TextView dot = new TextView(this);
        dot.setText("•");
        dot.setTextColor(Color.BLACK);
        dot.setTextSize(fontSize + 2); // Dot hơi lớn hơn chữ một chút
        dot.setTypeface(null, Typeface.BOLD);
        
        TextView content = new TextView(this);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        contentParams.leftMargin = (int) (8 * getResources().getDisplayMetrics().density);
        content.setLayoutParams(contentParams);

        // Hỗ trợ bôi đậm bằng thẻ <b> từ Database cho gạch đầu dòng
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            content.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            content.setText(Html.fromHtml(text));
        }

        content.setTextColor(Color.BLACK);
        content.setTextSize(fontSize);

        row.addView(dot);
        row.addView(content);
        layoutContent.addView(row);
    }

    private void addImageView(String url) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (16 * getResources().getDisplayMetrics().density);
        imageView.setLayoutParams(params);
        imageView.setAdjustViewBounds(true);
        Glide.with(this).load(url).into(imageView);
        layoutContent.addView(imageView);
    }
}
