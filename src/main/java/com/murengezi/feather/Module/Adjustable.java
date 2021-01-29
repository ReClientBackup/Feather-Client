package com.murengezi.feather.Module;

import com.murengezi.feather.Gui.Adjust.AdjustScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Arrays;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-22 at 18:56
 */
public class Adjustable extends Module {

	private float x = 2f, y = 2f;
	private float width, height;
	private Region region;
	private ScaledResolution resolution;
	private boolean dragging;

	private enum Region {
		TOP_LEFT(1, 1), TOP_CENTER(2, 1), TOP_RIGHT(3, 1),
		CENTER_LEFT(2, 1), CENTER(2, 2), CENTER_RIGHT(3, 3),
		BOTTOM_LEFT(3, 1), BOTTOM_CENTER(3, 2), BOTTOM_RIGHT(3, 3);

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

	public void render(int mouseX, int mouseY, ScaledResolution resolution) {
		this.resolution = resolution;
	}

	public void mouseClicked(int mouseX, int mouseY) {

	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;

		float width = (float) resolution.getScaledWidth();
		float height = (float) resolution.getScaledHeight();


		Arrays.stream(Region.values()).forEach(region -> {
			if (x <= width / 3 * region.getWidthThirds() && y <= height / 3 * region.getHeightThirds()) {
				setRegion(region);
			}
		});

	}

	public float getX() {
		if (dragging) {
			return x;
		}
		return x;
	}

	public float getY() {
		if (dragging) {
			return y;
		}
		return y;
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

	/*private AlignX alignX = AlignX.LEFT;
	private AlignY alignY = AlignY.TOP;
	private float x, y;
	private boolean lockedX, lockedY;
	private float lockX, lockY, lockMouseX, lockMouseY;

	private float dragX, dragY, width, height;

	public void render(int mouseX, int mouseY, ScaledResolution resolution, AdjustScreen screen) {
		if (isMouseOver(mouseX, mouseY, resolution) || screen.getSelected() == this) {
			Gui.drawBox(getRelativeX(resolution), getRelativeY(resolution), getWidth(), getHeight(), 0xffffffff);
		}

		if (screen.getSelected() == this) {
			if (lockedX) {
				setRelativeX(lockX, resolution);
				if (mouseX - lockMouseX > 20 || lockMouseX - mouseX > 20) {
					lockedX = false;
					setRelativeX(mouseX - getDragX(), resolution);
				}
			} else {
				setRelativeX(mouseX - getDragX(), resolution);
			}

			if (lockedY) {
				setRelativeY(lockY, resolution);
				if (mouseY - lockMouseY > 20 || lockMouseY - mouseY > 20) {
					lockedY = false;
					setRelativeY(mouseY - getDragY(), resolution);
				}
			} else {
				setRelativeY(mouseY - getDragY(), resolution);
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, ScaledResolution resolution, AdjustScreen screen) {
		if (isMouseOver(mouseX, mouseY, resolution)) {
			screen.setSelected(this);
			setDragX(mouseX - getRelativeX(resolution));
			setDragY(mouseY - getRelativeY(resolution));
		}
	}

	public void mouseReleased() {}

	public boolean isMouseOver(int mouseX, int mouseY, ScaledResolution resolution) {
		return mouseX >= getRelativeX(resolution) && mouseX <= getRelativeX(resolution) + getWidth() && mouseY >= getRelativeY(resolution) && mouseY <= getRelativeY(resolution) + getHeight();
	}

	public void lockX(int x, int mouseX) {
		if (!lockedX) {
			lockMouseX = mouseX;
		}
		lockedX = true;
		lockX = x;
	}
	public void lockY(int y, int mouseY) {
		if (!lockedY) {
			lockMouseY = mouseY;
		}
		lockedY = true;
		lockY = y;
	}

	public float getRelativeX(ScaledResolution resolution) {
		float width = resolution.getScaledWidth();
		switch (getAlignX()) {
			case LEFT:
				return (this.x * width);
			case CENTERLEFT:
				return width / 2 - (this.x * width);
			case CENTER:
				return width / 2;
			case CENTERRIGHT:
				return width / 2 + (this.x * width);
			case RIGHT:
				if (width - (this.x * width) + getWidth() > width) {
					return width - getWidth();
				}
				return width - (this.x * width);
		}
		return 0;
	}
	public float getRelativeY(ScaledResolution resolution) {
		float height = resolution.getScaledHeight();
		switch (getAlignY()) {
			case TOP:
				return (this.y * height);
			case CENTERTOP:
				return height / 2 - (this.y * height);
			case CENTER:
				return height / 2;
			case CENTERBOTTOM:
				return height / 2 + (this.y * height);
			case BOTTOM:
				if (height - (this.y * height) + getHeight() > height) {
					return height - getHeight();
				}
				return height - (this.y * height);
		}
		return 0;
	}

	public void setRelativeX(float x, ScaledResolution resolution) {
		float width = (float) resolution.getScaledWidth();


		if (x + getWidth() >= width) {
			x = width - getWidth();
		} else if (x <= 0) {
			x = 0;
		}

		if (x <= width / 3) {
			this.x = x;
			setAlignX(AlignX.LEFT);
		} else if (x <= width / 3 * 2) {
			if (x < width / 2) {
				setAlignX(AlignX.CENTERLEFT);
				this.x = width / 2 - x;
			} else if (x == width / 2) {
				setAlignX(AlignX.CENTER);
				this.x = 0;
			} else if (x > width / 2) {
				setAlignX(AlignX.CENTERRIGHT);
				this.x = x - width / 2;
			}
		} else if (x <= width) {
			setAlignX(AlignX.RIGHT);
			this.x = width - x;
		}

		this.x /= width;
	}
	public void setRelativeY(float y, ScaledResolution resolution) {
		float height = (float) resolution.getScaledHeight();

		if (y + getHeight() >= height) {
			y = height - getHeight();
		} else if (y <= 0) {
			y = 0;
		}

		if (y <= height / 3) {
			this.y = y;
			setAlignY(AlignY.TOP);
		} else if (y <= height / 3 * 2) {
			if (y < height / 2) {
				setAlignY(AlignY.CENTERTOP);
				this.y = height / 2 - y;
			} else if (y == height / 2) {
				setAlignY(AlignY.CENTER);
				this.y = 0;
			} else if (y > height / 2) {
				setAlignY(AlignY.CENTERBOTTOM);
				this.y = y - height / 2;
			}
		} else if (y <= height) {
			setAlignY(AlignY.BOTTOM);
			this.y = height - y;
		}

		this.y /= height;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Regular getters and setters
	 */

	/*public AlignX getAlignX() {
		return alignX;
	}
	public void setAlignX(AlignX alignX) {
		this.alignX = alignX;
	}
	public AlignY getAlignY() {
		return alignY;
	}
	public void setAlignY(AlignY alignY) {
		this.alignY = alignY;
	}
	public float getDragX() {
		return dragX;
	}
	public void setDragX(float dragX) {
		this.dragX = dragX;
	}
	public float getDragY() {
		return dragY;
	}
	public void setDragY(float dragY) {
		this.dragY = dragY;
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
	public enum AlignX {
		LEFT, CENTERLEFT, CENTER, CENTERRIGHT, RIGHT
	}
	public enum AlignY {
		TOP, CENTERTOP, CENTER, CENTERBOTTOM, BOTTOM
	}*/
}
