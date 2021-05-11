package com.murengezi.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderChicken extends RenderLiving<EntityChicken> {

	private static final ResourceLocation chickenTextures = new ResourceLocation("textures/entity/chicken.png");

	public RenderChicken(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, shadowSize);
	}

	protected ResourceLocation getEntityTexture(EntityChicken entity) {
		return chickenTextures;
	}

	protected float handleRotationFloat(EntityChicken livingBase, float partialTicks) {
		float f = livingBase.field_70888_h + (livingBase.wingRotation - livingBase.field_70888_h) * partialTicks;
		float f1 = livingBase.field_70884_g + (livingBase.destPos - livingBase.field_70884_g) * partialTicks;
		return (MathHelper.sin(f) + 1.0F) * f1;
	}

}
