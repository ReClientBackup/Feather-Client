package com.murengezi.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityEnchantmentTableParticleFX extends EntityFX {

    private final double coordX, coordY, coordZ;

    protected EntityEnchantmentTableParticleFX(World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
        super(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        this.motionX = xSpeed;
        this.motionY = ySpeed;
        this.motionZ = zSpeed;
        this.coordX = xCoord;
        this.coordY = yCoord;
        this.coordZ = zCoord;
        this.posX = this.prevPosX = xCoord + xSpeed;
        this.posY = this.prevPosY = yCoord + ySpeed;
        this.posZ = this.prevPosZ = zCoord + zSpeed;
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
        this.particleGreen *= 0.9F;
        this.particleRed *= 0.9F;
        this.particleMaxAge = (int)(Math.random() * 10.0D) + 30;
        this.noClip = true;
        this.setParticleTextureIndex((int)(Math.random() * 26.0D + 1.0D + 224.0D));
    }

    public int getBrightnessForRender(float partialTicks) {
        int i = super.getBrightnessForRender(partialTicks);
        float f = (float)this.particleAge / (float)this.particleMaxAge;
        f = f * f;
        f = f * f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k = k + (int)(f * 15.0F * 16.0F);

        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }

    public float getBrightness(float partialTicks) {
        float f = super.getBrightness(partialTicks);
        float f1 = (float)this.particleAge / (float)this.particleMaxAge;
        f1 = f1 * f1;
        f1 = f1 * f1;
        return f * (1.0F - f1) + f1;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        float f = (float)this.particleAge / (float)this.particleMaxAge;
        f = 1.0F - f;
        float f1 = 1.0F - f;
        f1 = f1 * f1;
        f1 = f1 * f1;
        this.posX = this.coordX + this.motionX * (double)f;
        this.posY = this.coordY + this.motionY * (double)f - (double)(f1 * 1.2F);
        this.posZ = this.coordZ + this.motionZ * (double)f;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }

    public static class EnchantmentTable implements IParticleFactory {
        public EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_) {
            return new EntityEnchantmentTableParticleFX(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        }
    }

}
