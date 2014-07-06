package edu.ucsd.e4e.sacpcontroller.view;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.e4e.sacpcontroller.ApplicationSingleton;
import edu.ucsd.e4e.sacpcontroller.R;
import edu.ucsd.e4e.sacpcontroller.model.ConsoleBuffer;

public class FragConsole extends Fragment implements Observer{


    private TextView vConsole;


    private ConsoleBuffer mConsoleBuffer;

    private ScrollView vScrollView;

    public FragConsole() {

    }







    @Override
    public void onResume() {
        super.onResume();


        //Get data model from appplication class
        mConsoleBuffer = ((ApplicationSingleton)getActivity().getApplication()).getConsoleBuffer();

        //register observation of data model
        mConsoleBuffer.addObserver(this);

        //initial update
        vConsole.setText(mConsoleBuffer.print());
    }

    @Override
    public void onPause() {
        super.onPause();

        mConsoleBuffer.deleteObserver(this);

    }










    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_console, container, false);


        try {
            //get handles to buttons, and set the fragment as onClick listener for each
            vConsole = (TextView) view.findViewById(R.id.console);
            vScrollView = (ScrollView) view.findViewById(R.id.scrollView);

        } catch (NullPointerException e) {
            Log.e("FragConsole", "Null pointer when finding console views");
            e.printStackTrace();
        }

        return view;
    }



    @Override
    public void update(Observable observable, Object data) {

        vConsole.setText(((ConsoleBuffer)data).print());
        vScrollView.smoothScrollTo(0, vConsole.getBottom());
    }
}
