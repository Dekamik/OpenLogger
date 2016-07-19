package se.dennisvonbargen.openlogger;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int intervalMillis = 250;
    private Handler handler;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private float basePressure = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        { // Get sensor
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float pressure = sensorEvent.values[0];
        EditText basePressureEdit = (EditText) findViewById(R.id.basePressure);
        TextView pressureText = (TextView) findViewById(R.id.pressure);
        TextView altitudeText = (TextView) findViewById(R.id.altitude);

        if (basePressure == -1f) {
            basePressure = pressure;
            basePressureEdit.setText(String.format(Locale.getDefault(), "%f", basePressure));
        }
        else {
            final String text = basePressureEdit.getText().toString();
            if (!text.isEmpty()) {
                basePressure = Float.parseFloat(text);
            }
        }

        { // Set new texts
            final String newPressure = String.format(Locale.getDefault(), ": %.2f hPa", pressure);
            pressureText.setText(newPressure);

            final String newAltitude = String.format(Locale.getDefault(), ": %d m", (int) SensorManager.getAltitude(basePressure, pressure));
            altitudeText.setText(newAltitude);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
