package com.murengezi.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelZombie;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPigZombie extends RenderBiped<EntityPigZombie> {

	private static final ResourceLocation ZOMBIE_PIGMAN_TEXTURE = new ResourceLocation("textures/entity/zombie_pigman.png");

	public RenderPigZombie(RenderManager renderManager) {
		super(renderManager, new ModelZombie(), 0.5F, 1.0F);
		this.addLayer(new LayerHeldItem(this));
		this.addLayer(new LayerBipedArmor(this) {
			protected void initArmor() {
				this.field_177189_c = new ModelZombie(0.5F, true);
				this.field_177186_d = new ModelZombie(1.0F, true);
			}
		});
	}

	protected ResourceLocation getEntityTexture(EntityPigZombie entity) {
		return ZOMBIE_PIGMAN_TEXTURE;
	}

}
