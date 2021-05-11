package com.murengezi.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;

public class RenderSheep extends RenderLiving<EntitySheep> {

	private static final ResourceLocation shearedSheepTextures = new ResourceLocation("textures/entity/sheep/sheep.png");

	public RenderSheep(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, shadowSize);
		this.addLayer(new LayerSheepWool(this));
	}

	protected ResourceLocation getEntityTexture(EntitySheep entity) {
		return shearedSheepTextures;
	}

}
