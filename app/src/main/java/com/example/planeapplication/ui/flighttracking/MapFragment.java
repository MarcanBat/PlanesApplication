package com.example.planeapplication.ui.flighttracking;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.planeapplication.GlobalActivity;
import com.example.planeapplication.R;

import com.example.planeapplication.data.FlightTrack;
import com.example.planeapplication.data.FlightTrackPath;
import com.example.planeapplication.ui.flightlist.FlightListFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;

import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;

import com.google.android.gms.maps.model.RoundCap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener
{
    private static final int COLOR_BLACK_ARGB = 0xff000000;


    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);




    private static final String TAG = MapFragment.class.getSimpleName();

    private static final String TRACK_FLIGHT = "track_flight";
    private static final String ICAO         = "icao";
    private static final String BEGIN        = "begin";
    private GoogleMap mGoogleMap;
    private MapView mapView;
    private MapViewModel mViewModel;
    private View root;
    private FlightTrack mFlightTrack;
    private List<LatLng> listPoint = new ArrayList<>();
    private Button btDetailPlane;

    public static MapFragment newInstance()
    {
        return new MapFragment();
    }

    public static MapFragment newInstance(String icao, Long time)
    {
        MapFragment fragment = newInstance();
        Bundle b = new Bundle();
        b.putBoolean(TRACK_FLIGHT, true);
        b.putLong(BEGIN, time);
        b.putString(ICAO, icao);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        Bundle arguments = getArguments();
        if(arguments!=null)
            mViewModel.loadData(arguments.getString(ICAO), arguments.getLong(BEGIN));

        mViewModel.flightTrack.observe(getViewLifecycleOwner(), new Observer<FlightTrack>()
        {
            @Override
            public void onChanged(FlightTrack flightTrack)
            {
                mFlightTrack = flightTrack;
                Log.i("TRACKING ", "ICAO" + flightTrack.getListPath());


                for (FlightTrackPath ftp : mFlightTrack.getListPath()){
                    listPoint.add(new LatLng(ftp.getLat(), ftp.getLon()));
                }
                Log.i("azjeklajzle", listPoint.toString());
                Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .addAll(listPoint));
                 stylePolyline(polyline1);
                CameraPosition cam = CameraPosition.builder().target(listPoint.get(0)).zoom(4).bearing(0).tilt(0).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam));

                btDetailPlane = root.findViewById(R.id.bt_detail);
                btDetailPlane.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        doSearchInfo();
                    }
                });
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        root = inflater.inflate(R.layout.flight_tracking_fragment, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) root.findViewById(R.id.map);
        if (mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }



    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }


    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(getContext(), "Route type " + polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Listens for clicks on a polygon.
     * @param polygon The polygon object that the user has clicked.
     */
    @Override
    public void onPolygonClick(Polygon polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        int color = polygon.getStrokeColor() ^ 0x00ffffff;
        polygon.setStrokeColor(color);
        color = polygon.getFillColor() ^ 0x00ffffff;
        polygon.setFillColor(color);

        Toast.makeText(getContext(), "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
    }


    private void doSearchInfo()
    {
        Long dateActuelle = System.currentTimeMillis()/1000;
        Log.i(TAG,mFlightTrack.getIcao());
        GlobalActivity.startActivity(getActivity(), mFlightTrack.getIcao(), dateActuelle, 2);
    }
}