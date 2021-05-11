package net.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;

public class RenderMagmaCube extends RenderLiving<EntityMagmaCube> {

	private static final ResourceLocation magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");

	public RenderMagmaCube(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelMagmaCube(), 0.25F);
	}

	protected ResourceLocation getEntityTexture(EntityMagmaCube entity) {
		return magmaCubeTextures;
	}

	protected void preRenderCallback(EntityMagmaCube magmaCube, float partialTickTime) {
		int i = magmaCube.getSlimeSize();
		float f = (magmaCube.prevSquishFactor + (magmaCube.squishFactor - magmaCube.prevSquishFactor) * partialTickTime) / ((float) i * 0.5F + 1.0F);
		float f1 = 1.0F / (f + 1.0F);
		float f2 = (float) i;
		GlStateManager.scale(f1 * f2, 1.0F / f1 * f2, f1 * f2);
	}

}
