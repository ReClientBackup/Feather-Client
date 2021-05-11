package com.murengezi.minecraft.client.renderer.entity.layers;

import com.murengezi.minecraft.client.entity.AbstractClientPlayer;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.entity.RenderPlayer;

public class LayerDeadmau5Head implements LayerRenderer<AbstractClientPlayer> {

	private final RenderPlayer playerRenderer;

	public LayerDeadmau5Head(RenderPlayer playerRenderer) {
		this.playerRenderer = playerRenderer;
	}

	public void doRenderLayer(AbstractClientPlayer entityLivingBase, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (entityLivingBase.getCommandSenderName().equals("deadmau5") && entityLivingBase.hasSkin() && !entityLivingBase.isInvisible()) {
			this.playerRenderer.bindTexture(entityLivingBase.getLocationSkin());

			for (int i = 0; i < 2; ++i) {
				float f = entityLivingBase.prevRotationYaw + (entityLivingBase.rotationYaw - entityLivingBase.prevRotationYaw) * partialTicks - (entityLivingBase.prevRenderYawOffset + (entityLivingBase.renderYawOffset - entityLivingBase.prevRenderYawOffset) * partialTicks);
				float f1 = entityLivingBase.prevRotationPitch + (entityLivingBase.rotationPitch - entityLivingBase.prevRotationPitch) * partialTicks;
				GlStateManager.pushMatrix();
				GlStateManager.rotate(f, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(f1, 1.0F, 0.0F, 0.0F);
				GlStateManager.translate(0.375F * (float) (i * 2 - 1), 0.0F, 0.0F);
				GlStateManager.translate(0.0F, -0.375F, 0.0F);
				GlStateManager.rotate(-f1, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-f, 0.0F, 1.0F, 0.0F);
				float f2 = 1.3333334F;
				GlStateManager.scale(f2, f2, f2);
				this.playerRenderer.getMainModel().renderDeadmau5Head(0.0625F);
				GlStateManager.popMatrix();
			}
		}
	}

	public boolean shouldCombineTextures() {
		return true;
	}

}
