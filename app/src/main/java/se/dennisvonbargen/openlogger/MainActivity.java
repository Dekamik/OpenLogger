package se.dennisvonbargen.openlogger;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import se.dennisvonbargen.openlogger.sensor.PressureSensor;

public class MainActivity extends AppCompatActivity {

    private static final int intervalMillis = 250;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private SensorManager sensorManager;
    private PressureSensor pressureSensor;

    private float pressure = -1f;
    private float basePressure = -1f;

    private Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locale = Locale.US;

        { // Get pressure sensor
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            pressureSensor = new PressureSensor(sensorManager);
        }
        { // Create update logic
            timerHandler = new Handler();
            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    pressure = pressureSensor.getPressure();
                    input();
                    updateGUI();
                    timerHandler.postDelayed(this, intervalMillis);
                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pressureSensor.enable();
        timerHandler.postDelayed(timerRunnable, intervalMillis);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        pressureSensor.disable();
    }

    public void input() {
        { // Base pressure
            EditText basePressureEdit = (EditText) findViewById(R.id.basePressure);
            if (basePressure == -1f) {
                basePressure = pressure;
                basePressureEdit.setText(String.format(locale, "%f", basePressure));
            } else {
                final String text = basePressureEdit.getText().toString();
                if (!text.isEmpty()) {
                    basePressure = Float.parseFloat(text);
                }
            }
        }
    }

    public void updateGUI() {
        { // Pressure and Altimeter
            TextView pressureText = (TextView) findViewById(R.id.pressure);
            TextView altitudeText = (TextView) findViewById(R.id.altitude);

            final String newPressure = String.format(locale, ": %.2f hPa", pressure);
            pressureText.setText(newPressure);

            final String newAltitude = String.format(locale, ": %d m", (int) SensorManager.getAltitude(basePressure, pressure));
            altitudeText.setText(newAltitude);
        }
    }
}
