package com.example.paulina.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class GraphActivity extends Activity {

    private GraphView gx,gy,gz,gh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        float[] gradient_x = getIntent().getFloatArrayExtra("gradient_x");
        float[] gradient_y = getIntent().getFloatArrayExtra("gradient_y");
        float[] gradient   = getIntent().getFloatArrayExtra("gradient");
        byte[] histogram  = getIntent().getByteArrayExtra("histogram");

        addListenerOnButton();

        gx = (GraphView)findViewById(R.id.gradient_x);
        LineGraphSeries<DataPoint> series_x = new LineGraphSeries<>(generateData(gradient_x));
        gx.addSeries(series_x);

        gy = (GraphView)findViewById(R.id.gradient_y);
        LineGraphSeries<DataPoint> series_y = new LineGraphSeries<>(generateData(gradient_y));
        gy.addSeries(series_y);

        gz = (GraphView)findViewById(R.id.gradient_z);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(generateData(gradient));
        gz.addSeries(series);

        gh = (GraphView)findViewById(R.id.histogram);
        LineGraphSeries<DataPoint> series_h = new LineGraphSeries<>(generateData(histogram));
        gh.addSeries(series_h);
    }

    public DataPoint[] generateData(float[] array) {
        if(array == null)
            return new DataPoint[0];

        DataPoint[] data = new DataPoint[array.length];
        for(int i=0; i < data.length; ++i) {
            data[i] = new DataPoint(i,array[i]);
        }
        return data;
    }

    public DataPoint[] generateData(byte[] array) {
        if(array == null)
            return new DataPoint[0];

        DataPoint[] data = new DataPoint[array.length];
        for(int i=0; i < data.length; ++i) {
            data[i] = new DataPoint(i,(int)array[i]);
        }
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addListenerOnButton() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.gx) {
                    gx.bringToFront();
                    gx.setVisibility(View.VISIBLE);
                    gx.invalidate();
                } else {
                    gx.setVisibility(View.INVISIBLE);
                }

                if(checkedId == R.id.gy) {
                    gy.bringToFront();
                    gy.setVisibility(View.VISIBLE);
                    gy.invalidate();
                } else {
                    gy.setVisibility(View.INVISIBLE);
                }
                if(checkedId == R.id.gz) {
                    gz.bringToFront();
                    gz.setVisibility(View.VISIBLE);
                    gz.invalidate();
                } else {
                    gz.setVisibility(View.INVISIBLE);
                }
                if(checkedId == R.id.gh) {
                    gh.bringToFront();
                    gh.setVisibility(View.VISIBLE);
                    gh.invalidate();
                } else {
                    gh.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
