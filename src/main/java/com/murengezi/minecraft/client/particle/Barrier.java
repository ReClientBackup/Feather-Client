package com.murengezi.minecraft.client.particle;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Barrier extends EntityFX {

    protected Barrier(World world, double p_i46286_2_, double p_i46286_4_, double p_i46286_6_, Item item) {
        super(world, p_i46286_2_, p_i46286_4_, p_i46286_6_, 0.0D, 0.0D, 0.0D);
        this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item));
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.particleGravity = 0.0F;
        this.particleMaxAge = 80;
    }

    public int getFXLayer() {
        return 1;
    }

    public void renderParticle(WorldRenderer worldRenderer, Entity entity, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
        float f = this.particleIcon.getMinU();
        float f1 = this.particleIcon.getMaxU();
        float f2 = this.particleIcon.getMinV();
        float f3 = this.particleIcon.getMaxV();
        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        worldRenderer.pos(f5 - p_180434_4_ * 0.5F - p_180434_7_ * 0.5F, f6 - p_180434_5_ * 0.5F, f7 - p_180434_6_ * 0.5F - p_180434_8_ * 0.5F).tex(f1, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
        worldRenderer.pos(f5 - p_180434_4_ * 0.5F + p_180434_7_ * 0.5F, f6 + p_180434_5_ * 0.5F, f7 - p_180434_6_ * 0.5F + p_180434_8_ * 0.5F).tex(f1, f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
        worldRenderer.pos(f5 + p_180434_4_ * 0.5F + p_180434_7_ * 0.5F, f6 + p_180434_5_ * 0.5F, f7 + p_180434_6_ * 0.5F + p_180434_8_ * 0.5F).tex(f, f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
        worldRenderer.pos(f5 + p_180434_4_ * 0.5F - p_180434_7_ * 0.5F, f6 - p_180434_5_ * 0.5F, f7 + p_180434_6_ * 0.5F - p_180434_8_ * 0.5F).tex(f, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_) {
            return new Barrier(world, xCoord, yCoord, zCoord, Item.getItemFromBlock(Blocks.barrier));
        }
    }

}
