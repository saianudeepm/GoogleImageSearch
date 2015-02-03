package com.salome.googleimagesearch.api;

import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.salome.googleimagesearch.activities.SearchActivity;
import com.salome.googleimagesearch.adapters.ImageResultsAdapter;
import com.salome.googleimagesearch.models.ImageResult;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by saianudeepm on 2/2/15.
 */
public class GoogleImageSearchHttpClient {

    //TODO need to clean up the client
    
    private static final String BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0";

    private static AsyncHttpClient client = new AsyncHttpClient();
    
    private static ImageResultsAdapter aImageResults;

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler); 
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
    
    
    public static void  get(String fullUrl, final ImageResultsAdapter aImgResults) {
        aImageResults = aImgResults;
        client.get(fullUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(response.toString(), this.toString());
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    // When you make changes to the adapter, it does modify the underlying data
                    aImageResults.addAll(ImageResult.fromJsonArray(imageResultsJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(this.toString(),errorResponse.toString(),throwable);
                System.out.println("------Failed to parse the response-----" + errorResponse);
            }
            
        });
    }

    
}
