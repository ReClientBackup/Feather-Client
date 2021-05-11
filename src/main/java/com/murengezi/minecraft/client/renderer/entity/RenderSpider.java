package com.murengezi.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelSpider;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;

public class RenderSpider<T extends EntitySpider> extends RenderLiving<T> {

	private static final ResourceLocation spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");

	public RenderSpider(RenderManager renderManager) {
		super(renderManager, new ModelSpider(), 1.0F);
		this.addLayer(new LayerSpiderEyes(this));
	}

	protected float getDeathMaxRotation(T entityLivingBaseIn) {
		return 180.0F;
	}

	protected ResourceLocation getEntityTexture(T entity) {
		return spiderTextures;
	}

}
