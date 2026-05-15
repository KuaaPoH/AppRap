package com.example.rapapp.utils;

import android.util.Log;
import com.example.rapapp.models.Cinema;
import com.example.rapapp.models.Product;
import com.example.rapapp.models.News;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSeeder {

    public static void seedNews() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<News> newsList = new ArrayList<>();

        // Tab Bình Luận (Review)
        newsList.add(new News("[Review] The Devil Wears Prada 2: Ai Cũng Sợ Mất Việc Thôi, Kể Cả Bà Hoàng Thời Trang", "https://i.ibb.co/Vp8nZ5T/news1.jpg", "Review", "Nội dung chi tiết bài review...", new Timestamp(new Date())));
        newsList.add(new News("[Review] Deadpool & Wolverine: Màn Hội Ngộ Đỉnh Cao Của Hai Gã Lầy Lội", "https://i.ibb.co/6yL5Y4P/news2.jpg", "Review", " Deadpool và Wolverine đã thực sự cứu vãn vũ trụ Marvel...", new Timestamp(new Date())));
        newsList.add(new News("[Review] Inside Out 2: Khi Những Cảm Xúc Mới Xuất Hiện Ở Tuổi Dậy Thì", "https://i.ibb.co/L6v3n4K/news3.jpg", "Review", "Pixar đã thành công trong việc khai thác tâm lý tuổi mới lớn...", new Timestamp(new Date())));

        // Tab Tin Tức (News)
        newsList.add(new News("Siêu Bom Tấn Avatar 3 Chính Thức Công Bố Ngày Phát Hành Toàn Cầu", "https://i.ibb.co/VWVz0H1/news4.jpg", "News", "James Cameron tiết lộ những hình ảnh đầu tiên về bộ tộc lửa...", new Timestamp(new Date())));
        newsList.add(new News("Đạo Diễn Christopher Nolan Trở Lại Với Dự Án Phim Về Điệp Viên", "https://i.ibb.co/abc/news5.jpg", "News", "Sau thành công của Oppenheimer, Nolan đang chuẩn bị cho dự án mới...", new Timestamp(new Date())));
        newsList.add(new News("Vũ Trụ Điện Ảnh Marvel Công Bố Danh Sách Phim Cho Giai Đoạn 6", "https://i.ibb.co/xyz/news6.jpg", "News", "Sẽ có sự xuất hiện của nhóm Fantastic Four...", new Timestamp(new Date())));
        newsList.add(new News("LHP Cannes 2026: Những Bộ Phim Sáng Giá Cho Giải Cành Cọ Vàng", "https://i.ibb.co/123/news7.jpg", "News", "Điện ảnh thế giới hội tụ tại Cannes...", new Timestamp(new Date())));

        // Tab Nhân Vật (Character)
        newsList.add(new News("Robert Downey Jr. Chia Sẻ Về Cảm Xúc Khi Quay Lại Vũ Trụ Marvel", "https://i.ibb.co/456/news8.jpg", "Character", "Lần trở lại này với vai phản diện Doctor Doom...", new Timestamp(new Date())));
        newsList.add(new News("Hành Trình Tỏa Sáng Của Cựu Người Mẫu Emily Blunt Tại Hollywood", "https://i.ibb.co/789/news9.jpg", "Character", "Từ những vai diễn phụ đến ngôi sao hạng A...", new Timestamp(new Date())));
        newsList.add(new News("Tom Cruise Và Những Pha Hành Động Không Cần Đóng Thế Ở Tuổi 60", "https://i.ibb.co/000/news10.jpg", "Character", "Anh vẫn tiếp tục chinh phục những giới hạn mới...", new Timestamp(new Date())));

        for (News n : newsList) {
            db.collection("news").add(n)
                .addOnSuccessListener(documentReference -> Log.d("DataSeeder", "Đã thêm tin tức: " + n.getTitle()))
                .addOnFailureListener(e -> Log.e("DataSeeder", "Lỗi thêm tin tức", e));
        }
    }

    public static void seedProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Product> products = new ArrayList<>();

        products.add(new Product("Ly nước Capybara", 350000, "https://i.ibb.co/L6v3n4K/capybara.jpg", "Seasonal"));
        products.add(new Product("Combo Bắp Nước Solo", 85000, "https://www.galaxycine.vn/media/2023/10/26/combo-1_1698310323381.jpg", "Movie"));
        products.add(new Product("Combo Bắp Nước Couple", 150000, "https://www.galaxycine.vn/media/2023/10/26/combo-2_1698310323381.jpg", "Movie"));
        products.add(new Product("Bình nước Iron Man", 250000, "https://i.ibb.co/VWVz0H1/ironman.jpg", "Seasonal"));

        for (Product p : products) {
            db.collection("products").add(p)
                .addOnSuccessListener(documentReference -> Log.d("DataSeeder", "Đã thêm sản phẩm: " + p.getName()))
                .addOnFailureListener(e -> Log.e("DataSeeder", "Lỗi thêm sản phẩm", e));
        }
    }

    public static void seedCinemas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Cinema> cinemas = new ArrayList<>();

        // Danh sách dữ liệu mẫu
        cinemas.add(createCinema("Galaxy Nguyễn Du", "116 Nguyễn Du, Quận 1, TP.HCM", "1900 2224", "TP Hồ Chí Minh", "https://www.galaxycine.vn/media/2023/10/26/nguyen-du-1_1698310323381.jpg"));
        cinemas.add(createCinema("Galaxy Tân Bình", "246 Nguyễn Hồng Đào, Q.Tân Bình, TP.HCM", "1900 2224", "TP Hồ Chí Minh", "https://www.galaxycine.vn/media/2023/10/26/tan-binh-1_1698310373807.jpg"));
        cinemas.add(createCinema("Galaxy Kinh Dương Vương", "718 Bis Kinh Dương Vương, Q.6, TP.HCM", "1900 2224", "TP Hồ Chí Minh", "https://www.galaxycine.vn/media/2023/10/26/kdv-1_1698310345091.jpg"));
        cinemas.add(createCinema("Galaxy Quang Trung", "Lầu 3, TTTM CoopMart, Quang Trung, Q.Gò Vấp, TP.HCM", "1900 2224", "TP Hồ Chí Minh", "https://www.galaxycine.vn/media/2023/10/26/quang-trung-1_1698310359871.jpg"));
        cinemas.add(createCinema("Galaxy Sala", "Tầng 3, Thiso Mall Sala, TP.Thủ Đức, TP.HCM", "1900 2224", "TP Hồ Chí Minh", "https://www.galaxycine.vn/media/2023/12/20/sala-4_1703063539824.jpg"));
        cinemas.add(createCinema("Galaxy Mipec Long Biên", "Tầng 6, TTTM Mipec Long Biên, Hà Nội", "1900 2224", "Hà Nội", "https://www.galaxycine.vn/media/2023/10/26/long-bien-1_1698310398687.jpg"));
        cinemas.add(createCinema("Galaxy Tràng Thi", "Tầng 10, Tòa nhà Center Building, Tràng Thi, Hà Nội", "1900 2224", "Hà Nội", "https://scontent.fhan14-2.fna.fbcdn.net/v/t39.30808-6/305282582_530467772420042_8123281096727289547_n.jpg?_nc_cat=111&ccb=1-7&_nc_sid=cc71e4&_nc_ohc=O_n8U2u0K7cQ7kNvgH0O4J8&_nc_zt=23&_nc_ht=scontent.fhan14-2.fna&_nc_gid=AnK8X8_K3_T5D_Z8_Y_L_V&oh=00_AYBa4R_yv_H5_G_E_X_L_V_N_A_C_C_M_S_A_B_C_D_E_F_G_H_I_J_K_L_M_N&oe=664D9283"));
        cinemas.add(createCinema("Galaxy Đà Nẵng", "Tầng 3, Coop Mart Đà Nẵng", "1900 2224", "Đà Nẵng", "https://www.galaxycine.vn/media/2023/10/26/da-nang-1_1698310411623.jpg"));
        cinemas.add(createCinema("Galaxy Hải Phòng", "Tầng 7, TTTM Nguyễn Kim - Sài Gòn Mall, Hải Phòng", "1900 2224", "Hải Phòng", "https://www.galaxycine.vn/media/2023/10/26/hai-phong-1_1698310424567.jpg"));
        cinemas.add(createCinema("Galaxy Bến Tre", "Tầng 1, TTTM Sense City Bến Tre", "1900 2224", "Bến Tre", "https://www.galaxycine.vn/media/2023/10/26/ben-tre-1_1698310438091.jpg"));

        for (Cinema c : cinemas) {
            db.collection("cinemas").add(c)
                .addOnSuccessListener(documentReference -> Log.d("DataSeeder", "Đã thêm rạp: " + c.getName()))
                .addOnFailureListener(e -> Log.e("DataSeeder", "Lỗi thêm rạp", e));
        }
    }

    private static Cinema createCinema(String name, String address, String phone, String city, String imageUrl) {
        Cinema c = new Cinema();
        c.setName(name);
        c.setAddress(address);
        c.setPhone(phone);
        c.setCity(city);
        c.setImageUrl(imageUrl);
        return c;
    }
}