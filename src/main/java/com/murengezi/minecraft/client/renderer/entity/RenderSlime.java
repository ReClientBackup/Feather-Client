package com.murengezi.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerSlimeGel;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;

public class RenderSlime extends RenderLiving<EntitySlime> {

	private static final ResourceLocation slimeTextures = new ResourceLocation("textures/entity/slime/slime.png");

	public RenderSlime(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, shadowSize);
		this.addLayer(new LayerSlimeGel(this));
	}

	public void doRender(EntitySlime entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.shadowSize = 0.25F * (float) entity.getSlimeSize();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	protected void preRenderCallback(EntitySlime slime, float partialTickTime) {
		float f = (float) slime.getSlimeSize();
		float f1 = (slime.prevSquishFactor + (slime.squishFactor - slime.prevSquishFactor) * partialTickTime) / (f * 0.5F + 1.0F);
		float f2 = 1.0F / (f1 + 1.0F);
		GlStateManager.scale(f2 * f, 1.0F / f2 * f, f2 * f);
	}

	protected ResourceLocation getEntityTexture(EntitySlime entity) {
		return slimeTextures;
	}

}
