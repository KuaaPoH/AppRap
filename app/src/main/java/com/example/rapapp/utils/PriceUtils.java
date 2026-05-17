package com.example.rapapp.utils;

import com.example.rapapp.models.Room;

public class PriceUtils {
    
    // Các mức phụ phí (Bạn có thể điều chỉnh ở đây)
    public static final int SURCHARGE_VIP = 10000;
    public static final int SURCHARGE_COUPLE = 20000; // Phụ phí thêm cho ghế đôi
    public static final int SURCHARGE_TRIPLE = 40000; // Phụ phí thêm cho ghế ba

    /**
     * Tính giá cho một ghế cụ thể
     * @param basePrice Giá gốc của phim (lấy từ Movie model)
     * @param seatType Loại ghế (S, V, C, B)
     * @return Tổng giá của ghế đó
     */
    public static double calculateSeatPrice(double basePrice, String seatType) {
        switch (seatType) {
            case Room.SEAT_TYPE_VIP:
                return basePrice + SURCHARGE_VIP;
            case Room.SEAT_TYPE_COUPLE:
                // Ghế đôi thường tính bằng 2 ghế đơn cộng phụ phí
                return (basePrice * 2) + SURCHARGE_COUPLE;
            case Room.SEAT_TYPE_TRIPLE:
                // Ghế ba tính bằng 3 ghế đơn cộng phụ phí
                return (basePrice * 3) + SURCHARGE_TRIPLE;
            case Room.SEAT_TYPE_SINGLE:
            default:
                return basePrice;
        }
    }

    /**
     * Định dạng tiền tệ sang VND (VD: 100,000đ)
     */
    public static String formatCurrency(double amount) {
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");
        return formatter.format(amount) + "đ";
    }
}
