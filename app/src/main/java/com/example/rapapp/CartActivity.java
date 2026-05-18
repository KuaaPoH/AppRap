package com.example.rapapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.adapters.CartAdapter;
import com.example.rapapp.models.CartItem;
import com.example.rapapp.utils.CartManager;

import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemChangeListener {

    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private TextView tvTotalAmount, tvFinalAmount, tvDiscount;
    private View layoutEmptyCart, layoutSummary;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setupRecyclerView();
        updateUI();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnContinueShopping).setOnClickListener(v -> finish());
        findViewById(R.id.btnOrder).setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopCheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        tvDiscount = findViewById(R.id.tvDiscount);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutSummary = findViewById(R.id.layoutSummary);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this, CartManager.getInstance().getCartItems(), this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);
    }

    private void updateUI() {
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            layoutEmptyCart.setVisibility(View.VISIBLE);
            layoutSummary.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.GONE);
        } else {
            layoutEmptyCart.setVisibility(View.GONE);
            layoutSummary.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.VISIBLE);

            long total = CartManager.getInstance().getTotalAmount();
            tvTotalAmount.setText(decimalFormat.format(total) + " VND");
            tvFinalAmount.setText(decimalFormat.format(total) + " VND");
            tvDiscount.setText("0 VND");
        }
    }

    @Override
    public void onQuantityChanged(String productId, int delta) {
        CartManager.getInstance().updateQuantity(productId, delta);
        cartAdapter.notifyDataSetChanged();
        updateUI();
    }

    @Override
    public void onRemoveItem(CartItem item) {
        CartManager.getInstance().removeProduct(item.getProduct());
        cartAdapter.notifyDataSetChanged();
        updateUI();
        Toast.makeText(this, "Đã xóa " + item.getProduct().getName(), Toast.LENGTH_SHORT).show();
    }
}
