package com.example.planeapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.planeapplication.ui.flightlist.FlightListFragment;
import com.example.planeapplication.ui.flighttracking.MapFragment;

import androidx.appcompat.app.AppCompatActivity;

public class GlobalActivity extends AppCompatActivity
{
    private static final String TRACK_FLIGHT = "track_flight";
    private static final String BEGIN        = "begin";
    private static final String END          = "end";
    private static final String IS_ARRIVAL   = "isArrival";
    private static final String ICAO         = "icao";

    public static void startActivity(Activity activity, long begin, long end, boolean isArrival, String icao)
    {
        Intent i = new Intent(activity, GlobalActivity.class);
        i.putExtra(BEGIN, begin);
        i.putExtra(END, end);
        i.putExtra(IS_ARRIVAL, isArrival);
        i.putExtra(ICAO, icao);
        activity.startActivity(i);
    }

    public static void startActivity(Activity activity, String icao, Long date)
    {
        Intent i = new Intent(activity, GlobalActivity.class);
        i.putExtra(TRACK_FLIGHT, true);
        i.putExtra(BEGIN, date);
        i.putExtra(ICAO, icao);
        activity.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_activity);

        Intent intent = getIntent();
        boolean trackingFlight = intent.getBooleanExtra(TRACK_FLIGHT, false);
        String icao = intent.getStringExtra(ICAO);

        long begin = intent.getLongExtra(BEGIN, -1);
        long end = intent.getLongExtra(END, -1);
        boolean isArrival = intent.getBooleanExtra(IS_ARRIVAL, false);

        if (trackingFlight) {
            Log.i("azejakzejlkazje ", icao);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, MapFragment.newInstance(icao, begin)).commitNow();
        }
        else if (savedInstanceState == null)
        {
            Log.i("azejakzejlsqdqsjkazje ", icao);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, FlightListFragment.newInstance(begin, end, isArrival, icao)).commitNow();
        }


    }
}
