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

public class CheckoutSummaryAdapter extends RecyclerView.Adapter<CheckoutSummaryAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    public CheckoutSummaryAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvProductName.setText(item.getProduct().getName());
        holder.tvQuantity.setText("SL: " + item.getQuantity());
        holder.tvPrice.setText(decimalFormat.format(item.getTotalPrice()) + " VND");

        Glide.with(context)
                .load(item.getProduct().getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .into(holder.ivProductThumb);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductThumb;
        TextView tvProductName, tvQuantity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductThumb = itemView.findViewById(R.id.ivProductThumb);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
