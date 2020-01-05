package com.example.planeapplication.ui.flightinfo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planeapplication.GlobalActivity;
import com.example.planeapplication.R;
import com.example.planeapplication.data.Flight;
import com.example.planeapplication.ui.flightlist.FlightListAdapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FlightInfoAdapter extends RecyclerView.Adapter<FlightInfoAdapter.FlightViewHolder> {

    private static final String TAG = FlightInfoAdapter.class.getSimpleName();

    List<Flight> mFlightsList = new ArrayList<>();
    FragmentActivity context = null;

    @NonNull
    @Override
    public FlightInfoAdapter.FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = (FragmentActivity) parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        final View flightInfoView = inflater.inflate(R.layout.item_flight, parent, false);

        return new FlightInfoAdapter.FlightViewHolder(flightInfoView);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightInfoAdapter.FlightViewHolder holder, int position)
    {
        Log.i(TAG, mFlightsList.get(position).getCallsign());
        final Flight currFlight = mFlightsList.get(position);

        holder.callSignView.setText(currFlight.getCallsign());

        holder.estDepartureAirportView.setText(currFlight.getEstDepartureAirport());
        holder.estArrivalAirportView.setText(currFlight.getEstArrivalAirport());

        holder.lastSeenView.setText(tsToStringFormat(currFlight.getLastSeen()));
        holder.firstSeenView.setText(tsToStringFormat(currFlight.getFirstSeen()));

        holder.flightTimeView.setText(tsToStringFormatHour(currFlight.getLastSeen()-currFlight.getFirstSeen()));

        holder.icao24 = currFlight.getIcao24();
        holder.begin = currFlight.getFirstSeen();

        Log.i("ICAO", "" + currFlight.getIcao24());
    }



    private String tsToStringFormat(Long ts){
        Timestamp stamp = new Timestamp(ts*1000);
        Date d = new Date(stamp.getTime());
        Log.i("qqqqsdqs", "" + ts);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return sdf.format(d);
    }

    private String tsToStringFormatHour(Long ts){
        Timestamp stamp = new Timestamp(ts*1000);
        Date d = new Date(stamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return sdf.format(d);
    }


    @Override
    public int getItemCount()
    {
        Log.i(TAG, "" + mFlightsList.size());
        return mFlightsList.size();
    }

    public void setFlights(List<Flight> flightsList)
    {
        mFlightsList.clear();
        mFlightsList.addAll(flightsList);
        notifyDataSetChanged();
    }

    class FlightViewHolder extends RecyclerView.ViewHolder
    {
        TextView estDepartureAirportView;
        TextView estArrivalAirportView;
        TextView firstSeenView;
        TextView lastSeenView;
        TextView flightTimeView;
        TextView callSignView;

        String icao24;
        Long begin;

        public FlightViewHolder(@NonNull final View itemView)
        {
            super(itemView);
            callSignView = (TextView) itemView.findViewById(R.id.nameFlight);
            estDepartureAirportView = (TextView) itemView.findViewById(R.id.aeroDep);
            estArrivalAirportView = (TextView) itemView.findViewById(R.id.aeroArr);
            firstSeenView = (TextView) itemView.findViewById(R.id.dateDep);
            lastSeenView = (TextView) itemView.findViewById(R.id.dateArr);
            flightTimeView = (TextView) itemView.findViewById(R.id.timeFlight);
        }
    }
}
