# LỘ TRÌNH THỰC HIỆN ĐỒ ÁN: APP RẠP CHIẾU PHIM (GALAXY CINEMA)

## 📌 GIAI ĐOẠN 1: THIẾT LẬP NỀN TẢNG & CƠ SỞ DỮ LIỆU
- [x] Khởi tạo dự án Android Studio (Java).
- [x] **Đã chuyển đổi từ SQLite sang Firebase Cloud Firestore**.
- [x] Tích hợp thành công Firebase (Analytics, BOM, Firestore).
- [x] Viết script tự động nạp dữ liệu mẫu lên Firebase (`DataSeeder`).
- [x] Chỉnh sửa các lớp Model (`Movie`) để ánh xạ dữ liệu trực tiếp từ Firestore.
- [x] Thêm thư viện hỗ trợ (Glide cho hình ảnh, Material Design).

## 🖼️ GIAI ĐOẠN 2: GIAO DIỆN TRANG CHỦ (UI/UX)
- [x] Thiết kế Layout Item phim (`item_movie.xml`) theo phong cách Galaxy: Bo góc, thêm nhãn Độ tuổi (T18, K) màu cam và Điểm đánh giá (Rating) đè lên poster.
- [x] Hiển thị danh sách phim dạng Grid (2 cột) lấy dữ liệu thật (Realtime) từ Firebase.
- [x] **Giới hạn hiển thị 6 phim ở màn hình chính và chuyển hướng sang màn hình "Xem Thêm" mới.**
- [x] Thêm Bottom Navigation chuyên nghiệp với 5 tab (Trang chủ, Rạp, Mua vé, Tin tức, Tài khoản), hỗ trợ hiệu ứng đổi màu khi chọn. Đã tùy chỉnh lại icon kính 3D/tòa nhà cho tab "Rạp".
- [x] Thêm Banner Slider trượt tự động (Sử dụng `ViewPager2` + `TabLayout` làm Dots Indicator).
- [x] **Hiệu ứng lấp ló banner kế tiếp và load dữ liệu banner từ collection `banners` trên Firestore (Mới cập nhật).**
- [x] **Xử lý sự kiện chuyển đổi giữa tab Đang chiếu và Sắp chiếu (Dựa trên `releaseDate` thời gian thực).**
- [x] **Thêm hiệu ứng Scale và đổi màu mượt mà khi chuyển Tab.**

## 📽️ GIAI ĐOẠN 2.5: DANH SÁCH PHIM CHI TIẾT (MỚI BỔ SUNG)
- [x] Tạo màn hình `MovieListActivity` để hiển thị toàn bộ danh sách phim.
- [x] Header chuyên nghiệp với nút Quay lại (`ic_arrow_back`) và tiêu đề trung tâm.
- [x] Tối ưu hóa tốc độ tải ảnh với Glide (Disk Cache & Thumbnail).
- [x] Đồng bộ logic lọc Tab Đang chiếu/Sắp chiếu giữa Trang chủ và Trang danh sách.

## 🎬 GIAI ĐOẠN 3: CHI TIẾT PHIM & TRAILER
- [ ] Thiết kế màn hình MovieDetailActivity.
- [ ] Hiển thị thông tin chi tiết: Nội dung, Đạo diễn, Diễn viên, Thời lượng.
- [ ] Tích hợp xem Trailer (YouTube API hoặc VideoView).
- [ ] Nút "Mua vé" để chuyển sang luồng đặt vé.

## 🎟️ GIAI ĐOẠN 4: LUỒNG ĐẶT VÉ (QUAN TRỌNG NHẤT)
- [ ] Màn hình chọn Suất chiếu (Chọn ngày -> Chọn rạp -> Chọn giờ).
- [ ] Màn hình chọn Ghế (Seat Map):
    - Hiển thị sơ đồ ghế theo phòng chiếu.
    - Xử lý chọn/hủy ghế, tính tổng tiền thời gian thực.
    - Phân loại ghế (Thường, VIP).
- [ ] Màn hình chọn Bắp nước (Combo Concession).

## 💳 GIAI ĐOẠN 5: XÁC NHẬN & THANH TOÁN
- [ ] Màn hình tổng quan đơn hàng (Review Order).
- [ ] Giao diện thanh toán giả lập (Nhập thẻ/Ví điện tử).
- [ ] Xuất vé điện tử (Mã QR/Barcode) và lưu vào Database.

## 👤 GIAI ĐOẠN 6: CÁ NHÂN HÓA & HOÀN THIỆN
- [ ] Đăng ký/Đăng nhập (Sử dụng Firebase Authentication).
- [ ] Xem lịch sử vé đã đặt.
- [ ] Tích điểm thành viên.
- [ ] Tối ưu hóa hiệu năng và kiểm lỗi (Bug fix).

---
## 🗄️ CẤU TRÚC CƠ SỞ DỮ LIỆU (FIREBASE FIRESTORE SCHEMA)
*(Dùng để gửi cho AI (ChatGPT, Gemini, Claude...) khi cần hỗ trợ code)*

**1. Collection `movies` (Danh sách phim):**
*   `title` (String): Tên phim.
*   `description` (String): Nội dung tóm tắt.
*   `posterUrl` (String): Link ảnh bìa.
*   `duration` (Number): Thời lượng (Phút).
*   `rating` (Number): Điểm đánh giá (Ví dụ: 8.5).
*   `ageRating` (String): Phân loại độ tuổi (Ví dụ: "T18", "K").
*   `releaseDate` (Timestamp): Ngày phát hành (Dùng để sắp xếp và lọc Tab).

**2. Collection `banners` (Danh sách Banner quảng cáo):**
*   `imageUrl` (String): Link ảnh banner.
*   `newsId` (String): ID bài báo liên quan (Dành cho giai đoạn sau).

**3. Collection `cinemas` (Danh sách rạp chiếu):**
*   `name` (String): Tên rạp (Ví dụ: "RapApp Nguyễn Trãi").
*   `address` (String): Địa chỉ.

**4. Collection `showtimes` (Lịch chiếu/Suất chiếu):**
*   `movieId` (String): Tham chiếu đến Document ID trong bảng `movies`.
*   `cinemaId` (String): Tham chiếu đến Document ID trong bảng `cinemas`.
*   `roomName` (String): Tên phòng chiếu (Ví dụ: "Phòng 1").
*   `price` (Number): Giá vé (Ví dụ: 100000).
*   `startTime` (String/Timestamp): Thời gian bắt đầu chiếu.
*   `bookedSeats` (Array of Strings): Mảng chứa các ghế đã được đặt (Ví dụ: `["A1", "A2", "D5"]`).

---
**CẬP NHẬT TIẾN ĐỘ HIỆN TẠI:**
- **Tiến độ:** Hoàn thành 100% Giai đoạn 2 và bổ sung thêm màn hình Danh sách phim chi tiết (`MovieListActivity`). App đã có logic phân loại phim thông minh dựa trên ngày phát hành (`releaseDate`). UI/UX được tinh chỉnh mượt mà với hiệu ứng scale tab và tối ưu hóa nạp ảnh Glide.
- **Trạng thái:** App hoạt động ổn định, dữ liệu Firestore đã được chuẩn hóa với Model Java (`toObject`). Sẵn sàng chuyển sang Giai đoạn 3 (Chi tiết phim).