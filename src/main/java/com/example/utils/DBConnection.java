package com.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_HOST = System.getenv("DB_HOST");
    private static final String DB_PORT = System.getenv("DB_PORT");
    private static final String DB_NAME = System.getenv("DB_NAME");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    private static final String DB_URL;

    static {
        // Debug print — for Render or Docker logs
        System.out.println("DB_HOST: " + DB_HOST);
        System.out.println("DB_PORT: " + DB_PORT);
        System.out.println("DB_NAME: " + DB_NAME);
        System.out.println("DB_USER: " + DB_USER);
        // Do not print DB_PASSWORD for security

        // Check if any variable is null
        if (DB_HOST == null || DB_PORT == null || DB_NAME == null || DB_USER == null || DB_PASSWORD == null) {
            throw new RuntimeException("One or more required DB environment variables are not set.");
        }

        DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";
        System.out.println("DB_URL: " + DB_URL);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
