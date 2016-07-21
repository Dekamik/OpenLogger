package se.dennisvonbargen.openlogger;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import se.dennisvonbargen.openlogger.hardware.LocationFacade;
import se.dennisvonbargen.openlogger.hardware.PressureFacade;

public class MainActivity extends AppCompatActivity {

    private static final int intervalMillis = 1000;
    private Handler timerHandler;
    private Runnable timerRunnable;

    private SensorManager sensorManager;
    private PressureFacade pressureFacade;
    private LocationManager locationManager;
    private LocationFacade locationFacade;

    private double latitude = 0;
    private double longitude = 0;
    private float speed = 0;
    private float pressure = -1f;
    private float basePressure = -1f;

    private Locale locale = Locale.US;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        { // Create hardware facades
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            pressureFacade = new PressureFacade(sensorManager);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationFacade = new LocationFacade(locationManager);
        }
        { // Create update logic
            timerHandler = new Handler();
            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    pressure = pressureFacade.getPressure();
                    latitude = locationFacade.getLatitude();
                    longitude = locationFacade.getLongitude();
                    speed = locationFacade.getSpeedKmh();
                    userInput();
                    updateGUI();
                    timerHandler.postDelayed(this, intervalMillis);
                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pressureFacade.enable();
        locationFacade.enable();
        timerHandler.postDelayed(timerRunnable, intervalMillis);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        pressureFacade.disable();
        locationFacade.disable();
    }

    public void userInput() {
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
        { // Pressure and altimeter
            TextView pressureText = (TextView) findViewById(R.id.pressure);
            TextView altitudeText = (TextView) findViewById(R.id.altitude);

            final String newPressure = String.format(locale, ": %.2f hPa", pressure);
            pressureText.setText(newPressure);

            final String newAltitude = String.format(locale, ": %d m", (int) SensorManager.getAltitude(basePressure, pressure));
            altitudeText.setText(newAltitude);
        }
        { // Location and speed
            TextView latitudeText = (TextView) findViewById(R.id.latitude);
            TextView longitudeText = (TextView) findViewById(R.id.longitude);
            TextView speedText = (TextView) findViewById(R.id.speed);

            final String newLatitude = String.format(locale, ": %f", latitude);
            latitudeText.setText(newLatitude);

            final String newLongitude = String.format(locale, ": %f", longitude);
            longitudeText.setText(newLongitude);

            final String newSpeed = String.format(locale, ": %.2f km/h", speed);
            speedText.setText(newSpeed);
        }
    }
}
