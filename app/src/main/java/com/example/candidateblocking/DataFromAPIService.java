package com.example.candidateblocking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                volleyStringResponseListener.OnResponse(response);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyStringResponseListener.OnError("Error while fetching student placement data");
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}
