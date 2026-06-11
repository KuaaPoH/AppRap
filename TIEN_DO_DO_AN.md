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
- [x] **Quản lý Khu vực (Location) động:** Danh sách 63 tỉnh thành được lưu trên Cloud (Collection `metadata`) và tải về động ở mọi màn hình (`HomeFragment`, `CinemaFragment`, `StarShopFragment`, `MovieListActivity`).

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
- [x] **Hệ thống Giỏ hàng (Shopping Cart):** Khởi tạo `CartManager` (Singleton). Icon giỏ hàng với Badge cập nhật realtime, thiết kế Outline mảnh chuẩn Modern UI.
- [x] **Hiệu ứng Xe đẩy bay (Flying Cart Animation):** Thay thế thông báo Toast bằng hiệu ứng icon sản phẩm "bay" từ nút bấm lên giỏ hàng ở Header khi nhấn "Thêm vào giỏ hàng". Thêm hiệu ứng Bounce (nảy) cho giỏ hàng mục tiêu khi nhận được sản phẩm.
- [x] **Màn hình Chi tiết Sản phẩm (`ProductDetailActivity`):** Hiển thị mô tả động, bộ chọn số lượng (+/-), tính tổng tiền động và Điều khoản căn lề Justify chuyên nghiệp. Tích hợp hiệu ứng bay và thu nhỏ nút bấm khi tương tác.
- [x] **Trang Chi tiết Khuyến mãi (`PromoDetailActivity`):** Dữ liệu 100% động từ Firestore Content Blocks. Hỗ trợ bôi đậm HTML (`<b>`), tự động phân cấp cỡ chữ 14sp/12sp cực kỳ tinh tế.
- [x] **Chuẩn hóa Typography:** Đồng bộ toàn bộ cỡ chữ nội dung về **12sp**, giảm khoảng cách (spacing) giúp giao diện gọn gàng và hiện đại hơn.
- [x] **Hệ thống Thông báo (Toast):** Tiêu chuẩn hóa toàn bộ thông báo ứng dụng hiển thị ở **phía trên cùng màn hình** thông qua `ToastUtils`.
- [x] **Hệ thống nội dung đa phương tiện (Content Blocks):** Cho phép bài viết chèn nhiều ảnh và đoạn văn bản đan xen, dàn trang linh hoạt.
- [x] **Màn hình Chi tiết Tin tức (`NewsDetailActivity`):** Hiển thị đầy đủ nội dung căn lề đều (Justify), cỡ chữ 12sp.
- [x] **Màn hình Thanh toán Star Shop (`Shop
    - Giao diện hiện đại: Nền xám nhạt, các khối nội dung trắng bo góc 12dp, đổ bóng nhẹ.
    - **Tóm tắt đơn hàng:** Danh sách sản phẩm rút gọn với giá đỏ/CheckoutActivity`):** cam nổi bật bên phải.
    - **Phương thức thanh toán:** Hệ thống chọn ZaloPay, MoMo, OnePay với hiệu ứng viền cam và dấu tích cam tùy chỉnh sắc nét.
    - **Khuyến mãi thông minh:** Hỗ trợ nhập Voucher và sử dụng điểm Stars (hiển thị số điểm hiện có) với hiệu ứng accordion mượt mà.
    - **Nơi nhận hàng chuyên nghiệp:** Bộ chọn Thành phố/Rạp dạng BottomSheet (1/3 màn hình) với dữ liệu 100% từ Cloud Firestore. Tự động hiển thị địa chỉ chi tiết của rạp đã chọn.
    - **Thông báo thành công & Điều hướng:** Thiết kế Success Dialog rộng 340dp chuyên nghiệp. Hỗ trợ quay về Trang chủ hoặc quay lại đúng Tab Star Shop để tiếp tục mua sắm (tự động làm sạch giỏ hàng).
    - **Chuẩn hóa thiết kế:** Đồng bộ cỡ chữ 12sp toàn màn hình, nút bấm compact (40dp height), căn chỉnh khoảng cách pixel-perfect theo hình mẫu.

