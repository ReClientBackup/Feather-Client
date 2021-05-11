package com.murengezi.minecraft.client.particle;

import net.minecraft.world.World;

public interface IParticleFactory {

    EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_);

}
