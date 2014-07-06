package edu.ucsd.e4e.sacpcontroller.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class MapPoint implements Parcelable {

    private double latitude;
    private double longitude;
    private long timestamp;
    private LatLng latLng;

    public LatLng getLatLng() {
        return latLng;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }



    public MapPoint(double latitude, double longitude, long timeStamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timeStamp;
        this.latLng = new LatLng(latitude, longitude);
    }



    public String toString(){
        return "MapPoint- Lat: "+latitude+" Lng: "+longitude+" Time: "+ timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeLong(this.timestamp);
        dest.writeParcelable(this.latLng, flags);
    }

    private MapPoint(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.timestamp = in.readLong();
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Parcelable.Creator<MapPoint> CREATOR = new Parcelable.Creator<MapPoint>() {
        public MapPoint createFromParcel(Parcel source) {
            return new MapPoint(source);
        }

        public MapPoint[] newArray(int size) {
            return new MapPoint[size];
        }
    };
}
