package com.example.rapapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.rapapp.models.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;

public class ProfileEditActivity extends AppCompatActivity {

    private ShapeableImageView ivAvatar;
    private TextInputEditText etName, etPhone, etBirthday;
    private AppCompatButton btnSaveProfile;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    
    private Uri imageUri;
    private String currentUserId;
    private String currentAvatarBase64 = "";

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    ivAvatar.setImageURI(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = mAuth.getCurrentUser().getUid();

        initViews();
        loadUserData();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        ivAvatar = findViewById(R.id.ivAvatar);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etBirthday = findViewById(R.id.etBirthday);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        findViewById(R.id.btnChangeAvatar).setOnClickListener(v -> openGallery());
        etBirthday.setOnClickListener(v -> showDatePicker());
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        db.collection("users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                etName.setText(user.getName());
                etPhone.setText(user.getPhone());
                etBirthday.setText(user.getBirthday());
                
                currentAvatarBase64 = user.getAvatarUrl(); // We use avatarUrl field to store Base64
                if (currentAvatarBase64 != null && !currentAvatarBase64.isEmpty()) {
                    try {
                        byte[] decodedString = Base64.decode(currentAvatarBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivAvatar.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        ivAvatar.setImageResource(R.drawable.bear);
                    }
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            etBirthday.setText(date);
        }, year, month, day).show();
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Vui lòng nhập tên");
            return;
        }

        btnSaveProfile.setEnabled(false);
        btnSaveProfile.setText("Đang lưu...");

        String avatarToSave = currentAvatarBase64;

        if (imageUri != null) {
            avatarToSave = encodeImageToBase64(imageUri);
            if (avatarToSave == null) {
                Toast.makeText(this, "Lỗi xử lý ảnh, vui lòng chọn ảnh khác nhẹ hơn", Toast.LENGTH_SHORT).show();
                btnSaveProfile.setEnabled(true);
                btnSaveProfile.setText("Lưu Thay Đổi");
                return;
            }
        }

        updateFirestore(name, phone, birthday, avatarToSave);
    }
    
    private String encodeImageToBase64(Uri uri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(uri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            
            // Tối ưu kích thước để Firestore không bị quá tải (Max Document Size là 1MB)
            int maxWidth = 400;
            int maxHeight = 400;
            float scale = Math.min(((float)maxWidth / selectedImage.getWidth()), ((float)maxHeight / selectedImage.getHeight()));
            
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postScale(scale, scale);
            Bitmap scaledBitmap = Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.getWidth(), selectedImage.getHeight(), matrix, true);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos); // Giảm chất lượng xuống 60%
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateFirestore(String name, String phone, String birthday, String avatarBase64) {
        db.collection("users").document(currentUserId)
                .update("name", name, "phone", phone, "birthday", birthday, "avatarUrl", avatarBase64)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Lưu Thay Đổi");
                });
    }
}
