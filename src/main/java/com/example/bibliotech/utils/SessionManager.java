package com.example.bibliotech.utils;

import com.example.bibliotech.model.Users;

public class SessionManager {
    private static volatile SessionManager instance;
    private Users currentUser;

    private SessionManager() {
        // Private constructor để đảm bảo Singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public void setCurrentUser(Users user) {
        this.currentUser = user;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void clearSession() {
        currentUser = null;
    }

    // Các phương thức tiện ích
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "Unknown";
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}
