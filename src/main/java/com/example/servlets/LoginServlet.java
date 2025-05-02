package com.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final String DB_URL = "jdbc:mysql://localhost:3306/news";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "RulerLovesYou";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.getWriter().println("Заповніть всі поля.");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        boolean isVerified = rs.getBoolean("is_verified");

                        if (!isVerified) {
                            out.println("❗ Ви ще не підтвердили свою електронну пошту. <a href='not-verified.jsp'>Надіслати лист знову</a>");
                            return;
                        }

                        boolean isAdmin = rs.getBoolean("is_admin");
                        HttpSession session = request.getSession();
                        session.setAttribute("username", username);
                        session.setAttribute("isAdmin", isAdmin);
                        session.setAttribute("role", isAdmin ? "admin" : "user");
                        response.sendRedirect(request.getContextPath() + "/home");
                    } else {
                        out.println("Невірне імʼя користувача або пароль.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.getWriter().println("Помилка БД: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            throw new ServletException("JDBC Driver не знайдено", e);
        }
    }
}
