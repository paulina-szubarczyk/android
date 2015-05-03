package com.example.paulina.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import android.view.WindowManager;
import android.widget.LinearLayout;

import thermapp.sdk.DeviceData;
import thermapp.sdk.DeviceData_Callback;


public class ThermDataActivity extends Activity implements DeviceData_Callback {

    private DeviceData mDeviceData;
    private String serialnum = null;

    @Override
    public void OnDownloadFinished() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "OK");
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void OnError(String s) {

    }

    @Override
    public void OnUpdateProgress(int i) {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Get serial number from main activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            serialnum = extras.getString("serialnum");
        }

        // Create instance for ThermApp device data
        mDeviceData = new DeviceData(serialnum);

        checkDeviceData();

    }

    protected void checkDeviceData() {

        if(mDeviceData.IsDataAvialable()) {
            OnDownloadFinished();
            return;
        }

        // Else - start downloading
        mDeviceData.StartDownload(this);
    }

}
