package com.murengezi.chocolate.Event;

import com.darkmagician6.eventapi.events.Event;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-09 at 23:36
 */
public class KeyboardPressEvent implements Event {

    private final int key;

    public KeyboardPressEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
