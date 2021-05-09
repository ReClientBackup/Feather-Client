package com.murengezi.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public class SoundPoolEntry {

    private final ResourceLocation location;
    private final boolean streamingSound;
    private double pitch, volume;

    public SoundPoolEntry(ResourceLocation location, double pitch, double volume, boolean streamingSound) {
        this.location = location;
        this.pitch = pitch;
        this.volume = volume;
        this.streamingSound = streamingSound;
    }

    public SoundPoolEntry(SoundPoolEntry location) {
        this.location = location.location;
        this.pitch = location.pitch;
        this.volume = location.volume;
        this.streamingSound = location.streamingSound;
    }

    public ResourceLocation getSoundPoolEntryLocation() {
        return this.location;
    }

    public double getPitch() {
        return this.pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getVolume() {
        return this.volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public boolean isStreamingSound() {
        return this.streamingSound;
    }

}
