package com.example.planeapplication.ui.flighttracking;

import android.app.Application;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.planeapplication.data.FlightTrack;
import com.example.planeapplication.data.FlightTrackPath;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MapViewModel extends AndroidViewModel
{
    private static final String TAG = MapViewModel.class.getSimpleName();

    MutableLiveData<FlightTrack> flightTrack = new MutableLiveData<>();

    public MapViewModel(@NonNull Application application)
    {
        super(application);
    }


    public void loadData(String icao, long date)
    {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());
        StringBuilder urlBuilder = new StringBuilder("https://opensky-network.org/api/tracks/");
        urlBuilder.append("all").append("?").append("icao24=").append(icao).append("&time=").append(date);

        Log.i(TAG, "URL is " + urlBuilder.toString());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlBuilder.toString(), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.i("String Reponse", "String response is " + response);

                List<FlightTrackPath> flightTrackPaths = new ArrayList<>();

                JsonObject flightTrackingObject = getFlightTrackingRequestJson(response);


                for (JsonElement flightPath : flightTrackingObject.getAsJsonArray("path"))
                {
                    flightTrackPaths.add(new FlightTrackPath(
                            flightPath.getAsJsonArray().get(0).getAsLong(),
                            flightPath.getAsJsonArray().get(1).getAsFloat(),
                            flightPath.getAsJsonArray().get(2).getAsFloat()));
                }

                flightTrack.setValue(new FlightTrack(flightTrackingObject.get("icao24").toString(), flightTrackPaths));
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private JsonObject getFlightTrackingRequestJson(String jsonString)
    {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        return jsonElement.getAsJsonObject();
    }
}