## 👤 GIAI ĐOẠN 3.6: TÀI KHOẢN & HỆ THỐNG
- [x] **Hoàn thiện Tab Tài khoản (Profile):** Mascot, ưu đãi Stars, Quà tặng, Đặc quyền và danh sách liên kết hỗ trợ.
- [x] **Xây dựng luồng Đăng nhập/Đăng ký:** DatePicker ngày sinh, tích hợp **Firebase Authentication** (Email/Password).
- [x] **Quản lý Thông tin Người dùng (Profile Edit):** Cho phép người dùng cập nhật Họ tên, SĐT, Ngày sinh.
- [x] **Hệ thống Avatar Base64:** Nén và mã hóa ảnh đại diện thành chuỗi Base64 lưu trực tiếp vào Firestore, giúp hiển thị ảnh Avatar cực nhanh mà không tốn phí lưu trữ Firebase Storage.
- [x] **Hệ thống Cài đặt:** LanguageActivity hỗ trợ chuyển đổi Tiếng Việt/English.
- [x] **Chuẩn hóa tương tác (Interaction UI):** Thêm hiệu ứng **Bounce & Scale** (thu nhỏ nhẹ khi nhấn và nảy lên khi thả) cho toàn bộ Bottom Navigation, các nút bấm chính (Đặt hàng, Mua ngay) và các mục trong danh sách.
- [x] **Chuẩn hóa UI/UX:** File `GEMINI.md` quy định chặt chẽ toàn dự án. Xóa bỏ hoàn toàn "Hardcode" dữ liệu tĩnh trong code Java.

## 🎬 GIAI ĐOẠN 4: CHI TIẾT PHIM & SUẤT CHIẾU (HOÀN THÀNH)
- [x] Tạo màn hình `MovieDetailActivity` và tích hợp `ViewPager2` với `TabLayout` (3 tab: Suất Chiếu, Thông Tin, Tin Tức).
- [x] **Hoàn thiện Tab Suất Chiếu:** 
    - Xây dựng danh sách rạp và giờ chiếu dạng accordion (thu/phóng) có hiệu ứng trượt mượt mà (sử dụng `DiffUtil` và `TransitionManager`).
    - **Lọc suất chiếu thông minh:** Tự động đồng bộ vị trí từ Trang chủ (ví dụ: Nghệ An, TP Hồ Chí Minh) vào màn hình suất chiếu. Hỗ trợ chế độ "Toàn quốc" để hiển thị tất cả suất chiếu trên cả nước.
    - **Tự động ẩn suất chiếu quá hạn:** Ẩn các suất chiếu đã chiếu quá 15 phút so với thời gian thực tế.
    - **Đồng bộ hóa Cloud:** Danh sách khu vực được tải động từ collection `metadata` thay vì hardcode.
- [x] **Sắp xếp danh sách phim thông minh:** 
    - Tab Đang chiếu: Sắp xếp theo ngày phát hành mới nhất (Giảm dần).
    - Tab Sắp chiếu: Sắp xếp theo ngày khởi chiếu gần nhất (Tăng dần).
- [x] **Hoàn thiện Tab Thông Tin:** Thiết kế Header poster nổi, nội dung xem thêm/thu gọn, danh sách Diễn viên/Đạo diễn và Gallery.
- [x] Xử lý chuyển hướng sang luồng đặt vé khi click vào một Suất chiếu.

## 🎟️ GIAI ĐOẠN 5: LUỒNG ĐẶT VÉ (HOÀN THÀNH)
- [x] **Màn hình Chọn Ghế (Seat Map):** Tải sơ đồ ghế động từ Firestore, tự động vẽ lưới ghế, tính toán giá vé theo loại ghế (Đơn, Đôi, Ba, VIP). Tích hợp Dropdown chọn nhanh giờ chiếu, hiệu ứng Pop animation mượt mà và căn giữa sơ đồ thông minh. Bắt buộc đăng nhập trước khi chọn ghế.
- [x] **Màn hình Chọn Bắp Nước (Combo):** Tải danh sách Combo từ Firestore, thiết kế CardView nhỏ gọn (Compact UI), tích hợp bộ đếm (+/-) và tự động tính tổng tiền.
- [x] **Lưu trữ Giỏ hàng (Persistent Cart):** Sử dụng SharedPreferences và Gson để lưu trữ giỏ hàng Star Shop cục bộ, đảm bảo không mất dữ liệu khi thoát app.

## 💳 GIAI ĐOẠN 6: XÁC NHẬN & THANH TOÁN (HOÀN THÀNH)
- [x] **Màn hình Giao dịch (Checkout):** Hiển thị hóa đơn chi tiết với hiệu ứng "Vé bị cắt viền" (Ticket Cutout) độc đáo, các đường kẻ đứt phân cách và phân loại giá Ghế/Combo rõ ràng.
- [x] **Giao diện thanh toán:** Chức năng chọn phương thức thanh toán (OnePay, MoMo, ZaloPay, ShopeePay) với thiết kế hiện đại, tinh tế. Bắt buộc đăng nhập trước khi thanh toán Star Shop.
- [x] **Đồng bộ Dữ liệu Real-time:** Tích hợp cập nhật trạng thái ghế (`bookedSeats`) lên Firebase ngay sau khi thanh toán thành công để khóa ghế cho các người dùng khác.
- [x] **Tích điểm (Stars):** Tự động tính toán (1 Star/20.000đ) và cộng dồn điểm vào tài khoản người dùng sau khi thanh toán đơn vé hoặc Star Shop.
- [x] **Lịch sử Giao dịch:** Hiển thị danh sách vé đã mua với thiết kế thẻ vé (khuyết 2 bên). Phân biệt vé phim và hóa đơn Star Shop.
- [x] **Chi tiết Giao dịch (Virtual Ticket):** Bóc tách chi tiết từng mã vé ghế, từng combo bắp nước kèm giá tiền và số Stars tương ứng. Giao diện chuẩn vé điện tử có dải phân cách răng cưa.
- [x] **Thông báo thành công:** Thiết kế Custom Success Dialog chuyên nghiệp và điều hướng luồng về trang chủ.
- [ ] **GIAI ĐOẠN CUỐI:** Xuất vé điện tử (Mã QR/Barcode).

