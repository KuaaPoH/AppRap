package com.example.rapapp.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.admin.adapters.AdminProductAdapter;
import com.example.rapapp.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminProductListActivity extends AppCompatActivity {

    private RecyclerView rvAdminProducts;
    private AdminProductAdapter adapter;
    private List<Product> productList;
    private List<Product> fullProductList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_list);

        db = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();

        initViews();
        setupSearch();
        loadProducts();
    }

    private void setupSearch() {
        android.widget.EditText etSearch = findViewById(R.id.etSearchProduct);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : fullProductList) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.updateList(filteredList);
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        rvAdminProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter(productList, product -> {
            Intent intent = new Intent(this, AdminProductFormActivity.class);
            intent.putExtra("productId", product.getId());
            startActivity(intent);
        }, product -> {
            deleteProduct(product);
        });
        rvAdminProducts.setAdapter(adapter);

        android.widget.ImageView btnAddProduct = findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminProductFormActivity.class));
        });
    }

    private void loadProducts() {
        db.collection("products").addSnapshotListener((value, error) -> {
            if (value != null) {
                productList.clear();
                fullProductList.clear();
                for (int i = 0; i < value.getDocuments().size(); i++) {
                    Product product = value.getDocuments().get(i).toObject(Product.class);
                    if (product != null) {
                        product.setId(value.getDocuments().get(i).getId());
                        productList.add(product);
                        fullProductList.add(product);
                    }
                }
                android.widget.EditText etSearch = findViewById(R.id.etSearchProduct);
                filterProducts(etSearch.getText().toString());
            }
        });
    }

    private void deleteProduct(Product product) {
        db.collection("products").document(product.getId()).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show());
    }
}
