package com.example.servlets;

import com.example.dao.NewsDAO;
import com.example.model.News;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String category = req.getParameter("category");
        String query = req.getParameter("query");
        List<News> newsList;

        if (query != null && !query.trim().isEmpty()) {
            newsList = NewsDAO.searchPublishedNewsByKeyword(query.trim());
        } else if (category != null && !category.equals("all")) {
            newsList = NewsDAO.getPublishedNewsByCategory(category);
        } else {
            newsList = NewsDAO.getAllPublishedNews();
        }

        req.setAttribute("newsList", newsList);
        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }
}
