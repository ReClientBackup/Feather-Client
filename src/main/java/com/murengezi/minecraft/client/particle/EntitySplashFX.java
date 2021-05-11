package com.murengezi.minecraft.client.particle;

import net.minecraft.world.World;

public class EntitySplashFX extends EntityRainFX {

    protected EntitySplashFX(World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
        super(world, xCoord, yCoord, zCoord);
        this.particleGravity = 0.04F;
        this.nextTextureIndexX();

        if (ySpeed == 0.0D && (xSpeed != 0.0D || zSpeed != 0.0D)) {
            this.motionX = xSpeed;
            this.motionY = ySpeed + 0.1D;
            this.motionZ = zSpeed;
        }
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_) {
            return new EntitySplashFX(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        }
    }

}
