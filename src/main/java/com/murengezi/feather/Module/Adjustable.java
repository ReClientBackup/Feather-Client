package com.murengezi.feather.Module;

import com.murengezi.minecraft.client.gui.ScaledResolution;

import java.util.Arrays;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-22 at 18:56
 */
public class Adjustable extends Module {

	private float x, y, width, height;
	private Region region;
	private Align alignX = Align.NONE, alignY = Align.NONE;
	private ScaledResolution resolution;
	private boolean dragging;

	public Adjustable() {
		x = 0.0f;
		y = 0.0f;
		region = Region.TOP_LEFT;
	}

	public Adjustable(float x, float y, Region region) {
		this.x = x;
		this.y = y;
		this.region = region;
	}

	public enum Region {
		TOP_LEFT(1, 1), TOP_CENTER(2, 1), TOP_RIGHT(3, 1),
		CENTER_LEFT(1, 2), CENTER(2, 2), CENTER_RIGHT(3, 2),
		BOTTOM_LEFT(1, 3), BOTTOM_CENTER(2, 3), BOTTOM_RIGHT(3, 3);

		private final int widthThirds, heightThirds;

		Region(int widthThirds, int heightThirds) {
			this.widthThirds = widthThirds;
			this.heightThirds = heightThirds;
		}

		public int getWidthThirds() {
			return widthThirds;
		}

		public int getHeightThirds() {
			return heightThirds;
		}
	}

	public enum Align {
		NONE, CENTER;
	}

	public void render(ScaledResolution resolution) {
		this.resolution = resolution;
	}

	public void mouseReleased() {
		float resolutionWidth = resolution.getScaledWidth();
		float resolutionHeight = resolution.getScaledHeight();
		float posX = getX();
		float posY = getY();

		if (getAlignX() == Align.CENTER) {
			posX = (resolutionWidth / 3) * getRegion().getWidthThirds() - (resolutionWidth / 6) - getWidth() / 2;
		}

		if (getAlignY() == Align.CENTER) {
			posY = (resolutionHeight / 3) * getRegion().getHeightThirds() - (resolutionHeight / 6) - getHeight() / 2;
		}

		setPosition(posX, posY);
	}

	public void setPosition(float x, float y) {
		float width = (float) resolution.getScaledWidth();
		float height = (float) resolution.getScaledHeight();

		Arrays.stream(Region.values()).forEach(region -> {
			if (x >= width / 3 * Math.max(region.getWidthThirds() - 1, 0)
					&& x <= width / 3 * region.getWidthThirds()
					&& y >= height / 3 * Math.max(region.getHeightThirds() - 1, 0)
					&& y <= height / 3 * region.getHeightThirds()) {
				setRegion(region);
			}
		});

		switch (getRegion()) {
			case TOP_LEFT:
			case CENTER_LEFT:
			case BOTTOM_LEFT:
				if (x < 1.0f && this.x < 1.0f) {
					this.x = 1.0f;
				} else {
					this.x = x;
				}
				break;
			case TOP_CENTER:
			case CENTER:
			case BOTTOM_CENTER:
				if (x >= width / 2) {
					this.x = (x - (width / 2)) + getWidth() / 2;
				} else {
					this.x = -((width / 2) - x) + getWidth() / 2;
				}
				break;
			case TOP_RIGHT:
			case CENTER_RIGHT:
			case BOTTOM_RIGHT:
				if (width - x - getWidth() < 1.0f && this.x < 1.0f) {
					this.x = 1.0f;
				} else {
					this.x = width - x - getWidth();
				}
				break;
		}

		switch (getRegion()) {
			case TOP_LEFT:
			case TOP_CENTER:
			case TOP_RIGHT:
				if (y < 1.0f && this.y < 1.0f) {
					this.y = 1.0f;
				} else {
					this.y = y;
				}
				break;
			case CENTER_LEFT:
			case CENTER:
			case CENTER_RIGHT:
				if (y >= height / 2) {
					this.y = (y - (height / 2)) + getHeight() / 2;
				} else {
					this.y = -((height / 2) - y) + getHeight() / 2;
				}
				break;
			case BOTTOM_LEFT:
			case BOTTOM_CENTER:
			case BOTTOM_RIGHT:
				if (height - y - getHeight() < 1.0f && this.y < 1.0f) {
					this.y = 1.0f;
				} else {
					this.y = height - y - getHeight();
				}
				break;
		}

		this.x /= width;
		this.y /= height;
	}

	public float getX() {
		float width = (float) resolution.getScaledWidth();
		switch (getRegion()) {
			case TOP_LEFT:
			case CENTER_LEFT:
			case BOTTOM_LEFT:
				return this.x * width;
			case TOP_CENTER:
			case CENTER:
			case BOTTOM_CENTER:
				return (width / 2) + (this.x * width) - getWidth() / 2;
			case TOP_RIGHT:
			case CENTER_RIGHT:
			case BOTTOM_RIGHT:
				return width - (this.x * width) - getWidth();
		}
		return this.x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getRelativeX() {
		return this.x;
	}

	public float getY() {
		float height = (float) resolution.getScaledHeight();
		switch (getRegion()) {
			case TOP_LEFT:
			case TOP_CENTER:
			case TOP_RIGHT:
				return this.y * height;
			case CENTER_LEFT:
			case CENTER:
			case CENTER_RIGHT:
				return (height / 2) + (this.y * height) - getHeight() / 2;
			case BOTTOM_LEFT:
			case BOTTOM_CENTER:
			case BOTTOM_RIGHT:
				return height - (this.y * height) - getHeight();
		}
		return y;
	}

	public float getRelativeY() {
		return this.y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public Align getAlignX() {
		return alignX;
	}

	public void setAlignX(Align alignX) {
		this.alignX = alignX;
	}

	public Align getAlignY() {
		return alignY;
	}

	public void setAlignY(Align alignY) {
		this.alignY = alignY;
	}

	public ScaledResolution getResolution() {
		return resolution;
	}

	public void setResolution(ScaledResolution resolution) {
		this.resolution = resolution;
	}

	public boolean isDragging() {
		return dragging;
	}

	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}
}
