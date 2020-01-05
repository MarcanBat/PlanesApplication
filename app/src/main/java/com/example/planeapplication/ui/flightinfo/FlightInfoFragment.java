package com.example.planeapplication.ui.flightinfo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planeapplication.GlobalActivity;
import com.example.planeapplication.R;
import com.example.planeapplication.data.Flight;
import com.example.planeapplication.data.FlightInfo;
import com.example.planeapplication.data.FlightState;
import com.example.planeapplication.data.FlightTrack;
import com.example.planeapplication.data.FlightTrackPath;
import com.example.planeapplication.ui.flightlist.FlightListAdapter;
import com.example.planeapplication.ui.flightlist.FlightListFragment;
import com.example.planeapplication.ui.flightlist.FlightListViewModel;
import com.example.planeapplication.ui.flighttracking.MapFragment;
import com.example.planeapplication.ui.flighttracking.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlightInfoFragment extends Fragment implements OnMapReadyCallback
{


    private static final String TAG = FlightInfoFragment.class.getSimpleName();

    private static final String TRACK_FLIGHT = "track_flight";
    private static final String ICAO         = "icao";
    private static final String BEGIN        = "begin";
    private GoogleMap mGoogleMap;
    private MapView mapView;
    private FlightInfoAdapter mAdapter;
    private FlightInfoViewModel mViewModel;
    private View root;
    private FlightInfo mFlightInfo;
    private List<LatLng> listPoint = new ArrayList<>();

    private TextView tvVelocity;
    private TextView tvAltitude;
    private TextView tvTraj;
    private TextView tvVolEnCours;
    private TextView tvIcao;


    public static FlightInfoFragment newInstance()
    {
        return new FlightInfoFragment();
    }

    public static FlightInfoFragment newInstance(String icao, Long time)
    {
        FlightInfoFragment fragment = newInstance();
        Bundle b = new Bundle();
        b.putInt(TRACK_FLIGHT, 2);
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

        mViewModel = ViewModelProviders.of(this).get(FlightInfoViewModel.class);
        Bundle arguments = getArguments();

        tvAltitude = root.findViewById(R.id.tv_altitude);
        tvTraj = root.findViewById(R.id.tv_traj);
        tvVelocity = root.findViewById(R.id.tv_vitesse);
        tvVolEnCours = root.findViewById(R.id.tv_volEnCours);
        tvIcao = root.findViewById(R.id.tv_icao);
        tvIcao.setText(arguments.getString(ICAO));

        if(arguments!=null) {
            mViewModel.loadData(arguments.getString(ICAO), arguments.getLong(BEGIN));
            mViewModel.loadDataHisto(arguments.getLong(BEGIN), arguments.getString(ICAO));
        }


        mViewModel.flightListLiveData.observe(getViewLifecycleOwner(), new Observer<List<Flight>>()
        {
            @Override
            public void onChanged(List<Flight> flights)
            {
                Log.i("SIZE", "updating list with size = " + flights.size());
                mAdapter.setFlights(flights);
            }
        });


        mViewModel.flightInfo.observe(getViewLifecycleOwner(), new Observer<FlightInfo>()
        {
            @Override
            public void onChanged(FlightInfo flightInfo)
            {
                mFlightInfo = flightInfo;
                Log.i("flightInfo ", "ICAO" + flightInfo.getListStat());


                for (FlightState fs : mFlightInfo.getListStat()){
                    double vitesse = fs.getVelocity()*3.6;
                    int i_vit = (int)vitesse;
                    String trajectoire = "";
                    tvAltitude.setText(fs.getGeo_altitude().toString() + " m");

                    tvVelocity.setText(i_vit + " km/h");

                    if (fs.getVertical_rate()<0)
                        trajectoire = "Descente";
                    else if(fs.getVertical_rate()>0)
                        trajectoire = "Montée";
                    else
                        trajectoire = "constant";

                    tvTraj.setText(trajectoire);
                    tvVolEnCours.setText(" Oui");

                    listPoint.add(new LatLng(fs.getLatitude(), fs.getLongitude()));
                }
                Log.i("listPoint", listPoint.toString());
                googleMap.addMarker(new MarkerOptions().position(listPoint.get(listPoint.size()-1)).title("l'avion"));
                CameraPosition cam = CameraPosition.builder().target(listPoint.get(listPoint.size()-1)).zoom(6).bearing(0).tilt(0).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam));
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        root = inflater.inflate(R.layout.fragment_info_realtime_plane, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.rv_info);
        mAdapter = new FlightInfoAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
        return root;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) root.findViewById(R.id.map_position);
        if (mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }
}
