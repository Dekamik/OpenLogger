package se.dennisvonbargen.openlogger;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by dennis on 2016-07-18.
 */
public class Altimeter {

    @Getter @Setter private float basePressure;

    public Altimeter() {

    }

    /**
     * @param basePressure pressure at sea-level or ground-level in hectopascal (hPa)
     * @param pressure measured pressure at current altitude in hPa
     * @param temperature measured temperature at current altitude in celsius
     * @return altitude in meters
     */
    public float calculateAltitude(final float basePressure, final float pressure,
                                   final float temperature) {
        final float ratio = basePressure / pressure;
        return (float) ((((ratio * (1 / 5.257)) - 1) * (temperature + 273.15)) / 0.0065);
    }
}
