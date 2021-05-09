package com.murengezi.minecraft.client.audio;

import net.minecraft.util.ITickable;

public interface ITickableSound extends ISound, ITickable {

    boolean isDonePlaying();

}
