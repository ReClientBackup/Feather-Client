package com.murengezi.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.murengezi.minecraft.client.model.ModelHorse;
import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class RenderHorse extends RenderLiving<EntityHorse> {

	private static final Map<String, ResourceLocation> field_110852_a = Maps.newHashMap();
	private static final ResourceLocation whiteHorseTextures = new ResourceLocation("textures/entity/horse/horse_white.png"), muleTextures = new ResourceLocation("textures/entity/horse/mule.png"), donkeyTextures = new ResourceLocation("textures/entity/horse/donkey.png"), zombieHorseTextures = new ResourceLocation("textures/entity/horse/horse_zombie.png"), skeletonHorseTextures = new ResourceLocation("textures/entity/horse/horse_skeleton.png");

	public RenderHorse(RenderManager renderManager, ModelHorse model, float shadowSize) {
		super(renderManager, model, shadowSize);
	}

	protected void preRenderCallback(EntityHorse entityHorse, float partialTickTime) {
		float f = 1.0F;
		int i = entityHorse.getHorseType();

		if (i == 1) {
			f *= 0.87F;
		} else if (i == 2) {
			f *= 0.92F;
		}

		GlStateManager.scale(f, f, f);
		super.preRenderCallback(entityHorse, partialTickTime);
	}

	protected ResourceLocation getEntityTexture(EntityHorse entity) {
		if (!entity.func_110239_cn()) {
			switch (entity.getHorseType()) {
				case 0:
				default:
					return whiteHorseTextures;
				case 1:
					return donkeyTextures;
				case 2:
					return muleTextures;
				case 3:
					return zombieHorseTextures;
				case 4:
					return skeletonHorseTextures;
			}
		} else {
			return this.func_110848_b(entity);
		}
	}

	private ResourceLocation func_110848_b(EntityHorse horse) {
		String s = horse.getHorseTexture();

		if (!horse.func_175507_cI()) {
			return null;
		} else {
			ResourceLocation resourcelocation = field_110852_a.get(s);

			if (resourcelocation == null) {
				resourcelocation = new ResourceLocation(s);
				Minecraft.getMinecraft().getTextureManager().loadTexture(resourcelocation, new LayeredTexture(horse.getVariantTexturePaths()));
				field_110852_a.put(s, resourcelocation);
			}

			return resourcelocation;
		}
	}

}
