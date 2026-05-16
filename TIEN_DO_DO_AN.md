# LỘ TRÌNH THỰC HIỆN ĐỒ ÁN: APP RẠP CHIẾU PHIM (GALAXY CINEMA)

## 📌 GIAI ĐOẠN 1: THIẾT LẬP NỀN TẢNG & CƠ SỞ DỮ LIỆU
- [x] Khởi tạo dự án Android Studio (Java).
- [x] **Đã chuyển đổi từ SQLite sang Firebase Cloud Firestore**.
- [x] Tích hợp thành công Firebase (Analytics, BOM, Firestore).
- [x] **Hệ thống nạp dữ liệu (Seeding) chuyên nghiệp:** Chuyển đổi từ nạp dữ liệu cứng sang nạp dữ liệu động từ file `seed_data.json` nằm trong thư mục Assets. Giúp quản lý toàn bộ dữ liệu (Phim, Rạp, Sản phẩm, Tin tức) tập trung tại một chỗ mà không cần sửa code Java.
- [x] Chỉnh sửa các lớp Model (`Movie`, `News`, `Product`...) để ánh xạ dữ liệu trực tiếp từ Firestore.
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
- [x] **Quản lý Khu vực (Location) động:** Xóa bỏ danh sách tỉnh thành cứng (Hardcoded array). Danh sách 63 tỉnh thành hiện được lưu trên Cloud (Collection `metadata`) và tải về động ở mọi màn hình (`HomeFragment`, `CinemaFragment`, `StarShopFragment`, `MovieListActivity`).

## 📽️ GIAI ĐOẠN 2.5: DANH SÁCH PHIM & RẠP PHIM
- [x] Tạo màn hình `MovieListActivity` để hiển thị toàn bộ danh sách phim.
- [x] Header chuyên nghiệp với nút Quay lại (`ic_arrow_back`) và tiêu đề trung tâm.
- [x] Tối ưu hóa tốc độ tải ảnh với Glide (Disk Cache & Thumbnail).
- [x] Đồng bộ logic lọc Tab Đang chiếu/Sắp chiếu giữa Trang chủ và Trang danh sách.
- [x] Thiết kế giao diện `item_cinema.xml` đẹp mắt (ảnh bo góc, chữ tối ưu maxLines và ellipsize).
- [x] Tích hợp logic **Lọc rạp theo Tỉnh thành** dựa trên bộ chọn Khu vực tải từ Cloud.

## 🛍️ GIAI ĐOẠN 3: STAR SHOP & TIN TỨC ĐIỆN ẢNH
- [x] Tạo màn hình `StarShopActivity` với thiết kế `item_product.xml` chuẩn mẫu: Ảnh sản phẩm, tên, giá cam, nút "Mua ngay".
- [x] Tích hợp Banner Slider và Bộ lọc danh mục (Seasonal / Movie) mượt mà.
- [x] Xử lý hiển thị giá tiền định dạng VND.
- [x] **Hệ thống nội dung đa phương tiện (Content Blocks):** Cho phép bài viết chèn nhiều ảnh và đoạn văn bản đan xen bất kỳ vị trí nào, tự động dàn trang linh hoạt.
- [x] **Màn hình Chi tiết Tin tức (`NewsDetailActivity`):** Hiển thị đầy đủ Tiêu đề, Ngày đăng, Chuyên mục. Nội dung căn lề đều (Justify), cỡ chữ 14sp chuyên nghiệp. Có nút Chia sẻ và nút "Mua vé ngay!" cố định dưới chân trang.

## 👤 GIAI ĐOẠN 3.6: TÀI KHOẢN & HỆ THỐNG
- [x] **Hoàn thiện Tab Tài khoản (Profile):** Mascot, ưu đãi Stars, Quà tặng, Đặc quyền và danh sách liên kết hỗ trợ.
- [x] **Xây dựng luồng Đăng nhập/Đăng ký:** DatePicker ngày sinh, UI cam chủ đạo.
- [x] **Hệ thống Cài đặt:** Quản lý Vị trí và Thông báo. LanguageActivity hỗ trợ chuyển đổi Tiếng Việt/English.
- [x] **Chuẩn hóa UI/UX:** File `GEMINI.md` quy định chặt chẽ toàn dự án. Xóa bỏ hoàn toàn "Hardcode" dữ liệu tĩnh trong code Java.

## 🎬 GIAI ĐOẠN 4: CHI TIẾT PHIM & SUẤT CHIẾU (ĐANG THỰC HIỆN)
- [x] Tạo màn hình `MovieDetailActivity` và tích hợp `ViewPager2` với `TabLayout` (3 tab: Suất Chiếu, Thông Tin, Tin Tức).
- [x] **Hoàn thiện Tab Suất Chiếu:** Xây dựng danh sách rạp và giờ chiếu dạng accordion (thu/phóng) có hiệu ứng trượt mượt mà (sử dụng `DiffUtil` và `TransitionManager`).
- [x] **Hoàn thiện Tab Thông Tin:** 
    - Thiết kế phần Header (Ảnh cover, Poster nổi, Điểm đánh giá, Ngày giờ chiếu).
    - Hiển thị Nội dung phim với chức năng Xem thêm/Thu gọn, xử lý chính xác ký tự xuống dòng (`\n`).
    - Tạo danh sách ngang cho Diễn viên và Đạo diễn.
    - Tạo danh sách ngang cho Thư viện ảnh (Gallery).
