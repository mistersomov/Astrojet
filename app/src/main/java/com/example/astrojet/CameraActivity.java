package com.example.astrojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;


public class CameraActivity extends AppCompatActivity implements ConnectionStatusListener{

    private SensorManager sensorManager;
    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout frameLayout;
    private static final String TAG = "CameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        frameLayout = findViewById(R.id.camera_layout);
        frameLayout.addView(mPreview);

        checkCameraHardware(this);
    }

    @Override
    public void connectLost() {

    }

    private boolean checkCameraHardware(Context context){
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }else{
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open();
        }catch (Exception e){
            Log.e(TAG, "Couldn't open camera", e);
        }
        return c;
    }

    private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera){
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);

        }

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            try{
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }catch (Exception e){
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            if (mHolder.getSurface() == null){
                return;
            }
            try{
                mCamera.stopPreview();
            }catch (Exception e){

            }
            try{
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            mCamera.release();
        }

    }
}