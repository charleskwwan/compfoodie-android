package cs.tufts.edu.compfoodie;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by ngapham on 10/9/16.
 *      Handles networking with our server
 */

public class VolleyUtils { // implements Response.Listener<JSONObject>, Response.ErrorListener {
    public static void POST (String url, JSONObject json,
                             final VolleyCallback<JSONObject> callback) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccessResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Volley", "Error. HTTP Status Code:"+networkResponse.statusCode);
                        }
                        if (error instanceof TimeoutError) {
                            Log.e("Volley", "TimeoutError");
                        }else if(error instanceof NoConnectionError){
                            Log.e("Volley", "NoConnectionError");
                        } else if (error instanceof AuthFailureError) {
                            Log.e("Volley", "AuthFailureError");
                        } else if (error instanceof ServerError) {
                            Log.e("Volley", "ServerError");
                        } else if (error instanceof NetworkError) {
                            Log.e("Volley", "NetworkError");
                        } else if (error instanceof ParseError) {
                            Log.e("Volley", "ParseError");
                        }
                    }
                });
        ApplicationController.getInstance().addToRequestQueue(req);
    }

    public static void getImage(String url, final Context context,
                                final VolleyCallback<Bitmap> callback) {
        ImageRequest req = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        callback.onSuccessResponse(bitmap);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Toast.makeText(context, "Connection timed out", Toast.LENGTH_SHORT)
                                    .show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(context, "Connection could not be established",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Something went wrong getting an image",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ApplicationController.getInstance().addToRequestQueue(req);
    }

//    public void onResponse(JSONObject response) {
//        VolleyLog.v("Response:%n %s", response.toString());
//    }
//
//    public void onErrorResponse(VolleyError error) {
//        VolleyLog.e("Error: ", error.getMessage());
//    }
}
