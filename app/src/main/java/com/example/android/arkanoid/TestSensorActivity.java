package com.example.android.arkanoid;

import android.app.Service;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TestSensorActivity extends AppCompatActivity implements SensorEventListener {
    private TextView xView;
    private TextView yView;
    private TextView zView;
    private TextView tView;

    private long timeStamp;
    private float tValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sensor);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.xView = findViewById(R.id.xInfo);
        this.yView = findViewById(R.id.yInfo);
        this.zView = findViewById(R.id.zInfo);
        this.tView = findViewById(R.id.tInfo);

        SensorManager sm = (SensorManager) this.getSystemService(Service.SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        System.out.println("Range: " + sensor.getMaximumRange());

        this.timeStamp = -1;
        this.tValue = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(this.timeStamp != -1){
            this.xView.setText("X: " + event.values[0]);
            this.yView.setText("Y: " + event.values[1]);
            this.zView.setText("Z: " + event.values[2]);

            long currentDelay = System.currentTimeMillis() - this.timeStamp;
            float dt = currentDelay / 1000.0f;

            float zInc = event.values[2] * dt;
            this.tValue += zInc;
            double degreeValue =(this.tValue * 180) / Math.PI;
            this.tView.setText("T: " + degreeValue % 360 + " - " + event.timestamp);
        }

        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}