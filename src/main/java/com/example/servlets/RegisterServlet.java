package com.example.servlets;

import com.example.model.User;
import com.example.utils.EmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final String DB_URL = System.getenv("DB_URL");
    private final String DB_USER = System.getenv("DB_USER");
    private final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String adminCode = request.getParameter("adminCode");

        if (username == null || email == null || password == null ||
                username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            response.getWriter().println("❌ Всі поля мають бути заповнені.");
            return;
        }

        boolean isAdmin = "1234".equals(adminCode);

        try (PrintWriter out = response.getWriter()) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                try (PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?")) {
                    checkStmt.setString(1, username);
                    checkStmt.setString(2, email);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0) {
                        response.sendRedirect(request.getContextPath() + "/index.html?error=user_exists");
                        return;
                    }
                }

                String emailToken = UUID.randomUUID().toString();

                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO users (username, password_hash, email, is_admin, email_token, is_verified) " +
                                "VALUES (?, ?, ?, ?, ?, ?)")) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, password);
                    insertStmt.setString(3, email);
                    insertStmt.setBoolean(4, isAdmin);
                    insertStmt.setString(5, emailToken);
                    insertStmt.setBoolean(6, false);

                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        String baseUrl = request.getRequestURL().toString()
                                .replace(request.getRequestURI(), request.getContextPath());

                        String message = EmailService.sendConfirmationEmail(email, username, emailToken, baseUrl);

                        request.setAttribute("emailMessage", message);
                        getServletContext().getRequestDispatcher("/not-verified.jsp").forward(request, response);
                        return;
                    } else {
                        out.println("❌ Помилка реєстрації.");
                    }
                }

            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
                out.println("❌ Помилка бази даних: " + sqlEx.getMessage());
            }

        } catch (ClassNotFoundException e) {
            throw new ServletException("❌ JDBC Driver не знайдено", e);
        }
    }
}
