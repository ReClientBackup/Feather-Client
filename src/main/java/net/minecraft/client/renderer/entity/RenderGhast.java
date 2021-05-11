package net.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelGhast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;

public class RenderGhast extends RenderLiving<EntityGhast> {

	private static final ResourceLocation ghastTextures = new ResourceLocation("textures/entity/ghast/ghast.png"), ghastShootingTextures = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

	public RenderGhast(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelGhast(), 0.5F);
	}

	protected ResourceLocation getEntityTexture(EntityGhast entity) {
		return entity.isAttacking() ? ghastShootingTextures : ghastTextures;
	}

	protected void preRenderCallback(EntityGhast entityGhast, float partialTickTime) {
		float f = 1.0F;
		float f1 = (8.0F + f) / 2.0F;
		float f2 = (8.0F + 1.0F / f) / 2.0F;
		GlStateManager.scale(f2, f1, f2);
		GlStateManager.colorAllMax();
	}

}
