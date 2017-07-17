package com.hyperether.nfcreader.api.volley;

/**
 * Api callback listener
 *
 * @author Slobodan Prijic
 * @version 1.0 - 07/14/2017
 */
public interface ApiResponse {

    void onSuccess(Object response);

    void onError(String message);
}
