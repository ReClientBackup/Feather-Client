package net.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerMooshroomMushroom;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.util.ResourceLocation;

public class RenderMooshroom extends RenderLiving<EntityMooshroom> {

	private static final ResourceLocation mooshroomTextures = new ResourceLocation("textures/entity/cow/mooshroom.png");

	public RenderMooshroom(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, shadowSize);
		this.addLayer(new LayerMooshroomMushroom(this));
	}

	protected ResourceLocation getEntityTexture(EntityMooshroom entity) {
		return mooshroomTextures;
	}

}
