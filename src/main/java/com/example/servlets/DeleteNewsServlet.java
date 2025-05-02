package com.example.servlets;

import com.example.dao.NewsDAO;
import com.example.dao.UserDAO;
import com.example.model.News;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/deletenews")
public class DeleteNewsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        if (username == null || role == null || !"admin".equals(role)) {
            resp.sendRedirect("home");
            return;
        }

        String idParam = req.getParameter("newsId");

        if (idParam == null || idParam.isEmpty()) {
            req.setAttribute("error", "ID новини не вказано.");
            req.getRequestDispatcher("home.jsp").forward(req, resp);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Некоректний ID новини.");
            req.getRequestDispatcher("home.jsp").forward(req, resp);
            return;
        }

        News news = NewsDAO.getNewsById(id);
                News news = NewsDAO.getNewsById(id);
        if (news == null) {
            req.setAttribute("error", "Новину не знайдено.");
            req.getRequestDispatcher("home.jsp").forward(req, resp);
            return;
        }

        int currentUserId = UserDAO.getUserIdByUsername(username);

        if (!"admin".equals(role) && news.getAuthorId() != currentUserId) {
            req.setAttribute("error", "Ви не маєте прав на видалення цієї новини.");
            req.getRequestDispatcher("home.jsp").forward(req, resp);
            return;
        }

        // 🧹 Видаляємо пов'язане зображення
        String imagePath = news.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            String fullImagePath = System.getProperty("java.io.tmpdir") + File.separator + "uploads" + File.separator + imagePath;
            File imageFile = new File(fullImagePath);
            if (imageFile.exists()) {
                imageFile.delete(); // ❗ ігноруємо результат, або можна логувати
            }
        }

        boolean deleted = NewsDAO.deleteNews(id);
    }
}
