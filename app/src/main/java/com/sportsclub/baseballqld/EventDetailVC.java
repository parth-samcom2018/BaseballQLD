package com.sportsclub.baseballqld;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sportsclub.baseballqld.models.Event;

public class EventDetailVC extends Fragment {

    public Event event;

    //VIEWS
    private TextView notesTV;
    private TextView titleTV;
    private TextView dateTv;
    private TextView timeTv;
    private TextView locationTV;

    public EventDetailVC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v= inflater.inflate(R.layout.fragment_event_detail_vc, container, false);

        titleTV = v.findViewById(R.id.titleTV);
        dateTv = v.findViewById(R.id.dateTV);
        timeTv = v.findViewById(R.id.timeTV);
        locationTV = v.findViewById(R.id.locationTV);
        notesTV = v.findViewById(R.id.notesTV);

        modelToView();


        return v;
    }

    public void modelToView()
    {
        if(event == null) return;
        titleTV.setText(event.eventName);
        timeTv.setText(event.getTimeString());
        dateTv.setText(event.getDateString());
        locationTV.setText(event.location);
        notesTV.setText(event.notes);
    }

    @Override
    public void onResume() {
        modelToView();
        super.onResume();
    }
}
