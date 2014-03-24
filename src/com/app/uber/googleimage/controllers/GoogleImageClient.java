package com.app.uber.googleimage.controllers;

import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

public class GoogleImageClient {
	private final static String API_REQUEST_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&start=%d";
	
	public GoogleImageClient(){
		
	}

	public void makeSearchRequest(String query, int count,
			Listener<JSONObject> responseSuccessListener,
			ErrorListener responseErrorListener) {
		
		String requestUrl = String.format(API_REQUEST_URL,Uri.encode(query), count);

		JsonObjectRequest request = new JsonObjectRequest(
				Method.GET,
				requestUrl,
				null,
				responseSuccessListener, responseErrorListener);
				
		VolleyController.getInstance().addToRequestQueue(request);		
	}
	
	
}
