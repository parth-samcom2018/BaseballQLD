package com.sportsclub.baseballqld;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationVC extends Fragment {


    public double longitude;
    public double lattitude;
    private GoogleMap map;

    public LocationVC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_location_vc, container, false);



        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(lattitude,longitude));
                options.title("Location");
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mappin));

                map.addMarker(options);
                //   map.getUiSettings().setMyLocationButtonEnabled(false);
                // map.setMyLocationEnabled(true);

                // Updates the location and zoom of the MapView

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude, longitude), 14);
                map.animateCamera(cameraUpdate);

            }
        });




        return v;
    }

}

