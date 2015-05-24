package com.example.paulina.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Observable;
import java.util.Observer;


/**
 * TODO: document your custom view class.
 */
public class RectangleView extends View {
    private Paint paint;
    private MRect rectangle;
    private int stateToSave;
    private boolean changeable;
    private float x0,x1,y0,y1;
    private ImageView cursor;
    public class MRect extends Observable {

        private RectF rectangle;
        private double scaleX, scaleY;
        private double scale_amount ;
        private int height, width;

        public MRect()
        {
            rectangle = new RectF(100,100,300,300);
            scaleX = scaleY = scale_amount = 0.5;
        }
        public void recalculate() {

            rectangle.top -= scaleY;
            rectangle.bottom += scaleY;
            rectangle.left -= scaleX;
            rectangle.right += scaleX;
        }

        /*
            @direction  0 - scalowanie w poziomie
                        1 - scalowanie w pionie
                       -1 - scalowanie w obu kierunkach
         */
        public void scale(boolean up, int direction) {
            height = getHeight();
            width = getWidth();

            switch(direction) {
                case 0:
                    scaleX = rescale(up);
                    scaleY = 0;
                    scaleX = checkBoundariesX(scaleX);
                    break;
                case 1:
                    scaleY = rescale(up);
                    scaleX = 0;
                    scaleY = checkBoundariesY(scaleY);
                    break;
                case -1:
                    scaleX = rescale(up);
                    scaleY = rescale(up);
                    scaleX = checkBoundariesX(scaleX);
                    scaleY = checkBoundariesY(scaleY);
                    break;
            }
            recalculate();
            setChanged();
            notifyObservers();
        }

        public void move(float x, float y) {
            height = getHeight();
            width = getWidth();

            float move_x = (x - rectangle.centerX());
            float move_y = (y - rectangle.centerY());

//            System.out.println(String.format("(x0,y0),(x1,y1) :   (%f,%f),(%f,%f) ", rectangle.top, rectangle.left, rectangle.bottom, rectangle.right));
//            System.out.println(String.format("(x,y),(cx,cy)   :   (%f,%f),(%f,%f) ", x,y,rectangle.centerX(),rectangle.centerY()));
//            System.out.println(String.format("move_x, move_y  :   (%f,%f) ", move_x,move_y));
//            System.out.println(String.format("width, height  :   (%d,%d) ", width,height));


            if(rectangle.top + move_y < 0) {
                move_y -= rectangle.top + move_y;
            }
            if(rectangle.left + move_x < 0) {
                move_x -= rectangle.left + move_x;
            }
            if(rectangle.bottom + move_y > height) {
                move_y -= rectangle.bottom + move_y - height;
            }
            if(rectangle.right > width ) {
                move_x -= rectangle.right + move_x - width;
            }
            rectangle.top += move_y;
            rectangle.bottom += move_y;
            rectangle.left += move_x;
            rectangle.right += move_x;
            setChanged();
            notifyObservers();
//            System.out.println(String.format("move_x, move_y  :   (%f,%f) ", move_x,move_y));
//            System.out.println(String.format("(x0,y0),(x1,y1) :   (%f,%f),(%f,%f) ", rectangle.top, rectangle.left, rectangle.bottom, rectangle.right));
        }

        private double rescale( boolean up) {
            return up ? scale_amount : -scale_amount;
        }

        private double checkBoundariesX(double scale){
            if ( rectangle.right - rectangle.left - scale*2 < 200 && rectangle.right - rectangle.left + scale*2 > width - 50)
                scale = 0;
            return scale;
        }

        private double checkBoundariesY(double scale){
            if ( rectangle.bottom - rectangle.top - scale*2 < 200 && rectangle.bottom - rectangle.top + scale*2 > height - 50)
                scale = 0;
            return scale;
        }

        public boolean contains(float x, float y){ return rectangle.contains(x,y); }
        public RectF getRectangle() {return rectangle;}
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
        rectangle = new MRect();
        changeable = false;
        setWillNotDraw(false);
    }

    public void setCursor(ImageView cursor){
        this.cursor = cursor;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        System.out.println("DRAW!!");
        canvas.drawRect(rectangle.getRectangle().left, rectangle.getRectangle().top,
                rectangle.getRectangle().right, rectangle.getRectangle().bottom, paint);
//        cursor.layout((int) rectangle.getRectangle().centerX() - cursor.getWidth()/2, (int) rectangle.getRectangle().centerY() - cursor.getHeight()/2,
 //               (int) rectangle.getRectangle().centerX() + cursor.getWidth()/2, (int) rectangle.getRectangle().centerY() + cursor.getHeight()/2);

//        cursor.bringToFront();
 //       cursor.invalidate();
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
        bundle.putBoolean(State.CHANGEABLE.toString(), this.changeable);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.stateToSave = bundle.getInt(State.STATE_TO_SAVE.toString());
            state = bundle.getParcelable(State.INSTANCE_SAVE.toString());
            rectangle.recalculate();
            setChangeable(bundle.getBoolean(State.CHANGEABLE.toString()));
        }
        super.onRestoreInstanceState(state);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!changeable) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x0 = event.getX();
                y0 = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                x1 = event.getX();
                y1 = event.getY();
                break;
        }

        float delta_x = x1 - x0;
        float delta_y = y1 - y0;

        float abs_delta_x = Math.abs(delta_x);
        float abs_delta_y = Math.abs(delta_y);

        if (abs_delta_x < 0.05 && abs_delta_y < 0.05) {
            System.out.println(String.format("abs_delta (x,y):  (%f, %f)", abs_delta_x, abs_delta_y));

            rectangle.move(x1, y1);

            bringToFront();
            invalidate();
            return true;
        }

        if (!getRectangle().contains(x0, y0) || !getRectangle().contains(x1, y1))
            return false;


        boolean atan1 = Math.atan2(abs_delta_x, abs_delta_y) <= 0.5;
        boolean atan2 = Math.atan2(abs_delta_y, abs_delta_x) <= 0.5;
        boolean d1 = delta_x < 0;
        boolean d2 = delta_y < 0;

        System.out.println(String.format("atan1 <= 0.5: (%b)", atan1));
        System.out.println(String.format("atan2 <= 0.5: (%b)", atan2));
        System.out.println(String.format("delta_x < 0:  (%b)", delta_x < 0));
        System.out.println(String.format("delta_y < 0:  (%b)", delta_y < 0));
        System.out.println("");

        if (!atan1 && !atan2) {
            rectangle.scale(!d1, -1);
        }
        if (atan1) {
            rectangle.scale(!d2, 1);
        } else {
            rectangle.scale(!d1, 0);
        }

        bringToFront();
        invalidate();
        return true;

    }
    public void addObserver (Observer observer) {
        rectangle.addObserver(observer);
    }

    enum State {
        INSTANCE_SAVE,
        STATE_TO_SAVE,
        SCALE,
        CHANGEABLE
    }
}