- [x] Xử lý trailer bằng Toast thông báo để giữ UI ổn định.
- [ ] Nút "Mua vé" để chuyển sang luồng đặt vé.

## 🎟️ GIAI ĐOẠN 5: LUỒNG ĐẶT VÉ (QUAN TRỌNG NHẤT)
- [ ] Màn hình chọn Suất chiếu (Chọn ngày -> Chọn rạp -> Chọn giờ).
- [ ] Màn hình chọn Ghế (Seat Map).
- [ ] Màn hình chọn Bắp nước (Combo Concession).

## 💳 GIAI ĐOẠN 6: XÁC NHẬN & THANH TOÁN
- [ ] Màn hình tổng quan đơn hàng (Review Order).
- [ ] Giao diện thanh toán giả lập.
- [ ] Xuất vé điện tử (Mã QR/Barcode).

---
## 🗄️ CẤU TRÚC CƠ SỞ DỮ LIỆU (FIREBASE FIRESTORE SCHEMA)
*(Dùng để gửi cho AI khi cần hỗ trợ code)*

**1. Collection `movies` (Danh sách phim):**
*   `title` (String): Tên phim.
*   `description` (String): Nội dung tóm tắt.
*   `posterUrl` (String): Link ảnh bìa.
*   `galleryUrls` (Array of Strings): Danh sách link ảnh thư viện phim.
*   `duration` (Number): Thời lượng (Phút).
*   `rating` (Number): Điểm đánh giá (Ví dụ: 8.5).
*   `ageRating` (String): Phân loại độ tuổi (Ví dụ: "T18", "K").
*   `releaseDate` (Timestamp): Ngày phát hành.
*   `trailerUrl` (String): Link Youtube trailer.
*   `director` (String): Tên đạo diễn.
*   `cast` (String): Tên các diễn viên.
*   `price` (Number): Giá vé cơ bản.

**2. Collection `banners` (Danh sách Banner quảng cáo):**
*   `imageUrl` (String): Link ảnh banner.
*   `newsId` (String): ID bài báo liên quan.

**3. Collection `cinemas` (Danh sách rạp chiếu):**
*   `name` (String): Tên rạp.
*   `address` (String): Địa chỉ.
*   `imageUrl` (String): Link ảnh đại diện rạp.
*   `phone` (String): Số điện thoại liên hệ.
*   `city` (String): Tỉnh/Thành phố (Phục vụ lọc khu vực).

**4. Collection `products` (Danh sách sản phẩm Star Shop):**
*   `name` (String): Tên sản phẩm.
*   `price` (Number): Giá tiền.
*   `imageUrl` (String): Link ảnh sản phẩm.
*   `category` (String): Danh mục ("Seasonal" hoặc "Movie").

**5. Collection `news` (Danh sách Tin tức Điện ảnh):**
*   `title` (String): Tiêu đề bài viết.
*   `imageUrl` (String): Link ảnh bìa.
*   `category` (String): Danh mục ("Review", "News", "Character").
*   `contentBlocks` (Array of Maps): `type` ("text"/"image") và `value`.
*   `publishedDate` (Timestamp): Ngày đăng bài.

**6. Collection `showtimes` (Danh sách suất chiếu):**
*   `movieId` (String): ID phim.
*   `cinemaId` (String): ID rạp chiếu.
*   `city` (String): Tỉnh/Thành phố của rạp.
*   `date` (String): Ngày chiếu ("yyyy-MM-dd").
*   `time` (String): Giờ chiếu ("HH:mm").
*   `format` (String): Định dạng và phòng (VD: "2D LỒNG TIẾNG").

**7. Collection `metadata` (Thông tin hệ thống):**
*   Document `locations`: Chứa mảng `list` gồm danh sách 63 tỉnh thành Việt Nam.

---
**CẬP NHẬT TIẾN ĐỘ HIỆN TẠI:**
- **Tiến độ:** Đã hoàn thành Giai đoạn 1 đến 3.6. Vừa thực hiện cuộc cách mạng **"Xóa sạch Hardcode"**, chuyển đổi toàn bộ dữ liệu sang JSON và Cloud Firestore. Hoàn thiện hệ thống **Tin tức đa phương tiện** và đạt bước tiến lớn trong **Giai đoạn 4 (Chi tiết phim)** với UI/UX Tab Suất Chiếu và Thông Tin cực kỳ chuyên nghiệp.
- **Trạng thái:** Ứng dụng hiện có khả năng mở rộng dữ liệu cực mạnh mà không cần sửa code. Sẵn sàng bước vào **Giai đoạn 5 (Luồng Đặt vé - Chọn ghế)**.
