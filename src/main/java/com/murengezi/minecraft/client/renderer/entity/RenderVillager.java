package com.murengezi.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelVillager;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderVillager extends RenderLiving<EntityVillager> {
    
	private static final ResourceLocation villagerTextures = new ResourceLocation("textures/entity/villager/villager.png"), farmerVillagerTextures = new ResourceLocation("textures/entity/villager/farmer.png"), librarianVillagerTextures = new ResourceLocation("textures/entity/villager/librarian.png"), priestVillagerTextures = new ResourceLocation("textures/entity/villager/priest.png"), smithVillagerTextures = new ResourceLocation("textures/entity/villager/smith.png"), butcherVillagerTextures = new ResourceLocation("textures/entity/villager/butcher.png");

	public RenderVillager(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelVillager(0.0F), 0.5F);
		this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
	}

	public ModelVillager getMainModel() {
		return (ModelVillager) super.getMainModel();
	}

	protected ResourceLocation getEntityTexture(EntityVillager entity) {
		switch (entity.getProfession()) {
			case 0:
				return farmerVillagerTextures;
			case 1:
				return librarianVillagerTextures;
            case 2:
				return priestVillagerTextures;
			case 3:
				return smithVillagerTextures;
			case 4:
				return butcherVillagerTextures;
			default:
				return villagerTextures;
		}
	}

	protected void preRenderCallback(EntityVillager villager, float partialTickTime) {
		float f = 0.9375F;

		if (villager.getGrowingAge() < 0) {
			f = (float) ((double) f * 0.5D);
			this.shadowSize = 0.25F;
		} else {
			this.shadowSize = 0.5F;
		}

		GlStateManager.scale(f, f, f);
	}

}
