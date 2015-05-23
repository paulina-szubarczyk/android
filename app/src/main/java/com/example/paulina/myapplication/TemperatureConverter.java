package com.example.paulina.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class TemperatureConverter {
    public static final int MODE_CONSTANT = 1;
    public static final int MODE_ADAPTIVE = 2;
    public static final int LEGEND_SIZE = 256;

    private static final int TEMPERATURE_SCALE = 100;

    private ColorMap colorMap;
    private float minTemperature;
    private float maxTemperature;
    private int mode;
    private int[] legend;
    private int rectX1;
    private int rectY1;
    private int rectX2;
    private int rectY2;
    private int lineX1;
    private int lineY1;
    private int lineX2;
    private int lineY2;

    private final int histogram[] = new int[256];
    private float gradient[];
    private final TemperatureRectangleData tempRectData = new TemperatureRectangleData();

    private FileDumper fileDumper;

    public TemperatureConverter(ColorMap colorMap, int mode, Context context) {
        this.colorMap = colorMap;
        if (mode != MODE_ADAPTIVE && mode != MODE_CONSTANT) {
            throw new IllegalArgumentException("Invalid mode");
        }
        this.mode = mode;
        init();
    }


    public TemperatureConverter(Context context) {
        this.colorMap = ColorMap.HOT;
        this.mode = MODE_ADAPTIVE;
        init();
    }

    public void init() {
        maxTemperature = minTemperature = 0;
        fileDumper = new FileDumper("temperature");
        createLegend();
    }

    public Bitmap convertTemperature(int[] temperature, int width, int height) {


        Mat mat = new Mat(height, width, CvType.CV_32SC1);
        mat.put(0, 0, temperature);

        analyzeRectangle(mat);
        analyzeGradient(mat);
        mat = this.scaleTemperatue(mat);
        mat = postprocess(mat);
        return createBitmapInColorMap(mat);
    }

    public Bitmap createBitmapInColorMap(Mat mat) {
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

    public void setAnalysedRectangle(int x1, int y1, int x2, int y2) {
        this.rectX1 = x1;
        this.rectY1 = y1;
        this.rectX2 = x2;
        this.rectY2 = y2;
    }

    public void setGradientLine(int x1, int y1, int x2, int y2) {

        this.lineX1 = x1;
        this.lineY1 = y1;
        this.lineX2 = x2;
        this.lineY2 = y2;
    }

    private Mat postprocess(Mat mat) {

        Imgproc.medianBlur(mat,mat,3);

        return mat;
    }

    public Bitmap getLegend(int height) {
        Mat mat = new Mat(height, LEGEND_SIZE, CvType.CV_32SC1);
        for(int i = 0; i < height; ++i) {
            mat.put(i,0,legend);
        }
        mat.convertTo(mat, CvType.CV_8UC1);
        return createBitmapInColorMap(mat);
    }

    private void createLegend() {
        legend = new int[LEGEND_SIZE];
        for(int i = 0; i < LEGEND_SIZE; ++i) {
            legend[i] = i+1;
        }
    }

    private void analyzeRectangle(Mat mat) {
        Mat roi = mat.adjustROI(rectY1, rectY2, rectX1, rectX2);

        Core.MinMaxLocResult result = Core.minMaxLoc(mat);
        tempRectData.tMax = (float)result.maxVal / this.TEMPERATURE_SCALE;
        tempRectData.xMax = (int)result.maxLoc.x;
        tempRectData.yMax = (int)result.maxLoc.y;
        tempRectData.tMin = (float)result.minVal / this.TEMPERATURE_SCALE;
        tempRectData.xMin = (int)result.minLoc.x;
        tempRectData.yMin = (int)result.minLoc.y;
    }

    private void analyzeGradient(Mat mat) {

        Mat floatMat = new Mat(mat.rows(), mat.cols(), CvType.CV_32FC1);
        Core.multiply(floatMat, new Scalar(1 / this.TEMPERATURE_SCALE), floatMat);

        int xSpan = Math.abs(lineX1 - lineX2);
        int ySpan = Math.abs(lineY1 - lineY2);
        gradient = new float[Math.max(xSpan, ySpan) - 1];

        int xStep = (int)Math.signum(lineX1 - lineX2);
        int yStep = (int)Math.signum(lineY1 - lineY2);

        int lastX = lineX1;
        int lastY = lineY1;
        int currentX = lineX1 + xStep;
        int currentY = lineY1 + yStep;
        int idx = 0;
        double stepLength = Math.sqrt(xStep*xStep + yStep*yStep);
        while(currentX != lineX2 || currentY != lineY2) {

            double grad = floatMat.get(currentY, currentX)[0] - floatMat.get(lastY, lastX)[0];
            gradient[idx] = (float)(grad / stepLength);
            lastX = currentX;
            lastY = currentY;
            currentX += xStep;
            currentY += yStep;
            ++idx;
        }
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

    public TemperatureRectangleData getTempRectData() {
        return tempRectData;
    }
}
