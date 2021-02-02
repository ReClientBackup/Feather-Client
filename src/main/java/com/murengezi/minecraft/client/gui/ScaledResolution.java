package com.murengezi.minecraft.client.gui;

import com.murengezi.feather.Util.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-02 at 11:22
 */
public class ScaledResolution extends MinecraftUtils {


	private final double scaledWidthD;
	private final double scaledHeightD;
	private int scaledWidth;
	private int scaledHeight;
	private int scaleFactor;

	public ScaledResolution() {
		//TODO use minecraft utils
		Minecraft mc = Minecraft.getMinecraft();
		this.scaledWidth = mc.displayWidth;
		this.scaledHeight = mc.displayHeight;
		this.scaleFactor = 1;
		boolean flag = mc.isUnicode();
		int i = mc.gameSettings.guiScale;

		if (i == 0) {
			i = 1000;
		}

		while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
			++this.scaleFactor;
		}

		if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
			--this.scaleFactor;
		}

		this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
		this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
		this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
		this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
	}

	public int getScaledWidth() {
		return this.scaledWidth;
	}

	public int getScaledHeight()
	{
		return this.scaledHeight;
	}

	public double getScaledWidth_double()
	{
		return this.scaledWidthD;
	}

	public double getScaledHeight_double()
	{
		return this.scaledHeightD;
	}

	public int getScaleFactor()
	{
		return this.scaleFactor;
	}
}
