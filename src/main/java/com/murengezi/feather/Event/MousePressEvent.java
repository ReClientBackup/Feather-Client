package com.murengezi.feather.Event;

import com.darkmagician6.eventapi.events.Event;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-22 at 14:38
 */
public class MousePressEvent implements Event {

	private final int mouseButton;

	public MousePressEvent(int mouseButton) {
		this.mouseButton = mouseButton;
	}

	public int getMouseButton() {
		return mouseButton;
	}
}
