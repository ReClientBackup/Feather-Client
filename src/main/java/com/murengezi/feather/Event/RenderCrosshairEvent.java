package com.murengezi.feather.Event;

import com.darkmagician6.eventapi.events.Cancellable;
import com.darkmagician6.eventapi.events.Event;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-28 at 11:12
 */
public class RenderCrosshairEvent implements Event, Cancellable {

	private boolean cancelled;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
