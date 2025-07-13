package com.example.sendnotifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeviceManager {

    // Callback interface for handling registration results
    public interface RegistrationCallback {
        void onSuccess(String message, Integer id, String token);
        void onError(String error);
    }

    public static void postRegistrarDispositivoEnServidor(String token, Context context, RegistrationCallback callback) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://thenett0.com/register";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject respObj = new JSONObject(response);

                            String code = respObj.getString("code");
                            String message = respObj.getString("message");
                            Integer id = respObj.getInt("id");

                            if ("OK".equals(code)) {

                                // Save token and ID to SharedPreferences
                                context.getSharedPreferences("SP_FILE", 0).edit()
                                        .putString("TOKEN", token).commit();

                                if (id != 0) {
                                    context.getSharedPreferences("SP_FILE", 0)
                                            .edit().putInt("ID", id).commit();
                                }

                                // Call success callback
                                if (callback != null) {
                                    callback.onSuccess(message, id, token);
                                }

                            } else {
                                String errorMsg = "Error: " + message;
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                                if (callback != null) {
                                    callback.onError(errorMsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String errorMsg = "JSON parsing error: " + e.getMessage();
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                            if (callback != null) {
                                callback.onError(errorMsg);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = "Error registrando token en servidor: " +
                        (error.getMessage() != null ? error.getMessage() : "Unknown error");
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                if (callback != null) {
                    callback.onError(errorMsg);
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TOKEN", token);

                if (context.getSharedPreferences("SP_FILE", 0).getInt("ID", 0) != 0) {
                    Integer val = context.getSharedPreferences("SP_FILE", 0).getInt("ID", 0);
                    params.put("ID", val.toString());
                }
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // Overloaded method for backward compatibility (without callback)
    public static void postRegistrarDispositivoEnServidor(String token, Context context) {
        postRegistrarDispositivoEnServidor(token, context, null);
    }


    public static void updateDeviceToken(String token, Context context, RegistrationCallback callback) {

        SharedPreferences preferences = context.getSharedPreferences("SP_FILE", 0);

        // Retrieve user_id and device_id from preferences
        int userId = preferences.getInt("user_id", -1);
        int deviceId = preferences.getInt("device_id", -1);

        // Debug Toast showing all values
        String debugMessage = "Token: " + token +
                "\nUserID: " + userId +
                "\nDeviceID: " + deviceId;

        Toast.makeText(context, "DToken: " + token, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "DUserID: " + userId, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "DDeviceID: " + deviceId, Toast.LENGTH_SHORT).show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://apinoti.thenett0.com/updateDeviceData";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject respObj = new JSONObject(response);
                        String code = respObj.getString("code");
                        String message = respObj.getString("message");

                        if ("OK".equals(code)) {
                            // Guardar en SharedPreferences
                            SharedPreferences.Editor editor = context.getSharedPreferences("SP_FILE", 0).edit();
                            editor.putString("token", token );
                            editor.putInt("user_id", userId);
                            editor.putInt("device_id", deviceId);
                            editor.apply();

                            if (callback != null) {
                                Toast.makeText(context, "succesLogin: " + message, Toast.LENGTH_SHORT).show();
                                callback.onSuccess(message, deviceId, token);
                            }
                        } else {
                            String errorMsg = "Server error: " + message;
                            if (callback != null) {
                                callback.onError(errorMsg);
                            }
                        }
                    } catch (JSONException e) {
                        if (callback != null) {
                            callback.onError("JSON error: " + e.getMessage());
                        }
                    }
                },
                error -> {
                    if (callback != null) {
                        callback.onError("Network error: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_token", token);
                params.put("user_id", String.valueOf(userId));
                params.put("device_id", String.valueOf(deviceId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        queue.add(request);
    }













}