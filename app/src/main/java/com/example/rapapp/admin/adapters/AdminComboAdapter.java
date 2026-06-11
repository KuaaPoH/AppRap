package com.example.rapapp.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rapapp.R;
import com.example.rapapp.admin.activities.AdminComboFormActivity;
import com.example.rapapp.models.Combo;
import com.example.rapapp.utils.PriceUtils;

import java.util.List;

public class AdminComboAdapter extends RecyclerView.Adapter<AdminComboAdapter.ViewHolder> {

    private Context context;
    private List<Combo> comboList;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDelete(Combo combo);
    }

    public AdminComboAdapter(Context context, List<Combo> comboList, OnDeleteClickListener deleteListener) {
        this.context = context;
        this.comboList = comboList;
        this.deleteListener = deleteListener;
    }

    public void updateList(List<Combo> newList) {
        comboList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_combo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Combo combo = comboList.get(position);

        holder.tvComboName.setText(combo.getName());
        holder.tvComboPrice.setText(PriceUtils.formatCurrency(combo.getPrice()));
        holder.tvComboDesc.setText(combo.getDescription());

        Glide.with(context)
                .load(combo.getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .into(holder.ivComboImage);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminComboFormActivity.class);
            intent.putExtra("comboId", combo.getId());
            intent.putExtra("comboName", combo.getName());
            intent.putExtra("comboPrice", combo.getPrice());
            intent.putExtra("comboDesc", combo.getDescription());
            intent.putExtra("comboImage", combo.getImageUrl());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(combo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comboList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivComboImage, btnEdit, btnDelete;
        TextView tvComboName, tvComboPrice, tvComboDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivComboImage = itemView.findViewById(R.id.ivComboImage);
            tvComboName = itemView.findViewById(R.id.tvComboName);
            tvComboPrice = itemView.findViewById(R.id.tvComboPrice);
            tvComboDesc = itemView.findViewById(R.id.tvComboDesc);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
