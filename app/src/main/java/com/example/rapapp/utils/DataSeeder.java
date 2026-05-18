package com.example.rapapp.utils;

import android.content.Context;
import android.util.Log;

import com.example.rapapp.models.Cinema;
import com.example.rapapp.models.Movie;
import com.example.rapapp.models.News;
import com.example.rapapp.models.Product;
import com.example.rapapp.models.Room;
import com.example.rapapp.models.Showtime;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataSeeder {

    private static final String TAG = "DataSeeder";

    public static void seedAllData(Context context) {
        try {
            String json = loadJSONFromAsset(context, "seed_data.json");
            if (json == null) return;

            JSONObject root = new JSONObject(json);
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // 1. Seed Banners
            seedBanners(db, root.optJSONArray("banners"));

            // 2. Seed Movies
            seedMovies(db, root.optJSONArray("movies"));

            // 3. Seed Cinemas
            seedCinemas(db, root.optJSONArray("cinemas"));

            // 4. Seed Products
            seedProducts(db, root.optJSONArray("products"));

            // 5. Seed News
            seedNews(db, root.optJSONArray("news"));

            // 6. Seed Locations
            seedLocations(db, root.optJSONArray("locations"));

            // 7. Seed Showtimes
            seedShowtimes(db, root.optJSONArray("showtimes"));

        } catch (Exception e) {
            Log.e(TAG, "Error seeding data", e);
        }
    }

    /**
     * Phương thức này để nạp 4 phòng chiếu mẫu cho rạp cụ thể
     * Bạn gọi phương thức này một lần trong MainActivity hoặc nơi nào đó để khởi tạo dữ liệu
     */
    public static void seedRooms(String cinemaId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Room> rooms = new ArrayList<>();

        // Phòng 1: Standard (Layout chuẩn)
        rooms.add(new Room("Phòng 1", cinemaId, 9, 12, Arrays.asList(
                "____SSSSS____", // Hàng I (Cuối)
                "SSSSSSSSSSSS", // Hàng H
                "SSSSSSSSSSSS", // Hàng G
                "SSVVVVVVVVSS", // Hàng F (VIP giữa)
                "SSVVVVVVVVSS", // Hàng E
                "SSVVVVVVVVSS", // Hàng D
                "SSSSSSSSSSSS", // Hàng C
                "SSSSSSSSSSSS", // Hàng B
                "__SSSSSSSS__"  // Hàng A (Gần màn hình)
        )));

        // Phòng 2: Standard
        rooms.add(new Room("Phòng 2", cinemaId, 9, 12, Arrays.asList(
                "CCCC__CCCC",   // Hàng I (Ghế đôi)
                "SSSSSSSSSS",   // Hàng H
                "SSSSSSSSSS",   // Hàng G
                "SVVVVVVVVS",   // Hàng F
                "SVVVVVVVVS",   // Hàng E
                "SSSSSSSSSS",   // Hàng D
                "SSSSSSSSSS",   // Hàng C
                "SSSSSSSSSS",   // Hàng B
                "__SSSSSS__"    // Hàng A
        )));

        // Phòng 3: Premium (Ít ghế hơn nhưng rộng hơn)
        rooms.add(new Room("Phòng 3", cinemaId, 7, 10, Arrays.asList(
                "BBBBBBBBBB",   // Hàng G (Ghế Ba)
                "VVVVVVVVVV",   // Hàng F
                "VVVVVVVVVV",   // Hàng E
                "VVVVVVVVVV",   // Hàng D
                "SSSSSSSSSS",   // Hàng C
                "SSSSSSSSSS",   // Hàng B
                "__SSSSSS__"    // Hàng A
        )));

        // Phòng 4: Premium
        rooms.add(new Room("Phòng 4", cinemaId, 8, 10, Arrays.asList(
                "CCCC__CCCC",   // Hàng H
                "VVVVVVVVVV",   // Hàng G
                "VVVVVVVVVV",   // Hàng F
                "VVVVVVVVVV",   // Hàng E
                "VVVVVVVVVV",   // Hàng D
                "VVVVVVVVVV",   // Hàng C
                "SSSSSSSSSS",   // Hàng B
                "__SSSSSS__"    // Hàng A
        )));

        for (Room r : rooms) {
            db.collection("rooms").add(r)
                    .addOnSuccessListener(doc -> Log.d(TAG, "Added Room: " + r.getName()))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding room", e));
        }
    }

    /**
     * Nạp thêm 2 suất chiếu cụ thể theo yêu cầu.
     * Cần gọi 1 lần trong MainActivity: DataSeeder.seedExtraShowtimes();
     */
    public static void seedExtraShowtimes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        String movieId = "test";
        String cinemaId = "test";
        String roomId = "P2FI2eO3POTAIzBCYFml";
        String city = "Hà Nội"; // Bạn có thể đổi lại nếu cần
        
        // Lấy ngày hôm nay
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateToday = sdfFull.format(calendar.getTime());

        // Suất chiếu 1 (Hôm nay, 19:00)
        Showtime s1 = new Showtime();
        s1.setMovieId(movieId);
        s1.setCinemaId(cinemaId);
        s1.setRoomId(roomId);
        s1.setCity(city);
        s1.setDate(dateToday);
        s1.setTime("19:00");
        s1.setFormat("2D LỒNG TIẾNG");
        s1.setBookedSeats(Arrays.asList("E5", "E6")); // Đặt sẵn 2 ghế VIP

        // Suất chiếu 2 (Hôm nay, 21:30)
        Showtime s2 = new Showtime();
        s2.setMovieId(movieId);
        s2.setCinemaId(cinemaId);
        s2.setRoomId(roomId);
        s2.setCity(city);
        s2.setDate(dateToday);
        s2.setTime("21:30");
        s2.setFormat("IMAX 3D");
        s2.setBookedSeats(new ArrayList<>()); // Trống

        db.collection("showtimes").add(s1)
                .addOnSuccessListener(doc -> Log.d(TAG, "Added Showtime 1: " + doc.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding Showtime 1", e));

        db.collection("showtimes").add(s2)
                .addOnSuccessListener(doc -> Log.d(TAG, "Added Showtime 2: " + doc.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding Showtime 2", e));
    }

    public static void seedCombos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<com.example.rapapp.models.Combo> comboList = new ArrayList<>();
        
        comboList.add(new com.example.rapapp.models.Combo("Combo 2 Big Extra...", "\"Nhân đôi sự sảng khoái!\" Combo gồm 1 bắp rang bơ lớn, 2 Pepsi cỡ lớn + 1 snack...", 134000, "https://firebasestorage.googleapis.com/v0/b/rapapp-9759c.appspot.com/o/popcorn.png?alt=media"));
        comboList.add(new com.example.rapapp.models.Combo("Snacking Combo 2", "1 Bắp ngọt + 2 Nước bất kỳ + 1 Món ăn nhẹ (Gà Karaage / Lạp xưởng / Khoai tây...", 169000, "https://firebasestorage.googleapis.com/v0/b/rapapp-9759c.appspot.com/o/popcorn.png?alt=media"));
        comboList.add(new com.example.rapapp.models.Combo("Combo 1 Big Extra...", "\"Thỏa mãn cơn thèm\" với 1 phần bắp rang bơ thơm ngon, 1 Pepsi mát lạnh và...", 115000, "https://firebasestorage.googleapis.com/v0/b/rapapp-9759c.appspot.com/o/popcorn.png?alt=media"));
        comboList.add(new com.example.rapapp.models.Combo("Teanema Combo 1...", "1 Bắp ngọt + 1 Trà (Trà mãng cầu / Trà quýt / Trà hibiscus / Trà chuối / Socola c...", 115000, "https://firebasestorage.googleapis.com/v0/b/rapapp-9759c.appspot.com/o/popcorn.png?alt=media"));

        for (com.example.rapapp.models.Combo c : comboList) {
            db.collection("combos").add(c)
                    .addOnSuccessListener(doc -> Log.d(TAG, "Added Combo: " + c.getName()))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding Combo", e));
        }
    }

    private static String loadJSONFromAsset(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Error loading JSON from asset", e);
            return null;
        }
    }

    private static void seedBanners(FirebaseFirestore db, JSONArray banners) throws Exception {
        if (banners == null) return;
        for (int i = 0; i < banners.length(); i++) {
            JSONObject obj = banners.getJSONObject(i);
            Map<String, Object> data = new HashMap<>();
            data.put("imageUrl", obj.getString("imageUrl"));
            db.collection("banners").add(data);
        }
    }

    private static void seedMovies(FirebaseFirestore db, JSONArray movies) throws Exception {
        if (movies == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < movies.length(); i++) {
            JSONObject obj = movies.getJSONObject(i);
            Movie m = new Movie();
            m.setTitle(obj.getString("title"));
            m.setDescription(obj.getString("description"));
            m.setPosterUrl(obj.getString("posterUrl"));
            m.setDuration(obj.getInt("duration"));
            m.setRating((float) obj.getDouble("rating"));
            m.setAgeRating(obj.getString("ageRating"));
            m.setReleaseDate(new Timestamp(sdf.parse(obj.getString("releaseDate"))));
            
            // Add optional fields if present
            if (obj.has("director")) m.setDirector(obj.getString("director"));
            if (obj.has("cast")) m.setCast(obj.getString("cast"));
            if (obj.has("trailerUrl")) m.setTrailerUrl(obj.getString("trailerUrl"));
            if (obj.has("price")) m.setPrice(obj.getInt("price"));
            
            if (obj.has("galleryUrls")) {
                JSONArray gallery = obj.getJSONArray("galleryUrls");
                List<String> list = new ArrayList<>();
                for (int j = 0; j < gallery.length(); j++) list.add(gallery.getString(j));
                m.setGalleryUrls(list);
            }

            db.collection("movies").add(m);
        }
    }

    private static void seedCinemas(FirebaseFirestore db, JSONArray cinemas) throws Exception {
        if (cinemas == null) return;
        for (int i = 0; i < cinemas.length(); i++) {
            JSONObject obj = cinemas.getJSONObject(i);
            Cinema c = new Cinema();
            c.setName(obj.getString("name"));
            c.setAddress(obj.getString("address"));
            c.setPhone(obj.getString("phone"));
            c.setCity(obj.getString("city"));
            c.setImageUrl(obj.getString("imageUrl"));
            db.collection("cinemas").add(c);
        }
    }

    private static void seedProducts(FirebaseFirestore db, JSONArray products) throws Exception {
        if (products == null) return;
        for (int i = 0; i < products.length(); i++) {
            JSONObject obj = products.getJSONObject(i);
            Product p = new Product(
                    obj.getString("name"),
                    (double) obj.getInt("price"),
                    obj.getString("imageUrl"),
                    obj.getString("category")
            );
            if (obj.has("description")) {
                p.setDescription(obj.getString("description"));
            }
            db.collection("products").add(p);
        }
    }

    private static void seedNews(FirebaseFirestore db, JSONArray news) throws Exception {
        if (news == null) return;
        for (int i = 0; i < news.length(); i++) {
            JSONObject obj = news.getJSONObject(i);
            List<News.ContentBlock> blocks = new ArrayList<>();
            JSONArray blocksArr = obj.optJSONArray("contentBlocks");
            if (blocksArr != null) {
                for (int j = 0; j < blocksArr.length(); j++) {
                    JSONObject b = blocksArr.getJSONObject(j);
                    blocks.add(new News.ContentBlock(b.getString("type"), b.getString("value")));
                }
            }
            News n = new News(
                    obj.getString("title"),
                    obj.getString("imageUrl"),
                    obj.getString("category"),
                    blocks,
                    Timestamp.now()
            );
            db.collection("news").add(n);
        }
    }

    private static void seedLocations(FirebaseFirestore db, JSONArray locations) throws Exception {
        if (locations == null) return;
        List<String> locList = new ArrayList<>();
        for (int i = 0; i < locations.length(); i++) {
            locList.add(locations.getString(i));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", locList);
        db.collection("metadata").document("locations").set(data);
    }

    private static void seedShowtimes(FirebaseFirestore db, JSONArray showtimes) throws Exception {
        if (showtimes == null) return;
        for (int i = 0; i < showtimes.length(); i++) {
            JSONObject obj = showtimes.getJSONObject(i);
            Showtime s = new Showtime();
            s.setMovieId(obj.getString("movieId"));
            s.setCinemaId(obj.getString("cinemaId"));
            s.setCity(obj.getString("city"));
            s.setDate(obj.getString("date"));
            s.setTime(obj.getString("time"));
            s.setFormat(obj.getString("format"));
            db.collection("showtimes").add(s);
        }
    }

    public void seedShowtimesCustom() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] cinemaIds = {"cinema_1", "ls0vPXYi2IJqbIx6BeqV", "m5SWtwuC0wiN6HhNnNdx", "mtaDT1DUauvtVNK9BCoe", "mtjzvwuGCxovi3qEYrs8", "nnxpL4jJrRXb1s2MWjdy", "oVfRlulMDgRTqvr9tK3g", "oZutMw2OTmkPWDQkioaI"};
        String[] cities = {"Hà Nội", "TP Hồ Chí Minh", "TP Hồ Chí Minh", "TP Hồ Chí Minh", "TP Hồ Chí Minh", "Nghệ An", "Nghệ An", "Hải Phòng"};
        String[] dates = {"2026-05-19", "2026-05-20"};
        String[] movieIds = {"movie1", "movie10", "movie6", "movie7", "movie8", "movie9", "test"};
        String[] roomIds = {"6AQN52PHZ02wJtcdA9r2", "DCg2NkYKnadI9RCCSbly", "P2FI2eO3POTAIzBCYFml", "kE8fbaz5qUQwpPIgRW6d"};
        String[] formats = {"2D VietSub", "2D Lồng Tiếng"};
        String[] times = {"16:00", "17:30", "18:45", "20:00"};

        for (int i = 0; i < cinemaIds.length; i++) {
            String cinemaId = cinemaIds[i];
            String city = cities[i];
            
            for (String date : dates) {
                for (String movieId : movieIds) {
                    // Mỗi phim sinh khoảng 2 suất chiếu ngẫu nhiên tại mỗi rạp/ngày
                    for (int j = 0; j < 2; j++) {
                        Showtime s = new Showtime();
                        s.setCinemaId(cinemaId);
                        s.setCity(city);
                        s.setDate(date);
                        s.setMovieId(movieId);
                        s.setRoomId(roomIds[(int) (Math.random() * roomIds.length)]);
                        s.setFormat(formats[(int) (Math.random() * formats.length)]);
                        s.setTime(times[(int) (Math.random() * times.length)]);
                        
                        db.collection("showtimes").add(s);
                    }
                }
            }
        }
        Log.d("DataSeeder", "Đã gửi lệnh đẩy dữ liệu suất chiếu mẫu lên Firebase");
    }
}
