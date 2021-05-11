package com.murengezi.minecraft.client.resources.data;

public class AnimationFrame {

	private final int frameIndex, frameTime;

	public AnimationFrame(int frameIndex) {
		this(frameIndex, -1);
	}

	public AnimationFrame(int frameIndex, int frameTime) {
		this.frameIndex = frameIndex;
		this.frameTime = frameTime;
	}

	public boolean hasNoTime() {
		return this.frameTime == -1;
	}

	public int getFrameTime() {
		return this.frameTime;
	}

	public int getFrameIndex() {
		return this.frameIndex;
	}

}
