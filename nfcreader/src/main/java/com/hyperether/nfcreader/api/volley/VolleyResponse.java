package com.hyperether.nfcreader.api.volley;

/**
 * Interface for server Volley responses
 *
 * @author Slobodan Prijic
 * @version 1.0 - 07/14/2017
 */
interface VolleyResponse {

    void onSuccess(String response, String error);

    void onError(int statusCode, String message);
}
