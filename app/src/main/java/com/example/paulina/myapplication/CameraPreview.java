package com.example.paulina.myapplication;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private BitmapDrawable surfaceDrawer;
    private YuvConfig yuvConfig;

    public CameraPreview(MainActivity activity,Camera camera) {
        super(activity.getApplicationContext());

        mCamera = camera;
        mCamera.setDisplayOrientation(90);
        yuvConfig = new YuvConfig(mCamera.getParameters(),0.15,0.85,50);


        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        surfaceDrawer = new BitmapDrawable((ImageView) activity.findViewById(R.id.camera_preview2));
    }

    public void surfaceCreated(SurfaceHolder holder) {

        Log.d(getClass().toString(), "Preview create");
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(getClass().toString(),
                    "Error seting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceDrawer.pause();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        Log.d(getClass().toString(), "Preview changed");
        if (mHolder.getSurface() == null)
            return;

        try {
            mCamera.stopPreview();
        } catch (Exception e) {

        }

        surfaceCreated(mHolder);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

//        IntBuffer intBuf =
//                ByteBuffer.wrap(data)
//                        .order(ByteOrder.BIG_ENDIAN)
//                        .asIntBuffer();
//        int[] array = new int[intBuf.remaining()];
//        intBuf.get(array);
//        mDrawer.post(temperature.convertTemperature
//                (array, yuvConfig.getRectangle().width(),yuvConfig.getRectangle().height()));
        surfaceDrawer.post(yuvConfig.compressToBitmap(data));
    }

}
