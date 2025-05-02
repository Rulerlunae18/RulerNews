package com.example.servlets;

import com.example.dao.UserDAO;
import com.example.dao.NewsDAO;
import com.example.model.News;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/ratenews")
public class RateNewsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int newsId = Integer.parseInt(request.getParameter("newsId"));
        String action = request.getParameter("action");

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username != null) {
            int userId = UserDAO.getUserIdByUsername(username);
            boolean result = NewsDAO.updateLikesDislikes(newsId, userId, action);

            if (result) {
                response.sendRedirect("home#news" + newsId);
            } else {
                request.setAttribute("error", "Ви вже поставили цю реакцію.");
                List<News> newsList = NewsDAO.getAllNews();
                request.setAttribute("newsList", newsList);
                request.getRequestDispatcher("/home.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect("index.html");
        }
    }
}
