package com.example.rapapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.rapapp.models.CartItem;
import com.example.rapapp.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREF_NAME = "CartPrefs";
    private static final String KEY_CART_ITEMS = "cart_items";

    private static CartManager instance;
    private List<CartItem> cartItems;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private CartManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCart();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    private void loadCart() {
        String json = sharedPreferences.getString(KEY_CART_ITEMS, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
            cartItems = gson.fromJson(json, type);
        }
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }

    private void saveCart() {
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(KEY_CART_ITEMS, json).apply();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void addProduct(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                saveCart();
                return;
            }
        }
        cartItems.add(new CartItem(product, 1));
        saveCart();
    }

    public void updateQuantity(String productId, int delta) {
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item.getProduct().getId().equals(productId)) {
                int newQuantity = item.getQuantity() + delta;
                if (newQuantity <= 0) {
                    cartItems.remove(i);
                } else {
                    item.setQuantity(newQuantity);
                }
                saveCart();
                return;
            }
        }
    }

    public void removeProduct(Product product) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(product.getId()));
        saveCart();
    }

    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public long getTotalAmount() {
        long total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }
    
    public void clearCart() {
        cartItems.clear();
        saveCart();
    }
}
