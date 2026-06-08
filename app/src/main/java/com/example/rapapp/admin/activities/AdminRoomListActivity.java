package com.example.rapapp.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.admin.adapters.AdminRoomAdapter;
import com.example.rapapp.models.Room;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminRoomListActivity extends AppCompatActivity {

    private RecyclerView rvAdminRooms;
    private AdminRoomAdapter adapter;
    private List<Room> roomList;
    private FirebaseFirestore db;

    private List<Room> fullRoomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room_list);

        db = FirebaseFirestore.getInstance();
        roomList = new ArrayList<>();

        initViews();
        setupSearch();
        loadRooms();
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearchRoom);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRooms(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterRooms(String query) {
        List<Room> filteredList = new ArrayList<>();
        for (Room room : fullRoomList) {
            if (room.getName() != null && room.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(room);
            }
        }
        adapter.updateList(filteredList);
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvAdminRooms = findViewById(R.id.rvAdminRooms);
        rvAdminRooms.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminRoomAdapter(roomList, room -> {
            Intent intent = new Intent(this, AdminRoomFormActivity.class);
            intent.putExtra("roomId", room.getId());
            startActivity(intent);
        }, room -> {
            deleteRoom(room);
        });
        rvAdminRooms.setAdapter(adapter);

        ImageView btnAddRoom = findViewById(R.id.btnAddRoom);
        btnAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminRoomFormActivity.class);
            startActivity(intent);
        });
    }

    private void loadRooms() {
        db.collection("rooms")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        roomList.clear();
                        fullRoomList.clear();
                        List<Room> objects = value.toObjects(Room.class);
                        for (int i = 0; i < value.getDocuments().size(); i++) {
                            objects.get(i).setId(value.getDocuments().get(i).getId());
                        }
                        roomList.addAll(objects);
                        fullRoomList.addAll(objects);
                        
                        EditText etSearch = findViewById(R.id.etSearchRoom);
                        filterRooms(etSearch.getText().toString());
                    }
                });
    }

    private void deleteRoom(Room room) {
        db.collection("rooms").document(room.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xoá phòng", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xoá", Toast.LENGTH_SHORT).show());
    }
}