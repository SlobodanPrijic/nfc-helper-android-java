package com.hyperether.nfcreader.api.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Class managing communication using Volley library
 *
 * @author Slobodan Prijic
 * @version 1.0 - 07/14/2017
 */
class VolleyRequestManager {

    private VolleyResponse callback;

    VolleyRequestManager(VolleyResponse listener) {
        this.callback = listener;
    }

    /**
     * JSON request POST method with access token
     *
     * @param url request URL
     * @param method type of request
     * @param requestBody request body
     * @param requestTag request description
     * @param params add String params if needed
     * @param headers add String headers if needed
     */
    public void addJsonRequest(
            String url,
            int method,
            JSONObject requestBody,
            String requestTag,
            final Map<String, String> params,
            final Map<String, String> headers,
            Context context) {

        try {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    method,
                    url,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.onSuccess(response.toString(), null);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String message = "";
                            int statusCode = -1;
                            if (error != null && error.networkResponse != null) {
                                message = new String(error.networkResponse.data);
                                statusCode = error.networkResponse.statusCode;
                            }
                            callback.onError(statusCode, message);
                        }
                    }) {

                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    params.put("", "");
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    headers.put("Authorization", "KISI-LINK 75388d1d1ff0dff6b7b04a7d5162cc6c");
                    return headers;
                }
            };

            jsObjRequest.setShouldCache(true);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyManager.getInstance().addToRequestQueue(jsObjRequest, requestTag, context);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * String request method
     *
     * @param url url of request
     * @param method type of request
     * @param requestTag Volley tag - for repeat or cancel request
     * @param params add String params if needed
     * @param headers add String headers if needed
     */
    void addStringRequest(
            String url,
            int method,
            String requestTag,
            final Map<String, String> params,
            final Map<String, String> headers,
            Context context) {

        try {
            StringRequest request = new StringRequest(
                    method,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            callback.onSuccess(response, null);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String message = "";
                            int statusCode = -1;
                            if (error != null && error.networkResponse != null) {
                                message = new String(error.networkResponse.data);
                                statusCode = error.networkResponse.statusCode;
                            }
                            callback.onError(statusCode, message);
                        }
                    }) {

                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    return null;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
//                    headers.put("Authorization", "KISI-LINK 75388d1d1ff0dff6b7b04a7d5162cc6c");
                    return headers;
                }
            };
            VolleyManager.getInstance().addToRequestQueue(request, requestTag, context);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
