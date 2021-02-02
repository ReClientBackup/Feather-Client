package com.murengezi.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-02 at 11:58
 */
public class PotionAttackDamage extends Potion {

	protected PotionAttackDamage(int id, ResourceLocation location, boolean badEffect, int liquidColor)
	{
		super(id, location, badEffect, liquidColor);
	}

	public double getAttributeModifierAmount(int p_111183_1_, AttributeModifier modifier) {
		return this.id == Potion.weakness.id ? (double)(-0.5F * (float)(p_111183_1_ + 1)) : 1.3D * (double)(p_111183_1_ + 1);
	}

}
