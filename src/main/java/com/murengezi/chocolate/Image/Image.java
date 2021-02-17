package com.murengezi.chocolate.Image;

import com.murengezi.chocolate.Util.TimerUtil;
import net.minecraft.util.ResourceLocation;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-13 at 16:23
 */
public class Image {

    private final ResourceLocation location;
    private final TimerUtil timer;

    public Image(ResourceLocation location) {
        this.location = location;
        this.timer = new TimerUtil();
    }

    public ResourceLocation getLocation() {
        timer.reset();
        return location;
    }

    public TimerUtil getTimer() {
        return timer;
    }
}
