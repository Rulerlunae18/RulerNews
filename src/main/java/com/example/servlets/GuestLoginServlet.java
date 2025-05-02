package com.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.UUID;

@WebServlet("/guestlogin")
public class GuestLoginServlet extends HttpServlet {
    private final String DB_URL = System.getenv("DB_URL");
    private final String DB_USER = System.getenv("DB_USER");
    private final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");

        String guestUsername = "Гість_" + UUID.randomUUID().toString().substring(0, 8);

        try (PrintWriter out = response.getWriter()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO users (username, is_guest) VALUES (?, TRUE)")) {
                    insertStmt.setString(1, guestUsername);
                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        HttpSession session = request.getSession();
                        session.setAttribute("username", guestUsername);
                        session.setAttribute("isAdmin", false);
                        session.setAttribute("isGuest", true);
                        session.setAttribute("role", "guest");
                        response.sendRedirect(request.getContextPath() + "/home");
                    } else {
                        out.println("Помилка при створенні гостя.");
                    }
                }
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
                out.println("Помилка бази даних: " + sqlEx.getMessage());
            }
        } catch (ClassNotFoundException e) {
            throw new ServletException("JDBC Driver не знайдений", e);
        }
    }
}
