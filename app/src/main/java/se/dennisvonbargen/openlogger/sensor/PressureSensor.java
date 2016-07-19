package se.dennisvonbargen.openlogger.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Facade for pressure sensor
 *
 * @author Dennis von Bargen
 */
public class PressureSensor implements SensorEventListener {

    private float pressure;
    private SensorManager sensorManager;
    private Sensor pressureSensor;

    public PressureSensor(SensorManager sensorManager) {
        pressure = -1f;
        this.sensorManager = sensorManager;
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        pressure = sensorEvent.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void enable() {
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void disable() {
        sensorManager.unregisterListener(this);
    }

    public float getPressure() {
        return pressure;
    }
}
