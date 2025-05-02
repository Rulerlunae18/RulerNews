package com.example.servlets;

import com.example.model.User;
import com.example.dao.UserDAO;
import com.example.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO(); // залежить від твого DAO

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
            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                out.println("Невірне імʼя користувача або пароль.");
                return;
            }

            // Пароль тимчасово порівнюється напряму (до впровадження BCrypt)
            if (!password.equals(user.getPasswordHash())) {
                out.println("Невірне імʼя користувача або пароль.");
                return;
            }

            if (!user.isVerified()) {
                out.println("❗ Ви ще не підтвердили свою електронну пошту. <a href='not-verified.jsp'>Надіслати лист знову</a>");
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("username", user.getUsername());
            session.setAttribute("isAdmin", user.isAdmin());
            session.setAttribute("role", user.isAdmin() ? "admin" : "user");

            response.sendRedirect(request.getContextPath() + "/home");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Помилка сервера: " + e.getMessage());
        }
    }
}
