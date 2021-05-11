package com.murengezi.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerWolfCollar;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

public class RenderWolf extends RenderLiving<EntityWolf> {

	private static final ResourceLocation wolfTextures = new ResourceLocation("textures/entity/wolf/wolf.png"), tamedWolfTextures = new ResourceLocation("textures/entity/wolf/wolf_tame.png"), anrgyWolfTextures = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

	public RenderWolf(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, shadowSize);
		this.addLayer(new LayerWolfCollar(this));
	}

	protected float handleRotationFloat(EntityWolf livingBase, float partialTicks) {
		return livingBase.getTailRotation();
	}

	public void doRender(EntityWolf entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (entity.isWolfWet()) {
			float f = entity.getBrightness(partialTicks) * entity.getShadingWhileWet(partialTicks);
			GlStateManager.color(f, f, f);
		}

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	protected ResourceLocation getEntityTexture(EntityWolf entity) {
		return entity.isTamed() ? tamedWolfTextures : (entity.isAngry() ? anrgyWolfTextures : wolfTextures);
	}

}
