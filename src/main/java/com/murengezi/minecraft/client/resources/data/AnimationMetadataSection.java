package com.murengezi.minecraft.client.resources.data;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class AnimationMetadataSection implements IMetadataSection {
	private final List<AnimationFrame> animationFrames;
	private final int frameWidth, frameHeight, frameTime;
	private final boolean interpolate;

	public AnimationMetadataSection(List<AnimationFrame> animationFrames, int frameWidth, int frameHeight, int frameTime, boolean interpolate) {
		this.animationFrames = animationFrames;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.frameTime = frameTime;
		this.interpolate = interpolate;
	}

	public int getFrameHeight() {
		return this.frameHeight;
	}

	public int getFrameWidth() {
		return this.frameWidth;
	}

	public int getFrameCount() {
		return this.animationFrames.size();
	}

	public int getFrameTime() {
		return this.frameTime;
	}

	public boolean isInterpolate() {
		return this.interpolate;
	}

	private AnimationFrame getAnimationFrame(int index) {
		return this.animationFrames.get(index);
	}

	public int getFrameTimeSingle(int index) {
		AnimationFrame animationframe = this.getAnimationFrame(index);
		return animationframe.hasNoTime() ? this.frameTime : animationframe.getFrameTime();
	}

	public boolean frameHasTime(int index) {
		return !this.animationFrames.get(index).hasNoTime();
	}

	public int getFrameIndex(int index) {
		return this.animationFrames.get(index).getFrameIndex();
	}

	public Set<Integer> getFrameIndexSet() {
		Set<Integer> set = Sets.newHashSet();

		for (AnimationFrame animationframe : this.animationFrames) {
			set.add(animationframe.getFrameIndex());
		}

		return set;
	}

}
