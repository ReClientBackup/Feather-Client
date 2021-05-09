package net.minecraft.client.particle;

import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class EntityBlockDustFX extends EntityDiggingFX
{
    protected EntityBlockDustFX(World world, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state)
    {
        super(world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
    }

    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World world, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            IBlockState iblockstate = Block.getStateById(p_178902_15_[0]);
            return iblockstate.getBlock().getRenderType() == -1 ? null : (new EntityBlockDustFX(world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, iblockstate)).func_174845_l();
        }
    }
}
