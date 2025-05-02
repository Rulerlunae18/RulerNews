package com.example.servlets;

import com.example.dao.NewsDAO;
import com.example.dao.UserDAO;
import com.example.model.News;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet("/createnews")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024)
public class CreateNewsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/createnews.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String author = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        if (author == null || !"admin".equals(role)) {
            resp.sendRedirect("home");
            return;
        }

        int authorId = UserDAO.getUserIdByUsername(author);
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String category = req.getParameter("category");

        String imagePath = null;
        Part filePart = req.getPart("image");

        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            if (fileName == null || fileName.isEmpty()) {
                throw new ServletException("File name is empty");
            }

            String uploadDir = getServletContext().getRealPath("/") + "uploads";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();

            String filePath = uploadDir + File.separator + fileName;

            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, Paths.get(filePath));
                imagePath = "uploads/" + fileName;
            }
        }

        News news = new News(0, title, content, authorId, 0, 0, imagePath, category);

        boolean success = NewsDAO.insertNews(news);

        if (success) {
            resp.sendRedirect("home");
        } else {
            throw new ServletException("Insert failed: " + title + " " + content + " " + authorId + " " + imagePath);
        }
    }
}