## 🛠️ GIAI ĐOẠN 7: HỆ THỐNG QUẢN TRỊ (ADMIN PANEL - HOÀN THÀNH)
- [x] **Kiến trúc Modern Package:** Tách biệt hoàn toàn mã nguồn Admin vào package `com.example.rapapp.admin` với cấu trúc `activities` và `adapters` riêng biệt.
- [x] **Giao diện Bento Dashboard:** Thiết kế Dashboard theo xu hướng **Bento Box** hiện đại. Thống kê Real-time số lượng và doanh thu.
- [x] **Bộ lọc Thống kê Kép (Dual Filter):** Lọc theo Rạp và Lọc theo Ngày bằng DatePicker.
- [x] **Quản lý Toàn diện (9 Module CRUD):** 
    - Phim, Rạp, Suất chiếu, Sản phẩm Star Shop, Phòng chiếu, Tin tức, Banner, **Người dùng (Users)**, **Combo Bắp Nước**.
- [x] **Ràng buộc Suất chiếu (Anti-Overlap):** Thuật toán tự động ngăn chặn tạo/sửa suất chiếu bị trùng giờ tại cùng một phòng (tính toán dựa trên thời lượng phim + 15p dọn dẹp).
- [x] **Quản lý Người dùng & Phân quyền:** Admin có thể thay đổi thông tin User (Tên, SĐT), thăng cấp lên Admin, điều chỉnh điểm Stars, và kiểm tra toàn bộ lịch sử mua sắm của bất kỳ tài khoản nào.
- [x] **Seat Map Editor Trực quan:** Xây dựng trình chỉnh sửa sơ đồ ghế (Grid Editor) tỉ lệ nhỏ (20dp). Hỗ trợ chạm để đổi trạng thái ghế (Đơn, Đôi, Ba, VIP, Trống).
- [x] **Format Toolbar (Rich Text Engine):** Tích hợp thanh công cụ chèn nhanh định dạng cho module Tin tức và Banner, giúp Admin soạn thảo nội dung chuyên nghiệp.
- [x] **Action Bar Pattern:** Đồng bộ giao diện danh sách quản lý: Thanh tìm kiếm (Search Bar) bên trái và Nút Thêm mới (Add Button) bên phải.
- [x] **Dropdown & Automation:** Tự động hóa bộ chọn Thành phố (63 tỉnh thành) và bộ chọn Phòng chiếu động theo Rạp đã chọn.

---
## 🗄️ CẤU TRÚC CƠ SỞ DỮ LIỆU (FIREBASE FIRESTORE SCHEMA)

**1. Collection `movies` (Danh sách phim):**
*   `title` (String): Tên phim.
*   `description` (String): Nội dung tóm tắt.
*   `posterUrl` (String): Link ảnh bìa.
*   `duration` (Number/Int): Thời lượng (Phút).
*   `rating` (Number/Double): Điểm đánh giá (Ví dụ: 8.5).
*   `ageRating` (String): Phân loại độ tuổi (Ví dụ: "T18", "K").
*   `releaseDate` (Timestamp): Ngày phát hành.
*   `trailerUrl` (String): Link Youtube trailer.
*   `director` (String): Tên đạo diễn.
*   `cast` (String): Tên các diễn viên.
*   `price` (Number/Double): Giá vé cơ bản.
*   `galleryUrls` (Array of Strings): Danh sách link ảnh thư viện phim.

**2. Collection `banners` (Danh sách Banner quảng cáo - Độc lập):**
*   `imageUrl` (String): Link ảnh banner.
*   `contentBlocks` (Array of Maps): Dữ liệu nội dung đa phương tiện hiển thị khi click (Gồm `type` và `value`).

