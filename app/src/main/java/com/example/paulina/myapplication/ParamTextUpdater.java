package com.example.paulina.myapplication;

import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParamTextUpdater  {

    ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
    public Runnable rnbl = rnblText();
    Future future = threadPoolExecutor.submit(rnbl);

    private TextView length_text, height_text, width_text, mat_text;
    private int length = 0;
    private int height = 0;
    private int width = 0;
    private String mat ;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setLength_text(TextView length_text) {
        this.length_text = length_text;
    }

    public void setHeight_text(TextView height_text) {
        this.height_text = height_text;
    }

    public void setWidth_text(TextView width_text) {
        this.width_text = width_text;
    }

    public void setMat_text(TextView mat_text) {
        this.mat_text = mat_text;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setMat(String mat) {
        this.mat = mat;
    }

    private Runnable rnblText() {
        return new Runnable() {
            public void run() {
                if (length_text != null)
                    length_text.setText(String.format("L: %d", length));
                if (width_text != null)
                    width_text.setText(String.format("w: %d", width));
                if (height_text != null)
                    height_text.setText(String.format("h: %d", height));
                if (mat_text != null) {
                    mat_text.setText(mat);
                    mat_text.invalidate();
                }
            }
        };
    }
    public void pause() {
        future.cancel(true);
        rnbl = rnblText();
        future = threadPoolExecutor.submit(rnbl);
    }
}
