package com.murengezi.minecraft.client.renderer.entity.layers;

import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.model.ModelSlime;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;

public class LayerSlimeGel implements LayerRenderer<EntitySlime> {

	private final RenderSlime slimeRenderer;
	private final ModelBase slimeModel = new ModelSlime(0);

	public LayerSlimeGel(RenderSlime slimeRendererIn) {
		this.slimeRenderer = slimeRendererIn;
	}

	public void doRenderLayer(EntitySlime entityLivingBase, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (!entityLivingBase.isInvisible()) {
			GlStateManager.colorAllMax();
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
			this.slimeModel.render(entityLivingBase, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
	}

	public boolean shouldCombineTextures() {
		return true;
	}

}
