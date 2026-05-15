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
- [x] Giới hạn hiển thị 6 phim ở màn hình chính và chuyển hướng sang màn hình "Xem Thêm" mới.
- [x] Thêm Bottom Navigation chuyên nghiệp với 5 tab (Trang chủ, Rạp phim, Star Shop, Điện ảnh, Tài khoản).
- [x] **Cập nhật UI Bottom Navigation:** Chuyển sang phong cách tối giản (phẳng, mỏng 70dp), thay thế toàn bộ Icon SVG nét thanh (Outline) chuẩn thiết kế hiện đại, tắt hiệu ứng nổi viên thuốc của Material 3 để giao diện thanh thoát hơn. Xử lý chuyển tab mượt mà không độ trễ bằng kỹ thuật `Reorder to Front` và vô hiệu hóa animation chuyển cảnh.
- [x] Thêm Banner Slider trượt tự động (Sử dụng `ViewPager2` + `TabLayout` làm Dots Indicator).
- [x] Hiệu ứng lấp ló banner kế tiếp và load dữ liệu banner từ collection `banners` trên Firestore.
- [x] Xử lý sự kiện chuyển đổi giữa tab Đang chiếu và Sắp chiếu (Dựa trên `releaseDate` thời gian thực).
- [x] Thêm hiệu ứng Scale và đổi màu mượt mà khi chuyển Tab.
- [x] **Thêm tính năng Bộ chọn khu vực (Location Picker):** Sử dụng BottomSheetDialog chứa NumberPicker với đầy đủ 63 tỉnh thành Việt Nam, kết hợp hiệu ứng scale khi chạm cực kỳ chân thực.

## 📽️ GIAI ĐOẠN 2.5: DANH SÁCH PHIM CHI TIẾT
- [x] Tạo màn hình `MovieListActivity` để hiển thị toàn bộ danh sách phim.
- [x] Header chuyên nghiệp với nút Quay lại (`ic_arrow_back`) và tiêu đề trung tâm.
- [x] Tối ưu hóa tốc độ tải ảnh với Glide (Disk Cache & Thumbnail).
- [x] Đồng bộ logic lọc Tab Đang chiếu/Sắp chiếu giữa Trang chủ và Trang danh sách.

## 🏢 GIAI ĐOẠN 2.6: DANH SÁCH RẠP PHIM (MỚI BỔ SUNG)
- [x] Tạo màn hình `CinemaListActivity`.
- [x] Thiết kế giao diện `item_cinema.xml` đẹp mắt (ảnh bo góc, chữ tối ưu maxLines và ellipsize).
- [x] Bổ sung DataSeeder nạp tự động 10 rạp chiếu mẫu từ các tỉnh thành khác nhau.
- [x] Tích hợp logic **Lọc rạp theo Tỉnh thành** dựa trên bộ chọn Khu vực ở Header.

## 🛍️ GIAI ĐOẠN 3: STAR SHOP (MỚI HOÀN THÀNH)
- [x] Tạo màn hình `StarShopActivity`.
- [x] Thiết kế `item_product.xml` chuẩn mẫu: Ảnh sản phẩm, tên, giá cam, nút "Mua ngay" và "Thêm vào giỏ hàng".
- [x] Tích hợp Banner Slider và Bộ lọc danh mục (Seasonal / Movie) mượt mà.
- [x] Xử lý hiển thị giá tiền định dạng VND.
- [x] Kết nối Firestore lấy dữ liệu từ collection `products` và viết script `DataSeeder`.

## 📰 GIAI ĐOẠN 3.5: ĐIỆN ẢNH - TIN TỨC (MỚI HOÀN THÀNH)
- [x] Tạo màn hình `NewsListActivity` với thiết kế giao diện tin tức chuyên nghiệp.
- [x] Thiết kế `item_news.xml`: Ảnh lớn tràn viền, tiêu đề in đậm, nút xem thêm.
- [x] Xây dựng thanh tìm kiếm động: Có hiệu ứng trượt đẩy nút "Hủy" và lọc kết quả Realtime.
- [x] Tạo bộ lọc Tab: Bình Luận, Tin Tức, Nhân Vật.
- [x] Thêm tính năng "Scroll to Top" (Nút cuộn lên đầu trang) với hiệu ứng làm mờ thông minh.
- [x] Kết nối Firestore lấy dữ liệu từ collection `news` và viết script nạp 10 tin mẫu.

