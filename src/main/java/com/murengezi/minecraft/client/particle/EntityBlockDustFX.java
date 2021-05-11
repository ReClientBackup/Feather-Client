package com.murengezi.minecraft.client.particle;

import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class EntityBlockDustFX extends EntityDiggingFX {

    protected EntityBlockDustFX(World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, IBlockState state) {
        super(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, state);
        this.motionX = xSpeed;
        this.motionY = ySpeed;
        this.motionZ = zSpeed;
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_) {
            IBlockState iblockstate = Block.getStateById(p_178902_15_[0]);
            return iblockstate.getBlock().getRenderType() == -1 ? null : (new EntityBlockDustFX(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, iblockstate)).func_174845_l();
        }
    }

}
