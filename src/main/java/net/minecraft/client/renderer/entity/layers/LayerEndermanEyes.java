package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.Config;
import net.optifine.shaders.Shaders;

public class LayerEndermanEyes implements LayerRenderer<EntityEnderman> {

	private static final ResourceLocation field_177203_a = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	private final RenderEnderman endermanRenderer;

	public LayerEndermanEyes(RenderEnderman endermanRenderer) {
		this.endermanRenderer = endermanRenderer;
	}

	public void doRenderLayer(EntityEnderman entityLivingBase, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		this.endermanRenderer.bindTexture(field_177203_a);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.blendFunc(1, 1);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(!entityLivingBase.isInvisible());
		int i = 61680;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
		GlStateManager.enableLightning();
		GlStateManager.colorAllMax();

		if (Config.isShaders()) {
			Shaders.beginSpiderEyes();
		}

		Config.getRenderGlobal().renderOverlayEyes = true;
		this.endermanRenderer.getMainModel().render(entityLivingBase, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
		Config.getRenderGlobal().renderOverlayEyes = false;

		if (Config.isShaders()) {
			Shaders.endSpiderEyes();
		}

		this.endermanRenderer.func_177105_a(entityLivingBase, partialTicks);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
	}

	public boolean shouldCombineTextures() {
		return false;
	}

}
