package com.murengezi.minecraft.client.particle;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.Tessellator;
import com.murengezi.minecraft.client.renderer.WorldRenderer;
import com.murengezi.minecraft.client.renderer.texture.TextureManager;
import com.murengezi.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFootStepFX extends EntityFX {

    private static final ResourceLocation FOOTPRINT_TEXTURE = new ResourceLocation("textures/particle/footprint.png");
    private int footstepAge;
    private final int footstepMaxAge;
    private final TextureManager currentFootSteps;

    protected EntityFootStepFX(TextureManager currentFootStepsIn, World world, double xCoord, double yCoord, double zCoord) {
        super(world, xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
        this.currentFootSteps = currentFootStepsIn;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.footstepMaxAge = 200;
    }

    public void renderParticle(WorldRenderer worldRenderer, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
        float f = ((float)this.footstepAge + partialTicks) / (float)this.footstepMaxAge;
        f = f * f;
        float f1 = 2.0F - f * 2.0F;

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        f1 = f1 * 0.2F;
        GlStateManager.disableLighting();
        float f2 = 0.125F;
        float f3 = (float)(this.posX - interpPosX);
        float f4 = (float)(this.posY - interpPosY);
        float f5 = (float)(this.posZ - interpPosZ);
        float f6 = this.worldObj.getLightBrightness(new BlockPos(this));
        this.currentFootSteps.bindTexture(FOOTPRINT_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(f3 - 0.125F, f4, f5 + 0.125F).tex(0.0D, 1.0D).color(f6, f6, f6, f1).endVertex();
        worldRenderer.pos(f3 + 0.125F, f4, f5 + 0.125F).tex(1.0D, 1.0D).color(f6, f6, f6, f1).endVertex();
        worldRenderer.pos(f3 + 0.125F, f4, f5 - 0.125F).tex(1.0D, 0.0D).color(f6, f6, f6, f1).endVertex();
        worldRenderer.pos(f3 - 0.125F, f4, f5 - 0.125F).tex(0.0D, 0.0D).color(f6, f6, f6, f1).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLightning();
    }

    public void onUpdate() {
        ++this.footstepAge;

        if (this.footstepAge == this.footstepMaxAge) {
            this.setDead();
        }
    }

    public int getFXLayer() {
        return 3;
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_) {
            return new EntityFootStepFX(Minecraft.getMinecraft().getTextureManager(), world, xCoord, yCoord, zCoord);
        }
    }

}
