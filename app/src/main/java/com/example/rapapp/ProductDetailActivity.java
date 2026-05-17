package com.example.rapapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
            // Thêm sản phẩm với số lượng hiện tại vào giỏ hàng
            for (int i = 0; i < currentQuantity; i++) {
                CartManager.getInstance().addProduct(product);
            }
            updateCartBadge();
            Toast.makeText(this, "Đã thêm " + currentQuantity + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        btnBuyNowBottom.setOnClickListener(v -> {
            for (int i = 0; i < currentQuantity; i++) {
                CartManager.getInstance().addProduct(product);
            }
            startActivity(new Intent(this, CartActivity.class));
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
}