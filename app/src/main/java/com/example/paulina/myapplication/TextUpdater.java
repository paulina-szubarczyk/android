package com.example.paulina.myapplication;

import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;

public class TextUpdater {

    ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
    public Runnable rnbl = rnblText();
    Future future = threadPoolExecutor.submit(rnbl);

    private TextView min;
    private TextView max;
    private TextView log;
    private ImageView min_cross, max_cross;
    private TemperatureConverter temperature;
    private RectF r;

    private Runnable rnblText() {
        return new Runnable() {
            public void run() {

                TemperatureRectangleData rect =  temperature.getTempRectData();
                RectF r2 = temperature.getRectF();

                TemperatureRectangleData temp = new TemperatureRectangleData();
                temp.xMin = rect.yMin + (int)r.left;
                temp.yMin = rect.xMin + (int)r.top;
                temp.xMax = rect.yMax + (int)r.left;
                temp.yMax = rect.xMax + (int)r.top;

                min.setText(String.format("%f", rect.tMin));
                max.setText(String.format("%f", rect.tMax));
                log.setText(String.format("MIN(%d,%d), MAX(%d,%d), R(%f,%f,%f,%f), R(%f,%f,%f,%f)",
                        rect.xMin,rect.yMin,rect.xMax,rect.yMax,
                        r.left,r.top,r.right,r.bottom,
                        r2.left,r2.top,r2.right,r2.bottom));

                min.setX(temp.xMin);
                min.setY(temp.yMin);
                min.bringToFront();
                min.setVisibility(View.VISIBLE);
                min.invalidate();

                min_cross.setX(temp.xMin);
                min_cross.setY(temp.yMin);
                min_cross.bringToFront();
                min_cross.setVisibility(View.VISIBLE);
                min_cross.invalidate();

                max.setX(temp.xMax);
                max.setY(temp.yMax);
                max.bringToFront();
                max.setVisibility(View.VISIBLE);
                max.invalidate();

                max_cross.setX(temp.xMax);
                max_cross.setY(temp.yMax);
                max_cross.bringToFront();
                max_cross.setVisibility(View.VISIBLE);
                max_cross.invalidate();

                log.bringToFront();
                log.setVisibility(View.VISIBLE);
                log.invalidate();


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

    public void setLog(TextView log) {
        this.log = log;
    }

    public void setR(RectF r) {
        this.r = r;
    }
}
