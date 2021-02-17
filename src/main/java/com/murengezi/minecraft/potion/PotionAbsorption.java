package com.murengezi.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.util.ResourceLocation;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-02 at 12:02
 */
public class PotionAbsorption extends Potion{

	protected PotionAbsorption(int id, ResourceLocation location, boolean badEffect, int liquidColor) {
		super(id, location, badEffect, liquidColor);
	}

	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBase, BaseAttributeMap attributeMap, int amplifier) {
		entityLivingBase.setAbsorptionAmount(entityLivingBase.getAbsorptionAmount() + (float)(4 * (amplifier + 1)));
		super.applyAttributesModifiersToEntity(entityLivingBase, attributeMap, amplifier);
	}

	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBase, BaseAttributeMap attributeMap, int amplifier) {
		entityLivingBase.setAbsorptionAmount(entityLivingBase.getAbsorptionAmount() - (float)(4 * (amplifier + 1)));
		super.removeAttributesModifiersFromEntity(entityLivingBase, attributeMap, amplifier);
	}
}
