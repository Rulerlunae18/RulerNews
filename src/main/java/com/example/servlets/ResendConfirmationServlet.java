package com.example.servlets;

import com.example.utils.EmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.UUID;

@WebServlet("/resendconfirmation")
public class ResendConfirmationServlet extends HttpServlet {
    private final String DB_URL = "jdbc:mysql://localhost:3306/news";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "RulerLovesYou";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String email = request.getParameter("email");

        try (PrintWriter out = response.getWriter()) {
            if (email == null || email.trim().isEmpty()) {
                out.println("❌ Email не вказано.");
                return;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                String query = "SELECT username, is_verified FROM users WHERE email = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, email);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String username = rs.getString("username");
                        boolean isVerified = rs.getBoolean("is_verified");

                        if (isVerified) {
                            out.println("✅ Пошта вже підтверджена.");
                        } else {
                            String token = UUID.randomUUID().toString();

                            try (PreparedStatement updateStmt = conn.prepareStatement(
                                    "UPDATE users SET email_token = ? WHERE email = ?")) {
                                updateStmt.setString(1, token);
                                updateStmt.setString(2, email);
                                updateStmt.executeUpdate();
                            }

                            String baseUrl = request.getScheme() + "://" +
                                    request.getServerName() + ":" +
                                    request.getServerPort() +
                                    request.getContextPath();

                            String message = EmailService.sendConfirmationEmail(email, username, token, baseUrl);
                            out.println(message);
                        }
                    } else {
                        out.println("❌ Користувача з таким email не знайдено.");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                out.println("❌ Помилка бази даних: " + e.getMessage());
            }

        } catch (ClassNotFoundException e) {
            throw new ServletException("JDBC Driver не знайдено", e);
        }
    }
}
