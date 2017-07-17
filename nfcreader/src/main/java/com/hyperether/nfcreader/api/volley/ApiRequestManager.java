package com.hyperether.nfcreader.api.volley;

import android.content.Context;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Api requests management class
 *
 * @author Slobodan Prijic
 * @version 1.0 - 07/14/2017
 */
public class ApiRequestManager {

    private static ApiRequestManager instance;

    /**
     * Singleton constructor
     *
     * @return instance
     */
    public static ApiRequestManager getInstance() {
        if (instance == null)
            instance = new ApiRequestManager();
        return instance;
    }

    /**
     * Unlock KISI door
     *
     * @param context caller context
     * @param listener api callback
     */
    public void unlock(Context context, final ApiResponse listener) {
        try {
            new VolleyRequestManager(new VolleyResponse() {
                @Override
                public void onSuccess(String response, String error) {
                    listener.onSuccess(response);
                }

                @Override
                public void onError(int statusCode, String message) {
                    listener.onError(message);
                }
            }).addJsonRequest("https://api.getkisi.com/locks/5124/access",
                    Request.Method.POST,
                    new JSONObject(),
                    "unlock",
                    new HashMap<String, String>(),
                    new HashMap<String, String>(),
                    context);
        } catch (Throwable throwable) {
            listener.onError(throwable.getMessage());
        }
    }
}
