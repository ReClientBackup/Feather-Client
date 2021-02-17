package com.murengezi.chocolate.Util;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 20:10
 */
public class TimerUtil {

    private long lastMS;

    public TimerUtil() {
        reset();
    }

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public boolean hasPassed(long ms) {
        return System.currentTimeMillis() - lastMS >= ms;
    }

    public long getLastMS() {
        return lastMS;
    }

    public long getPassed() {
        return System.currentTimeMillis() - lastMS;
    }
}
