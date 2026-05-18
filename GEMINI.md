# Quy tắc UI/UX Dự án RapApp (Galaxy Cinema Clone)

File này chứa các nguyên tắc thiết kế giao diện (UI/UX) chuẩn của dự án. AI cần tự động tuân thủ các quy tắc này trong mọi quá trình thiết kế và cập nhật giao diện mà không cần người dùng nhắc nhở.

## 1. Hệ màu sắc (Colors)
- **Màu cam chủ đạo (Primary Orange):** `#F58020` (Được dùng cho nút bấm Đăng ký, dấu tick chọn, màu nhấn).
  - Tên trong `colors.xml`: `@color/orange_primary`
  - Nút Outline Cam: `@drawable/bg_btn_outline_orange`
  - Màu nền nút Cam: `@drawable/bg_btn_orange`
- **Màu xanh chủ đạo (Primary Blue):** `#034EA2` (Dùng cho icon mũi tên, header, text link như Hotline/Email).
  - Tên trong `colors.xml`: `@color/galaxy_blue`
- **Màu văn bản:**
  - Tiêu đề/Văn bản chính: `@color/black`
  - Gợi ý (Hint) / Mô tả phụ / Chữ mờ: `#999999` hoặc `@color/gray_text` (`#888888`).
- **Màu nền (Background):**
  - Màn hình chính: `@color/white`
  - Màu nền khối nhóm (Group background): `#EAEAEA` hoặc `#F5F5F5`
  - Đường kẻ phân cách (Divider): `@color/divider_gray` (`#EEEEEE`)
- **Màu ảnh Placeholder (khi chưa load dữ liệu):** Sử dụng `@drawable/bg_placeholder` (khối xám nhạt bo góc 8dp). Tuyệt đối không dùng màu đen hoặc hình mặc định hệ thống.

## 2. Kích thước văn bản (Typography)
Để giao diện nhỏ gọn và đồng bộ, tuyệt đối không dùng chữ quá to (>= 18sp) trừ phi là yêu cầu đặc biệt.
- **Tiêu đề trang (Header):** `16sp` và `textStyle="bold"`.
- **Tiêu đề nhóm/Văn bản nhấn mạnh:** `16sp`.
- **Văn bản thông thường (Label, Dữ liệu, Form Input):** `14sp`.
- **Nút bấm (Buttons):** `13sp` hoặc `14sp`, `textStyle="bold"`.
- **Văn bản phụ (Footer, Phụ đề, Điều khoản, Icon label):** `12sp`.

## 3. Kích thước & Khoảng cách (Spacings)
- **Padding hai bên màn hình (Screen Margin):** `16dp` hoặc `24dp` (`paddingHorizontal`).
- **Khoảng cách dọc giữa các khối (Margin Top):** Các mốc chuẩn là `8dp`, `12dp`, `16dp`, `24dp`. Không dùng các số lẻ.
- **Chiều cao tiêu chuẩn của phần tử tương tác:**
  - Header/Toolbar: `50dp` hoặc `56dp`.
  - Ô nhập liệu (EditText): `48dp`.
  - Nút bấm (Button): `36dp` (nút vừa/nhỏ) hoặc `48dp` (nút lớn/full width).
  - Row (Danh sách cài đặt/hỗ trợ): `48dp` hoặc `64dp`.
- **Khoảng trống dưới cùng (Bottom Spacing):** Phải trừ hao khoảng `70dp` - `80dp` cho màn hình chính (để không bị Bottom Navigation che) hoặc cho các nút chốt ở cuối màn hình.

## 4. Quy chuẩn Component (Widgets)
- **EditText:** Sử dụng background `@drawable/bg_edittext` (Viền xám, bo góc 4dp, nền trắng). Padding bên trong là `16dp`. Nếu có icon, icon đặt ở `drawableStart`, cách chữ `drawablePadding="12dp"`.
- **Nút Disable (Vô hiệu hóa):** Dùng `@drawable/bg_btn_disabled` (Nền xám `#EAEAEA`, bo góc 4dp). Chữ trên nút disable màu `#999999`.
- **Icon dạng nét mảnh (Outline):** Các icon ở form đăng ký, đăng nhập và cài đặt (User, Lock, Mũi tên, Bánh răng) phải là dạng Outline (đường viền mảnh, nền rỗng).
- **Trạng thái chọn (Selector):** RadioButton, CheckBox, Switch phải có màu cam khi được chọn (VD: `app:buttonTint="@color/selector_orange_tint"` hoặc `trackTint="@color/switch_track_color"`).

## 5. Quy tắc khác
- **Giao diện tràn ngang (Match Parent):** Ưu tiên bọc màn hình bằng `ScrollView`/`NestedScrollView` thay vì để kích thước tĩnh nhằm tương thích với bàn phím và nhiều kích cỡ màn hình.
- Các thuộc tính phải được tối giản, gom nhóm (VD: dùng `paddingHorizontal` thay cho `paddingLeft` và `paddingRight`).

## 6. Hiệu ứng Animation & Tương tác (Interactions)
- **Hiệu ứng Click (Bounce Effect):** Tất cả các nút bấm quan trọng (Mua ngay, Đặt hàng, Thêm vào giỏ hàng) và các Tab điều hướng phải có hiệu ứng thu nhỏ nhẹ (`scale 0.9x` hoặc `0.95x`) khi nhấn để tạo cảm giác phản hồi vật lý.
- **Hiệu ứng Giỏ hàng (Flying Cart):** Khi thêm sản phẩm vào giỏ hàng, sử dụng hiệu ứng icon bay từ vị trí nút bấm lên biểu tượng giỏ hàng trên Header. Tuyệt đối không sử dụng Toast thông báo gây gián đoạn trải nghiệm người dùng.
- **Hiệu ứng Nảy (Bounce Feedback):** Các phần tử mục tiêu (như icon giỏ hàng) phải có hiệu ứng nảy nhẹ (`scale 1.2x` rồi về `1.0x`) khi nhận được tác động từ animation bay tới.
- **Chuyển động Accordion:** Sử dụng `TransitionManager` với `AccelerateDecelerateInterpolator` cho các danh sách thu/phóng (ví dụ: Suất chiếu theo rạp) để đảm bảo chuyển động mượt mà ở cả hai đầu.