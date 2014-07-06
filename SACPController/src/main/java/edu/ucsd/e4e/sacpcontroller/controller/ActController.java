package edu.ucsd.e4e.sacpcontroller.controller;


import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ucsd.e4e.sacpcontroller.ApplicationSingleton;
import edu.ucsd.e4e.sacpcontroller.Constants;
import edu.ucsd.e4e.sacpcontroller.model.ConsoleBuffer;
import edu.ucsd.e4e.sacpcontroller.model.MapPoint;
import edu.ucsd.e4e.sacpcontroller.model.MapPointsBuffer;
import edu.ucsd.e4e.sacpcontroller.view.FragConsole;
import edu.ucsd.e4e.sacpcontroller.view.FragControlButtons;
import edu.ucsd.e4e.sacpcontroller.R;
import edu.ucsd.e4e.sacpcontroller.view.FragMap;


public class ActController extends Activity implements FragControlButtons.Callbacks {


    //map point spec: map,lat,lng,time (ex: "map,32.981059,-117.26751,1404428728")
    private static final String MAP_POINT_PREFIX = "map";


    private FragConsole mConsoleFrag;
    private FragMap mMapFrag;


    private String lineBuffer = "";


    private UsbSerialPort port;
    private ConsoleBuffer consoleBuffer;
    private MapPointsBuffer mapPointsBuffer;


    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;

    private boolean mMapIsShowing =  false;

