package com.example.sendnotifications.domain.repository;

import com.example.sendnotifications.domain.model.User;

public interface AuthRepository {
    void login(String name, String password, Callback<User> callback);
    void register(User user, Callback<Boolean> callback);
    boolean isUserLoggedIn();
    void logout();
    void registerFCMToken(String token, Callback<Boolean> callback);
    void updateFCMToken(String token, Callback<Boolean> callback);
}