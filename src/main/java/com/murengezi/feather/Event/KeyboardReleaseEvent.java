package com.murengezi.feather.Event;

import com.darkmagician6.eventapi.events.Event;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-22 16:07
 */
public class KeyboardReleaseEvent implements Event {

    private final int key;

    public KeyboardReleaseEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
