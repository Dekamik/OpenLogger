package se.dennisvonbargen.openlogger.data;

import java.util.ArrayList;

/**
 *
 * Created by dennis on 2016-07-25.
 */
public class RawFlightLog {

    private long timeStart = -1L;
    private long timeFinish = -1L;
    private ArrayList<RawFlightLogData> data;

    public RawFlightLog() {
        data = new ArrayList<>();
    }

    /**
     * Adds the rawFlightLogData if timeFinish isn't set.
     * If timeStart isn't set, it'll set time start to the current time before adding the log
     *
     * @param rawFlightLogData the rawFlightLogData to add
     */
    public void add(RawFlightLogData rawFlightLogData) {
        if (timeFinish != -1L) {
            if (timeStart == -1L) {
                timeStart = System.currentTimeMillis();
            }
            data.add(rawFlightLogData);
        }
    }

    public void finish() {
        timeFinish = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return timeFinish != -1L;
    }

    public boolean isStarted() {
        return timeStart != -1L;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeFinish() {
        return timeFinish;
    }

    public ArrayList<RawFlightLogData> getData() {
        return data;
    }
}
