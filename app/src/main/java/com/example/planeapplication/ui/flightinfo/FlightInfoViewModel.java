package com.example.planeapplication.ui.flightinfo;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.planeapplication.data.Flight;
import com.example.planeapplication.data.FlightInfo;
import com.example.planeapplication.data.FlightState;
import com.example.planeapplication.data.FlightTrack;
import com.example.planeapplication.data.FlightTrackPath;
import com.example.planeapplication.ui.flighttracking.MapViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class FlightInfoViewModel extends AndroidViewModel {

    private static final String TAG = FlightInfoViewModel.class.getSimpleName();

    MutableLiveData<FlightInfo> flightInfo = new MutableLiveData<>();
    MutableLiveData<List<Flight>> flightListLiveData = new MutableLiveData<>();

    public FlightInfoViewModel(@NonNull Application application)
    {
        super(application);
    }


    public void loadData(String icao, long date)
    {
        // Instantiate the RequestQueue.
        icao = icao.replace("\"","");
        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());
        StringBuilder urlBuilder = new StringBuilder("https://opensky-network.org/api/states/");
        urlBuilder.append("all").append("?").append("time=").append(date).append("&icao24=").append(icao);

        Log.i(TAG, "URL is " + urlBuilder.toString());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlBuilder.toString(), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.i(TAG+" String Reponse", "String response is " + response);


                List<FlightState> flightStates = new ArrayList<>();
                JsonObject flightInfoObject = getFlightInfoRequestJson(response);
                try {
                    for (JsonElement flightState : flightInfoObject.getAsJsonArray("states")) {
                        flightStates.add(new FlightState(
                                flightState.getAsJsonArray().get(0).getAsString(),
                                flightState.getAsJsonArray().get(1).getAsString(),
                                flightState.getAsJsonArray().get(2).getAsString(),
                                flightState.getAsJsonArray().get(3).getAsInt(),
                                flightState.getAsJsonArray().get(4).getAsInt(),
                                flightState.getAsJsonArray().get(5).getAsFloat(),
                                flightState.getAsJsonArray().get(6).getAsFloat(),
                                flightState.getAsJsonArray().get(7).getAsFloat(),
                                flightState.getAsJsonArray().get(8).getAsBoolean(),
                                flightState.getAsJsonArray().get(9).getAsFloat(),
                                flightState.getAsJsonArray().get(10).getAsFloat(),
                                flightState.getAsJsonArray().get(11).getAsFloat(),
                                flightState.getAsJsonArray().get(13).getAsFloat(),
                                flightState.getAsJsonArray().get(14).getAsString(),
                                flightState.getAsJsonArray().get(15).getAsBoolean(),
                                flightState.getAsJsonArray().get(16).getAsInt()));
                    }

                    flightInfo.setValue(new FlightInfo(flightStates));

                }
                catch (Exception e){ }
                }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.i(TAG, "erreur req");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void loadDataHisto(long end, String icao){
        // Instantiate the RequestQueue.

        long begin = end - 259200*2;

        icao = icao.replace("\"","");
        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());
        StringBuilder urlBuilder = new StringBuilder("https://opensky-network.org/api/flights/");
        urlBuilder.append("aircraft").append("?").append("icao24=").append(icao).append("&begin=").append(begin).append("&end=")
                .append(end);

        Log.i(TAG, "URL is " + urlBuilder.toString());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlBuilder.toString(), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.i("String Reponse", "String response is " + response);

                List<Flight> flightList = new ArrayList<>();
                Gson gson = new Gson();

                JsonArray flightsJsonArray = getFlightsRequestJson(response);
                for (JsonElement flightObject : flightsJsonArray)
                {
                    flightList.add(gson.fromJson(flightObject.getAsJsonObject(), Flight.class));
                }
                Log.i(TAG, "flight list has size" + flightList.size());
                flightListLiveData.setValue(flightList);
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

    private JsonObject getFlightInfoRequestJson(String jsonString)
    {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        return jsonElement.getAsJsonObject();
    }

    private JsonArray getFlightsRequestJson(String jsonString)

    {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        return jsonElement.getAsJsonArray();
    }
}
