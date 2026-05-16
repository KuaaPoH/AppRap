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

## 📽️ GIAI ĐOẠN 2.5: DANH SÁCH PHIM CHI TIẾT
- [x] Tạo màn hình `MovieListActivity` để hiển thị toàn bộ danh sách phim.
- [x] Header chuyên nghiệp với nút Quay lại (`ic_arrow_back`) và tiêu đề trung tâm.
- [x] Tối ưu hóa tốc độ tải ảnh với Glide (Disk Cache & Thumbnail).
- [x] Đồng bộ logic lọc Tab Đang chiếu/Sắp chiếu giữa Trang chủ và Trang danh sách.

## 🏢 GIAI ĐOẠN 2.6: DANH SÁCH RẠP PHIM (MỚI BỔ SUNG)
- [x] Tạo màn hình `CinemaListActivity`.
- [x] Thiết kế giao diện `item_cinema.xml` đẹp mắt (ảnh bo góc, chữ tối ưu maxLines và ellipsize).
- [x] Tích hợp logic **Lọc rạp theo Tỉnh thành** dựa trên bộ chọn Khu vực tải từ Cloud.

## 🛍️ GIAI ĐOẠN 3: STAR SHOP (MỚI HOÀN THÀNH)
- [x] Tạo màn hình `StarShopActivity`.
- [x] Thiết kế `item_product.xml` chuẩn mẫu: Ảnh sản phẩm, tên, giá cam, nút "Mua ngay" và "Thêm vào giỏ hàng".
- [x] Tích hợp Banner Slider và Bộ lọc danh mục (Seasonal / Movie) mượt mà.
- [x] Xử lý hiển thị giá tiền định dạng VND.
- [x] Kết nối Firestore lấy dữ liệu từ collection `products`.

## 📰 GIAI ĐOẠN 3.5: ĐIỆN ẢNH - TIN TỨC & CHI TIẾT (MỚI HOÀN THÀNH)
- [x] Tạo màn hình `NewsListActivity` với thiết kế giao diện tin tức chuyên nghiệp.
- [x] Thiết kế `item_news.xml`: Ảnh lớn tràn viền, tiêu đề in đậm, nút xem thêm.
- [x] Xây dựng thanh tìm kiếm động và bộ lọc Tab: Bình Luận, Tin Tức, Nhân Vật.
- [x] **Hệ thống nội dung đa phương tiện (Content Blocks):** Cho phép bài viết chèn nhiều ảnh và đoạn văn bản đan xen bất kỳ vị trí nào, tự động dàn trang linh hoạt.
- [x] **Màn hình Chi tiết Tin tức (`NewsDetailActivity`):**
    - Hiển thị đầy đủ Tiêu đề, Ngày đăng, Chuyên mục.
    - Nội dung bài viết căn lề đều 2 bên (Justify), cỡ chữ 14sp chuyên nghiệp.
    - Tự động hiển thị các khối nội dung và hình ảnh từ Database.
    - Header có nút Chia sẻ và nút "Mua vé ngay!" cố định dưới chân trang.

## 👤 GIAI ĐOẠN 3.6: TÀI KHOẢN & HỆ THỐNG (MỚI HOÀN THÀNH)
- [x] **Hoàn thiện Tab Tài khoản (Profile):** Mascot, ưu đãi Stars, Quà tặng, Đặc quyền và danh sách liên kết hỗ trợ.
- [x] **Xây dựng luồng Đăng nhập/Đăng ký:** DatePicker ngày sinh, UI cam chủ đạo.
- [x] **Hệ thống Cài đặt:** Quản lý Vị trí và Thông báo. LanguageActivity hỗ trợ chuyển đổi Tiếng Việt/English.
- [x] **Chuẩn hóa UI/UX:** File `GEMINI.md` quy định chặt chẽ toàn dự án. Xóa bỏ hoàn toàn "Hardcode" dữ liệu tĩnh trong code Java.

## 🎬 GIAI ĐOẠN 4: CHI TIẾT PHIM & TRAILER
- [ ] Thiết kế màn hình MovieDetailActivity.
- [ ] Hiển thị thông tin chi tiết: Nội dung, Đạo diễn, Diễn viên, Thời lượng.
- [ ] Tích hợp xem Trailer (YouTube API hoặc VideoView).
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

**5. Collection `news` (Danh sách Tin tức Điện ảnh):**
*   `title` (String): Tiêu đề bài viết.
*   `imageUrl` (String): Link ảnh bìa bài viết.
*   `category` (String): Danh mục ("Review", "News", "Character").
*   `contentBlocks` (Array of Maps): 
    - `type` (String): "text" hoặc "image".
    - `value` (String): Nội dung chữ hoặc URL ảnh.
*   `publishedDate` (Timestamp): Ngày đăng bài.

**6. Collection `metadata` (Thông tin hệ thống):**
*   Document `locations`: Chứa mảng `list` gồm danh sách 63 tỉnh thành Việt Nam.

---
**CẬP NHẬT TIẾN ĐỘ HIỆN TẠI:**
- **Tiến độ:** Đã hoàn thành Giai đoạn 1 đến 3.6. Vừa thực hiện cuộc cách mạng **"Xóa sạch Hardcode"**, chuyển đổi toàn bộ dữ liệu quản lý sang JSON và Cloud Firestore metadata. Hoàn thành hệ thống **Tin tức đa phương tiện** với giao diện chi tiết cực kỳ chuyên nghiệp.
- **Trạng thái:** Sẵn sàng bước vào **Giai đoạn 4: Chi tiết phim**. Ứng dụng hiện tại có khả năng mở rộng dữ liệu cực mạnh mà không cần sửa code.