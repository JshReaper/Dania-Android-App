package com.example.jshch.daniaandroidapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button toggleBtn;
    boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //We use the onCreate to instantiate all the sensors and the button.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) //Here we "ask" permission to use the camera, this is needed after 6.0
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 4);

        }
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) //This loop makes sure that we give permissions before the app tries to use the camera, preventing a crash.
                == PackageManager.PERMISSION_DENIED){

        }

        active = false;
        toggleBtn = (Button)findViewById(R.id.toggleButton);

        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleService();
            }
        });


    }
    Intent serviceIntent;
    public void ToggleService(){ //quick method to start/stop the background service together with the toggle button.
        if(!active){
            active = true;
            serviceIntent = new Intent(this, FlashService.class);
            startService(serviceIntent);
        }else{
            active = false;
            stopService(serviceIntent);
        }
    }

    //We don't need these since the entire app is background and written in the class "Flash service"
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void onStop(){
        super.onStop();
    }
}
