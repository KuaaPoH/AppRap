package com.example.rapapp.utils;

import android.content.Context;
import android.util.Log;

import com.example.rapapp.models.Cinema;
import com.example.rapapp.models.Movie;
import com.example.rapapp.models.News;
import com.example.rapapp.models.Product;
import com.example.rapapp.models.Showtime;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
}
