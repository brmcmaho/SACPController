package edu.ucsd.e4e.sacpcontroller.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Observable;

public class ConsoleBuffer extends Observable implements Parcelable {


    private String consoleText = "";




    public void append(String message){
        consoleText += message;
        triggerObservers();
    }

    public String print() {

        return consoleText;
    }


    public void clear()
    {
        consoleText = "";
        triggerObservers();
    }


    private void triggerObservers() {
        setChanged();
        notifyObservers(this);
    }



    //parcelable stuff
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.consoleText);
    }

    public ConsoleBuffer() {
    }

    private ConsoleBuffer(Parcel in) {
        this.consoleText = in.readString();
    }

    public static final Creator<ConsoleBuffer> CREATOR = new Creator<ConsoleBuffer>() {
        public ConsoleBuffer createFromParcel(Parcel source) {
            return new ConsoleBuffer(source);
        }

        public ConsoleBuffer[] newArray(int size) {
            return new ConsoleBuffer[size];
        }
    };
}
