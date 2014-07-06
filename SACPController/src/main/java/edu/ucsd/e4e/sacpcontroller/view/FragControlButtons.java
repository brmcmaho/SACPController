package edu.ucsd.e4e.sacpcontroller.view;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import edu.ucsd.e4e.sacpcontroller.Constants;
import edu.ucsd.e4e.sacpcontroller.R;


public class FragControlButtons extends Fragment implements View.OnClickListener {


    private Callbacks mCallbacks;


    private ImageButton vRollLeft;
    private ImageButton vRollRight;
    private ImageButton vYawLeft;
    private ImageButton vYawRight;
    private ImageButton vPitchUp;
    private ImageButton vRPitchDown;
    private ImageButton vTakePicture;
    private ImageButton vMultiShot;
    private Button vReset;
    private Button vAutoFocus;
    private Button vFilter;
    private Button vStabilization;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        //callback for long press on map
        public void onButtonClick(char button);

    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onButtonClick(char button) {
        }

    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_buttons, container, false);


        try {
            //get handles to buttons, and set the fragment as onClick listener for each
            (vRollLeft = (ImageButton) view.findViewById(R.id.roll_left)).setOnClickListener(this);
            (vRollRight = (ImageButton) view.findViewById(R.id.roll_right)).setOnClickListener(this);
            (vYawLeft = (ImageButton) view.findViewById(R.id.yaw_left)).setOnClickListener(this);
            (vYawRight = (ImageButton) view.findViewById(R.id.yaw_right)).setOnClickListener(this);
            (vPitchUp = (ImageButton) view.findViewById(R.id.pitch_up)).setOnClickListener(this);
            (vRPitchDown = (ImageButton) view.findViewById(R.id.pitch_down)).setOnClickListener(this);
            (vTakePicture = (ImageButton) view.findViewById(R.id.take_picture)).setOnClickListener(this);
            (vMultiShot = (ImageButton) view.findViewById(R.id.multi_shot)).setOnClickListener(this);
            (vAutoFocus = (Button) view.findViewById(R.id.auto_focus)).setOnClickListener(this);
            (vFilter = (Button) view.findViewById(R.id.filter)).setOnClickListener(this);
            (vStabilization = (Button) view.findViewById(R.id.stabilization)).setOnClickListener(this);
            (vReset = (Button) view.findViewById(R.id.reset)).setOnClickListener(this);
        } catch (NullPointerException e) {
            Log.e("FragControlButtons", "Null pointer when finding button views");
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.roll_left:
                mCallbacks.onButtonClick(Constants.ROLL_LEFT);
                break;
            case R.id.roll_right:
                mCallbacks.onButtonClick(Constants.ROLL_RIGHT);
                break;
            case R.id.yaw_left:
                mCallbacks.onButtonClick(Constants.YAW_LEFT);
                break;
            case R.id.yaw_right:
                mCallbacks.onButtonClick(Constants.YAW_RIGHT);
                break;
            case R.id.pitch_up:
                mCallbacks.onButtonClick(Constants.PITCH_UP);
                break;
            case R.id.pitch_down:
                mCallbacks.onButtonClick(Constants.PITCH_DOWN);
                break;
            case R.id.take_picture:
                mCallbacks.onButtonClick(Constants.TAKE_PICTURE);
                break;
            case R.id.multi_shot:
                if (vMultiShot.isSelected()) { //if multishot was on

                    vMultiShot.setSelected(false); //turn it off
                    mCallbacks.onButtonClick(Constants.MULTI_SHOT_OFF);

                } else { //if multishot was off
                    vMultiShot.setSelected(true); //turn it on
                    mCallbacks.onButtonClick(Constants.MULTI_SHOT);
                }
                break;
            case R.id.auto_focus:
                if (vAutoFocus.isSelected()) { //if autofocus was on
                    vAutoFocus.setSelected(false); //turn it off
                    mCallbacks.onButtonClick(Constants.AUTO_FOCUS_OFF);

                } else { //if autofocus was off
                    vAutoFocus.setSelected(true); //turn it on
                    mCallbacks.onButtonClick(Constants.AUTO_FOCUS);
                }
                break;
            case R.id.filter:
                mCallbacks.onButtonClick(Constants.FILTER);
                break;
            case R.id.stabilization:
                mCallbacks.onButtonClick(Constants.STABILIZATION);
                break;
            case R.id.reset:
                mCallbacks.onButtonClick(Constants.RESET);
                break;
        }

    }

}
