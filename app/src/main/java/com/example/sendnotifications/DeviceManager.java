package com.example.sendnotifications;

import android.content.Context;
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
}