package com.example.astrojet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


public class CameraActivity extends AppCompatActivity implements ConnectionStatusListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);



    }

    @Override
    public void connect() {

    }

    @Override
    public void connectError() {

    }

    @Override
    public void disconnect() {

    }
}