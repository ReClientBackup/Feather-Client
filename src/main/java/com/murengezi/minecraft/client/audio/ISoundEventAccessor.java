package com.murengezi.minecraft.client.audio;

public interface ISoundEventAccessor<T> {

    int getWeight();

    T cloneEntry();

}
