package com.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/confirmemail")
public class ConfirmEmailServlet extends HttpServlet {
    private final String DB_URL = System.getenv("DB_URL");
    private final String DB_USER = System.getenv("DB_USER");
    private final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        String token = request.getParameter("token");

        if (token == null || token.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/not-verified.jsp?error=invalid_token");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                try (PreparedStatement stmt = conn.prepareStatement(
                        "SELECT id, is_verified FROM users WHERE email_token = ?")) {
                    stmt.setString(1, token);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        boolean alreadyVerified = rs.getBoolean("is_verified");

                        if (alreadyVerified) {
                            response.sendRedirect(request.getContextPath() + "/home.jsp?info=already_verified");
                        } else {
                            int userId = rs.getInt("id");

                            try (PreparedStatement updateStmt = conn.prepareStatement(
                                    "UPDATE users SET is_verified = TRUE, email_token = NULL WHERE id = ?")) {
                                updateStmt.setInt(1, userId);
                                updateStmt.executeUpdate();

                                response.sendRedirect(request.getContextPath() + "/home.jsp?success=email_verified");
                            }
                        }
                    } else {
                        response.sendRedirect(request.getContextPath() + "/not-verified.jsp?error=token_not_found");
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/not-verified.jsp?error=sql");
            }

        } catch (ClassNotFoundException e) {
            throw new ServletException("❌ JDBC Driver не знайдений", e);
        }
    }
}
