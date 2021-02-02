package com.murengezi.feather.Event;

import com.darkmagician6.eventapi.events.Event;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-27 at 12:53
 */
public class RenderScoreboardEvent implements Event {

	private final ScoreObjective objective;
	private final ScaledResolution resolution;

	public RenderScoreboardEvent(ScoreObjective objective, ScaledResolution resolution) {
		this.objective = objective;
		this.resolution = resolution;
	}

	public ScoreObjective getObjective() {
		return objective;
	}

	public ScaledResolution getResolution() {
		return resolution;
	}
}
