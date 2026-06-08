package com.example.rapapp.admin.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.Room;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminRoomFormActivity extends AppCompatActivity {

    private TextInputEditText etName, etTotalRows, etTotalCols;
    private TextView tvFormTitle, btnSave;
    private LinearLayout seatContainer;
    private FirebaseFirestore db;
    private String roomId;
    private String[][] currentLayoutMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room_form);

        db = FirebaseFirestore.getInstance();
        initViews();

        roomId = getIntent().getStringExtra("roomId");
        if (roomId != null) {
            tvFormTitle.setText("Chỉnh sửa Phòng chiếu");
            loadRoomData();
        }

        btnSave.setOnClickListener(v -> saveRoom());
        findViewById(R.id.btnGenerateGrid).setOnClickListener(v -> generateGridFromInput());
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        btnSave = findViewById(R.id.btnSave);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        etTotalRows = findViewById(R.id.etTotalRows);
        etTotalCols = findViewById(R.id.etTotalCols);
        seatContainer = findViewById(R.id.seatContainer);
    }

    private void loadRoomData() {
        db.collection("rooms").document(roomId).get().addOnSuccessListener(doc -> {
            Room room = doc.toObject(Room.class);
            if (room != null) {
                etName.setText(room.getName());
                etTotalRows.setText(String.valueOf(room.getTotalRows()));
                etTotalCols.setText(String.valueOf(room.getTotalCols()));

                if (room.getLayout() != null) {
                    currentLayoutMatrix = new String[room.getTotalRows()][room.getTotalCols()];
                    for (int r = 0; r < room.getTotalRows(); r++) {
                        String rowString = room.getLayout().get(r);
                        for (int c = 0; c < room.getTotalCols(); c++) {
                            if (c < rowString.length()) {
                                currentLayoutMatrix[r][c] = String.valueOf(rowString.charAt(c));
                            } else {
                                currentLayoutMatrix[r][c] = "_";
                            }
                        }
                    }
                    drawGrid();
                }
            }
        });
    }

    private void generateGridFromInput() {
        String rStr = etTotalRows.getText().toString();
        String cStr = etTotalCols.getText().toString();
        if (rStr.isEmpty() || cStr.isEmpty()) return;

        int r = Integer.parseInt(rStr);
        int c = Integer.parseInt(cStr);
        
        // Preserve old layout if possible
        String[][] newMatrix = new String[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                if (currentLayoutMatrix != null && i < currentLayoutMatrix.length && j < currentLayoutMatrix[i].length) {
                    newMatrix[i][j] = currentLayoutMatrix[i][j];
                } else {
                    newMatrix[i][j] = "S"; // Default to Single Seat
                }
            }
        }
        currentLayoutMatrix = newMatrix;
        drawGrid();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void drawGrid() {
        if (currentLayoutMatrix == null) return;
        int rows = currentLayoutMatrix.length;
        if (rows == 0) return;
        int cols = currentLayoutMatrix[0].length;

        seatContainer.removeAllViews();

        int size = dpToPx(20);
        int margin = dpToPx(2);

        for (int r = 0; r < rows; r++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            rowLayout.setPadding(0, dpToPx(2), 0, dpToPx(2));

            for (int c = 0; c < cols; c++) {
                String type = currentLayoutMatrix[r][c];

                TextView seatView = new TextView(this);
                
                int width = size;
                if (type.equals(Room.SEAT_TYPE_COUPLE)) width = size * 2 + margin * 2;
                if (type.equals(Room.SEAT_TYPE_TRIPLE)) width = size * 3 + margin * 4;

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, size);
                params.setMargins(margin, margin, margin, margin);
                seatView.setLayoutParams(params);
                seatView.setGravity(Gravity.CENTER);
                seatView.setTextSize(8); // SP
                seatView.setTextColor(Color.WHITE);

                updateSeatViewAppearance(seatView, type);

                final int finalR = r;
                final int finalC = c;
                seatView.setOnClickListener(v -> {
                    String currentType = currentLayoutMatrix[finalR][finalC];
                    String nextType = getNextSeatType(currentType);
                    currentLayoutMatrix[finalR][finalC] = nextType;
                    drawGrid(); // Redraw grid to update widths safely
                });

                rowLayout.addView(seatView);
            }
            seatContainer.addView(rowLayout);
        }
    }

    private String getNextSeatType(String current) {
        switch (current) {
            case "S": return "V"; // Single -> VIP
            case "V": return "C"; // VIP -> Couple
            case "C": return "B"; // Couple -> Triple
            case "B": return "_"; // Triple -> Empty
            case "_": return "S"; // Empty -> Single
            default: return "S";
        }
    }

    private void updateSeatViewAppearance(TextView view, String type) {
        view.setText(type);
        switch (type) {
            case "S": view.setBackgroundColor(Color.parseColor("#CCCCCC")); break; // Grey
            case "V": view.setBackgroundColor(Color.parseColor("#FFD700")); break; // Yellow
            case "C": view.setBackgroundColor(Color.parseColor("#034EA2")); break; // Blue
            case "B": view.setBackgroundColor(Color.parseColor("#F58020")); break; // Orange
            case "_": 
                view.setBackgroundColor(Color.parseColor("#E0E0E0")); // Light Gray
                view.setText("");
                break;
        }
    }

    private void saveRoom() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty() || currentLayoutMatrix == null) {
            Toast.makeText(this, "Vui lòng nhập tên phòng và tạo lưới", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> layout = new ArrayList<>();
        for (int r = 0; r < currentLayoutMatrix.length; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < currentLayoutMatrix[r].length; c++) {
                sb.append(currentLayoutMatrix[r][c]);
            }
            layout.add(sb.toString());
        }

        Room room = new Room();
        room.setName(name);
        room.setTotalRows(currentLayoutMatrix.length);
        room.setTotalCols(currentLayoutMatrix[0].length);
        room.setLayout(layout);

        if (roomId != null) {
            db.collection("rooms").document(roomId).set(room)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            db.collection("rooms").add(room)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}
