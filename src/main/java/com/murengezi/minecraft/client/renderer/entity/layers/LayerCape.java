package com.murengezi.minecraft.client.renderer.entity.layers;

import com.murengezi.minecraft.client.entity.AbstractClientPlayer;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class LayerCape implements LayerRenderer<AbstractClientPlayer> {

	private final RenderPlayer playerRenderer;

	public LayerCape(RenderPlayer playerRendererIn) {
		this.playerRenderer = playerRendererIn;
	}

	public void doRenderLayer(AbstractClientPlayer entityLivingBase, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (entityLivingBase.hasPlayerInfo() && !entityLivingBase.isInvisible() && entityLivingBase.isWearing(EnumPlayerModelParts.CAPE) && entityLivingBase.getLocationCape() != null) {
			GlStateManager.colorAllMax();
			this.playerRenderer.bindTexture(entityLivingBase.getLocationCape());
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, 0.125F);
			double d0 = entityLivingBase.prevChasingPosX + (entityLivingBase.chasingPosX - entityLivingBase.prevChasingPosX) * (double) partialTicks - (entityLivingBase.prevPosX + (entityLivingBase.posX - entityLivingBase.prevPosX) * (double) partialTicks);
			double d1 = entityLivingBase.prevChasingPosY + (entityLivingBase.chasingPosY - entityLivingBase.prevChasingPosY) * (double) partialTicks - (entityLivingBase.prevPosY + (entityLivingBase.posY - entityLivingBase.prevPosY) * (double) partialTicks);
			double d2 = entityLivingBase.prevChasingPosZ + (entityLivingBase.chasingPosZ - entityLivingBase.prevChasingPosZ) * (double) partialTicks - (entityLivingBase.prevPosZ + (entityLivingBase.posZ - entityLivingBase.prevPosZ) * (double) partialTicks);
			float f = entityLivingBase.prevRenderYawOffset + (entityLivingBase.renderYawOffset - entityLivingBase.prevRenderYawOffset) * partialTicks;
			double d3 = MathHelper.sin(f * (float) Math.PI / 180.0F);
			double d4 = -MathHelper.cos(f * (float) Math.PI / 180.0F);
			float f1 = (float) d1 * 10.0F;
			f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
			float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
			float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

			if (f2 < 0.0F) {
				f2 = 0.0F;
			}

			if (f2 > 165.0F) {
				f2 = 165.0F;
			}

			if (f1 < -5.0F) {
				f1 = -5.0F;
			}

			float f4 = entityLivingBase.prevCameraYaw + (entityLivingBase.cameraYaw - entityLivingBase.prevCameraYaw) * partialTicks;
			f1 = f1 + MathHelper.sin((entityLivingBase.prevDistanceWalkedModified + (entityLivingBase.distanceWalkedModified - entityLivingBase.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

			if (entityLivingBase.isSneaking()) {
				f1 += 25.0F;
				GlStateManager.translate(0.0F, 0.142F, -0.0178F);
			}

			GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			this.playerRenderer.getMainModel().renderCape(0.0625F);
			GlStateManager.popMatrix();
		}
	}

	public boolean shouldCombineTextures() {
		return false;
	}

}
