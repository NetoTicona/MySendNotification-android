package com.example.sendnotifications;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private LinearLayout[] elements;
    private Handler handler = new Handler();
    private ArrayList<Integer> remainingElements = new ArrayList<>();
    private static final int REQUEST_CODE_POST_NOTIFICATION = 1001;
    private TextView tvStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        registrarDispositivo();


        // Initialize elements array
        elements = new LinearLayout[]{
                findViewById(R.id.element0),
                findViewById(R.id.element1),
                findViewById(R.id.element2),
                findViewById(R.id.element3)
        };
        tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setText("Conectando...");




        /*LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        //showElementSequence();
                        prepareElementSequence();
                    }
                },
                new IntentFilter("SHOW_ELEMENTS")
        );*/

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Check notification permission first
                        if (hasNotificationPermission()) {
                            prepareElementSequence();
                        } else {
                            /*runOnUiThread(() -> {
                                tvStatus.setText("Active el permiso para continuar");
                                Toast.makeText(MainActivity.this,
                                        "Por favor active los permisos de notificación",
                                        Toast.LENGTH_LONG).show();
                            });
                            // Optionally request permission again
                            checkAndRequestNotificationPermission();*/
                        }
                    }
                },
                new IntentFilter("SHOW_ELEMENTS")
        );


    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        // Below Android 13 always returns true since permission isn't required
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_POST_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted (notifications will work in background)
                tvStatus.setText("Esperando notificación...");
            } else {
                tvStatus.setText("Dar permiso de notificaiones");
                //checkAndRequestNotificationPermission();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationPermissionStatus();
    }

    private void checkNotificationPermissionStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED);

            runOnUiThread(() -> {
                if (hasPermission) {
                    tvStatus.setText("Esperando notificación...");
                } else {
                    tvStatus.setText("Active el permiso de notificaciones");
                }
            });
        } else {
            // Below Android 13 - permission not needed
            runOnUiThread(() -> tvStatus.setText("Esperando notificación..."));
        }
    }


    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS )
                    != PackageManager.PERMISSION_GRANTED) {

                // Show rationale if needed
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.POST_NOTIFICATIONS)) {
                    tvStatus.setText("Active el permiso de notificaciones");
                }
                // Request the permission
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATION
                );
            }else{
                tvStatus.setText("Esperando notificación...");
            }


        }else{
            tvStatus.setText("Esperando notificación...");
        }
        // No need for permission below Android 13
    }



    private void prepareElementSequence() {
        // Create list of elements 1,2,3
        remainingElements.clear();
        remainingElements.add(1);
        remainingElements.add(2);
        remainingElements.add(3);

        // Shuffle the list
        Collections.shuffle(remainingElements);

        // Start with first random element
        showNextElement();
    }


    private void showNextElement() {
        if (remainingElements.isEmpty()) {
            // All elements shown, return to element0
            returnToElement0();
            return;
        }

        // Get and remove first element from list
        int nextElement = remainingElements.remove(0);

        // Hide all elements
        for (LinearLayout element : elements) {
            element.setVisibility(View.GONE);
        }

        // Show the selected element
        elements[nextElement].setVisibility(View.VISIBLE);

        // Schedule next element after 3 seconds
        handler.postDelayed(this::showNextElement, 3000);
    }

    private void returnToElement0() {
        // Hide all colored elements
        for (int i = 1; i <= 3; i++) {
            elements[i].setVisibility(View.GONE);
        }
        // Show waiting element
        elements[0].setVisibility(View.VISIBLE);
    }


    private void showElementSequence() {
        // Start with element1
        showElement(1, 0);
    }


    private void showElement(final int currentIndex, final int delay) {

        handler.postDelayed(() -> {
            // Hide all elements first
            for (LinearLayout element : elements) {
                element.setVisibility(View.GONE);
            }

            // Show current element
            elements[currentIndex].setVisibility(View.VISIBLE);

            // Schedule next element or return to element0
            if (currentIndex < 3) {
                showElement(currentIndex + 1, 3000); // 3 sec delay
            } else {

                //cuando es el currentIndex = 4, sea hacevisible el primerp ylos demas se ocultan
                handler.postDelayed(() -> {
                    elements[0].setVisibility(View.VISIBLE);

                    for (int i = 1; i <= 3; i++) {
                        elements[i].setVisibility(View.GONE);
                    }
                }, 3000);

            }


        }, delay);
    }













    private void registrarDispositivo() {



        /*FirebaseMessaging.getInstance().subscribeToTopic("todos")
                .addOnCompleteListener(response -> {
                    if(response.isSuccessful()) {
                        runOnUiThread(() -> {

                            tvStatus.setText("Permita notificaciones");
                            checkAndRequestNotificationPermission();
                        });
                    } else {

                        runOnUiThread(() -> {
                            tvStatus.setText("Fallo al conectar a FCM");
                            Toast.makeText(MainActivity.this, "❌ Error al suscribirse al topic",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                }); */




        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.getException());
                            tvStatus.setText("Fallo al obtener token FCM");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        String tokenGuardado = getSharedPreferences("SP_FILE", 0).getString("TOKEN", null);

                        if (token != null) {
                            if (tokenGuardado == null || !token.equals(tokenGuardado)) {
                                // Register device with callback
                                DeviceManager.postRegistrarDispositivoEnServidor(token, MainActivity.this,
                                        new DeviceManager.RegistrationCallback() {
                                            @Override
                                            public void onSuccess(String message, Integer id, String token) {
                                                runOnUiThread(() -> {
                                                    tvStatus.setText("✅ Dispositivo registrado exitosamente");
                                                    Toast.makeText(MainActivity.this, "✅ Success: " + message,
                                                            Toast.LENGTH_SHORT).show();

                                                    // Now check for notification permissions
                                                    checkAndRequestNotificationPermission();
                                                });
                                            }

                                            @Override
                                            public void onError(String error) {
                                                runOnUiThread(() -> {
                                                    tvStatus.setText("❌ Error al registrar dispositivo");
                                                    Toast.makeText(MainActivity.this, "❌ " + error,
                                                            Toast.LENGTH_SHORT).show();
                                                });
                                            }
                                        });
                            } else {
                                // Token hasn't changed, device already registered
                                tvStatus.setText("✅ Dispositivo ya registrado");
                                checkAndRequestNotificationPermission();
                            }
                        }
                    }
                });
    }



















}