package com.example.paulina.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

import thermapp.sdk.ThermAppAPI;
import thermapp.sdk.ThermAppAPI_Callback;


public class ThermAppActivity extends Activity implements ThermAppAPI_Callback {

    private RelativeLayout relativeLayout;
    private CameraView cameraView;
    private Menu mMenu;
    private RectangleView rectangleView;
    private VIEW_MODE mode;

    private ThermAppAPI mDeviceSdk = null;
    private BroadcastReceiver mUsbReceiver;
    private BitmapDrawable mDrawer;
    private FileDumper fileDumper;
    private TemperatureConverter temperature;
    private boolean TAKE_PHOTO;
    private  TextUpdater max_minText;
    enum VIEW_MODE {
        THERM_CAMERA,
        CAMERA,
        GRAPHS
    }

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therm_app);

        mode = VIEW_MODE.THERM_CAMERA;
        thermCreate();
        mainActivity();
    }

    private void mainActivity() {
        rectangleView = (RectangleView) findViewById(R.id.rectangle);
        relativeLayout = (RelativeLayout) findViewById(R.id.main);
        rectangleView.setVisibility(View.INVISIBLE);
        relativeLayout.invalidate();
        rectangleView.setCursor((ImageView) findViewById(R.id.cursor));
        RectF rect = rectangleView.getRectangle().getRectangle();
        temperature.setAnalysedRectangle((int) rect.left, (int)rect.top,(int)rect.right,(int)rect.bottom );
        rectangleView.addObserver(temperature);
        drawLegend();
        createMaxMinRnbl();
    }


    @Override
    protected void onNewIntent(Intent intent) {

        onCreate(new Bundle());
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        mDrawer.pause();
        max_minText.pause();
        super.onPause();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

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
            case R.id.rectangle_button:
                rectangleView.setChangeable(!rectangleView.isChangeable());
                if(rectangleView.isChangeable())
                    rectangleView.setVisibility(View.VISIBLE);
                else
                    rectangleView.setVisibility(View.INVISIBLE);
                rectangleView.bringToFront();
                return true;
            case R.id.photo:
                if(mode == VIEW_MODE.CAMERA && cameraView != null)
                    cameraView.takePhoto();
                else if(mode == VIEW_MODE.THERM_CAMERA)
                    takePhoto();
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
            mode = VIEW_MODE.THERM_CAMERA;

            relativeLayout.invalidate();
            rectangleView.visibilityStatus();
        } else {
            cameraView.onCreate();
            cameraView.setOn(true);
            rectangleView.bringToFront();
            mode = VIEW_MODE.CAMERA;
            relativeLayout.invalidate();
            rectangleView.visibilityStatus();
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

    private boolean InitSdk() {

        if(mDeviceSdk == null) {
            mDeviceSdk = new ThermAppAPI(this);
        }

        try {
            mDeviceSdk.ConnectToDevice();
        } catch (Exception e) {
            mDeviceSdk = null;
            return false;
        }
        return true;
    }

    public void thermCreate() {
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


    public void drawLegend() {
        ImageView legend = (ImageView) findViewById(R.id.legend);
        legend.setImageBitmap(temperature.getLegend(20));
        TextView min = (TextView) findViewById(R.id.min_legend);
        TextView max = (TextView) findViewById(R.id.max_legend);
        min.setText(String.format("%f",temperature.getMinTemperature()));
        max.setText(String.format("%f",temperature.getMaxTemperature()));
    }

    public void createMaxMinRnbl() {

        max_minText = new TextUpdater();
        max_minText.setMax((TextView) findViewById(R.id.rect_max));
        max_minText.setMin((TextView) findViewById(R.id.rect_min));
        max_minText.setMin_cross((ImageView) findViewById(R.id.min_cross));
        max_minText.setMax_cross((ImageView) findViewById(R.id.max_cross));
        max_minText.setTemperature(temperature);
    }

    @Override
    public void OnFrameGetThermAppTemperatures(int[] ints, int i, int i1) {
        Bitmap bitmap = temperature.convertTemperature(ints, i, i1);
        mDrawer.post(bitmap);
        if(TAKE_PHOTO) {
//            fileDumper.dumpScreen(ints, i, i1);
            fileDumper.takePicture(bitmap);
            TAKE_PHOTO = false;
        }
        if(rectangleView.getVisibility() == View.VISIBLE) {
            runOnUiThread(max_minText.rnbl);
        }
    }

    public void startThermCamera(){
        try {
            mDeviceSdk.StartVideo();
        } catch (Exception e) {
            // Report error to use
        }
    }

    @Override
    public void OnFrameGetThermAppBMP(Bitmap bitmap) {
        //mDrawer.post(bitmap);
    }

    public void takePhoto() {
        TAKE_PHOTO = true;
    }
    private void init(){

        try{
            final Intent intent = new Intent(this, ThermDataActivity.class);
            intent.putExtra("serialnum", Integer.toString(mDeviceSdk.GetSerialNumber()));
            startActivityForResult(intent, 1);

        } catch(Exception e) {

        }
    }
}
