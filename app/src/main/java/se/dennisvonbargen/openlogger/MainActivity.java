package se.dennisvonbargen.openlogger;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import se.dennisvonbargen.openlogger.data.Export;
import se.dennisvonbargen.openlogger.data.ExternalStorage;
import se.dennisvonbargen.openlogger.data.RawFlightLog;
import se.dennisvonbargen.openlogger.data.RawFlightLogData;
import se.dennisvonbargen.openlogger.hardware.LocationFacade;
import se.dennisvonbargen.openlogger.hardware.PressureFacade;

public class MainActivity extends AppCompatActivity {

    private static final int updateIntervalMillis = 100;
    private static final int loggerIntervalMillis = 1000;
    private static final Locale locale = Locale.US;
    private static final int ALL_PERMISSIONS = 0;

    private Handler handler;
    private Runnable updateRunnable, loggerRunnable;
    private State currentState = State.IDLE;

    private PressureFacade pressureFacade;
    private LocationFacade locationFacade;

    private double latitude = 0;
    private double longitude = 0;
    private float speed = 0;
    private float pressure = -1f;
    private float groundPressure = -1f;
    private float seaPressure = -1f;

    private RawFlightLog flightLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        { // Hardware facades
            pressureFacade = new PressureFacade((SensorManager) getSystemService(Context.SENSOR_SERVICE));
            locationFacade = new LocationFacade((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        }
        { // Update loop
            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    pressure = pressureFacade.getPressure();
                    latitude = locationFacade.getLatitude();
                    longitude = locationFacade.getLongitude();
                    speed = locationFacade.getSpeedKmh();
                    userInput();
                    updateGUI();
                    handler.postDelayed(updateRunnable, updateIntervalMillis);
                }
            };
        }
        { // Logger loop
            loggerRunnable = new Runnable() {
                @Override
                public void run() {
                    if (State.RECORDING == currentState) {
                        if (flightLog == null) flightLog = new RawFlightLog();
                        flightLog.add(new RawFlightLogData(System.currentTimeMillis(), latitude,
                                longitude, seaPressure, groundPressure, pressure, speed));
                        handler.postDelayed(loggerRunnable, loggerIntervalMillis);
                    }
                }
            };
        }
        { // Button listeners
            findViewById(R.id.btn_record).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            { // Toggle recording
                                if (State.IDLE == currentState) {
                                    currentState = State.RECORDING;
                                    handler.postDelayed(loggerRunnable, loggerIntervalMillis);
                                }
                                else {
                                    currentState = State.IDLE;
                                    if (flightLog != null) {
                                        if (flightLog.isStarted() && !flightLog.isFinished()) {
                                            flightLog.finish();
                                            if (ExternalStorage.isExternalStorageWritable()) {
                                                ExternalStorage.saveFile("test.json",
                                                        Export.toJSONString(flightLog));
                                            }
                                            else {
                                                throw new RuntimeException("External storage not writable");
                                            }
                                        }
                                    }
                                    handler.removeCallbacks(loggerRunnable);
                                }
                            }
                        }
                    }
            );
        }
        { // Check permissions
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                    getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                    getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[] {
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 0);
            }
            else {
                pressureFacade.enable();
                locationFacade.enable();
                handler.postDelayed(updateRunnable, updateIntervalMillis);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS) {
            for (int result : grantResults) {
                if (PackageManager.PERMISSION_DENIED == result) {
                    { // Alert and shut down
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.dia_no_permissions_quit_text)
                                .setTitle(R.string.dia_no_permissions_quit_title)
                                .setNeutralButton(R.string.com_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        pressureFacade.disable();
                                        locationFacade.disable();
                                        handler.removeCallbacks(updateRunnable);
                                        handler.removeCallbacks(loggerRunnable);
                                        finish();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
            pressureFacade.enable();
            locationFacade.enable();
            handler.postDelayed(updateRunnable, updateIntervalMillis);
        }
    }

    private void userInput() {
        { // Base pressure
            final EditText basePressureEdit = (EditText) findViewById(R.id.basePressure);
            if (groundPressure == -1f) {
                groundPressure = pressure;
                basePressureEdit.setText(String.format(locale, "%f", groundPressure));
            } else {
                final String text = basePressureEdit.getText().toString();
                if (!text.isEmpty()) {
                    groundPressure = Float.parseFloat(text);
                }
            }
        }
    }

    private void updateGUI() {
        { // Pressure and altimeter
            final String newPressure = String.format(locale, ": %.2f hPa", pressure);
            final String newAltitude = String.format(locale, ": %d m", (int) SensorManager.getAltitude(groundPressure, pressure));

            ((TextView) findViewById(R.id.pressure)).setText(newPressure);
            ((TextView) findViewById(R.id.altitude)).setText(newAltitude);
        }
        { // Location and speed
            final String newLatitude = String.format(locale, ": %f", latitude);
            final String newLongitude = String.format(locale, ": %f", longitude);
            final String newSpeed = String.format(locale, ": %.2f km/h", speed);

            ((TextView) findViewById(R.id.latitude)).setText(newLatitude);
            ((TextView) findViewById(R.id.longitude)).setText(newLongitude);
            ((TextView) findViewById(R.id.speed)).setText(newSpeed);
        }
        { // Record button
            if (State.IDLE == currentState) { ((Button) findViewById(R.id.btn_record)).setText(R.string.btn_record); }
            else                            { ((Button) findViewById(R.id.btn_record)).setText(R.string.btn_recording); }
        }
    }
}
