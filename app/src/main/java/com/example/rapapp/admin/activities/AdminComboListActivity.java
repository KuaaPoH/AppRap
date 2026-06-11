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
import com.example.rapapp.admin.adapters.AdminComboAdapter;
import com.example.rapapp.models.Combo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminComboListActivity extends AppCompatActivity {

    private RecyclerView rvAdminCombos;
    private AdminComboAdapter adapter;
    private List<Combo> comboList = new ArrayList<>();
    private List<Combo> fullComboList = new ArrayList<>();
    private FirebaseFirestore db;
    private EditText etSearchCombo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_combo_list);

        db = FirebaseFirestore.getInstance();

        initViews();
        loadCombos();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddCombo).setOnClickListener(v -> 
            startActivity(new Intent(this, AdminComboFormActivity.class))
        );

        etSearchCombo = findViewById(R.id.etSearchCombo);
        etSearchCombo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCombos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        rvAdminCombos = findViewById(R.id.rvAdminCombos);
        rvAdminCombos.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AdminComboAdapter(this, comboList, this::deleteCombo);
        rvAdminCombos.setAdapter(adapter);
    }

    private void loadCombos() {
        db.collection("combos").addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                return;
            }
            if (value != null) {
                fullComboList.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                    Combo combo = doc.toObject(Combo.class);
                    if (combo != null) {
                        combo.setId(doc.getId());
                        fullComboList.add(combo);
                    }
                }
                filterCombos(etSearchCombo.getText().toString());
            }
        });
    }

    private void filterCombos(String query) {
        String lowerQuery = query.toLowerCase().trim();
        List<Combo> filteredList = new ArrayList<>();
        
        for (Combo combo : fullComboList) {
            if (combo.getName().toLowerCase().contains(lowerQuery)) {
                filteredList.add(combo);
            }
        }
        
        adapter.updateList(filteredList);
    }

    private void deleteCombo(Combo combo) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xoá Combo")
                .setMessage("Bạn có chắc chắn muốn xoá combo '" + combo.getName() + "' không?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    db.collection("combos").document(combo.getId()).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xoá combo", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
