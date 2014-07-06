package edu.ucsd.e4e.sacpcontroller.view;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.e4e.sacpcontroller.ApplicationSingleton;
import edu.ucsd.e4e.sacpcontroller.R;
import edu.ucsd.e4e.sacpcontroller.model.MapPoint;
import edu.ucsd.e4e.sacpcontroller.model.MapPointsBuffer;

public class FragMap extends Fragment implements Observer {


    public MapView mMapView;
    private GoogleMap mMap;

    private MapPointsBuffer mMapPointsBuffer;


    public FragMap() {

    }




    @Override
    public void update(Observable observable, Object data) {
        if(observable instanceof MapPointsBuffer){
            updateMapMarkers((MapPointsBuffer)data);
        }

    }

    private void updateMapMarkers(MapPointsBuffer data) {
        Log.e("*************", "UPDATING MAP MARKERS");
        for(MapPoint p: data.getMapPoints()){

            Log.e("*****", "updating point");

            mMap.addMarker(new MarkerOptions()
                    .position(p.getLatLng())
                    .title(""+p.getTimestamp()));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_map, container, false);
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        setUpMapIfNeeded();


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        setUpMapIfNeeded();

        //Get data model from appplication class
        mMapPointsBuffer = ((ApplicationSingleton) getActivity().getApplication()).getMapPointsBuffer();

        //initial update
        updateMapMarkers(mMapPointsBuffer);

        //register observation of data model
        mMapPointsBuffer.addObserver(this);




    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();


        //register observation of data model
        mMapPointsBuffer.deleteObserver(this);
    }


    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = mMapView.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);


            } else {
                Log.e("GoogleMaps:", "Failed to set up map fragment");
            }
        }
    }


}