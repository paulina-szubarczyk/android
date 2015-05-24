package com.example.paulina.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;

import org.opencv.core.Mat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class FileDumper {

    private String camera_name;
    final private int FILE_LIMIT = 1;
    private int counter = 0;

    private File dir;

    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;

    public FileDumper(String scamera_name) {
        camera_name = scamera_name;
        checkExternalMedia();
        if(mExternalStorageWriteable)
            createDir();
    }
    private void checkExternalMedia(){
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        System.out.println("\n\nExternal Media: readable="
                + mExternalStorageAvailable + " writable=" + mExternalStorageWriteable);
    }


    private void createDir() {
        File root = Environment.getExternalStorageDirectory();
        dir = new File(root.getAbsolutePath() + "/paulina");
        dir.mkdirs();
        dir.setReadable(true, false);
    }

    private File prepareFile(String suffix) {
        Integer timestamp = (int)(System.currentTimeMillis()/1000);
        String file_name = camera_name+timestamp.toString()+suffix;

        File file = new File(dir,file_name);
        file.setReadable(true,false);
        return file;
    }

    private void incrementCounter() {
        counter++;
    }

    public void dumpScreen(int[] data, int width, int height) {

        if (!dumpingScreen())
            return;

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(prepareFile(".txt")));
            String header = String.format("w: %d, h: %d, l: %d \n",width,height,data.length);
            writer.write(header);

            for (int i : data) {
                writer.write(String.valueOf(i));
                writer.write("\t");
            }

            writer.flush();
            writer.close();
            incrementCounter();

        } catch(IOException e ){
            System.out.println("Couldn't open file " + e.getMessage());
        }
    }


    public boolean dumpingScreen() {
        return !(mExternalStorageWriteable && counter > FILE_LIMIT);
    }

    public void dumpScreen(Mat data, int width, int height) {

        if (mExternalStorageWriteable && counter > FILE_LIMIT)
            return;

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(prepareFile(".txt")));
            String header = String.format("w: %d, h: %d \n",width,height);
            writer.write(header);
            writer.write(data.dump());
            writer.flush();
            writer.close();
            incrementCounter();

        } catch(IOException e ){
            System.out.println("Couldn't open file " + e.getMessage());
        }
    }

    public void takePicture(Bitmap pictureBitmap) {

        OutputStream fOut;
        try {
            fOut = new FileOutputStream(prepareFile(".jpg"));
            pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush();
            fOut.close();
            System.out.println("Smile!!!");

        } catch (IOException e ){
            System.out.println(":( " + e.getMessage());

        }
    }
}
