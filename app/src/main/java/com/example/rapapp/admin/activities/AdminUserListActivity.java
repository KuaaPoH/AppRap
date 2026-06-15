package com.example.rapapp.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.admin.adapters.AdminUserAdapter;
import com.example.rapapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListActivity extends AppCompatActivity {

    private RecyclerView rvAdminUsers;
    private AdminUserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private List<User> fullUserList = new ArrayList<>();
    private FirebaseFirestore db;
    private EditText etSearchUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        db = FirebaseFirestore.getInstance();

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnAddUser).setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thêm Người dùng mới")
                .setMessage("Để đảm bảo bảo mật và đồng bộ mật khẩu, việc đăng ký tài khoản mới cần được thực hiện qua màn hình Đăng ký của App hoặc trực tiếp trên Firebase Authentication Console.")
                .setPositiveButton("Đã hiểu", null)
                .show();
        });

        etSearchUser = findViewById(R.id.etSearchUser);
        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        rvAdminUsers = findViewById(R.id.rvAdminUsers);
        rvAdminUsers.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AdminUserAdapter(this, userList, 
            user -> {
                Intent intent = new Intent(this, AdminUserDetailActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            },
            new AdminUserAdapter.OnActionClickListener() {
                @Override
                public void onEditClick(User user) {
                    Intent intent = new Intent(AdminUserListActivity.this, AdminUserFormActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }

                @Override
                public void onDeleteClick(User user) {
                    deleteUser(user);
                }
            }
        );
        rvAdminUsers.setAdapter(adapter);
    }

    private void loadUsers() {
        db.collection("users").get().addOnSuccessListener(value -> {
            if (value != null) {
                fullUserList.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        fullUserList.add(user);
                    }
                }
                filterUsers(etSearchUser.getText().toString());
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải danh sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void filterUsers(String query) {
        String lowerQuery = query.toLowerCase().trim();
        List<User> filteredList = new ArrayList<>();
        
        for (User user : fullUserList) {
            if (user.getName().toLowerCase().contains(lowerQuery) || 
                user.getEmail().toLowerCase().contains(lowerQuery) || 
                (user.getPhone() != null && user.getPhone().contains(lowerQuery))) {
                filteredList.add(user);
            }
        }
        
        adapter.updateList(filteredList);
    }
    
    private void deleteUser(User user) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xoá Hồ sơ")
                .setMessage("Bạn có chắc chắn muốn xoá hồ sơ dữ liệu của '" + user.getName() + "' không?\nLưu ý: Thao tác này chỉ xoá dữ liệu trên app, tài khoản đăng nhập (Auth) vẫn tồn tại.")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    db.collection("users").document(user.getUid()).delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Đã xoá hồ sơ người dùng", Toast.LENGTH_SHORT).show();
                                loadUsers(); // Refresh list
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
