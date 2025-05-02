package com.example.model;

import java.sql.Timestamp;

public class News {
    private int id;
    private String title;
    private String content;
    private int authorId;
    private String author;
    private int likes;
    private int dislikes;
    private String imageUrl;
    private String category;
    private Timestamp createdAt; 

    public News(int id, String title, String content, int authorId, String author, int likes, int dislikes, String imageUrl, String category, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.author = author;
        this.likes = likes;
        this.dislikes = dislikes;
        this.imageUrl = imageUrl;
        this.category = category;
        this.createdAt = createdAt;
    }

    public News(int id, String title, String content, int authorId, String author, int likes, int dislikes, String category) {
        this(id, title, content, authorId, author, likes, dislikes, null, category, null);
    }

    public News(int id, String title, String content, int authorId, int likes, int dislikes, String imageUrl, String category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.imageUrl = imageUrl;
        this.category = category;
    }


    public News(int id, String title, String content, int likes, int dislikes, int authorId, String category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.dislikes = dislikes;
        this.authorId = authorId;
        this.category = category;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
