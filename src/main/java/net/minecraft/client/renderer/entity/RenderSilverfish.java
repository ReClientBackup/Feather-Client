package net.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelSilverfish;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderSilverfish extends RenderLiving<EntitySilverfish> {

	private static final ResourceLocation silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");

	public RenderSilverfish(RenderManager renderManager) {
		super(renderManager, new ModelSilverfish(), 0.3F);
	}

	protected float getDeathMaxRotation(EntitySilverfish entityLivingBase) {
		return 180.0F;
	}

	protected ResourceLocation getEntityTexture(EntitySilverfish entity) {
		return silverfishTextures;
	}

}
