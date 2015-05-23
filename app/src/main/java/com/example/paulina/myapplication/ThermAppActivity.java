package com.example.paulina.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.hardware.usb.UsbManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

import thermapp.sdk.ThermAppAPI;
import thermapp.sdk.ThermAppAPI_Callback;


public class ThermAppActivity extends ActionBarActivity implements ThermAppAPI_Callback {

    private ThermAppAPI mDeviceSdk = null;
    private BroadcastReceiver mUsbReceiver;
    private BitmapDrawable mDrawer;

    private RelativeLayout relativeLayout;
    private TemperatureConverter temperature;
    private FileDumper fileDumper;
    private CameraView cameraView;
    private Menu mMenu;
    private RectangleView rectangleView;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    private boolean InitSdk() {
        if(mDeviceSdk == null)
            mDeviceSdk = new ThermAppAPI(this);

        try {
            mDeviceSdk.ConnectToDevice();
        } catch (Exception e) {
            mDeviceSdk = null;
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        rectangleView.invalidate();

        if(rectangleView.isChangeable()) {
            if (action == MotionEvent.ACTION_DOWN) {

                if (rectangleView.getRectangle().contains(x, y)) {
                    rectangleView.getRectangle().scale(false); // true is scale up, false is scale down
                }
            }
            if (action == MotionEvent.ACTION_UP) {
                if (rectangleView.getRectangle().contains(x, y)) {
                    rectangleView.getRectangle().scale(true); // true is scale up, false is scale down
                }
            }
        }

        return true;
    }
    public Menu getMenu() {
        return mMenu;
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
                return true;
            case R.id.rectangle_button:
                rectangleView.setChangeable(!rectangleView.isChangeable());
                if(rectangleView.isChangeable())
                    rectangleView.setVisibility(View.VISIBLE);
                else
                    rectangleView.setVisibility(View.INVISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void menageDeviceCamera() {

        if(cameraView == null) {
            cameraView = new CameraView(this);
        }
        if(cameraView.isOn()) {
            cameraView.onPause();
            cameraView.setOn(false);
            rectangleView.bringToFront();

            relativeLayout.invalidate();
            rectangleView.visibilityStatus();
        } else {
            cameraView.onCreate();
            cameraView.setOn(true);
            rectangleView.bringToFront();

            relativeLayout.invalidate();
            rectangleView.visibilityStatus();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therm_app);
        thermCreate();
        mainActivity();
    }

    private void mainActivity() {
        rectangleView = (RectangleView) findViewById(R.id.rectangle);
        relativeLayout = (RelativeLayout) findViewById(R.id.main);
        rectangleView.bringToFront();
        relativeLayout.invalidate();
        rectangleView.visibilityStatus();
        drawLegend();
    }

    private void drawLegend() {
        ImageView legend = (ImageView) findViewById(R.id.legend);
        legend.setImageBitmap(temperature.getLegend(40));
        TextView min = (TextView) findViewById(R.id.min_legend);
        TextView max = (TextView) findViewById(R.id.max_legend);
//        min.setText((int)temperature.getMinTemperature());
//        max.setText((int)temperature.getMaxTemperature());
    }

    private void thermCreate() {
        mDrawer = new BitmapDrawable((ImageView) findViewById(R.id.imageView1));
        temperature = new TemperatureConverter(getApplicationContext());
        fileDumper = new FileDumper("thermapp");

        if(InitSdk()) {
            init();

            // Define USB detached event receiver
            if (mUsbReceiver == null) {
                mUsbReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent
                                .getAction())) ;
                    }
                };
            }

            // Listen for new devices
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(mUsbReceiver, filter);

        }
    }

    private void init(){

        try{
            final Intent intent = new Intent(this, ThermDataActivity.class);
            intent.putExtra("serialnum", Integer.toString(mDeviceSdk.GetSerialNumber()));
            startActivityForResult(intent, 1);

        } catch(Exception e) {

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        onCreate(new Bundle());
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDrawer.pause();
    }

    @Override
    protected  void onStop() {
        try {
            unregisterReceiver(mUsbReceiver);
        } catch (Exception e) {

        }
        super.onStop();
    }

    @Override
    public void OnFrameGetThermAppBMP(Bitmap bitmap) {
        //mDrawer.post(bitmap);
    }

    @Override
    public void OnFrameGetThermAppTemperatures(int[] ints, int i, int i1) {
        Bitmap bitmap = temperature.convertTemperature(ints, i, i1);
        mDrawer.post(bitmap);
        rectangleView.bringToFront();
        fileDumper.dumpScreen(ints,i,i1);
    }

    public void startThermCamera(){
        try {
            mDeviceSdk.StartVideo();
        } catch (Exception e) {
            // Report error to use
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");

                if (result.equals("OK")) {
                    startThermCamera();
                } else if (result.equals("EXIT")) {
                }
            }
            if (resultCode == RESULT_CANCELED) {

            }
        }
    }
}
