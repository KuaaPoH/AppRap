package com.example.rapapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.adapters.ComboAdapter;
import com.example.rapapp.models.Combo;
import com.example.rapapp.utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

public class ComboSelectionActivity extends AppCompatActivity {

    private RecyclerView rvCombos;
    private TextView tvSelectedSeats, tvTotalPrice;
    private double seatTotalPrice = 0;
    private double comboTotalPrice = 0;
    private ArrayList<String> selectedSeats;
    private List<Combo> currentCombos = new ArrayList<>();
    private java.util.Map<String, Integer> currentComboQtys = new java.util.HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_selection);

        // Lấy dữ liệu từ màn hình chọn ghế truyền sang
        selectedSeats = getIntent().getStringArrayListExtra("selectedSeats");
        seatTotalPrice = getIntent().getDoubleExtra("totalPrice", 0);

        initViews();
        setupComboList();
    }

    private void initViews() {
        rvCombos = findViewById(R.id.rvCombos);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnContinue).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, CheckoutActivity.class);
            // Copy data from previous intent
            android.content.Intent prevIntent = getIntent();
            intent.putExtras(prevIntent);
            
            // Add combo data
            ArrayList<String> selectedComboStrs = new ArrayList<>();
            ArrayList<String> selectedComboPrices = new ArrayList<>();
            for (Combo c : currentCombos) {
                int qty = currentComboQtys.containsKey(c.getId()) ? currentComboQtys.get(c.getId()) : 0;
                if (qty > 0) {
                    selectedComboStrs.add(qty + "x " + c.getName());
                    selectedComboPrices.add(PriceUtils.formatCurrency(qty * c.getPrice()));
                }
            }
            intent.putStringArrayListExtra("selectedCombos", selectedComboStrs);
            intent.putStringArrayListExtra("selectedComboPrices", selectedComboPrices);
            intent.putExtra("comboTotalPrice", comboTotalPrice);
            intent.putExtra("finalTotalPrice", seatTotalPrice + comboTotalPrice);
            
            startActivity(intent);
        });

        // Hiển thị ghế và giá vé ban đầu
        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            tvSelectedSeats.setText(selectedSeats.size() + "x ghế: " + TextUtils.join(", ", selectedSeats));
        }
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = seatTotalPrice + comboTotalPrice;
        tvTotalPrice.setText("Tổng Cộng: " + PriceUtils.formatCurrency(total));
    }

    private void setupComboList() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("combos").get().addOnSuccessListener(queryDocumentSnapshots -> {
            currentCombos.clear();
            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                Combo c = doc.toObject(Combo.class);
                if (c != null) {
                    c.setId(doc.getId());
                    currentCombos.add(c);
                }
            }

            ComboAdapter adapter = new ComboAdapter(this, currentCombos, (extraPrice, qtys) -> {
                comboTotalPrice = extraPrice;
                currentComboQtys = qtys;
                updateTotalPrice(); // Chỉ update giá tiền
            });

            rvCombos.setAdapter(adapter);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải danh sách Combo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
