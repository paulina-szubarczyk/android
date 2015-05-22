package com.example.paulina.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.hardware.usb.UsbManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import thermapp.sdk.ThermAppAPI;
import thermapp.sdk.ThermAppAPI_Callback;


public class ThermAppActivity extends ActionBarActivity implements ThermAppAPI_Callback {

    int[] gray_palette;
    int[] therm_palette;
    int[] my_palette;

    private ThermAppAPI mDeviceSdk = null;
    private BroadcastReceiver mUsbReceiver;
    private BitmapDrawable mDrawer;
    private ParamTextUpdater paramTextUpdater;
    private TemperatureConverter temperature;
    private String mat_string = "";
    private FileDumper fileDumper;

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
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therm_app);
        thermCreate();
    }

    private void thermCreate() {
        CreatePalettes();
        mDrawer = new BitmapDrawable((ImageView) findViewById(R.id.imageView1));

        paramTextUpdater = new ParamTextUpdater();

        paramTextUpdater.setHeight_text((TextView) findViewById(R.id.height));
        paramTextUpdater.setWidth_text((TextView) findViewById(R.id.width));
        paramTextUpdater.setLength_text((TextView) findViewById(R.id.length));
        paramTextUpdater.setMat_text((TextView) findViewById(R.id.mat));

        temperature = new TemperatureConverter(getApplicationContext());

        fileDumper = new FileDumper("thermapp");

        try {
            super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(Exception e) {

        }

        if(InitSdk()) {
            init();
        }
        // Define USB detached event receiver
        if(mUsbReceiver == null) {
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
        paramTextUpdater.pause();
    }

    @Override
    protected  void onStop() {
        unregisterReceiver(mUsbReceiver);
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
        fileDumper.dumpScreen(ints,i,i1);
    }


    private void CreatePalettes() {
        gray_palette = new int[256];
        for (int i = 0; i < 256; i++)
            gray_palette[i] = 0xFF000000 | (i << 0) | (i << 8) | (i << 16);

        int PALETTE_MAX_IND = 256 - 1;
        therm_palette = new int[256];

        for (int i = 0; i < 256; i++)
            therm_palette[i] = 0xFF000000
                    | (i << 16)
                    | ((PALETTE_MAX_IND - ((i * (PALETTE_MAX_IND - i)) >> 6)) << 8)
                    | ((PALETTE_MAX_IND - i) << 0);

        my_palette = createPalette(253, 250, 0, 86, 0, 154);
    }

    private int[] createPalette(int sR, int sG, int sB, int eR, int eG, int eB) {
        int[] my_palette = new int[256];
        float pr;
        float Red;
        float Green;
        float Blue;

        for (int i = 0; i < 256; i++) {
            pr = (float) i / (float) 256;
            Red = sR * pr + eR * (1 - pr);
            Green = sG * pr + eG * (1 - pr);
            Blue = sB * pr + eB * (1 - pr);

            my_palette[i] = 0xFF000000 | (Math.round(Red) << 16)
                    | (Math.round(Green) << 8) | (Math.round(Blue) << 0);
        }
        return my_palette;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");

                if (result.equals("OK")) {
                    try {
                        mDeviceSdk.StartVideo();
                    } catch (Exception e) {
                        // Report error to use
                    }

                } else if (result.equals("EXIT")) {
                }
            }
            if (resultCode == RESULT_CANCELED) {

            }
        }
    }
}
