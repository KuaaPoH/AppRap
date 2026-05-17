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
import com.example.rapapp.models.Combo;
import com.example.rapapp.utils.PriceUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComboAdapter extends RecyclerView.Adapter<ComboAdapter.ComboViewHolder> {

    private Context context;
    private List<Combo> combos;
    private Map<String, Integer> quantities = new HashMap<>();
    private OnComboQuantityChangeListener listener;

    public interface OnComboQuantityChangeListener {
        void onQuantityChanged(double extraPrice, Map<String, Integer> quantities);
    }

    public ComboAdapter(Context context, List<Combo> combos, OnComboQuantityChangeListener listener) {
        this.context = context;
        this.combos = combos;
        this.listener = listener;
        for (Combo c : combos) {
            quantities.put(c.getId(), 0);
        }
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_combo, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        Combo combo = combos.get(position);
        holder.tvComboName.setText(combo.getName());
        holder.tvComboDescription.setText(combo.getDescription());
        holder.tvComboPrice.setText("Price: " + PriceUtils.formatCurrency(combo.getPrice()));

        Glide.with(context)
                .load(combo.getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .into(holder.ivComboImage);

        int quantity = quantities.get(combo.getId());
        holder.tvQuantity.setText(String.valueOf(quantity));

        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = quantities.get(combo.getId());
            if (currentQty > 0) {
                quantities.put(combo.getId(), currentQty - 1);
                holder.tvQuantity.setText(String.valueOf(currentQty - 1));
                calculateTotalExtraPrice();
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int currentQty = quantities.get(combo.getId());
            quantities.put(combo.getId(), currentQty + 1);
            holder.tvQuantity.setText(String.valueOf(currentQty + 1));
            calculateTotalExtraPrice();
        });
    }

    private void calculateTotalExtraPrice() {
        double total = 0;
        for (Combo c : combos) {
            total += c.getPrice() * quantities.get(c.getId());
        }
        if (listener != null) {
            listener.onQuantityChanged(total, quantities);
        }
    }

    @Override
    public int getItemCount() {
        return combos != null ? combos.size() : 0;
    }

    static class ComboViewHolder extends RecyclerView.ViewHolder {
        ImageView ivComboImage;
        TextView tvComboName, tvComboDescription, tvComboPrice;
        TextView btnMinus, tvQuantity, btnPlus;

        public ComboViewHolder(@NonNull View itemView) {
            super(itemView);
            ivComboImage = itemView.findViewById(R.id.ivComboImage);
            tvComboName = itemView.findViewById(R.id.tvComboName);
            tvComboDescription = itemView.findViewById(R.id.tvComboDescription);
            tvComboPrice = itemView.findViewById(R.id.tvComboPrice);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}
