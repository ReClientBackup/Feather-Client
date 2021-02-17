package com.murengezi.chocolate.Util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

import static net.minecraft.client.renderer.OpenGlHelper.defaultTexUnit;
import static net.minecraft.client.renderer.OpenGlHelper.lightmapTexUnit;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-13 at 22:03
 */
public class EntityUtils extends MinecraftUtils {

    public static void drawEntityOnScreen(EntityLivingBase entityLivingBase, int posX, int posY, float scale, float mouseX, float mouseY) {
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.colorAllMax();
        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float renderYawOffset = entityLivingBase.renderYawOffset;
        float rotationYaw = entityLivingBase.rotationYaw;
        float rotationPitch = entityLivingBase.rotationPitch;
        float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        entityLivingBase.renderYawOffset = (float)Math.atan((mouseX / 40.0F)) * 20.0F;
        entityLivingBase.rotationYaw = (float)Math.atan((mouseX / 40.0F)) * 40.0F;
        entityLivingBase.rotationPitch = -((float)Math.atan((mouseY / 40.0F))) * 20.0F;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        try {
            RenderManager rendermanager = getMc().getRenderManager();
            rendermanager.setPlayerViewY(180.0F);
            rendermanager.setRenderShadow(false);
            rendermanager.renderEntityWithPosYaw(entityLivingBase, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
            rendermanager.setRenderShadow(true);
        } finally {
            entityLivingBase.renderYawOffset = renderYawOffset;
            entityLivingBase.rotationYaw = rotationYaw;
            entityLivingBase.rotationPitch = rotationPitch;
            entityLivingBase.prevRotationYawHead = prevRotationYawHead;
            entityLivingBase.rotationYawHead = rotationYawHead;
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.setActiveTexture(lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(defaultTexUnit);
            GlStateManager.translate(0.0F, 0.0F, 20.0F);
        }
    }

    public static float getEntityScale(EntityLivingBase ent, float baseScale, float targetHeight) {
        return targetHeight / Math.max(ent.width, ent.height) * baseScale;
    }

    public static float getModelSize() {
        return 1.8F;
    }
}
