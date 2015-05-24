package com.example.paulina.myapplication;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextUpdater {

    ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
    public Runnable rnbl = rnblText();
    Future future = threadPoolExecutor.submit(rnbl);

    private TextView min;
    private TextView max;
    private ImageView min_cross, max_cross;
    private TemperatureConverter temperature;

    private Runnable rnblText() {
        return new Runnable() {
            public void run() {

                TemperatureRectangleData rect =  temperature.getTempRectData();

                min.setText(String.format("%f, %d, %d", rect.tMin, rect.xMin, rect.yMin));
                max.setText(String.format("%f, %d, %d", rect.tMax, rect.xMax, rect.yMax));


                min.setX(rect.xMin);
                min.setY(rect.yMin);
                min.bringToFront();
                min.setVisibility(View.VISIBLE);
                min.invalidate();

                min_cross.setX(rect.xMin);
                min_cross.setY(rect.yMin);
                min_cross.bringToFront();
                min_cross.setVisibility(View.VISIBLE);
                min_cross.invalidate();

                max.setX(rect.xMax);
                max.setY(rect.yMax);
                max.bringToFront();
                max.setVisibility(View.VISIBLE);
                max.invalidate();

                max_cross.setX(rect.xMax);
                max_cross.setY(rect.yMax);
                max_cross.bringToFront();
                max_cross.setVisibility(View.VISIBLE);
                max_cross.invalidate();


            }
        };
    }
    public void pause() {
        future.cancel(true);
        rnbl = rnblText();
        future = threadPoolExecutor.submit(rnbl);
    }

    public void setMin(TextView min) {
        this.min = min;
    }

    public void setMax(TextView max) {
        this.max = max;
    }

    public void setMin_cross(ImageView min_cross) {
        this.min_cross = min_cross;
    }

    public void setMax_cross(ImageView max_cross) {
        this.max_cross = max_cross;
    }

    public void setTemperature(TemperatureConverter temperature) {
        this.temperature = temperature;
    }

}
