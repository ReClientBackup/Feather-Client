package com.murengezi.chocolate.Event;

import com.darkmagician6.eventapi.events.Event;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-04 at 08:13
 */
public class KeyboardEvent implements Event {

	private final int key;

	public KeyboardEvent(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}
}
