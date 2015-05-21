package com.example.paulina.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


public class CameraActivity  {

    private Camera mCamera;
    private CameraPreview mPrieview;
    private MainActivity mainActivity;
    private MenuItem mItem;
    private boolean camera_available = false;
    private boolean on = false;

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        if(on && camera_available) {
            this.on = true;
            if(mItem != null)
                mItem.setIcon(R.drawable.close_camera);

        } else {
            this.on = false;
            if(mItem != null)
                mItem.setIcon(R.drawable.camera);

        }
    }


    public CameraActivity( MainActivity activity) {

        Log.d(getClass().toString(), "Camera ");
        mainActivity = activity;
        mItem = mainActivity.getMenu().findItem(R.id.camera);
        checkCameraHardware(mainActivity.getApplicationContext());
    }

    public void onCreate() {
        if(camera_available) {
            Log.d(getClass().toString(), "Camera onCreate");
            mainActivity.setContentView(R.layout.activity_main);

            mCamera = getCameraInstance();
            if(mCamera != null) {
                mPrieview = new CameraPreview(mainActivity, mCamera);
                FrameLayout preview = (FrameLayout) mainActivity.findViewById(R.id.camera_preview);
                preview.addView(mPrieview);
            }

        }
    }

    protected void onPause() {

        Log.d(getClass().toString(), "Camera onPause");
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mPrieview = null;
            mCamera = null;
            FrameLayout preview = (FrameLayout) mainActivity.findViewById(R.id.camera_preview);
            preview.removeAllViews();
        }
    }

    private void checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            camera_available = true;
        }
    }

    public static android.hardware.Camera getCameraInstance() {
        System.out.println("camera instance");
        android.hardware.Camera camera = null;
        for(int i=0; i<100; ++i) {
            try {
                camera = android.hardware.Camera.open();
                break;
            } catch (Exception e) {
                System.out.println("AAAAA!!");
            }
        }
        return camera;
    }

}
