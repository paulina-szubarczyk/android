package com.example.paulina.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BitmapDrawable {


    ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
    private Runnable rnbl = rnblDraw();
    private Runnable clear = rnblClear();
    Future future = threadPoolExecutor.submit(rnbl);

    private Bitmap bmp_ptr = null;
    private ImageView imageView = null;
    private Matrix matrix_rot_90 = null;

    private Runnable rnblDraw() {
        return new Runnable() {
            public void run() {
                imageView.setVisibility(View.VISIBLE);

                Bitmap bitmap = Bitmap.createBitmap(bmp_ptr, 0, 0,
                        bmp_ptr.getWidth(), bmp_ptr.getHeight(), matrix_rot_90,
                        true);
                imageView.setImageBitmap(bitmap);

            }
        };
    }

    private Runnable rnblClear() {
        return new Runnable() {
            public void run() {
                imageView.setVisibility(View.GONE);
                System.out.println("GONE bitmap!!!");
            }
        };

    }

    BitmapDrawable(ImageView view) {
        imageView = view;
        imageView.bringToFront();
        matrix_rot_90 = new Matrix();
//        matrix_rot_90.postRotate( 90 );
    }

    public void pause() {

        future.cancel(true);
        clear = rnblClear();
        future = threadPoolExecutor.submit(clear);
        imageView.post(clear);
        future.cancel(true);
        rnbl = rnblDraw();
        future = threadPoolExecutor.submit(rnbl);
    }

    public void post(Bitmap bitmap) {
        if (null != bitmap) {
            bmp_ptr = bitmap;
            imageView.post(rnbl);
        }
    }
}
