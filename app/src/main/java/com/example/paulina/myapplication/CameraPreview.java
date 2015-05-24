package com.example.paulina.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraPreview implements Camera.PreviewCallback {

    private Camera mCamera;
    private BitmapDrawable surfaceDrawer;
    private YuvConfig yuvConfig;
    private FileDumper fileDumper;
    private boolean TAKE_PHOTO;

    public CameraPreview(Activity activity,Camera camera) {

        mCamera = camera;
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();

        yuvConfig = new YuvConfig(mCamera.getParameters(), 0.15, 0.85, 90);
        surfaceDrawer = new BitmapDrawable((ImageView) activity.findViewById(R.id.camera_preview2));
        fileDumper = new FileDumper("camera");
        TAKE_PHOTO = false;
    }

    public void onPause(){
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mCamera.release();
        surfaceDrawer.pause();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        IntBuffer intBuf =
                ByteBuffer.wrap(data)
                        .order(ByteOrder.BIG_ENDIAN)
                        .asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        Bitmap bitmap = yuvConfig.compressToBitmap(data);
        surfaceDrawer.post(bitmap);

        if(TAKE_PHOTO) {
//            fileDumper.dumpScreen(array, yuvConfig.getWidth(), yuvConfig.getHeight());
            fileDumper.takePicture(bitmap);
            TAKE_PHOTO = false;
        }
    }

    public void setTAKE_PHOTO(boolean TAKE_PHOTO) {
        this.TAKE_PHOTO = TAKE_PHOTO;
    }
}
