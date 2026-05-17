package com.example.rapapp.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.models.Cinema;
import com.example.rapapp.models.DateModel;
import com.example.rapapp.models.Showtime;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowtimeFragment extends Fragment {
    private String movieId;
    private RecyclerView rvDates, rvShowtimes;
    private TextView tvFullDate, tvFilterCity, tvFilterCinema;
    private FirebaseFirestore db;
    
    private List<DateModel> dateList = new ArrayList<>();
    private List<Showtime> filteredShowtimes = new ArrayList<>();
    private List<String> availableDates = new ArrayList<>();
    
    private String selectedCity = "TP Hồ Chí Minh";
    private String selectedCinemaId = "";
    private String selectedCinemaName = "Tất cả rạp";
    private String selectedDate = "";
    private Map<String, Boolean> cinemaExpandMap = new HashMap<>();

    private final String[] locations = {
        "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn", "Bạc Liêu", "Bắc Ninh", "Bến Tre", "Bình Định", "Bình Dương", "Bình Phước", "Bình Thuận", "Cà Mau", "Cần Thơ", "Cao Bằng", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu", "Lâm Đồng", "Lạng Sơn", "Lào Cai", "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang", "TP Hồ Chí Minh", "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
    };

    public static ShowtimeFragment newInstance(String movieId) {
        ShowtimeFragment fragment = new ShowtimeFragment();
        Bundle args = new Bundle();
        args.putString("movieId", movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getString("movieId");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_showtime, container, false);
        rvDates = view.findViewById(R.id.rvDates);
        rvShowtimes = view.findViewById(R.id.rvShowtimes);
        tvFullDate = view.findViewById(R.id.tvFullDate);
        tvFilterCity = view.findViewById(R.id.tvFilterCity);
        tvFilterCinema = view.findViewById(R.id.tvFilterCinema);

        tvFilterCity.setText(selectedCity);
        tvFilterCinema.setText(selectedCinemaName);

        setupDateSelector();
        setupFilters();

        return view;
    }

    private void setupDateSelector() {
        dateList.clear();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd / MM", Locale.getDefault());
        SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE dd, 'tháng' M yyyy", new Locale("vi", "VN"));

        db.collection("showtimes").whereEqualTo("movieId", movieId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            availableDates.clear();
            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                availableDates.add(doc.getString("date"));
            }

            for (int i = 0; i < 7; i++) {
                String day = (i == 0) ? "Hôm nay" : sdfDay.format(calendar.getTime());
                String dateStr = sdfDate.format(calendar.getTime());
                String full = sdfFull.format(calendar.getTime());
                String display = sdfDisplay.format(calendar.getTime());
                if (i == 0) {
                    selectedDate = full;
                    tvFullDate.setText(display);
                }
                dateList.add(new DateModel(day, dateStr, full, display, i == 0));
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            rvDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvDates.setAdapter(new DateAdapter());
            loadShowtimes();
        });
    }

    private void setupFilters() {
        tvFilterCity.setOnClickListener(v -> showLocationPicker());
        tvFilterCinema.setOnClickListener(v -> showCinemaPicker());
    }

    private void showLocationPicker() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        dialog.setContentView(v);

        NumberPicker picker = v.findViewById(R.id.locationPicker);
        picker.setMinValue(0);
        picker.setMaxValue(locations.length - 1);
        picker.setDisplayedValues(locations);
        
        for (int i = 0; i < locations.length; i++) {
            if (locations[i].equals(selectedCity)) {
                picker.setValue(i);
                break;
            }
        }

        v.findViewById(R.id.btnConfirm).setOnClickListener(view -> {
            selectedCity = locations[picker.getValue()];
            tvFilterCity.setText(selectedCity);
            selectedCinemaId = "";
            selectedCinemaName = "Tất cả rạp";
            tvFilterCinema.setText(selectedCinemaName);
            loadShowtimes();
            dialog.dismiss();
        });
        v.findViewById(R.id.btnClose).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void showCinemaPicker() {
        db.collection("cinemas").whereEqualTo("city", selectedCity).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Cinema> cinemas = queryDocumentSnapshots.toObjects(Cinema.class);
            for (int i = 0; i < cinemas.size(); i++) {
                cinemas.get(i).setId(queryDocumentSnapshots.getDocuments().get(i).getId());
            }
            
            if (cinemas.isEmpty()) {
                Toast.makeText(getContext(), "Không có rạp nào tại " + selectedCity, Toast.LENGTH_SHORT).show();
                return;
            }

            String[] cinemaNames = new String[cinemas.size() + 1];
            cinemaNames[0] = "Tất cả rạp";
            for (int i = 0; i < cinemas.size(); i++) cinemaNames[i+1] = cinemas.get(i).getName();

            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            View v = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
            dialog.setContentView(v);
            ((TextView)v.findViewById(R.id.tvTitle)).setText("Chọn Rạp");

            NumberPicker picker = v.findViewById(R.id.locationPicker);
            picker.setMinValue(0);
            picker.setMaxValue(cinemaNames.length - 1);
            picker.setDisplayedValues(cinemaNames);

            v.findViewById(R.id.btnConfirm).setOnClickListener(view -> {
                int pos = picker.getValue();
                selectedCinemaName = cinemaNames[pos];
                selectedCinemaId = (pos == 0) ? "" : cinemas.get(pos - 1).getId();
                tvFilterCinema.setText(selectedCinemaName);
                loadShowtimes();
                dialog.dismiss();
            });
            v.findViewById(R.id.btnClose).setOnClickListener(view -> dialog.dismiss());
            dialog.show();
        });
    }

    private void loadShowtimes() {
        db.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("city", selectedCity)
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    filteredShowtimes.clear();
                    List<com.google.firebase.firestore.DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < docs.size(); i++) {
                        Showtime s = docs.get(i).toObject(Showtime.class);
                        if (s != null) {
                            s.setId(docs.get(i).getId());
                            if (selectedCinemaId.isEmpty() || s.getCinemaId().equals(selectedCinemaId)) {
                                filteredShowtimes.add(s);
                            }
                        }
                    }
                    displayShowtimes();
                });
    }

    // Lớp model nội bộ để DiffUtil có thể so sánh chính xác
    private static class DisplayItem {
        static final int TYPE_CINEMA = 0;
        static final int TYPE_FORMAT = 1;
        
        int type;
        String id; // ID rạp hoặc Format Title
        String name; // Tên rạp
        String formatTitle; // Dành cho format
        List<Showtime> times;
        boolean isLast;
        boolean isExpanded;

        DisplayItem(int type, String id) { this.type = type; this.id = id; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DisplayItem that = (DisplayItem) o;
            return type == that.type && 
                   isLast == that.isLast &&
                   isExpanded == that.isExpanded &&
                   java.util.Objects.equals(id, that.id) &&
                   java.util.Objects.equals(name, that.name) &&
                   java.util.Objects.equals(formatTitle, that.formatTitle) &&
                   java.util.Objects.equals(times, that.times);
        }
    }

    private List<DisplayItem> flatList = new ArrayList<>();
    private ShowtimeFlatAdapter showtimeAdapter;

    private void displayShowtimes() {
        db.collection("cinemas").whereEqualTo("city", selectedCity).get().addOnSuccessListener(cinemaDocs -> {
            Map<String, String> cinemaNameMap = new HashMap<>();
            for (com.google.firebase.firestore.DocumentSnapshot doc : cinemaDocs) {
                cinemaNameMap.put(doc.getId(), doc.getString("name"));
            }

            Map<String, Map<String, List<Showtime>>> nestedMap = new HashMap<>();
            for (Showtime s : filteredShowtimes) {
                if (!nestedMap.containsKey(s.getCinemaId())) {
                    nestedMap.put(s.getCinemaId(), new HashMap<>());
                    if (!cinemaExpandMap.containsKey(s.getCinemaId())) cinemaExpandMap.put(s.getCinemaId(), true);
                }
                Map<String, List<Showtime>> formatMap = nestedMap.get(s.getCinemaId());
                if (!formatMap.containsKey(s.getFormat())) {
                    formatMap.put(s.getFormat(), new ArrayList<>());
                }
                formatMap.get(s.getFormat()).add(s);
            }

            List<DisplayItem> newList = new ArrayList<>();
            List<String> cinemaIds = new ArrayList<>(nestedMap.keySet());
            for (String cid : cinemaIds) {
                boolean isExpanded = cinemaExpandMap.getOrDefault(cid, true);
                DisplayItem header = new DisplayItem(DisplayItem.TYPE_CINEMA, cid);
                header.name = cinemaNameMap.getOrDefault(cid, "Rạp không xác định");
                header.isExpanded = isExpanded;
                newList.add(header);

                if (isExpanded) {
                    Map<String, List<Showtime>> formats = nestedMap.get(cid);
                    List<String> formatKeys = new ArrayList<>(formats.keySet());
                    for (int i = 0; i < formatKeys.size(); i++) {
                        String f = formatKeys.get(i);
                        DisplayItem formatItem = new DisplayItem(DisplayItem.TYPE_FORMAT, cid + "_" + f);
                        formatItem.formatTitle = f;
                        formatItem.name = header.name; // Lưu tên rạp để truyền đi
                        formatItem.times = formats.get(f);
                        formatItem.isLast = (i == formatKeys.size() - 1);
                        newList.add(formatItem);
                    }
                }
            }

            // Sử dụng DiffUtil để tính toán sự thay đổi chính xác giữa danh sách cũ và mới
            androidx.recyclerview.widget.DiffUtil.DiffResult diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(new androidx.recyclerview.widget.DiffUtil.Callback() {
                @Override
                public int getOldListSize() { return flatList.size(); }
                @Override
                public int getNewListSize() { return newList.size(); }
                @Override
                public boolean areItemsTheSame(int oldPos, int newPos) {
                    return flatList.get(oldPos).id.equals(newList.get(newPos).id);
                }
                @Override
                public boolean areContentsTheSame(int oldPos, int newPos) {
                    return flatList.get(oldPos).equals(newList.get(newPos));
                }
            });

            // Tinh chỉnh hiệu ứng: Dùng AccelerateDecelerate để pha bắt đầu và kết thúc đều mượt mà
            android.transition.TransitionSet set = new android.transition.TransitionSet()
                    .addTransition(new android.transition.ChangeBounds())
                    .setDuration(500) // 500ms là tỷ lệ vàng cho các chuyển động accordion dài
                    .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            
            android.transition.TransitionManager.beginDelayedTransition(rvShowtimes, set);

            flatList.clear();
            flatList.addAll(newList);

            if (showtimeAdapter == null) {
                showtimeAdapter = new ShowtimeFlatAdapter();
                rvShowtimes.setLayoutManager(new LinearLayoutManager(getContext()));
                rvShowtimes.setAdapter(showtimeAdapter);
            } else {
                diffResult.dispatchUpdatesTo(showtimeAdapter);
            }
        });
    }

    private class ShowtimeFlatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public int getItemViewType(int position) {
            return flatList.get(position).type;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == DisplayItem.TYPE_CINEMA) return new CinemaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cinema_header, parent, false));
            return new GroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime_group, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            DisplayItem item = flatList.get(position);
            if (holder instanceof CinemaViewHolder) {
                CinemaViewHolder vh = (CinemaViewHolder) holder;
                vh.tvCinemaName.setText(item.name);
                vh.ivArrow.animate().rotation(item.isExpanded ? 90 : 0).setDuration(250).start();
                
                vh.itemView.setOnClickListener(v -> {
                    v.animate().scaleX(0.98f).scaleY(0.98f).setDuration(100).withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        cinemaExpandMap.put(item.id, !item.isExpanded);
                        displayShowtimes();
                    }).start();
                });
            } else if (holder instanceof GroupViewHolder) {
                GroupViewHolder h = (GroupViewHolder) holder;
                h.tvFormatTitle.setText(item.formatTitle);
                h.divider.setVisibility(item.isLast ? View.GONE : View.VISIBLE);
                
                h.rvTimeGrid.setLayoutManager(new GridLayoutManager(getContext(), 4));
                h.rvTimeGrid.setAdapter(new RecyclerView.Adapter<TimeViewHolder>() {
                    @NonNull
                    @Override
                    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new TimeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false));
                    }
                    @Override
                    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
                        Showtime s = item.times.get(position);
                        holder.tvTime.setText(s.getTime());
                        holder.itemView.setOnClickListener(v -> {
                            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                                android.content.Intent intent = new android.content.Intent(getContext(), com.example.rapapp.SeatSelectionActivity.class);
                                intent.putExtra("movieId", s.getMovieId());
                                intent.putExtra("showtimeId", s.getId());
                                intent.putExtra("cinemaId", s.getCinemaId());
                                intent.putExtra("cinemaName", item.name);
                                intent.putExtra("format", s.getFormat());
                                intent.putExtra("time", s.getTime());
                                
                                String movieTitle = "";
                                if (getActivity() != null && getActivity().getIntent() != null) {
                                    movieTitle = getActivity().getIntent().getStringExtra("movieTitle");
                                }
                                intent.putExtra("movieTitle", movieTitle);
                                
                                startActivity(intent);
                            }).start();
                        });
                    }
                    @Override
                    public int getItemCount() { return item.times.size(); }
                });
            }
        }
        @Override
        public int getItemCount() { return flatList.size(); }
    }

    private class DateAdapter extends RecyclerView.Adapter<DateViewHolder> {
        @NonNull
        @Override
        public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
            DateModel date = dateList.get(position);
            holder.tvDayOfWeek.setText(date.getDayOfWeek());
            holder.tvDateMonth.setText(date.getDateMonth());
            
            boolean hasShowtime = availableDates.contains(date.getFullDate());
            
            // Xử lý mờ và khóa click triệt để
            holder.itemView.setAlpha(hasShowtime ? 1.0f : 0.3f);
            holder.itemView.setEnabled(hasShowtime);

            int color = date.isSelected() ? Color.WHITE : Color.BLACK;
            int bgColor = date.isSelected() ? Color.parseColor("#034EA2") : Color.WHITE;
            holder.tvDayOfWeek.setTextColor(color);
            holder.tvDateMonth.setTextColor(color);
            holder.cardDate.setCardBackgroundColor(ColorStateList.valueOf(bgColor));
            holder.cardDate.setStrokeColor(date.isSelected() ? Color.TRANSPARENT : Color.parseColor("#EEEEEE"));

            if (hasShowtime) {
                holder.itemView.setOnClickListener(v -> {
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        for (DateModel d : dateList) d.setSelected(false);
                        date.setSelected(true);
                        selectedDate = date.getFullDate();
                        tvFullDate.setText(date.getDisplayFullDate());
                        notifyDataSetChanged();
                        loadShowtimes();
                    }).start();
                });
            } else {
                holder.itemView.setOnClickListener(null); // Vô hiệu hóa listener
            }
        }
        @Override
        public int getItemCount() { return dateList.size(); }
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvDateMonth;
        MaterialCardView cardDate;
        DateViewHolder(View v) {
            super(v);
            tvDayOfWeek = v.findViewById(R.id.tvDayOfWeek);
            tvDateMonth = v.findViewById(R.id.tvDateMonth);
            cardDate = v.findViewById(R.id.cardDate);
        }
    }

    static class CinemaViewHolder extends RecyclerView.ViewHolder {
        TextView tvCinemaName;
        ImageView ivArrow;
        CinemaViewHolder(View v) {
            super(v);
            tvCinemaName = v.findViewById(R.id.tvCinemaName);
            ivArrow = v.findViewById(R.id.ivArrow);
        }
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvFormatTitle;
        RecyclerView rvTimeGrid;
        View divider;
        GroupViewHolder(View v) {
            super(v);
            tvFormatTitle = v.findViewById(R.id.tvFormatTitle);
            rvTimeGrid = v.findViewById(R.id.rvTimeGrid);
            divider = v.findViewById(R.id.divider);
        }
    }

    static class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TimeViewHolder(View v) {
            super(v);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }
}
