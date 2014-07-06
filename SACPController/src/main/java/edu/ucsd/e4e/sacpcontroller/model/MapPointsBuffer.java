package edu.ucsd.e4e.sacpcontroller.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Observable;

//TODO
public class MapPointsBuffer extends Observable implements Parcelable {

    public ArrayList<MapPoint> getMapPoints() {
        return mapPoints;
    }

    private ArrayList<MapPoint> mapPoints;

    public void add(MapPoint point){
        mapPoints.add(point);
        triggerObservers();
    }

    public void clear(){
        mapPoints.clear();
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mapPoints);
    }

    public MapPointsBuffer() {
        mapPoints = new ArrayList<MapPoint>();
    }

    private MapPointsBuffer(Parcel in) {
        this.mapPoints = (ArrayList<MapPoint>) in.readSerializable();
    }

    public static final Parcelable.Creator<MapPointsBuffer> CREATOR = new Parcelable.Creator<MapPointsBuffer>() {
        public MapPointsBuffer createFromParcel(Parcel source) {
            return new MapPointsBuffer(source);
        }

        public MapPointsBuffer[] newArray(int size) {
            return new MapPointsBuffer[size];
        }
    };
}
