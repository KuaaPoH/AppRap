# PHÂN CHIA NHIỆM VỤ ÔN TẬP ĐỒ ÁN: APP RẠP CHIẾU PHIM

Tài liệu này phân chia chi tiết các file code và logic cho 4 thành viên trong nhóm để chuẩn bị cho buổi bảo vệ đồ án.

---

## 🟢 NGƯỜI 1: TRÁI TIM CỦA APP (BOOKING & LOGIC)
**Vai trò:** Nắm giữ các thuật toán khó nhất, xử lý dữ liệu thời gian thực và luồng đặt vé chính.

### 📁 Các file code cần học:
*   **Màn hình User:**
    *   `app/src/main/java/com/example/rapapp/MovieDetailActivity.java` (Tab Suất chiếu & Logic lọc).
    *   `app/src/main/java/com/example/rapapp/SeatSelectionActivity.java` (Vẽ sơ đồ ghế, chọn ghế).
    *   `app/src/main/java/com/example/rapapp/CheckoutActivity.java` (Thanh toán vé phim, tạo hóa đơn).
*   **Màn hình Admin:**
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminShowtimeListActivity.java`
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminShowtimeFormActivity.java` (Thuật toán chống trùng lịch).
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminRoomListActivity.java`
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminRoomFormActivity.java` (Sửa sơ đồ ghế).
*   **Models tương ứng:**
    *   `app/src/main/java/com/example/rapapp/models/Showtime.java`
    *   `app/src/main/java/com/example/rapapp/models/Room.java`
    *   `app/src/main/java/com/example/rapapp/models/Booking.java`

### 💡 Kiến thức trọng tâm:
1.  Thuật toán vẽ Grid ghế dựa trên số hàng/cột.
2.  Cách dùng `bookedSeats` để đánh dấu ghế đã bán.
3.  Logic tính thời gian kết thúc phim (`duration + 15p`) để tránh trùng lịch chiếu.

---

## 🔵 NGƯỜI 2: THƯƠNG MẠI & NỘI DUNG (SHOP & NEWS)
**Vai trò:** Quản lý hệ thống bán lẻ bắp nước, giỏ hàng và tin tức đa phương tiện.

### 📁 Các file code cần học:
*   **Màn hình User:**
    *   `app/src/main/java/com/example/rapapp/fragments/StarShopFragment.java` (Danh sách sản phẩm).
    *   `app/src/main/java/com/example/rapapp/ProductDetailActivity.java` (Chi tiết & Thêm vào giỏ).
    *   `app/src/main/java/com/example/rapapp/CartActivity.java` (Quản lý giỏ hàng cục bộ).
    *   `app/src/main/java/com/example/rapapp/ShopCheckoutActivity.java` (Thanh toán Star Shop).
    *   `app/src/main/java/com/example/rapapp/NewsDetailActivity.java` (Hiển thị tin tức).
*   **Màn hình Admin:**
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminProductListActivity.java`
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminNewsListActivity.java`
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminComboListActivity.java`
*   **Models tương ứng:**
    *   `app/src/main/java/com/example/rapapp/models/Product.java`
    *   `app/src/main/java/com/example/rapapp/models/News.java`
    *   `app/src/main/java/com/example/rapapp/models/Combo.java`

### 💡 Kiến thức trọng tâm:
1.  **Singleton Pattern:** Cách hoạt động của `CartManager.java`.
2.  **Content Blocks:** Cách duyệt danh sách các Map để hiển thị ảnh/chữ xen kẽ trong tin tức.
3.  Hiệu ứng Animation "bay" vào giỏ hàng.

---

## 🟡 NGƯỜI 3: GIAO DIỆN & TÌM KIẾM (UI & DISCOVERY)
**Vai trò:** Quản lý bộ mặt ứng dụng, trang chủ, rạp phim và trải nghiệm người dùng.

### 📁 Các file code cần học:
*   **Màn hình User:**
    *   `app/src/main/java/com/example/rapapp/fragments/HomeFragment.java` (Banner, Tab Phim).
    *   `app/src/main/java/com/example/rapapp/fragments/CinemaFragment.java` (Danh sách rạp).
    *   `app/src/main/java/com/example/rapapp/MovieListActivity.java` (Xem tất cả phim, Search).
    *   `app/src/main/java/com/example/rapapp/MainActivity.java` (Bottom Navigation).
*   **Màn hình Admin:**
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminMovieListActivity.java`
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminCinemaListActivity.java`
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminBannerListActivity.java`
*   **Models tương ứng:**
    *   `app/src/main/java/com/example/rapapp/models/Movie.java`
    *   `app/src/main/java/com/example/rapapp/models/Cinema.java`
    *   `app/src/main/java/com/example/rapapp/models/Banner.java`

### 💡 Kiến thức trọng tâm:
1.  **Glide Library:** Tối ưu hóa việc tải ảnh Poster.
2.  **Location Metadata:** Cách lấy danh sách 63 tỉnh thành từ Firestore Document `locations`.
3.  Cách sử dụng `ViewPager2` kết hợp `TabLayout` cho Banner.

---

## 🔴 NGƯỜI 4: TÀI KHOẢN & HỆ THỐNG (AUTH & ANALYTICS)
**Vai trò:** Quản lý bảo mật, phân quyền, người dùng và các con số kinh doanh.

### 📁 Các file code cần học:
*   **Màn hình User:**
    *   `app/src/main/java/com/example/rapapp/LoginActivity.java` (Đăng nhập).
    *   `app/src/main/java/com/example/rapapp/RegisterActivity.java` (Đăng ký).
    *   `app/src/main/java/com/example/rapapp/fragments/ProfileFragment.java` (Điểm Stars, đặc quyền).
    *   `app/src/main/java/com/example/rapapp/TransactionHistoryActivity.java` (Lịch sử giao dịch).
*   **Màn hình Admin:**
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminDashboardActivity.java` (Thống kê doanh thu).
    *   `app/src/main/java/com/example/rapapp/admin/activities/AdminUserListActivity.java` (Quản lý User).
*   **Models tương ứng:**
    *   `app/src/main/java/com/example/rapapp/models/User.java`

### 💡 Kiến thức trọng tâm:
1.  **Firebase Authentication:** Luồng đăng ký tài khoản mới và lưu thông tin vào Firestore.
2.  **Firestore Aggregation:** Cách dùng `Query` để tính tổng doanh thu cho Dashboard.
3.  **Base64 Encoding:** Cách nén ảnh đại diện để tiết kiệm dung lượng lưu trữ.

---

## 🛠️ CÁC THƯ MỤC DÙNG CHUNG (TẤT CẢ PHẢI BIẾT)
*   **Layouts:** `app/src/main/res/layout/` (Giao diện XML).
*   **Adapters:** `app/src/main/java/com/example/rapapp/adapters/` (Cầu nối dữ liệu).
*   **Utils:** `app/src/main/java/com/example/rapapp/utils/` (Thông báo Toast, Format tiền).
*   **Firebase:** `google-services.json` (Cấu hình kết nối Firebase).
