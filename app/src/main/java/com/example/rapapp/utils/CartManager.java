package com.example.rapapp.utils;

import com.example.rapapp.models.CartItem;
import com.example.rapapp.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void addProduct(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cartItems.add(new CartItem(product, 1));
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
                return;
            }
        }
    }

    public void removeProduct(Product product) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(product.getId()));
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
    }
}
