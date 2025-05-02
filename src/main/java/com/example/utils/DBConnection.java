package com.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String name = System.getenv("DB_NAME");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASSWORD");

        // Debug log
        System.out.println("DB_HOST: " + host);
        System.out.println("DB_PORT: " + port);
        System.out.println("DB_NAME: " + name);
        System.out.println("DB_USER: " + user);
        // DO NOT print password

        if (host == null || port == null || name == null || user == null || pass == null) {
            throw new RuntimeException("One or more DB environment variables are not set.");
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false&serverTimezone=UTC";
        System.out.println("DB_URL: " + url);

        return DriverManager.getConnection(url, user, pass);
    }
}
