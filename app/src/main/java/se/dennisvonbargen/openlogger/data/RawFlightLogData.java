package se.dennisvonbargen.openlogger.data;

import android.support.annotation.NonNull;

/**
 *
 * Created by dennis on 2016-07-25.
 */
public class RawFlightLogData implements Comparable<RawFlightLogData> {

    // ID
    private long timestamp;

    // Location
    private double latitude;
    private double longitude;

    // Pressure
    private float seaPressure;
    private float groundPressure;
    private float pressure;

    // Measurements
    private float speedKmh;

    public RawFlightLogData(long timestamp, double latitude, double longitude, float seaPressure,
                            float groundPressure, float pressure, float speedKmh) {
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.seaPressure = seaPressure;
        this.groundPressure = groundPressure;
        this.pressure = pressure;
        this.speedKmh = speedKmh;
    }

    @Override
    public int compareTo(@NonNull RawFlightLogData other) {
        if (timestamp < other.getTimestamp())
            return -1;
        else if (timestamp > other.timestamp)
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawFlightLogData that = (RawFlightLogData) o;

        return timestamp == that.getTimestamp();

    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getSeaPressure() {
        return seaPressure;
    }

    public float getGroundPressure() {
        return groundPressure;
    }

    public float getPressure() {
        return pressure;
    }

    public float getSpeedKmh() {
        return speedKmh;
    }
}
