package com.example.rapapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.rapapp.LoginActivity;
import com.example.rapapp.R;
import com.example.rapapp.RegisterActivity;
import com.example.rapapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvUsername, tvUserSubtext, tvUserStars;
    private LinearLayout llAuthButtons, llStarsInfo;
    private AppCompatButton btnLogout;
    private View btnAdmin, btnTransactions;

    private com.google.android.material.imageview.ShapeableImageView ivUserAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ivUserAvatar = view.findViewById(R.id.ivUserAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserSubtext = view.findViewById(R.id.tvUserSubtext);
        tvUserStars = view.findViewById(R.id.tvUserStars);
        llAuthButtons = view.findViewById(R.id.llAuthButtons);
        llStarsInfo = view.findViewById(R.id.llStarsInfo);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnTransactions = view.findViewById(R.id.btnTransactions);
        
        AppCompatButton btnProfileRegister = view.findViewById(R.id.btnProfileRegister);
        AppCompatButton btnProfileLogin = view.findViewById(R.id.btnProfileLogin);
        ImageView btnSettings = view.findViewById(R.id.btnSettings);
        btnAdmin = view.findViewById(R.id.btnAdmin);

        if (ivUserAvatar != null) {
            ivUserAvatar.setOnClickListener(v -> {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getActivity(), com.example.rapapp.ProfileEditActivity.class));
                }
            });
        }

        if (btnTransactions != null) {
            btnTransactions.setOnClickListener(v -> {
                if (mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(getActivity(), com.example.rapapp.TransactionHistoryActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Vui lòng đăng nhập để xem lịch sử", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnAdmin != null) {
            btnAdmin.setOnClickListener(v -> {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    db.collection("users").document(firebaseUser.getUid()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                User user = documentSnapshot.toObject(User.class);
                                if (user != null && "admin".equals(user.getRole())) {
                                    Intent intent = new Intent(getActivity(), com.example.rapapp.admin.activities.AdminDashboardActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), "Bạn không có quyền truy cập Admin", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.example.rapapp.SettingsActivity.class);
                startActivity(intent);
            });
        }

        if (btnProfileRegister != null) {
            btnProfileRegister.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            });
        }

        if (btnProfileLogin != null) {
            btnProfileLogin.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                updateUI();
                Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // Logged in
            if (llAuthButtons != null) llAuthButtons.setVisibility(View.GONE);
            if (btnLogout != null) btnLogout.setVisibility(View.VISIBLE);
            if (llStarsInfo != null) llStarsInfo.setVisibility(View.VISIBLE);
            
            db.collection("users").document(firebaseUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if (tvUsername != null) tvUsername.setText(user.getName());
                            if (tvUserSubtext != null) tvUserSubtext.setText(user.getEmail());
                            if (tvUserStars != null) tvUserStars.setText(user.getStars() + " Stars");
                            
                            if (ivUserAvatar != null) {
                                String avatarData = user.getAvatarUrl();
                                if (avatarData != null && !avatarData.isEmpty()) {
                                    try {
                                        byte[] decodedString = android.util.Base64.decode(avatarData, android.util.Base64.DEFAULT);
                                        android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        ivUserAvatar.setImageBitmap(decodedByte);
                                    } catch (Exception e) {
                                        ivUserAvatar.setImageResource(R.drawable.bear);
                                    }
                                } else {
                                    ivUserAvatar.setImageResource(R.drawable.bear);
                                }
                            }
                        }
                    });
        } else {
            // Not logged in
            if (llAuthButtons != null) llAuthButtons.setVisibility(View.VISIBLE);
            if (btnLogout != null) btnLogout.setVisibility(View.GONE);
            if (llStarsInfo != null) llStarsInfo.setVisibility(View.GONE);
            
            if (tvUsername != null) tvUsername.setText("Đăng Ký Thành Viên Star");
            if (tvUserSubtext != null) tvUserSubtext.setText("Nhận Ngay Ưu Đãi!");
            if (ivUserAvatar != null) ivUserAvatar.setImageResource(R.drawable.bear);
        }
    }
}

