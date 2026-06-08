package com.example.rapapp.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.models.Room;

import java.util.List;

public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.ViewHolder> {

    private List<Room> roomList;
    private OnRoomClickListener editListener;
    private OnRoomClickListener deleteListener;

    public interface OnRoomClickListener {
        void onClick(Room room);
    }

    public void updateList(List<Room> newList) {
        this.roomList = newList;
        notifyDataSetChanged();
    }

    public AdminRoomAdapter(List<Room> roomList, OnRoomClickListener editListener, OnRoomClickListener deleteListener) {
        this.roomList = roomList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.tvRoomName.setText(room.getName());
        holder.tvCinemaId.setText("Cinema ID: " + room.getCinemaId());
        holder.tvDimensions.setText(room.getTotalRows() + " hàng x " + room.getTotalCols() + " cột");

        holder.btnEdit.setOnClickListener(v -> editListener.onClick(room));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onClick(room));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView btnEdit, btnDelete;
        TextView tvRoomName, tvCinemaId, tvDimensions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvCinemaId = itemView.findViewById(R.id.tvCinemaId);
            tvDimensions = itemView.findViewById(R.id.tvDimensions);
        }
    }
}