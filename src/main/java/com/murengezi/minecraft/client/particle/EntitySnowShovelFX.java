package com.murengezi.minecraft.client.particle;

import com.murengezi.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowShovelFX extends EntityFX {

    float snowDigParticleScale;

    protected EntitySnowShovelFX(World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
        this(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, 1.0F);
    }

    protected EntitySnowShovelFX(World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, float p_i1228_14_) {
        super(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.motionX += xSpeed;
        this.motionY += ySpeed;
        this.motionZ += zSpeed;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F - (float)(Math.random() * 0.30000001192092896D);
        this.particleScale *= 0.75F;
        this.particleScale *= p_i1228_14_;
        this.snowDigParticleScale = this.particleScale;
        this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int)((float)this.particleMaxAge * p_i1228_14_);
        this.noClip = false;
    }

    public void renderParticle(WorldRenderer worldRenderer, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
        float f = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge * 32.0F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        this.particleScale = this.snowDigParticleScale * f;
        super.renderParticle(worldRenderer, entityIn, partialTicks, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.motionY -= 0.03D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9900000095367432D;
        this.motionY *= 0.9900000095367432D;
        this.motionZ *= 0.9900000095367432D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_) {
            return new EntitySnowShovelFX(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        }
    }

}
