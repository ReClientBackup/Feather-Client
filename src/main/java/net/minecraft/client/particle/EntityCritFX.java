package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityCritFX extends EntitySmokeFX
{
    protected EntityCritFX(World world, double xCoordIn, double yCoordIn, double zCoordIn, double p_i1201_8_, double p_i1201_10_, double p_i1201_12_)
    {
        super(world, xCoordIn, yCoordIn, zCoordIn, p_i1201_8_, p_i1201_10_, p_i1201_12_, 2.5F);
    }

    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World world, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new EntityCritFX(world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
