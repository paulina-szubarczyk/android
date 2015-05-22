package com.example.paulina.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;


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

        public RectF getRectangle() {
            return rectangle;
        }

        private double scale;
        private double scale_amount ;
        private View view;
        public MRect(double scale_, View view_)
        {
            rectangle = new RectF();
            scale_amount = 0.05;
            scale = scale_;
            view = view_;

            rectangle.top = 100;
            rectangle.bottom = 400;
            rectangle.left = 100;
            rectangle.right = 400;
        }
        public void recalculate() {

            rectangle.top = (int) (scale*view.getHeight());
            rectangle.bottom = (int) ((1-scale)*view.getHeight());
            rectangle.left = (int) (scale*view.getWidth());
            rectangle.right = (int) ((1-scale)*view.getWidth());
            System.out.println(rectangle.top+" " + rectangle.bottom + " " + rectangle.left + " " +rectangle.right);
        }

        public void scale(boolean up) {
            scale += up ? scale_amount : -scale_amount;
            if (scale > 0.5)
                scale = 0.5;
            else if (scale < 0.05)
                scale = 0.05;

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
        super(context,attrs);
        init();
    }
    public RectangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        init();
    }
    private void init() {
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        rectangle = new MRect(0.25,this);
        changeable = false;
    }

    @Override
    public void onDraw(Canvas canvas) {

        System.out.println("DRAw!!");
        canvas.drawRect(rectangle.getRectangle().left, rectangle.getRectangle().top,
                        rectangle.getRectangle().right, rectangle.getRectangle().bottom, paint);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(State.INSTANCE_SAVE.toString(), super.onSaveInstanceState());
        bundle.putInt(State.STATE_TO_SAVE.toString(), this.stateToSave);
        bundle.putDouble(State.SCALE.toString(), this.rectangle.getScale());
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
        }
        super.onRestoreInstanceState(state);
    }

    enum State {
        INSTANCE_SAVE,
        STATE_TO_SAVE,
        SCALE
    }


}


