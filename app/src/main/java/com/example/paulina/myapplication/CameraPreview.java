package com.example.paulina.myapplication;

import android.hardware.Camera;
import android.widget.ImageView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class CameraPreview implements Camera.PreviewCallback {

    private Camera mCamera;
    private BitmapDrawable surfaceDrawer;
    private YuvConfig yuvConfig;
    private FileDumper fileDumper;

    public CameraPreview(MainActivity activity,Camera camera) {

        mCamera = camera;
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();

        yuvConfig = new YuvConfig(mCamera.getParameters(), 0.15, 0.85, 50);
        surfaceDrawer = new BitmapDrawable((ImageView) activity.findViewById(R.id.camera_preview2));
        fileDumper = new FileDumper("camera");
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
        surfaceDrawer.post(yuvConfig.compressToBitmap(data));
        fileDumper.dumpScreen(array,yuvConfig.getWidth(),yuvConfig.getHeight());
    }

}
