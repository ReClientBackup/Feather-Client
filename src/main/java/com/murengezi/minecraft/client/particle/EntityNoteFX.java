package com.murengezi.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityNoteFX extends EntityFX {

    float noteParticleScale;

    protected EntityNoteFX(World world, double xCoord, double yCoord, double zCoord, double p_i46353_8_) {
        this(world, xCoord, yCoord, zCoord, p_i46353_8_, 2.0F);
    }

    protected EntityNoteFX(World world, double xCoord, double yCoord, double zCoord, double p_i1217_8_, float p_i1217_14_) {
        super(world, xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.009999999776482582D;
        this.motionY *= 0.009999999776482582D;
        this.motionZ *= 0.009999999776482582D;
        this.motionY += 0.2D;
        this.particleRed = MathHelper.sin(((float)p_i1217_8_ + 0.0F) * (float)Math.PI * 2.0F) * 0.65F + 0.35F;
        this.particleGreen = MathHelper.sin(((float)p_i1217_8_ + 0.33333334F) * (float)Math.PI * 2.0F) * 0.65F + 0.35F;
        this.particleBlue = MathHelper.sin(((float)p_i1217_8_ + 0.6666667F) * (float)Math.PI * 2.0F) * 0.65F + 0.35F;
        this.particleScale *= 0.75F;
        this.particleScale *= p_i1217_14_;
        this.noteParticleScale = this.particleScale;
        this.particleMaxAge = 6;
        this.noClip = false;
        this.setParticleTextureIndex(64);
    }

    public void renderParticle(WorldRenderer worldRenderer, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
        float f = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge * 32.0F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        this.particleScale = this.noteParticleScale * f;
        super.renderParticle(worldRenderer, entityIn, partialTicks, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.6600000262260437D;
        this.motionY *= 0.6600000262260437D;
        this.motionZ *= 0.6600000262260437D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_) {
            return new EntityNoteFX(world, xCoord, yCoord, zCoord, xSpeed);
        }
    }

}
