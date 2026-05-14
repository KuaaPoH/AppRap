package com.example.rapapp.utils;

import android.util.Log;
import com.example.rapapp.models.Cinema;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class DataSeeder {

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