package com.example.paulina.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.paulina.MESSAGE";
    private CameraActivity cameraActivity;
    private Menu mMenu;

    public Menu getMenu() {
        return mMenu;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void openSearch() {

    }

    public void openSettings() {

    }

    public void menageDeviceCamera() {

        if(cameraActivity == null) {
            cameraActivity = new CameraActivity(this);
        }
        if(cameraActivity.isOn()) {
            cameraActivity.onPause();
            cameraActivity.setOn(false);
        } else {
            cameraActivity.onCreate();
            cameraActivity.setOn(true);
        }

    }

    public void startThermCamera() {
        Intent intent = new Intent(this, ThermAppActivity.class);
        startActivity(intent);
    }

}