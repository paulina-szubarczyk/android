package com.example.paulina.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import java.io.ByteArrayOutputStream;

public class YuvConfig {
    int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public Rect getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rect rectangle) {
        this.rectangle = rectangle;
    }

    int height;
    int format;
    int quality;
    Rect rectangle;
    ByteArrayOutputStream compressedYuvJpeg;

    YuvConfig(Camera.Parameters parameters, double top, double bottom, int quality_) {

        quality = quality_;
        width = parameters.getPreviewSize().width;
        height = parameters.getPreviewSize().height;
        compressedYuvJpeg = new ByteArrayOutputStream();
        format = parameters.getPreviewFormat();
        rectangle = new Rect((int) (width * top), (int) (height * top),
                (int) (width * bottom), (int) (height * bottom));
    }

    public Bitmap compressToBitmap(byte[] data) {
        YuvImage yuv = new YuvImage(data, format, width, height, null);

        compressedYuvJpeg.reset();
        yuv.compressToJpeg(rectangle, quality, compressedYuvJpeg);

        byte[] bytes = compressedYuvJpeg.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
