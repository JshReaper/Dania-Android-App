package com.example.jshch.daniaandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button toggleBtn;
    boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        active = false;
        toggleBtn = (Button)findViewById(R.id.toggleButton);

        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleService();
            }
        });


    }

    public void ToggleService(){
        if(!active){
            startService(this.getIntent());
        }else{
            stopService(this.getIntent());
        }
    }

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
