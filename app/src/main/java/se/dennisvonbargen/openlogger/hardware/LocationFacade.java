package se.dennisvonbargen.openlogger.hardware;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 *
 * Created by dennis on 2016-07-21.
 */
public class LocationFacade implements HardwareFacade, LocationListener {

    private double latitude;
    private double longitude;
    private float speed;
    private LocationManager locationManager;

    public LocationFacade(@NonNull LocationManager locationManager) {
        latitude = 0;
        longitude = 0;
        this.locationManager = locationManager;
    }

    @Override
    public void enable() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        catch (SecurityException e) {
            throw new RuntimeException("No GPS permission");
        }
    }

    @Override
    public void disable() {
        try {
            locationManager.removeUpdates(this);
        }
        catch (SecurityException e) {
            throw new RuntimeException("No GPS permission");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speed = location.getSpeed();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public double getLongitude() { return longitude; }

    public double getLatitude() { return latitude; }

    /** @return speed in meters per second */
    public float getSpeed() { return speed; }

    public float getSpeedKmh() { return 3.6f * getSpeed(); }
}
