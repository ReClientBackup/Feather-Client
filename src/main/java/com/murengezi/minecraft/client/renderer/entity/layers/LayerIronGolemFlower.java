package com.murengezi.minecraft.client.renderer.entity.layers;

import com.murengezi.minecraft.client.model.ModelIronGolem;
import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.renderer.BlockRendererDispatcher;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.OpenGlHelper;
import com.murengezi.minecraft.client.renderer.entity.RenderIronGolem;
import com.murengezi.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Blocks;

public class LayerIronGolemFlower implements LayerRenderer<EntityIronGolem> {

	private final RenderIronGolem ironGolemRenderer;

	public LayerIronGolemFlower(RenderIronGolem ironGolemRenderer) {
		this.ironGolemRenderer = ironGolemRenderer;
	}

	public void doRenderLayer(EntityIronGolem entityLivingBase, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (entityLivingBase.getHoldRoseTick() != 0) {
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.rotate(5.0F + 180.0F * ((ModelIronGolem) this.ironGolemRenderer.getMainModel()).ironGolemRightArm.rotateAngleX / (float) Math.PI, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(-0.9375F, -0.625F, -0.9375F);
			float f = 0.5F;
			GlStateManager.scale(f, -f, f);
			int i = entityLivingBase.getBrightnessForRender(partialTicks);
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
			GlStateManager.colorAllMax();
			this.ironGolemRenderer.bindTexture(TextureMap.locationBlocksTexture);
			blockrendererdispatcher.renderBlockBrightness(Blocks.red_flower.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	public boolean shouldCombineTextures() {
		return false;
	}

}
