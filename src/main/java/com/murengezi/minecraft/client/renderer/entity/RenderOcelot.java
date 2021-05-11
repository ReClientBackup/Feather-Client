package com.murengezi.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;

public class RenderOcelot extends RenderLiving<EntityOcelot> {

	private static final ResourceLocation blackOcelotTextures = new ResourceLocation("textures/entity/cat/black.png"), ocelotTextures = new ResourceLocation("textures/entity/cat/ocelot.png"), redOcelotTextures = new ResourceLocation("textures/entity/cat/red.png"), siameseOcelotTextures = new ResourceLocation("textures/entity/cat/siamese.png");

	public RenderOcelot(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, shadowSize);
	}

	protected ResourceLocation getEntityTexture(EntityOcelot entity) {
		switch (entity.getTameSkin()) {
			case 0:
			default:
				return ocelotTextures;
			case 1:
				return blackOcelotTextures;
			case 2:
				return redOcelotTextures;
			case 3:
				return siameseOcelotTextures;
		}
	}

	protected void preRenderCallback(EntityOcelot ocelot, float partialTickTime) {
		super.preRenderCallback(ocelot, partialTickTime);

		if (ocelot.isTamed()) {
			GlStateManager.scale(0.8F, 0.8F, 0.8F);
		}
	}

}
