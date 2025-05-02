package com.example.dao;

import com.example.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static int getUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    System.out.println("❌ Користувача не знайдено: " + username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

