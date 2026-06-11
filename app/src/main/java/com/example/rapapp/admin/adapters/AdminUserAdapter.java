package com.example.rapapp.admin.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.models.User;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private OnItemClickListener listener;
    private OnActionClickListener actionListener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }
    
    public interface OnActionClickListener {
        void onEditClick(User user);
        void onDeleteClick(User user);
    }

    public AdminUserAdapter(Context context, List<User> userList, OnItemClickListener listener, OnActionClickListener actionListener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
        this.actionListener = actionListener;
    }

    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUserName.setText(user.getName());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserPhone.setText(user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "Chưa cập nhật SĐT");
        holder.tvUserStars.setText(String.valueOf(user.getStars()));

        if ("admin".equals(user.getRole())) {
            holder.tvUserRole.setText("Admin");
            holder.tvUserRole.setBackgroundResource(R.drawable.bg_btn_orange);
            holder.tvUserRole.setTextColor(Color.WHITE);
        } else {
            holder.tvUserRole.setText("User");
            holder.tvUserRole.setBackgroundResource(R.drawable.bg_btn_outline_blue);
            holder.tvUserRole.setTextColor(context.getResources().getColor(R.color.galaxy_blue));
        }
        
        String avatarData = user.getAvatarUrl();
        if (avatarData != null && !avatarData.isEmpty()) {
            try {
                byte[] decodedString = android.util.Base64.decode(avatarData, android.util.Base64.DEFAULT);
                android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.ivUserAvatar.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.ivUserAvatar.setImageResource(R.drawable.ic_person_outline);
            }
        } else {
            holder.ivUserAvatar.setImageResource(R.drawable.ic_person_outline);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(user));
        
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEditClick(user);
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDeleteClick(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserPhone, tvUserRole, tvUserStars;
        ImageView ivUserAvatar, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserStars = itemView.findViewById(R.id.tvUserStars);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
