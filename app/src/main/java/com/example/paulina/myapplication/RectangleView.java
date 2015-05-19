package com.example.paulina.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class RectangleView extends View {
        Paint paint = new Paint();

        public RectangleView(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas) {
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(100, 100, 200, 200, paint);
        }
}
