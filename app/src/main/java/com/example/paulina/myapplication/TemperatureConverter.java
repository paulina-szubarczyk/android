package com.example.paulina.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class TemperatureConverter {
    public static final int MODE_CONSTANT = 1;
    public static final int MODE_ADAPTIVE = 2;

    private static final int TEMPERATURE_SCALE = 100;

    private ColorMap colorMap;
    private float minTemperature;
    private float maxTemperature;
    private int mode;

    private FileDumper fileDumper;

    public TemperatureConverter(ColorMap colorMap, int mode, Context context) {
        this.colorMap = colorMap;
        if(mode != MODE_ADAPTIVE && mode != MODE_CONSTANT) {
            throw new IllegalArgumentException("Invalid mode");
        }
        this.mode = mode;

        fileDumper = new FileDumper("temperature");

    }

    public TemperatureConverter(Context context) {
        this.colorMap = ColorMap.HOT;
        this.mode = MODE_ADAPTIVE;

        fileDumper = new FileDumper("temperature");
    }

    public Bitmap convertTemperature(int[] temperature, int width, int height) {


        Mat mat = new Mat(height, width, CvType.CV_32SC1);
        mat.put(0, 0, temperature);
        mat = this.scaleTemperatue(mat);
        mat = postprocess(mat);

        Imgproc.applyColorMap(mat, mat, colorMap.value);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGBA);

         Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bitmap);
        }
        catch (CvException e) {
            Log.d("Couldn't create bitmap", e.getMessage());
        }

        return bitmap;
    }

    public Mat scaleTemperatue(Mat mat) {
        // if mode == this.MODE_CONSTANT
        float maxTemp = this.maxTemperature;
        float minTemp = this.minTemperature;

        mat.convertTo(mat, CvType.CV_32FC1);
        Core.multiply(mat, new Scalar(1.0f / this.TEMPERATURE_SCALE), mat);

        if(this.mode == this.MODE_ADAPTIVE) {
            Core.MinMaxLocResult result = Core.minMaxLoc(mat);
            maxTemp = (float)result.maxVal;
            minTemp = (float)result.minVal;
        }

        Core.subtract(mat, new Scalar(minTemp), mat);
        maxTemp -= minTemp;

        // clip smaller temps -> sets to 0
        Imgproc.threshold(mat, mat, 0, 0, Imgproc.THRESH_TOZERO);

        // clip higher temps - > sets to maxTemp
        Imgproc.threshold(mat, mat, maxTemp, maxTemp, Imgproc.THRESH_TRUNC);

        // now we have values between 0 and maxTemp -> multiply by (255 / maxTemp);
        Core.multiply(mat, new Scalar(255.0f / maxTemp), mat);

        mat.convertTo(mat, CvType.CV_8UC1);
        return mat;
    }

    private Mat postprocess(Mat mat) {

        Imgproc.medianBlur(mat,mat,3);

        return mat;
    }

    public ColorMap getColorMap() {
        return colorMap;
    }

    public void setColorMap(ColorMap colorMap) {
        this.colorMap = colorMap;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }
}
