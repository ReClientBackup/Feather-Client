package com.murengezi.feather.Module;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.RenderOverlayEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-22 at 18:56
 */
public class Adjustable extends Module {

	private double relativeX, relativeY;
	private int dragX, dragY, width, height;
	private boolean selected;

	public Adjustable(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void render(int mouseX, int mouseY, ScaledResolution resolution) {
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();
		Gui.drawRect(getX(width), getY(height), getX(width) + getWidth(), getY(height) + getHeight(), Integer.MAX_VALUE);

		if (this.isSelected()) {
			setX(mouseX - getDragX(), width);
			setY(mouseY - getDragY(), height);
		}
	}

	public void mouseClicked(int mouseX, int mouseY, ScaledResolution resolution) {
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();
		if (mouseX >= getX(width) && mouseX <= getX(width) + getWidth() && mouseY >= getY(height) && mouseY <= getY(height) + getHeight()) {
			if (!this.isSelected()) {
				this.setSelected(true);
				setDragX(mouseX - getX(width) - getWidth());
				setDragY(mouseY - getY(height) - getHeight());
			}
		}
	}

	public void mouseReleased() {
		if (this.isSelected()) {
			this.setSelected(false);
		}
	}

	public int getX(int width) {
		if (relativeX < 0.5d) {
			return (int) (relativeX * width);
		} else if (relativeX > 0.5d) {
			return (int) (relativeX * width) - getWidth();
		} else if (relativeX == 0.5d) {
			return width / 2;
		}
		return 0;
	}

	public double getRelativeX() {
		return relativeX;
	}

	public void setX(int x, int width) {
		if (x - getWidth() > 0 && x < width) {
			double relative = (double) x / (double) width;
			if (relative < 0.5d) {
				this.relativeX = ((double) x - (double)getWidth()) / (double) width;
			} else if (relative >= 0.5d) {
				this.relativeX = (double) x / (double) width;
			}
		}
	}

	public void setRelativeX(double x) {
		this.relativeX = x;
	}

	public int getY(int height) {
		if (relativeY < 0.5d) {
			return (int) (relativeY * height);
		} else if (relativeY > 0.5d) {
			return (int) (relativeY * height) - getHeight();
		} else if (relativeY == 0.5d) {
			return height / 2;
		}
		return 0;
	}

	public double getRelativeY() {
		return relativeY;
	}

	public void setY(int y, int height) {
		if (y - getHeight() > 1 && y < height) {
			double relative = (double) y / (double) height;
			if (relative <= 0.5d) {
				this.relativeY = ((double) y - (double)getHeight()) / (double) height;
			} else if (relative > 0.5d) {
				this.relativeY = (double) y / (double) height;
			}
		}
	}

	public void setRelativeY(double y) {
		this.relativeY = y;
	}

	public int getDragX() {
		return dragX;
	}

	public void setDragX(int dragX) {
		this.dragX = dragX;
	}

	public int getDragY() {
		return dragY;
	}

	public void setDragY(int dragY) {
		this.dragY = dragY;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
