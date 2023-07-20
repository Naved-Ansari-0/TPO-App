package com.example.candidateblocking.utils;

import android.content.Context;

import com.android.volley.Request;

import com.android.volley.toolbox.StringRequest;

public class DataFromAPIService {
    public Context context;
    public interface VolleyStringResponseListener {
        void OnError(String message);
        void OnResponse(String response);
    }

    public DataFromAPIService(Context context) {
        this.context = context;
    }

    public void getStringDataFromApi(String url, VolleyStringResponseListener volleyStringResponseListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> volleyStringResponseListener.OnResponse(response),
                error -> volleyStringResponseListener.OnError("Error while making api request."));
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}
