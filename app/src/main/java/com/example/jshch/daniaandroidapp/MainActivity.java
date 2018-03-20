package com.example.jshch.daniaandroidapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.widget.Toast;

import java.lang.reflect.Parameter;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mLightSensor;
    private ShakeDetector mShakeDetector;

    Context context = this;
    Camera camera;
    Parameters p;
    boolean hasFlash;
    boolean flashIsOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasFlash = getApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if(!hasFlash){
            Toast.makeText(MainActivity.this, "No camera on device", Toast.LENGTH_LONG);
        }else{
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
            Toast.makeText(this,
                    "Sensor.TYPE_LIGHT Available",
                    Toast.LENGTH_LONG).show();
            mSensorManager.registerListener(
                    LightSensorListener,
                    mLightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }else{

        }
    }

    private final SensorEventListener LightSensorListener
            = new SensorEventListener(){
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                //textLIGHT_reading.setText("LIGHT: " + event.values[0]);
            }
        }
    };

    @Override
    public void onResume() {
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    protected void onStop(){
        super.onStop();

    }

    private void handleShakeEvent(int count) {
        Flashlight();
    }

    private void Flashlight(){
        if(!flashIsOn){
            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
            camera.startPreview();
            flashIsOn = true;

        }else{
            p.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.startPreview();
            flashIsOn = false;
        }
    }

}