## 🎬 GIAI ĐOẠN 4: CHI TIẾT PHIM & TRAILER
- [ ] Thiết kế màn hình MovieDetailActivity.
- [ ] Hiển thị thông tin chi tiết: Nội dung, Đạo diễn, Diễn viên, Thời lượng.
- [ ] Tích hợp xem Trailer (YouTube API hoặc VideoView).
- [ ] Nút "Mua vé" để chuyển sang luồng đặt vé.

## 🎟️ GIAI ĐOẠN 5: LUỒNG ĐẶT VÉ (QUAN TRỌNG NHẤT)
- [ ] Màn hình chọn Suất chiếu (Chọn ngày -> Chọn rạp -> Chọn giờ).
- [ ] Màn hình chọn Ghế (Seat Map):
    - Hiển thị sơ đồ ghế theo phòng chiếu.
    - Xử lý chọn/hủy ghế, tính tổng tiền thời gian thực.
    - Phân loại ghế (Thường, VIP).
- [ ] Màn hình chọn Bắp nước (Combo Concession).

## 💳 GIAI ĐOẠN 6: XÁC NHẬN & THANH TOÁN
- [ ] Màn hình tổng quan đơn hàng (Review Order).
- [ ] Giao diện thanh toán giả lập (Nhập thẻ/Ví điện tử).
- [ ] Xuất vé điện tử (Mã QR/Barcode) và lưu vào Database.

## 👤 GIAI ĐOẠN 7: CÁ NHÂN HÓA & HOÀN THIỆN
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
*   `name` (String): Tên rạp (Ví dụ: "Galaxy Nguyễn Du").
*   `address` (String): Địa chỉ.
*   `imageUrl` (String): Link ảnh đại diện rạp.
*   `phone` (String): Số điện thoại liên hệ.
*   `city` (String): Thuộc Tỉnh/Thành phố (Phục vụ lọc khu vực).

**4. Collection `products` (Danh sách sản phẩm Star Shop):**
*   `name` (String): Tên sản phẩm.
*   `price` (Number): Giá tiền (Ví dụ: 350000).
*   `imageUrl` (String): Link ảnh sản phẩm.
*   `category` (String): Danh mục ("Seasonal" hoặc "Movie").

**5. Collection `news` (Danh sách Tin tức Điện ảnh):**
*   `title` (String): Tiêu đề bài viết.
*   `imageUrl` (String): Link ảnh bìa bài viết.
*   `category` (String): Danh mục ("Review", "News", "Character").
*   `content` (String): Nội dung chi tiết.
*   `publishedDate` (Timestamp): Ngày đăng bài.

**6. Collection `showtimes` (Lịch chiếu/Suất chiếu):**
*   `movieId` (String): Tham chiếu đến Document ID trong bảng `movies`.
*   `cinemaId` (String): Tham chiếu đến Document ID trong bảng `cinemas`.
*   `roomName` (String): Tên phòng chiếu (Ví dụ: "Phòng 1").
*   `price` (Number): Giá vé (Ví dụ: 100000).
*   `startTime` (String/Timestamp): Thời gian bắt đầu chiếu.
*   `bookedSeats` (Array of Strings): Mảng chứa các ghế đã được đặt (Ví dụ: `["A1", "A2", "D5"]`).

---
**CẬP NHẬT TIẾN ĐỘ HIỆN TẠI:**
- **Tiến độ:** Đã hoàn thiện xong toàn bộ Giai đoạn 2 (Trang chủ), Giai đoạn 2.5 (Danh sách phim), Giai đoạn 2.6 (Danh sách rạp), **Giai đoạn 3 (Star Shop)** và **Giai đoạn 3.5 (Điện ảnh)**. Đặc biệt, đã thực hiện **tái cấu trúc toàn bộ ứng dụng sang kiến trúc Single Activity + Fragments**, giải quyết triệt để lỗi nhảy icon Bottom Navigation và tối ưu hóa hiệu năng ứng dụng.
- **Trạng thái:** 4 tab chính (Trang chủ, Rạp phim, Star Shop, Điện ảnh) đã hoạt động hoàn hảo, mượt mà với UI/UX đồng bộ, chuẩn Flat Design. Dữ liệu các tab đã được nạp Realtime từ Firebase. Sẵn sàng bước vào **Giai đoạn 4 (Chi tiết phim)** và xây dựng luồng Đặt vé cốt lõi.