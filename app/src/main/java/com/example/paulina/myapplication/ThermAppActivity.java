package com.example.paulina.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import thermapp.sdk.ThermAppAPI;
import thermapp.sdk.ThermAppAPI_Callback;


public class ThermAppActivity extends Activity implements ThermAppAPI_Callback {


    private ThermAppAPI mDeviceSdk = null;
    private Bitmap bmp_ptr = null;
    private ImageView imageView = null;
    private Matrix matrix_imrot_90 = null;

    private boolean InitSdk() {
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
        matrix_imrot_90 = new Matrix();

        if(InitSdk()) {

        }
    }

    @Override
    public void OnFrameGetThermAppBMP(Bitmap bitmap) {
        if (null != bitmap) {
            bmp_ptr = bitmap;
            imageView.setImageBitmap(Bitmap.createBitmap(bmp_ptr, 0, 0,
                    bmp_ptr.getWidth(), bmp_ptr.getHeight(), matrix_imrot_90,
                    true));
        }


    }

    @Override
    public void OnFrameGetThermAppTemperatures(int[] ints, int i, int i1) {

    }
}
