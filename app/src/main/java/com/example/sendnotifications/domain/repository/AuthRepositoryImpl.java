package com.example.sendnotifications.domain.repository;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sendnotifications.DeviceManager;
import com.example.sendnotifications.domain.model.User;
import com.example.sendnotifications.domain.repository.AuthRepository;
import com.example.sendnotifications.domain.repository.Callback;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthRepositoryImpl implements AuthRepository {

    private final Context context;
    private final RequestQueue requestQueue;
    private final SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS_NAME = "SP_FILE";
    private static final String KEY_TOKEN = "token";

    // Constructor: Recibe el contexto para inicializar Volley y SharedPreferences
    public AuthRepositoryImpl(Context context) {
        this.context = context.getApplicationContext();
        this.requestQueue = Volley.newRequestQueue(context);
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void login(String name, String password, Callback<User> callback) {

        String url = "http://apinoti.thenett0.com/login"; // Reemplaza con tu endpoint

        // 1. Crear el cuerpo de la petición (JSON)
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("name", name);
            jsonBody.put("pass", password);

        } catch (JSONException e) {
            callback.onError("Error en los datos");
            return;
        }

        // 2. Configurar la petición POST con Volley
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String code = response.getString("code");
                        if (!"OK".equals(code)) {
                            callback.onError(response.optString("message", "Login failed"));
                            return;
                        }

                        // Parse response data
                        int userId = response.getInt("user_id");
                        String userName = response.getString("name");
                        int deviceId = response.getInt("device_id");

                        Toast.makeText(context, "UserID: " + userId, Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "DeviceID: " + deviceId, Toast.LENGTH_SHORT).show();

                        // Save all data to SharedPreferences
                        sharedPreferences.edit()

                                .putInt("user_id", userId)
                                .putString("user_name", userName)
                                .putInt("device_id", deviceId)
                                .apply();

                        // Create and return User object
                        User user = new User(
                                String.valueOf(userId),
                                userName,
                                password
                        );

                        callback.onSuccess(user);
                    } catch (JSONException e) {
                        callback.onError("Error al procesar la respuesta");
                    }
                },
                error -> {
                    // 6. Manejar error de red
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Error desconocido";
                    callback.onError(errorMessage);
                }
        );

        // 7. Encolar la petición
        requestQueue.add(request);
    }

    @Override
    public void register(User user, Callback<Boolean> callback) {
        String url = "http://apinoti.thenett0.com/registerUserDevice"; // Reemplaza con tu endpoint

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", user.getName());
            jsonBody.put("pass", user.getPassword());
        } catch (JSONException e) {
            callback.onError("Error en los datos");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    try {

                        if (!response.has("data") || response.isNull("data")) {
                            callback.onError("Missing data field in response");
                            return;
                        }

                        JSONObject data = response.getJSONObject("data");

                        // 2. Validate and extract user_id
                        if (!data.has("user_id") || data.isNull("user_id")) {
                            callback.onError("Missing user_id in response");
                            return;
                        }
                        int userId = data.getInt("user_id");

                        // 3. Validate and extract device_id
                        if (!data.has("device_id") || data.isNull("device_id")) {
                            callback.onError("Missing device_id in response");
                            return;
                        }
                        int deviceId = data.getInt("device_id");

                        // 4. Validate and extract name
                        if (!data.has("name") || data.isNull("name")) {
                            callback.onError("Missing name in response");
                            return;
                        }
                        String name = data.getString("name");

                        // Save all data to SharedPreferences
                        sharedPreferences.edit()
                                .putInt("user_id", userId)
                                .putInt("device_id", deviceId)
                                .putString("user_name", name)
                                .apply();

                        callback.onSuccess(true);

                    } catch (JSONException e) {
                        callback.onError("Invalid server response format: " + e.getMessage());
                    }
                },
                error -> {
                    callback.onError(error.getMessage());
                }
        );

        requestQueue.add(request);
    }

    @Override
    public boolean isUserLoggedIn() {
        // Verificar si hay un token guardado
        return sharedPreferences.getString(KEY_TOKEN, null) != null;
    }

    @Override
    public void logout() {
        // Eliminar token y limpiar SharedPreferences
        sharedPreferences.edit().remove(KEY_TOKEN).apply();
    }



    @Override
    public void registerFCMToken(String token, Callback<Boolean> callback) {

        DeviceManager.postRegistrarDispositivoEnServidor(token, context,
                new DeviceManager.RegistrationCallback() {
                    @Override
                    public void onSuccess(String message, Integer id, String token) {
                        callback.onSuccess(true);
                    }
                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
    }



    @Override
    public void updateFCMToken(String token, Callback<Boolean> callback) {

        DeviceManager.updateDeviceToken(token, context,
                new DeviceManager.RegistrationCallback() {
                    @Override
                    public void onSuccess(String message, Integer id, String token) {
                        Toast.makeText(context, "succesLogin: " + message, Toast.LENGTH_SHORT).show();
                        callback.onSuccess(true);
                    }
                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
    }







}