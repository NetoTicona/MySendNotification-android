package com.example.sendnotifications.domain.repository;

public interface Callback<T> {
    void onSuccess(T response);
    void onError(String error);
}