package com.example.paulina.myapplication;

import org.opencv.imgproc.Imgproc;

/**
 * Created by adam on 12.05.15.
 */
public enum ColorMap {
    AUTUMN(Imgproc.COLORMAP_AUTUMN),
    BONE(Imgproc.COLORMAP_BONE),
    COOL(Imgproc.COLORMAP_COOL),
    HOT(Imgproc.COLORMAP_HOT),
    HSV(Imgproc.COLORMAP_HSV),
    JET(Imgproc.COLORMAP_JET),
    OCEAN(Imgproc.COLORMAP_OCEAN),
    PINK(Imgproc.COLORMAP_PINK),
    RAINBOW(Imgproc.COLORMAP_RAINBOW),
    SPRING(Imgproc.COLORMAP_SPRING),
    SUMMER(Imgproc.COLORMAP_SUMMER),
    WINTER(Imgproc.COLORMAP_WINTER);

    ColorMap(int value) {
        this.value = value;
    }

    public int value;
}
