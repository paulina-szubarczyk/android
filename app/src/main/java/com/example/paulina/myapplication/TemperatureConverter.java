package com.example.paulina.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class TemperatureConverter implements Observer  {
    public static final int MODE_CONSTANT = 1;
    public static final int MODE_ADAPTIVE = 2;
    public static final int LEGEND_SIZE = 256;

    private static final int TEMPERATURE_SCALE = 100;
    private static int WIDTH, HEIGHT;
    private static int imgWIDTH, imgHEIGHT;

    private ColorMap colorMap;
    private float minTemperature;
    private float maxTemperature;
    private int mode;
    private int[] legend;
    private RectF rectF;
    private int lineX1;
    private int lineY1;
    private int lineX2;
    private int lineY2;

    private final Mat histogram = new Mat();
    private float gradient[];
    private float gradient_x[];
    private float gradient_y[];

    private final TemperatureRectangleData tempRectData = new TemperatureRectangleData();
    private Mat temperatureImg;
    private Context context;

    private final FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
    private final DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
    private final DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

    private static final double FRAC = 0.15;
    private static final int DIST_LIMIT = 40;

    private boolean TAKE_TEMPERATURE;
    private FileDumper fileDumper;

    public TemperatureConverter(ColorMap colorMap, int mode, Context context) {
        this.colorMap = colorMap;
        if (mode != MODE_ADAPTIVE && mode != MODE_CONSTANT) {
            throw new IllegalArgumentException("Invalid mode");
        }
        this.mode = mode;
        init(context);
    }


    public TemperatureConverter(Context context) {

        this.colorMap = ColorMap.HOT;
        this.mode = MODE_ADAPTIVE;
        this.TAKE_TEMPERATURE = false;
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        maxTemperature = minTemperature = 0;
        fileDumper = new FileDumper("temperature");
        temperatureImg = new Mat();
        createLegend();
    }

    public Bitmap convertTemperature(int[] temperature, int width, int height) {

        WIDTH = width; HEIGHT = height;

        Mat mat = new Mat(height, width, CvType.CV_32SC1);
        mat.put(0, 0, temperature);
        mat = mat.t();

        analyzeRectangle(mat);
        calcGradients(mat);

        mat = this.scaleTemperatue(mat);
        mat = postprocess(mat);

        computeHistogram(mat);
        // save image for correspondence checking
        Imgproc.resize(mat, temperatureImg, new Size(mat.rows() / 2, mat.cols() / 2));

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

        if(this.TAKE_TEMPERATURE) {
            Core.MinMaxLocResult result = Core.minMaxLoc(mat);
            this.maxTemperature = maxTemp = (float)result.maxVal;
            this.minTemperature = minTemp = (float)result.minVal;
            this.TAKE_TEMPERATURE = false;
            this.mode = this.MODE_CONSTANT;
        } else if (this.mode == this.MODE_ADAPTIVE) {
            Core.MinMaxLocResult result = Core.minMaxLoc(mat);
            this.maxTemperature = maxTemp = (float)result.maxVal;
            this.minTemperature = minTemp = (float)result.minVal;
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
        rectF = new RectF(x1,y1,x2,y2);
    }

    public void setGradientLine(int x1, int y1, int x2, int y2) {

        this.lineX1 = (int)rectF.left;
        this.lineY1 = (int)rectF.top;
        this.lineX2 = (int)rectF.right;
        this.lineY2 = (int)rectF.bottom;
    }

    private Mat postprocess(Mat mat) {

        Imgproc.medianBlur(mat, mat, 3);

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
            legend[i] = i;
        }
    }

    private void analyzeRectangle(Mat mat) {
        Mat roi = mat.submat((int) rectF.left, (int) rectF.right, (int) rectF.top, (int) rectF.bottom);

        Core.MinMaxLocResult result = Core.minMaxLoc(roi);
        if(Math.abs(tempRectData.tMax - result.maxVal/100) > 0.1) {
            tempRectData.tMax = (float) result.maxVal / 100;
            tempRectData.yMax = (int) (result.maxLoc.x) * imgHEIGHT / HEIGHT;
            tempRectData.xMax = (int) (result.maxLoc.y) * imgWIDTH / WIDTH;
        }
        if(Math.abs(tempRectData.tMin - result.minVal/100) > 0.1) {
            tempRectData.tMin = (float) result.minVal / 100;
            tempRectData.yMin = (int) (result.minLoc.x) * imgHEIGHT / HEIGHT;
            tempRectData.xMin = (int) (result.minLoc.y) * imgWIDTH / WIDTH;
        }
    }

    private void calcGradients(Mat mat) {

        if(Math.abs(lineX1 - lineX2) > Math.abs(lineY1 - lineY2)) {
            int deltaY = Math.abs(lineY1 - lineY2);
            gradient = analyzeGradient(mat, lineX1,lineY1,lineX1+deltaY,lineY2);
        } else {
            int deltaX = Math.abs(lineX1 - lineX2);
            gradient =analyzeGradient(mat, lineX1,lineY1,lineX2,lineY1+deltaX);

        }
        gradient_x = analyzeGradient(mat,lineX1,lineY1,lineX1,lineY2);
        gradient_y = analyzeGradient(mat,lineX1,lineY1,lineX2,lineY1);
    }
    private float[] analyzeGradient(Mat mat,int x1, int y1, int x2, int y2) {

        Mat floatMat = new Mat(mat.rows(), mat.cols(), CvType.CV_32FC1);
        mat.convertTo(floatMat, CvType.CV_32FC1);
        Core.multiply(floatMat, new Scalar(1 / TEMPERATURE_SCALE), floatMat);

        int xSpan = Math.abs(x1 - x2);
        int ySpan = Math.abs(y1 - y2);
        int size = Math.max(xSpan, ySpan)-1;
        if(size < 1)
            return null;

        float[] gradient = new float[size];

        int xStep = (int)Math.signum(x2 - x1);
        int yStep = (int)Math.signum(y2 - y1);

        int lastX = x1;
        int lastY = y1;
        int currentX = x1 + xStep;
        int currentY = y1 + yStep;
        int idx = 0;
        double stepLength = Math.sqrt(xStep*xStep + yStep*yStep);
        while((currentX != x2 || currentY != y2) && idx < size) {
            double grad = (float)(floatMat.get(currentY, currentX)[0]) - (float)(floatMat.get(lastY, lastX)[0]);
            gradient[idx] = (float)(grad / stepLength);
            lastX = currentX;
            lastY = currentY;
            currentX += xStep;
            currentY += yStep;
            ++idx;
        }
        return gradient;
    }

    Point estimateRelativeLocation(byte[] cameraBytes, int width, int height) {

        Mat cameraMat = new Mat(height, width, CvType.CV_8UC3);
        cameraMat.put(0, 0, cameraBytes);
        Imgproc.cvtColor(cameraMat, cameraMat, Imgproc.COLOR_RGB2GRAY);
        width /= 2;
        height /= 2;
        Imgproc.resize(cameraMat, cameraMat, new Size(height, width));

//        int x1 = (int)(FRAC * width);
//        int x2 = width - x1;
//        int y1 = (int)(FRAC * height);
//        int y2 = height - y1;
//        cameraMat = cameraMat.adjustROI(y1, y2, x1, x2);

        MatOfKeyPoint tempKeypoints = new MatOfKeyPoint();
        MatOfKeyPoint camKeypoints = new MatOfKeyPoint();
        Mat tempDescriptors = new Mat();
        Mat camDescriptors = new Mat();
        MatOfDMatch  matches = new MatOfDMatch();

        detector.detect(temperatureImg, tempKeypoints);
        descriptor.compute(temperatureImg, tempKeypoints, tempDescriptors);

        detector.detect(cameraMat, camKeypoints);
        descriptor.compute(cameraMat, camKeypoints, camDescriptors);

        matcher.match(camDescriptors, tempDescriptors, matches);

        List<DMatch> matchList = matches.toList();
        List<DMatch> finalMatchList = new ArrayList<DMatch>();
        for(DMatch match : matchList) {
            if(match.distance < DIST_LIMIT) {
                finalMatchList.add(match);
                break;
            }
        }

        DMatch match = finalMatchList.get(0);
        Point camPoint = camKeypoints.toArray()[match.queryIdx].pt;
        Point tempPoint = tempKeypoints.toArray()[match.trainIdx].pt;

        return new Point(2 * (camPoint.x - tempPoint.x), 2 * (camPoint.y - tempPoint.y));
    }

    private void computeHistogram(Mat mat) {

        List<Mat> matList = Collections.singletonList(mat);
        MatOfInt channels = new MatOfInt(0);
        Mat mask = new Mat();
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat ranges = new MatOfFloat(0, 256);

        Imgproc.calcHist(matList, channels, mask, histogram, histSize, ranges);
        histogram.convertTo(histogram,CvType.CV_8UC1);
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

    public RectF getRectF() {
        return rectF;
    }

    public byte[] getHistogram() {
        byte[] hist = new byte[histogram.rows() * histogram.cols()];
        histogram.get(0, 0, hist);
        return hist;
    }

    public float[] getGradient() {
        return gradient;
    }
    public float[] getGradientX() {
        return gradient_x;
    }

    public float[] getGradientY() {
        return gradient_y;
    }


    @Override
    public void update(Observable observable, Object data) {
        if(observable instanceof RectangleView.MRect) {
            RectangleView.MRect mRect = ((RectangleView.MRect) observable);
            RectF rect = mRect.getRectangle();
            imgWIDTH = mRect.getWidth();
            imgHEIGHT = mRect.getHeight();
            setAnalysedRectangle(
                    (int)rect.left*WIDTH/imgWIDTH,
                    (int)rect.top*HEIGHT/imgHEIGHT,
                    (int)rect.right*WIDTH/imgWIDTH,
                    (int)rect.bottom*HEIGHT/imgHEIGHT);

            setGradientLine(
                    (int) rect.left * WIDTH / imgWIDTH,
                    (int) rect.top * HEIGHT / imgHEIGHT,
                    (int) rect.left * WIDTH / imgWIDTH,
                    (int) rect.bottom * HEIGHT / imgHEIGHT);
        }
    }


    public boolean isAdaptiveMode() {
        return mode == MODE_ADAPTIVE;
    }

    public void setConstantMode(boolean mode) {
        if (mode && isAdaptiveMode()) {
            this.TAKE_TEMPERATURE = true;
        } else if (!mode && !isAdaptiveMode()) {
            this.mode = MODE_ADAPTIVE;
        }
    }
}
