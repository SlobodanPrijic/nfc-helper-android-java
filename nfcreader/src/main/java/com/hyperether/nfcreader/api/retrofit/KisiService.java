package com.hyperether.nfcreader.api.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Api requests management class
 *
 * @author Slobodan Prijic
 * @version 1.0 - 07/14/2017
 */
public interface KisiService {

    @Headers("Authorization: KISI-LINK 75388d1d1ff0dff6b7b04a7d5162cc6c")
    @POST("./")
    Call<ResponseBody> unlockKisi();
}
