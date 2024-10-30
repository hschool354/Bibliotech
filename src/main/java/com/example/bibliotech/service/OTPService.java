package com.example.bibliotech.service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

public class OTPService {
    public static String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public static void saveOTP(Connection conn, int userId, String otp) throws SQLException {
        String sql = "INSERT INTO OTP (user_id, otp_code) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, otp);
            stmt.executeUpdate();
        }
    }

    public static boolean verifyOTP(Connection conn, int userId, String otp) throws SQLException {
        String sql = "SELECT otp_code, expiration_time FROM OTP WHERE user_id = ? ORDER BY otp_id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedOTP = rs.getString("otp_code");
                Timestamp expirationTime = rs.getTimestamp("expiration_time");

                return storedOTP.equals(otp) &&
                        expirationTime.after(Timestamp.valueOf(LocalDateTime.now()));
            }
        }
        return false;
    }
}
