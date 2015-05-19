package com.example.paulina.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;


public class CameraActivity  {

    private Camera mCamera;
    private CameraPreview mPrieview;
    private MainActivity mainActivity;
    private MenuItem mItem;
    private boolean camera_available = false;
    private boolean on = false;

    private RectangleView rectangleView;


    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        if(on && camera_available) {
            this.on = true;
            if(mItem != null)
                mItem.setIcon(R.drawable.close_camera);

            rectangleView.setVisibility(View.VISIBLE);
        } else {
            this.on = false;
            if(mItem != null)
                mItem.setIcon(R.drawable.camera);

            rectangleView.setVisibility(View.INVISIBLE);
        }
    }


    public CameraActivity( MainActivity activity) {

        Log.d(getClass().toString(), "Camera ");
        mainActivity = activity;
        mItem = mainActivity.getMenu().findItem(R.id.camera);
        checkCameraHardware(mainActivity.getApplicationContext());

        rectangleView = new RectangleView(activity.getApplicationContext());

    }

    public void onCreate() {
        if(camera_available) {
            Log.d(getClass().toString(), "Camera onCreate");
            mainActivity.setContentView(R.layout.activity_main);

            mCamera = getCameraInstance();
            mPrieview = new CameraPreview(mainActivity, mCamera);
            FrameLayout preview = (FrameLayout) mainActivity.findViewById(R.id.camera_preview);
            preview.addView(mPrieview);
            preview.addView(rectangleView);

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
        try {
            camera = android.hardware.Camera.open();
        } catch (Exception e) {
            System.out.println("AAAAA!!");
        }
        return camera;
    }

}
