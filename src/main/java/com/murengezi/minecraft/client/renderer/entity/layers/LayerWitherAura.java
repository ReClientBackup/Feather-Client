package com.murengezi.minecraft.client.renderer.entity.layers;

import com.murengezi.minecraft.client.model.ModelWither;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerWitherAura implements LayerRenderer<EntityWither> {

	private static final ResourceLocation WITHER_ARMOR = new ResourceLocation("textures/entity/wither/wither_armor.png");
	private final RenderWither witherRenderer;
	private final ModelWither witherModel = new ModelWither(0.5F);

	public LayerWitherAura(RenderWither witherRendererIn) {
		this.witherRenderer = witherRendererIn;
	}

	public void doRenderLayer(EntityWither entityLivingBase, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (entityLivingBase.isArmored()) {
			GlStateManager.depthMask(!entityLivingBase.isInvisible());
			this.witherRenderer.bindTexture(WITHER_ARMOR);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f = (float) entityLivingBase.ticksExisted + partialTicks;
			float f1 = MathHelper.cos(f * 0.02F) * 3.0F;
			float f2 = f * 0.01F;
			GlStateManager.translate(f1, f2, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float f3 = 0.5F;
			GlStateManager.color(f3, f3, f3, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(1, 1);
			this.witherModel.setLivingAnimations(entityLivingBase, p_177141_2_, p_177141_3_, partialTicks);
			this.witherModel.setModelAttributes(this.witherRenderer.getMainModel());
			this.witherModel.render(entityLivingBase, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLightning();
			GlStateManager.disableBlend();
		}
	}

	public boolean shouldCombineTextures() {
		return false;
	}

}
