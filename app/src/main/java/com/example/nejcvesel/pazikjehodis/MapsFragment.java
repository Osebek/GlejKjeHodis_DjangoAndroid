package com.example.nejcvesel.pazikjehodis;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nejcvesel.pazikjehodis.retrofitAPI.Models.Location;
import com.example.nejcvesel.pazikjehodis.retrofitAPI.Models.LocationInterface;
import com.example.nejcvesel.pazikjehodis.retrofitAPI.ServiceGenerator;
import com.facebook.AccessToken;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brani on 12/19/2016.
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback{
    private final static String TAG_FRAGMENT = "MapsFragment";

    private GoogleMap mMap;
    HashMap<Marker,Location> markerLocationMap  = new HashMap<Marker,Location>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = MapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map,mapFragment).commit();
        //MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ljubljana = new LatLng(46.056946, 14.505751);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ljubljana));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 12.0f ) );

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                ((MainActivity)getActivity()).OpenFormFragment();
                return true;
            }
        });



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                MainActivity main = (MainActivity)getActivity();
                Marker marker = null;
                if(main.isMarker()){
                    main.RemoveMarker();
                }
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(200f))
                        .title("Hello world"));
                main.AddMarker(marker);
            }




        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener ()
        {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Location loc = markerLocationMap.get(marker);
                if (loc != null) {
                    Fragment fragment = LocationDetailFragment.newInstance(loc);
                    FragmentManager fragmentManager = (getActivity()).getFragmentManager();
                    FragmentTransaction fragmentTransaction =
                            fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment,"LocationDetail");
                    fragmentTransaction.addToBackStack("LocationDetail");
                    fragmentTransaction.commit();
                }
                else
                {
                    ((MainActivity)getActivity()).OpenFormFragment();

                }

                return true;
            }




        });




        List<Location> locations = new ArrayList<Location>();
        LocationInterface service =
                ServiceGenerator.createService(LocationInterface.class, MainActivity.authToken);

        Call<List<Location>> call = service.getAllLocations();
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                List<Location> locations = response.body();
                for (Location loc : locations)
                {
                    LatLng lok = new LatLng(Double.valueOf(loc.getLatitude()),Double.valueOf(loc.getLongtitude()));
                    Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(lok)
                    .title(loc.getTitle()));
                    markerLocationMap.put(marker,loc);

                }

            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                System.out.println("Fetching locations did not work");
            }
        });


    }




}
