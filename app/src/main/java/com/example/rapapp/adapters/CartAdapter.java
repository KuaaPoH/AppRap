package com.example.rapapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rapapp.R;
import com.example.rapapp.models.CartItem;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartItemChangeListener {
        void onQuantityChanged(String productId, int delta);
        void onRemoveItem(CartItem item);
    }

    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvProductName.setText(item.getProduct().getName());
        holder.tvProductPrice.setText(decimalFormat.format(item.getProduct().getPrice()) + " VND");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        
        // Giả lập mô tả cho sản phẩm nếu không có trong model
        holder.tvProductDesc.setText(item.getProduct().getName() + " thơm ngon, chất lượng...");

        Glide.with(context)
                .load(item.getProduct().getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .into(holder.imgProduct);

        holder.btnPlus.setOnClickListener(v -> listener.onQuantityChanged(item.getProduct().getId(), 1));
        holder.btnMinus.setOnClickListener(v -> listener.onQuantityChanged(item.getProduct().getId(), -1));
        holder.btnDelete.setOnClickListener(v -> listener.onRemoveItem(item));
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnDelete;
        TextView tvProductName, tvProductDesc, tvProductPrice, tvQuantity, btnMinus, btnPlus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}