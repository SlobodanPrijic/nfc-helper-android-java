package com.hyperether.nfcreader.api.volley;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Class for managing networking
 *
 * @author Slobodan Prijic
 * @version 1.0 - 07/14/2017.
 */
public class VolleyManager {

    private static final String TAG = VolleyManager.class.getSimpleName();
    private static VolleyManager mInstance;
    private RequestQueue mRequestQueue;

    /**
     * Singleton constructor
     *
     * @return instance
     */
    public static synchronized VolleyManager getInstance() {
        if (mInstance == null)
            mInstance = new VolleyManager();
        return mInstance;
    }

    /**
     * Get request queue
     *
     * @return request queue
     */
    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    /**
     * Add to request queue
     *
     * @param req request
     * @param tag parameter
     * @param <T> object
     */
    public <T> void addToRequestQueue(Request<T> req, String tag, Context context) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue(context).add(req);
    }

    /**
     * Cancel request
     *
     * @param tag parameter
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}