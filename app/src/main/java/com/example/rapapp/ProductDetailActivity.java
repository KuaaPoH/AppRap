package com.example.rapapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rapapp.CartActivity;
import com.example.rapapp.models.Product;
import com.example.rapapp.utils.CartManager;

import java.text.DecimalFormat;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgProduct, btnBack;
    private TextView tvProductName, tvProductPrice, tvDescription, tvPriceBottom, tvCartBadge, tvQuantity, btnMinus, btnPlus;
    private View btnBuyNowBottom, btnAddToCartBottom, layoutCart;
    private Product product;
    private int currentQuantity = 1;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        getDataIntent();
        setupListeners();
        updateCartBadge();
    }

    private void initViews() {
        imgProduct = findViewById(R.id.imgProduct);
        btnBack = findViewById(R.id.btnBack);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvPriceBottom = findViewById(R.id.tvPriceBottom);
        tvCartBadge = findViewById(R.id.tvCartBadge);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnBuyNowBottom = findViewById(R.id.btnBuyNowBottom);
        btnAddToCartBottom = findViewById(R.id.btnAddToCartBottom);
        layoutCart = findViewById(R.id.layoutCart);
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product_id")) {
            String id = intent.getStringExtra("product_id");
            String name = intent.getStringExtra("product_name");
            double price = intent.getDoubleExtra("product_price", 0);
            String imageUrl = intent.getStringExtra("product_image");
            String description = intent.getStringExtra("product_desc");

            product = new Product(name, price, imageUrl, "", description);
            product.setId(id);

            tvProductName.setText(product.getName());
            tvProductPrice.setText(decimalFormat.format(product.getPrice()) + " VND");
            updateTotalPrice();
            
            if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                tvDescription.setText(product.getDescription());
            } else {
                tvDescription.setText("Mô tả sản phẩm đang được cập nhật...");
            }

            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.bg_placeholder)
                    .into(imgProduct);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        layoutCart.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        btnPlus.setOnClickListener(v -> {
            currentQuantity++;
            tvQuantity.setText(String.valueOf(currentQuantity));
            updateTotalPrice();
        });

        btnMinus.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                tvQuantity.setText(String.valueOf(currentQuantity));
                updateTotalPrice();
            }
        });

        btnAddToCartBottom.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                    .withEndAction(() -> {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        // Thêm sản phẩm với số lượng hiện tại vào giỏ hàng
                        for (int i = 0; i < currentQuantity; i++) {
                            CartManager.getInstance().addProduct(product);
                        }
                        playAddToCartAnimation(v, layoutCart);
                    }).start();
        });

        btnBuyNowBottom.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                    .withEndAction(() -> {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        for (int i = 0; i < currentQuantity; i++) {
                            CartManager.getInstance().addProduct(product);
                        }
                        startActivity(new Intent(this, CartActivity.class));
                    }).start();
        });
    }

    private void updateTotalPrice() {
        if (product != null) {
            double total = product.getPrice() * currentQuantity;
            tvPriceBottom.setText(decimalFormat.format(total) + " VND");
        }
    }

    private void updateCartBadge() {
        int total = CartManager.getInstance().getTotalQuantity();
        if (total > 0) {
            tvCartBadge.setText(String.valueOf(total));
            tvCartBadge.setVisibility(View.VISIBLE);
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void playAddToCartAnimation(View startView, View endView) {
        // Tạo một ImageView tạm thời để bay
        final ImageView flyingIcon = new ImageView(this);
        flyingIcon.setImageResource(R.drawable.ic_cart);
        flyingIcon.setLayoutParams(new FrameLayout.LayoutParams(60, 60));
        
        // Lấy root view để add icon vào
        final ViewGroup rootView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.addView(flyingIcon);

        // Lấy vị trí bắt đầu và kết thúc
        int[] startLoc = new int[2];
        startView.getLocationInWindow(startLoc);
        int[] endLoc = new int[2];
        endView.getLocationInWindow(endLoc);

        // Thiết lập vị trí ban đầu cho icon bay
        flyingIcon.setX(startLoc[0] + startView.getWidth() / 2f - 30);
        flyingIcon.setY(startLoc[1] + startView.getHeight() / 2f - 30);

        // Hiệu ứng bay
        ObjectAnimator animX = ObjectAnimator.ofFloat(flyingIcon, "translationX", endLoc[0] + endView.getWidth() / 2f - 30);
        ObjectAnimator animY = ObjectAnimator.ofFloat(flyingIcon, "translationY", endLoc[1] + endView.getHeight() / 2f - 30);
        animY.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(flyingIcon, "scaleX", 1.0f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(flyingIcon, "scaleY", 1.0f, 0.5f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(flyingIcon, "alpha", 1.0f, 0.5f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animX, animY, scaleX, scaleY, alpha);
        animatorSet.setDuration(800);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rootView.removeView(flyingIcon);
                updateCartBadge();
                
                // Hiệu ứng nảy cho giỏ hàng
                endView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                        .withEndAction(() -> endView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start())
                        .start();
            }
        });
        animatorSet.start();
    }
}