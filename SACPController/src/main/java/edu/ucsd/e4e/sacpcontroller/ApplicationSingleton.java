package edu.ucsd.e4e.sacpcontroller;



import com.hoho.android.usbserial.driver.UsbSerialPort;

import edu.ucsd.e4e.sacpcontroller.model.ConsoleBuffer;
import edu.ucsd.e4e.sacpcontroller.model.MapPointsBuffer;

public class ApplicationSingleton extends android.app.Application {


    static UsbSerialPort port = null;
    static ConsoleBuffer consoleBuffer = null;
    static MapPointsBuffer mapPointsBuffer = null;



    public ConsoleBuffer getConsoleBuffer() {

        if(consoleBuffer == null)
            consoleBuffer = new ConsoleBuffer();

        return consoleBuffer;
    }



    public MapPointsBuffer getMapPointsBuffer() {

        if(mapPointsBuffer == null)
                mapPointsBuffer = new MapPointsBuffer();

        return mapPointsBuffer;
    }




    public UsbSerialPort getPort() {
        return port;
    }

    public void setPort(UsbSerialPort port) {
        this.port = port;
    }








}
