package com.murengezi.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityCritFX extends EntitySmokeFX {

    protected EntityCritFX(World world, double xCoord, double yCoord, double zCoord, double p_i1201_8_, double p_i1201_10_, double p_i1201_12_) {
        super(world, xCoord, yCoord, zCoord, p_i1201_8_, p_i1201_10_, p_i1201_12_, 2.5F);
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_) {
            return new EntityCritFX(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        }
    }

}
