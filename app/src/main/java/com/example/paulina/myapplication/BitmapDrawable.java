package com.example.paulina.myapplication;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BitmapDrawable {


    ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
    private Runnable rnbl = rnblDraw();
    Future future = threadPoolExecutor.submit(rnbl);

    private Bitmap bmp_ptr = null;
    private ImageView imageView = null;
    private Matrix matrix_imrot_90 = null;


    private Runnable rnblDraw() {
        return new Runnable() {
            public void run() {
                imageView.setImageBitmap(Bitmap.createBitmap(bmp_ptr, 0, 0,
                        bmp_ptr.getWidth(), bmp_ptr.getHeight(), matrix_imrot_90,
                        true));
            }
        };
    }

    BitmapDrawable(ImageView view) {
        imageView = view;
        matrix_imrot_90 = new Matrix();
        matrix_imrot_90.postRotate(90);
        imageView.setVisibility(View.VISIBLE);
    }

    public void pause() {
        future.cancel(true);
        rnbl = rnblDraw();
        future = threadPoolExecutor.submit(rnbl);
        imageView.setVisibility(View.INVISIBLE);
    }

    public void post(Bitmap bitmap) {
        if (null != bitmap) {
            bmp_ptr = bitmap;
            imageView.post(rnbl);
        }
    }
}
