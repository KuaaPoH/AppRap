package com.example.rapapp.admin.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.adapters.TransactionAdapter;
import com.example.rapapp.models.Booking;
import com.example.rapapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminUserDetailActivity extends AppCompatActivity {

    private RecyclerView rvUserTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Booking> bookingList = new ArrayList<>();
    private TextView tvEmpty;
    private FirebaseFirestore db;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        db = FirebaseFirestore.getInstance();
        currentUser = (User) getIntent().getSerializableExtra("user");

        if (currentUser == null) {
            finish();
            return;
        }

        initViews();
        displayUserInfo();
        loadTransactions();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvUserTransactions = findViewById(R.id.rvUserTransactions);
        tvEmpty = findViewById(R.id.tvEmpty);

        transactionAdapter = new TransactionAdapter(this, bookingList);
        rvUserTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvUserTransactions.setAdapter(transactionAdapter);
    }

    private void displayUserInfo() {
        View layoutUserInfo = findViewById(R.id.layoutUserInfo);
        TextView tvUserName = layoutUserInfo.findViewById(R.id.tvUserName);
        TextView tvUserEmail = layoutUserInfo.findViewById(R.id.tvUserEmail);
        TextView tvUserPhone = layoutUserInfo.findViewById(R.id.tvUserPhone);
        TextView tvUserRole = layoutUserInfo.findViewById(R.id.tvUserRole);
        TextView tvUserStars = layoutUserInfo.findViewById(R.id.tvUserStars);
        android.widget.ImageView ivUserAvatar = layoutUserInfo.findViewById(R.id.ivUserAvatar);

        tvUserName.setText(currentUser.getName());
        tvUserEmail.setText(currentUser.getEmail());
        tvUserPhone.setText(currentUser.getPhone() != null && !currentUser.getPhone().isEmpty() ? currentUser.getPhone() : "Chưa cập nhật SĐT");
        tvUserStars.setText(String.valueOf(currentUser.getStars()));

        if ("admin".equals(currentUser.getRole())) {
            tvUserRole.setText("Admin");
            tvUserRole.setBackgroundResource(R.drawable.bg_btn_orange);
            tvUserRole.setTextColor(Color.WHITE);
        } else {
            tvUserRole.setText("User");
            tvUserRole.setBackgroundResource(R.drawable.bg_btn_outline_blue);
            tvUserRole.setTextColor(getResources().getColor(R.color.galaxy_blue));
        }

        String avatarData = currentUser.getAvatarUrl();
        if (avatarData != null && !avatarData.isEmpty()) {
            try {
                byte[] decodedString = android.util.Base64.decode(avatarData, android.util.Base64.DEFAULT);
                android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivUserAvatar.setImageBitmap(decodedByte);
            } catch (Exception e) {
                ivUserAvatar.setImageResource(R.drawable.ic_person_outline);
            }
        } else {
            ivUserAvatar.setImageResource(R.drawable.ic_person_outline);
        }
    }

    private void loadTransactions() {
        db.collection("bookings")
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        bookingList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Booking booking = document.toObject(Booking.class);
                            booking.setId(document.getId());
                            bookingList.add(booking);
                        }
                        
                        if (bookingList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            rvUserTransactions.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            rvUserTransactions.setVisibility(View.VISIBLE);
                            transactionAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Lỗi tải lịch sử giao dịch", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
