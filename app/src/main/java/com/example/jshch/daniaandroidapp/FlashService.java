package com.example.jshch.daniaandroidapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;


//This the the flashlight service class, this service makes the flashlight app work even when not in focus
public class FlashService extends Service {
    //Sensor classes used
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mLightSensor, mProximity;

        //Custom sensor
    private ShakeDetector mShakeDetector;

    //Fields used to control the camera and it's flashlight.
    private Context context = this;
    private Camera camera;
    private Parameters p;
    private boolean hasFlash, flashIsOn = false, lowLux = false;

    //fields for using the proximity sensor
    private int sensorAccuracy = 1;
    private boolean pocket = false;

    public FlashService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Set a boolean to the camera availability to check it later.
        hasFlash = getApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        //Check if the phone has a camera attachec through the hardware
        if(!hasFlash){
            Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG);
        }else{
            Toast.makeText(this, "Camera detected", Toast.LENGTH_LONG);
            this.camera = Camera.open(0);
            this.p = this.camera.getParameters();
        }

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                handleShakeEvent(count);
            }
        });

        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(mLightSensor != null){
            Toast.makeText(this, "Sensor.TYPE_LIGHT Available", Toast.LENGTH_LONG).show(); //Debug
            mSensorManager.registerListener(LightSensorListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (mProximity != null){
            Toast.makeText(this, "Sensor.TYPE_Proximity Available", Toast.LENGTH_LONG).show(); //Debug
            mSensorManager.registerListener(ProximitySensorListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    private final SensorEventListener ProximitySensorListener
            = new SensorEventListener(){
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                if (event.values[0] >= -sensorAccuracy && event.values[0] <= sensorAccuracy) {
                    //near
                    pocket = true;
                } else {
                    //far
                    pocket = false;
                }
            }
        }
    };

    private final SensorEventListener LightSensorListener
            = new SensorEventListener(){
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                if (event.values[0] < 5) {
                    lowLux = true;
                }else if (event.values[0] > 5){
                    lowLux = false;
                }
            }
        }
    };

    private void handleShakeEvent(int count) {
        if(lowLux && !flashIsOn && !pocket){
            //turn the flashlight on with the torch mode
            //this method is old and should ideally not be used as there has been made a new
            //class for this, however to support devices from older vertions of android we had to use this
            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
            camera.startPreview();
            flashIsOn = true;
        }
        else if(flashIsOn){
            //Turn the flashlight off with the off mode from parameters
            //this method is old and should ideally not be used as there has been made a new
            //class for this, however to support devices from older vertions of android we had to use this
            p.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.startPreview();
            flashIsOn = false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
// Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(LightSensorListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(mShakeDetector);
        mSensorManager.unregisterListener(LightSensorListener);
        p.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(p);
        camera.startPreview();
        camera.release();
        super.onDestroy();

    }
}
