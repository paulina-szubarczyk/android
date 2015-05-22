package com.example.paulina.myapplication;

import android.support.test.runner.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    @Test
    public void shouldHaveHappySmiles() throws Exception {
        assertThat("Hello World!", equalTo("Hello World!"));
    }

    @Test
    public void temperatureConverterTest() {

        TemperatureConverter converter = new TemperatureConverter();
        int width = 3;
        int height = 2;
        int[] img = new int[6];
        img[0] = 1;
        img[1] = 2;
        img[2] = 3;
        img[3] = 4;
        img[4] = 5;
        img[5] = 6;

        System.out.println(img);

        Mat mat = new Mat(width, height, CvType.CV_32SC1);
        mat.put(0, 0, img);
        System.out.println(img);


    }

}