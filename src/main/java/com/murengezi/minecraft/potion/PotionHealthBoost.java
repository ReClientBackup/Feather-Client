package com.murengezi.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.util.ResourceLocation;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-02 at 12:01
 */
public class PotionHealthBoost extends Potion {

	protected PotionHealthBoost(int id, ResourceLocation location, boolean badEffect, int liquidColor) {
		super(id, location, badEffect, liquidColor);
	}

	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, BaseAttributeMap attributeMap, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMap, amplifier);

		if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth()) {
			entityLivingBaseIn.setHealth(entityLivingBaseIn.getMaxHealth());
		}
	}
}
