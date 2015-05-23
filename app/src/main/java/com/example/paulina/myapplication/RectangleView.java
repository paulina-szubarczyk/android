package com.example.paulina.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import org.opencv.core.Rect;


/**
 * TODO: document your custom view class.
 */
public class RectangleView extends View {
    private Paint paint;
    private MRect rectangle;
    private int stateToSave;
    private boolean changeable;

    public class MRect {

        private RectF rectangle;
        private RectF screen;

        public RectF getRectangle() {
            return rectangle;
        }

        private double scale;
        private double scale_amount ;
        int height, width;
        public MRect(double scale_, Context context)
        {
            rectangle = new RectF();
            scale_amount = 0.05;
            scale = scale_;
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels - 100;
            width = displaymetrics.widthPixels;
            recalculate();
        }
        public void recalculate() {

            rectangle.top = (int) (scale*height);
            rectangle.bottom = (int) ((1-scale)*height);
            rectangle.left = (int) (scale*width);
            rectangle.right = (int) ((1-scale)*width);
            System.out.println(rectangle.top+" " + rectangle.bottom + " " + rectangle.left + " " +rectangle.right);
        }

        public void scale(boolean up) {
            scale += up ? scale_amount : -scale_amount;
            if (scale > 0.3)
                scale = 0.3;
            else if (scale < 0.1)
                scale = 0.1;

            recalculate();
        }

        public boolean contains(float x, float y){
            return rectangle.contains(x,y);
        }
        public double getScale() {
            return scale;
        }
        public void setScale(double scale) {
            this.scale = scale;
        }
    }

    public MRect getRectangle() {
        return rectangle;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

    public RectangleView(Context context) {
        super(context);
        init();
    }

    public RectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public RectangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        rectangle = new MRect(0.25,getContext());
        changeable = false;
        setWillNotDraw(false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("DRAW!!");
        canvas.drawRect(rectangle.getRectangle().left, rectangle.getRectangle().top,
                rectangle.getRectangle().right, rectangle.getRectangle().bottom, paint);
    }

    public void visibilityStatus() {

        if(getVisibility() == View.VISIBLE) {
            System.out.println("VISIBLE!!!!");
        } else if (getVisibility() == View.INVISIBLE) {
            System.out.println("INVISIBLE!!!!");
        } else if (getVisibility() == View.GONE) {
            System.out.println("GONE!!!");
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(State.INSTANCE_SAVE.toString(), super.onSaveInstanceState());
        bundle.putInt(State.STATE_TO_SAVE.toString(), this.stateToSave);
        bundle.putDouble(State.SCALE.toString(), this.rectangle.getScale());
        bundle.putBoolean(State.CHANGEABLE.toString(), this.changeable);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.stateToSave = bundle.getInt(State.STATE_TO_SAVE.toString());
            state = bundle.getParcelable(State.INSTANCE_SAVE.toString());
            rectangle.setScale(bundle.getDouble(State.SCALE.toString()));
            rectangle.recalculate();
            setChangeable(bundle.getBoolean(State.CHANGEABLE.toString()));
        }
        super.onRestoreInstanceState(state);
    }

    enum State {
        INSTANCE_SAVE,
        STATE_TO_SAVE,
        SCALE,
        CHANGEABLE
    }


}