    private static final String TAG = "ActController";


    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateReceivedData(data);
                        }
                    });
                }
            };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.act_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);



        //restorePersistedData(savedInstanceState);

        //get handle to console so we can append
        mConsoleFrag = new FragConsole();
        mMapFrag = new FragMap();


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.upper_container, new FragControlButtons()) //add buttons to upper container
                    .add(R.id.lower_container, mConsoleFrag) //add already created console to lower container
                    .commit();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        stopIoManager();
        if (port != null) {
            try {
                port.close();
            } catch (IOException e) {
                // Ignore.
            }
            port = null;
        }
        finish();
    }


    @Override
    public void onResume() {
        super.onResume();


        //get static android application object
        ApplicationSingleton app = (ApplicationSingleton) getApplication();

        consoleBuffer = app.getConsoleBuffer();

        mapPointsBuffer = app.getMapPointsBuffer();

        //get stored port
        port = app.getPort();


        connectToSerialDevice();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_map_toggle:
                if(mMapIsShowing) {
                    //show console
                    getFragmentManager().beginTransaction()
                            .remove(mMapFrag)
                            .add(R.id.lower_container, mConsoleFrag) //add already created console to lower container
                            .commit();
                    mMapIsShowing = false;
                }else{
                    getFragmentManager().beginTransaction()
                            .remove(mConsoleFrag)
                            .add(R.id.lower_container, mMapFrag) //add already created console to lower container
                            .commit();
                    mMapIsShowing = true;
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void connectToSerialDevice() {
        Log.d(TAG, "Resumed, port=" + port);
        if (port == null) {
            Log.e(TAG, "No serial device.");    //TODO relaunch chooser
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(port.getDriver().getDevice());
            if (connection == null) {
                Log.e(TAG, "Opening device failed");
                return;
            }

            try {
                port.open(connection);
                port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                processReceivedLine("Error opening device: " + e.getMessage());    //TODO relaunch chooser
                try {
                    port.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                port = null;
                return;
            }
            processReceivedLine("Serial device: " + port.getClass().getSimpleName());
            Log.i(TAG, "Serial device: " + port.getClass().getSimpleName());
        }
        onDeviceStateChange();
    }


    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (port != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(port, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {

        if (data[0] == 13) { //if we got newline, print and reset line buffer
            processReceivedLine(lineBuffer);
            lineBuffer = "";
        } else {

            lineBuffer += new String(data);
        }
    }

    private void processReceivedLine(String text) {

        //if line is a map point being reported
        if (text.startsWith(MAP_POINT_PREFIX)) {
            addMapPointFromLine(text);
        }

        //print line to console
        consoleBuffer.append(text+'\n');
    }

    private void addMapPointFromLine(String text) {

        try {
            String[] tokens = text.split(",");

            Double latitude = Double.valueOf(tokens[1]);
            Double longitude = Double.valueOf(tokens[2]);
            long timestamp = Long.valueOf(tokens[3]);


            MapPoint point = new MapPoint(latitude, longitude, timestamp);


            mapPointsBuffer.add(point);

            Log.d(TAG, "Adding " + point);


        } catch (Exception e) {
            Log.e(TAG, "Tried to add map point and failed");
            e.printStackTrace();
        }


    }


    //callback for FragControlButtons to report button clicks
    //set how we react to buttons here
    @Override
    public void onButtonClick(char button) {
        String debugMsg = "";

        //when a button is pressed, set debug message based on id
        switch (button) {
            case Constants.ROLL_LEFT:
                debugMsg = "Rolling left 5 degrees";
                break;
            case Constants.ROLL_RIGHT:
                debugMsg = "Rolling right 5 degrees";
                break;
            case Constants.YAW_LEFT:
                debugMsg = "Yawing left 10 degrees";
                break;
            case Constants.YAW_RIGHT:
                debugMsg = "Yawing right 10 degrees";
                break;
            case Constants.PITCH_UP:
                debugMsg = "Pitching up 5 degrees";
                break;
            case Constants.PITCH_DOWN:
                debugMsg = "Pitching down 5 degrees";
                break;
            case Constants.TAKE_PICTURE:
                debugMsg = "Taking Picture";
                break;
            case Constants.MULTI_SHOT:
                debugMsg = "Multi-shot enabled";
                break;
            case Constants.AUTO_FOCUS:
                debugMsg = "Auto-focus enabled";
                break;
            case Constants.MULTI_SHOT_OFF:
                debugMsg = "Multi-shot disabled";
                break;
            case Constants.AUTO_FOCUS_OFF:
                debugMsg = "Auto-focus disabled";
                break;
            case Constants.FILTER:
                debugMsg = "Filter enabled";
                break;
            case Constants.STABILIZATION:
                debugMsg = "Stabilization enabled";
                break;
            case Constants.RESET:
                debugMsg = "Reseting Arduino";
                break;
            default:
                Log.d("SACP Controller", "Unknown button press");
                break;
        }

        sendOverSerial(button);


        //report message to debug log
        Log.d("SACP Controller", debugMsg);

    }


    private void sendOverSerial(char button) {


        String n = "" + button;


        mSerialIoManager.writeAsync(n.getBytes());

    }


//    static void show(Context context, UsbSerialPort port) {
//        mPort = port;
//        final Intent intent = new Intent(context, ActController.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
//        context.startActivity(intent);
//    }


//data persistence
//
//    private static final String PERSISTED_CONSOLE_BUFFER = "PERSISTED_CONSOLE_BUFFER";
//    private static final String PERSISTED_MAP_POINTS_BUFFER = "PERSISTED_MAP_POINTS_BUFFER";
//    private static final String PERSISTED_DATA_BUNDLE = "PERSISTED_DATA_BUNDLE";
//
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        persistData(outState);
//    }
//
//    private void persistData(Bundle outState) {// All Parcelable objects of the API  (eg. LatLng, MarkerOptions, etc.) can be set
//        // directly in the given Bundle.
//        //outState.putParcelable(SOME_API_PARCELABLE, mApiParcelable);
//
//        // All custom Parcelable objects must be wrapped in another Bundle. Indeed,
//        // failing to do so would throw a ClassNotFoundException. This is due to the fact that
//        // this Bundle is being parceled (losing its ClassLoader at this time) and unparceled
//        // later in a different ClassLoader.
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(PERSISTED_CONSOLE_BUFFER, consoleBuffer);
//        //bundle.putParcelable(PERSISTED_MAP_POINTS_BUFFER, mMapPointsBuffer);
//        outState.putBundle(PERSISTED_DATA_BUNDLE, bundle);
//
//
//    }
//
//    private void restorePersistedData(Bundle savedInstanceState) {
//
//        //if there is no saved bundle, use dummy
//        Bundle bundle = ((savedInstanceState != null) ? savedInstanceState.getBundle(PERSISTED_DATA_BUNDLE) : new Bundle());
//
//        //restore data, or create new buffer if null
//        if ((consoleBuffer = bundle.getParcelable(PERSISTED_CONSOLE_BUFFER)) == null) {
//            consoleBuffer = ((ApplicationSingleton)getApplication()).getConsoleBuffer();
//        }
//
////        //restore data, or create new buffer if null
////        if ((mMapPointsBuffer = bundle.getParcelable(PERSISTED_MAP_POINTS_BUFFER)) == null)
////            mMapPointsBuffer = new MapPointsBuffer();
//
//
//    }


}
