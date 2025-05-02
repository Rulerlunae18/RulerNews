package com.example.model;

public class User {
    private String username;
    private String passwordHash;
    private boolean isVerified;
    private boolean isAdmin;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", isVerified=" + isVerified +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
