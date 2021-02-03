package com.murengezi.minecraft.potion;

import net.minecraft.util.ResourceLocation;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-02 at 11:59
 */
public class PotionHealth extends Potion {

	protected PotionHealth(int id, ResourceLocation location, boolean badEffect, int liquidColor) {
		super(id, location, badEffect, liquidColor);
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	public boolean isReady(int p_76397_1_, int p_76397_2_) {
		return p_76397_1_ >= 1;
	}
}
