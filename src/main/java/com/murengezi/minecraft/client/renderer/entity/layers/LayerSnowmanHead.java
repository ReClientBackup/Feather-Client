package com.murengezi.minecraft.client.renderer.entity.layers;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.block.model.ItemCameraTransforms;
import com.murengezi.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.entity.monster.EntitySnowman;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class LayerSnowmanHead implements LayerRenderer<EntitySnowman> {

    private final RenderSnowMan snowManRenderer;

	public LayerSnowmanHead(RenderSnowMan snowManRendererIn) {
		this.snowManRenderer = snowManRendererIn;
	}

	public void doRenderLayer(EntitySnowman entityLivingBase, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (!entityLivingBase.isInvisible()) {
			GlStateManager.pushMatrix();
			this.snowManRenderer.getMainModel().head.postRender(0.0625F);
			float f = 0.625F;
			GlStateManager.translate(0.0F, -0.34375F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(f, -f, -f);
			Minecraft.getMinecraft().getItemRenderer().renderItem(entityLivingBase, new ItemStack(Blocks.pumpkin, 1), ItemCameraTransforms.TransformType.HEAD);
			GlStateManager.popMatrix();
		}
	}

	public boolean shouldCombineTextures() {
		return true;
	}

}
