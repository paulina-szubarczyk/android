package com.example.paulina.myapplication;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import org.opencv.android.OpenCVLoader;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.paulina.MESSAGE";
    private CameraView cameraView;
    private Menu mMenu;
    private RectangleView rectangleView;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    public Menu getMenu() {
        return mMenu;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rectangleView = (RectangleView) findViewById(R.id.rectangle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.camera:
                menageDeviceCamera();
                return true;
            case R.id.therm_camera:
                startThermCamera();
                return true;
            case R.id.rectangle_button:
                rectangleView.setChangeable(!rectangleView.isChangeable());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void manageRectangleView() {
        if(rectangleView != null) {
            rectangleView.setVisibility(View.VISIBLE);
            rectangleView.bringToFront();
            FrameLayout frameLayout = (FrameLayout)findViewById(R.id.main);
            System.out.println("\n\nRECTANGLE ON TOP");
        }
    }

    public void menageDeviceCamera() {

        if(cameraView == null) {
            cameraView = new CameraView(this);
        }
        if(cameraView.isOn()) {
            cameraView.onPause();
            cameraView.setOn(false);
        } else {
            cameraView.onCreate();
            cameraView.setOn(true);
        }
        manageRectangleView();
    }

    public void startThermCamera() {
        Intent intent = new Intent(this, ThermAppActivity.class);
        startActivity(intent);
        manageRectangleView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        if(rectangleView.isChangeable()) {
            if (action == MotionEvent.ACTION_DOWN) {
                if (rectangleView.getRectangle().contains(x, y)) {
                    rectangleView.getRectangle().scale(false); // true is scale up, false is scale down
                    rectangleView.invalidate();
                }
            }
            if (action == MotionEvent.ACTION_UP) {
                if (rectangleView.getRectangle().contains(x, y)) {
                    rectangleView.getRectangle().scale(true); // true is scale up, false is scale down
                    rectangleView.invalidate();
                }
            }
        }

        return true;
    }

}