package com.example.dao;

import com.example.model.News;
import com.example.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NewsDAO {

    public static List<News> getAllPublishedNews() {
        return getAllNews();
    }

    public static List<News> getPublishedNewsByCategory(String category) {
        return getNewsByCategory(category);
    }

    public static List<News> searchPublishedNewsByKeyword(String keyword) {
        return searchNewsByKeyword(keyword);
    }

    public static List<News> getAllNews() {
        List<News> newsList = new ArrayList<>();
        String sql = "SELECT n.*, u.username AS author_name " +
                "FROM news n LEFT JOIN users u ON n.author_id = u.id " +
                "WHERE n.is_published = TRUE ORDER BY n.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                News news = new News(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("author_id"),
                        rs.getString("author_name"),
                        rs.getInt("likes"),
                        rs.getInt("dislikes"),
                        rs.getString("image_url"),
                        rs.getString("category"),
                        rs.getTimestamp("created_at")
                );
                newsList.add(news);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newsList;
    }

    public static List<News> getNewsByCategory(String category) {
        List<News> newsList = new ArrayList<>();
        String sql = "SELECT n.*, u.username AS author_name " +
                "FROM news n LEFT JOIN users u ON n.author_id = u.id " +
                "WHERE n.category = ? AND n.is_published = true ORDER BY n.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                News news = new News(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("author_id"),
                        rs.getString("author_name"),
                        rs.getInt("likes"),
                        rs.getInt("dislikes"),
                        rs.getString("image_url"),
                        rs.getString("category"),
                        rs.getTimestamp("created_at")
                );
                newsList.add(news);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newsList;
    }

    public static boolean insertNews(News news) {
        String sql = "INSERT INTO news (title, content, author_id, likes, dislikes, image_url, category) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("Saving image path: " + news.getImageUrl());

            ps.setString(1, news.getTitle());
            ps.setString(2, news.getContent());
            ps.setInt(3, news.getAuthorId());
            ps.setInt(4, news.getLikes());
            ps.setInt(5, news.getDislikes());
            ps.setString(6, news.getImageUrl());
            ps.setString(7, news.getCategory());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static News getNewsById(int id) {
        String sql = "SELECT n.*, u.username AS author_name FROM news n " +
                "LEFT JOIN users u ON n.author_id = u.id " +
                "WHERE n.id = ? AND n.is_published = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new News(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getInt("author_id"),
                            rs.getString("author_name"),
                            rs.getInt("likes"),
                            rs.getInt("dislikes"),
                            rs.getString("image_url"),
                            rs.getString("category"),
                            rs.getTimestamp("created_at")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<News> searchNewsByKeyword(String keyword) {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, u.username AS author FROM news n JOIN users u ON n.author_id = u.id " +
                "WHERE (n.title LIKE ? OR n.content LIKE ?) AND n.is_published = true ORDER BY n.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String wildcard = "%" + keyword + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    News news = new News(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getInt("author_id"),
                            rs.getString("author"),
                            rs.getInt("likes"),
                            rs.getInt("dislikes"),
                            rs.getString("image_url"),
                            rs.getString("category"),
                            rs.getTimestamp("created_at")
                    );
                    list.add(news);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateLikesDislikes(int newsId, int userId, String action) {
        String checkSql = "SELECT reaction FROM user_news_reactions WHERE user_id = ? AND news_id = ?";
        String insertSql = "INSERT INTO user_news_reactions (user_id, news_id, reaction) VALUES (?, ?, ?)";
        String updateReactionSql = "UPDATE user_news_reactions SET reaction = ? WHERE user_id = ? AND news_id = ?";
        String incrementLike = "UPDATE news SET likes = likes + 1 WHERE id = ?";
        String decrementLike = "UPDATE news SET likes = likes - 1 WHERE id = ?";
        String incrementDislike = "UPDATE news SET dislikes = dislikes + 1 WHERE id = ?";
        String decrementDislike = "UPDATE news SET dislikes = dislikes - 1 WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            String currentReaction = null;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, newsId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        currentReaction = rs.getString("reaction");
                    }
                }
            }

            if (currentReaction == null) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, newsId);
                    insertStmt.setString(3, action);
                    insertStmt.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement("like".equals(action) ? incrementLike : incrementDislike)) {
                    ps.setInt(1, newsId);
                    ps.executeUpdate();
                }

                return true;
            }

            if (currentReaction.equals(action)) {
                return false;
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateReactionSql)) {
                updateStmt.setString(1, action);
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, newsId);
                updateStmt.executeUpdate();
            }

            if ("like".equals(action)) {
                try (PreparedStatement inc = conn.prepareStatement(incrementLike);
                     PreparedStatement dec = conn.prepareStatement(decrementDislike)) {
                    inc.setInt(1, newsId);
                    dec.setInt(1, newsId);
                    inc.executeUpdate();
                    dec.executeUpdate();
                }
            } else if ("dislike".equals(action)) {
                try (PreparedStatement inc = conn.prepareStatement(incrementDislike);
                     PreparedStatement dec = conn.prepareStatement(decrementLike)) {
                    inc.setInt(1, newsId);
                    dec.setInt(1, newsId);
                    inc.executeUpdate();
                    dec.executeUpdate();
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getImageUrlById(int id) {
        String sql = "SELECT image_url FROM news WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("image_url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteNews(int id) {
        String deleteReactionsSql = "DELETE FROM user_news_reactions WHERE news_id = ?";
        String deleteNewsSql = "DELETE FROM news WHERE id = ?";
        String imagePath = getImageUrlById(id);

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Files.deleteIfExists(Paths.get("uploads/" + imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement ps1 = conn.prepareStatement(deleteReactionsSql);
                    PreparedStatement ps2 = conn.prepareStatement(deleteNewsSql)
            ) {
                ps1.setInt(1, id);
                ps1.executeUpdate();

                ps2.setInt(1, id);
                int rowsAffected = ps2.executeUpdate();

                conn.commit();
                return rowsAffected > 0;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
