package com.example.paulina.myapplication;

import android.content.Context;
import android.os.Environment;

import org.opencv.core.Mat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDumper {

    private String camera_name;
    final private int FILE_LIMIT = 10;
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

    private File prepareFile() {
        Integer timestamp = (int)(System.currentTimeMillis()/1000);
        String file_name = camera_name+timestamp.toString()+".txt";

        File file = new File(dir,file_name);
        file.setReadable(true,false);
        return file;
    }

    private void incrementCounter() {
        counter++;
    }

    public void dumpScreen(int[] data, int width, int height) {

        if (mExternalStorageWriteable && counter > FILE_LIMIT)
            return;

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(prepareFile()));
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


    public void dumpScreen(Mat data, int width, int height) {

        if (mExternalStorageWriteable && counter > FILE_LIMIT)
            return;

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(prepareFile()));
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

}
