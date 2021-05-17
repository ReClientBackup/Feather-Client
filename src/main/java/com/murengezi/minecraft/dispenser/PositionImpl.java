package com.murengezi.minecraft.dispenser;

public class PositionImpl implements IPosition {

	protected final double x, y, z;

	public PositionImpl(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

}