**3. Collection `cinemas` (Danh sách rạp chiếu):**
*   `name` (String): Tên rạp.
*   `address` (String): Địa chỉ.
*   `imageUrl` (String): Link ảnh đại diện rạp.
*   `phone` (String): Số điện thoại liên hệ.
*   `city` (String): Tỉnh/Thành phố (63 tỉnh thành).

**4. Collection `products` (Danh sách sản phẩm Star Shop):**
*   `name` (String): Tên sản phẩm.
*   `price` (Number/Double): Giá tiền.
*   `imageUrl` (String): Link ảnh sản phẩm.
*   `category` (String): Danh mục ("Seasonal" hoặc "Movie").
*   `description` (String): Mô tả chi tiết sản phẩm.

**5. Collection `news` (Danh sách Tin tức Điện ảnh):**
*   `title` (String): Tiêu đề bài viết.
*   `imageUrl` (String): Link ảnh bìa.
*   `category` (String): Danh mục ("Review", "News", "Character").
*   `contentBlocks` (Array of Maps): `type` ("text"/"image"/"bullet") và `value`.
*   `publishedDate` (Timestamp): Ngày đăng bài.

**6. Collection `rooms` (Danh sách Phòng chiếu):**
*   `cinemaId` (String): ID rạp chiếu chứa phòng này (Tùy chọn).
*   `name` (String): Tên phòng (VD: "Phòng 1").
*   `totalRows` (Number/Int): Tổng số hàng ghế.
*   `totalCols` (Number/Int): Tổng số cột ghế.
*   `layout` (Array of Strings): Bản đồ ghế theo hàng (Ký tự `S`, `V`, `C`, `B`, `_`).

**7. Collection `showtimes` (Danh sách suất chiếu):**
*   `movieId` (String): ID phim.
*   `cinemaId` (String): ID rạp chiếu.
*   `roomId` (String): ID phòng chiếu (Liên kết động với `rooms`).
*   `city` (String): Tỉnh/Thành phố của rạp.
*   `date` (String): Ngày chiếu ("yyyy-MM-dd").
*   `time` (String): Giờ chiếu ("HH:mm").
*   `format` (String): Định dạng (VD: "2D LỒNG TIẾNG").
*   `bookedSeats` (Array of Strings): Danh sách mã ghế đã bán (VD: `["E5", "E6"]`).

**8. Collection `users` (Thông tin tài khoản - MỚI):**
*   `uid` (String): Mã ID định danh từ Firebase Auth.
*   `name` (String): Họ và tên.
*   `email` (String): Địa chỉ Email.
*   `phone` (String): Số điện thoại liên hệ.
*   `birthday` (String): Ngày sinh.
*   `role` (String): Quyền hạn ("user" hoặc "admin").
*   `stars` (Number/Int): Tổng điểm tích lũy.
*   `avatarUrl` (String): Chuỗi mã hóa ảnh đại diện Base64 (hoặc URL ảnh).

**9. Collection `combos` (Danh sách Combo bắp nước - MỚI):**
*   `name` (String): Tên combo.
*   `price` (Number/Double): Giá tiền.
*   `imageUrl` (String): Link ảnh minh họa.
*   `description` (String): Nội dung combo.

**10. Collection `bookings` (Thống kê đơn hàng - CẬP NHẬT):**
*   `userId` (String): ID của khách hàng thực hiện mua (Liên kết `users`).
*   `type` (String): Loại giao dịch ("movie_ticket" hoặc "star_shop").
*   `mainTitle` (String): Tên hiển thị tóm tắt (Tên phim hoặc Tóm tắt món hàng).
*   `mainImage` (String): Link ảnh đại diện đơn hàng.
*   `totalPrice` (Number/Double): Tổng số tiền thanh toán thành công.
*   `timestamp` (Date/ServerTimestamp): Thời gian thanh toán.
*   *(Chi tiết vé)*: `cinemaId`, `cinemaName`, `movieId`, `showtimeId`, `seats` (Mảng ghế), `seatTotalPrice`.
*   *(Chi tiết combo/shop)*: `combos` (Mảng tên combo), `comboPrices` (Mảng giá combo), `items` (Mảng Map chứa chi tiết giỏ hàng Star Shop).

**11. Collection `metadata` (Thông tin hệ thống):**
*   Document `locations`: Chứa mảng `list` gồm danh sách 63 tỉnh thành Việt Nam.

---
**CẬP NHẬT TIẾN ĐỘ HIỆN TẠI:**
- **Tiến độ:** Đã hoàn thiện 100% hệ thống Quản trị (Admin Panel) với đầy đủ tính năng CRUD nâng cao, thống kê doanh thu và bộ công cụ soạn thảo trực quan.
- **Trạng thái:** Toàn bộ hệ thống quản lý đã đồng bộ Real-time với Cloud Firestore. Dự án đã sẵn sàng cho bước cuối cùng.

