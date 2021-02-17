package com.murengezi.chocolate.Event;

import com.darkmagician6.eventapi.events.Event;
import com.murengezi.minecraft.client.gui.ScaledResolution;

public class RenderOverlayEvent implements Event {

    private final float partialTicks;
    private final ScaledResolution scaledResolution;

    public RenderOverlayEvent(float partialTicks, ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

}
