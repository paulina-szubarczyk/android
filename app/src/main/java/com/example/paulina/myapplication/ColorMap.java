package com.example.paulina.myapplication;

import android.graphics.ImageFormat;

import org.opencv.imgproc.Imgproc;

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

    static ColorMap fromString(String map){
        if(AUTUMN.toString().equals(map))
            return ColorMap.AUTUMN;
        if(BONE.toString().equals(map))
            return ColorMap.BONE;
        if(COOL.toString().equals(map))
            return ColorMap.COOL;
        if(HOT.toString().equals(map))
            return ColorMap.HOT;
        if(HSV.toString().equals(map))
            return ColorMap.HSV;
        if(JET.toString().equals(map))
            return ColorMap.JET;
        if(OCEAN.toString().equals(map))
            return ColorMap.OCEAN;
        if(PINK.toString().equals(map))
            return ColorMap.PINK;
        if(RAINBOW.toString().equals(map))
            return ColorMap.RAINBOW;
        if(SPRING.toString().equals(map))
            return ColorMap.SPRING;
        if(SUMMER.toString().equals(map))
            return ColorMap.SUMMER;
        if(WINTER.toString().equals(map))
            return ColorMap.WINTER;
        return ColorMap.AUTUMN;
    }

    public int value;
}


