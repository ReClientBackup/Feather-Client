package com.murengezi.minecraft.client.renderer.entity.layers;

import com.murengezi.minecraft.client.model.ModelBox;
import com.murengezi.minecraft.client.model.ModelRenderer;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.RenderHelper;
import com.murengezi.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class LayerArrow implements LayerRenderer<EntityLivingBase> {
	private final RendererLivingEntity field_177168_a;

	public LayerArrow(RendererLivingEntity p_i46124_1_) {
		this.field_177168_a = p_i46124_1_;
	}

	public void doRenderLayer(EntityLivingBase entityLivingBase, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		int i = entityLivingBase.getArrowCountInEntity();

		if (i > 0) {
			Entity entity = new EntityArrow(entityLivingBase.worldObj, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ);
			Random random = new Random(entityLivingBase.getEntityId());
			RenderHelper.disableStandardItemLighting();

			for (int j = 0; j < i; ++j) {
				GlStateManager.pushMatrix();
				ModelRenderer modelrenderer = this.field_177168_a.getMainModel().getRandomModelBox(random);
				ModelBox modelbox = modelrenderer.cubeList.get(random.nextInt(modelrenderer.cubeList.size()));
				modelrenderer.postRender(0.0625F);
				float f = random.nextFloat();
				float f1 = random.nextFloat();
				float f2 = random.nextFloat();
				float f3 = (modelbox.posX1 + (modelbox.posX2 - modelbox.posX1) * f) / 16.0F;
				float f4 = (modelbox.posY1 + (modelbox.posY2 - modelbox.posY1) * f1) / 16.0F;
				float f5 = (modelbox.posZ1 + (modelbox.posZ2 - modelbox.posZ1) * f2) / 16.0F;
				GlStateManager.translate(f3, f4, f5);
				f = f * 2.0F - 1.0F;
				f1 = f1 * 2.0F - 1.0F;
				f2 = f2 * 2.0F - 1.0F;
				f = f * -1.0F;
				f1 = f1 * -1.0F;
				f2 = f2 * -1.0F;
				float f6 = MathHelper.sqrt_float(f * f + f2 * f2);
				entity.prevRotationYaw = entity.rotationYaw = (float) (Math.atan2(f, f2) * 180.0D / Math.PI);
				entity.prevRotationPitch = entity.rotationPitch = (float) (Math.atan2(f1, f6) * 180.0D / Math.PI);
				double d0 = 0.0D;
				double d1 = 0.0D;
				double d2 = 0.0D;
				this.field_177168_a.getRenderManager().renderEntityWithPosYaw(entity, d0, d1, d2, 0.0F, partialTicks);
				GlStateManager.popMatrix();
			}

			RenderHelper.enableStandardItemLighting();
		}
	}

	public boolean shouldCombineTextures() {
		return false;
	}
}
