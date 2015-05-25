package com.example.paulina.myapplication;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class Legend  {

    Context context;
    TemperatureConverter temperature;
    public Runnable rnbl = rnblDraw();

    public Legend(Context context, TemperatureConverter temperatureConverter) {
        this.context = context;
        this.temperature = temperatureConverter;
    }

    public void draw() {
        ImageView legend = (ImageView) ((Activity)context).findViewById(R.id.legend);
        legend.setImageBitmap(temperature.getLegend(20));
        TextView min = (TextView) ((Activity)context).findViewById(R.id.min_legend);
        TextView max = (TextView) ((Activity)context).findViewById(R.id.max_legend);
        min.setText(String.format("%f",temperature.getMinTemperature()));
        max.setText(String.format("%f",temperature.getMaxTemperature()));
        legend.bringToFront();
        min.bringToFront();
        max.bringToFront();
        legend.invalidate();
        min.invalidate();
        max.invalidate();
    }

    private Runnable rnblDraw() {
        return new Runnable() {
            public void run() {
                draw();
            }
        };
    }
}
