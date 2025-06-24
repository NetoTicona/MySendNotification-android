package com.example.sendnotifications;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM_LOGS";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // 1. Log reception
        Log.d(TAG, "═══════════════════════════════════");
        Log.d(TAG, "Push Notification Received");

        // 2. Extract title and body from notification payload
        String title = "No Title";
        String message = "No Message";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle() != null ?
                    remoteMessage.getNotification().getTitle() : title;

            message = remoteMessage.getNotification().getBody() != null ?
                    remoteMessage.getNotification().getBody() : message;

            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Message: " + message);
        }

        // 3. Show Toast with both title and message
        showFormattedToast(title, message);

        Intent intent = new Intent("SHOW_ELEMENTS");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        //change background color of the app
    }

    private void showFormattedToast(String title, String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            String toastText = String.format("%s\n%s", title, message);
            //Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();

        });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "New FCM Token: " + token);
    }
}