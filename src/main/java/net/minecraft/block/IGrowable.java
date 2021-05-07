package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IGrowable
{
    /**
     * Whether this IGrowable can grow
     */
    boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient);

    boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state);

    void grow(World world, Random rand, BlockPos pos, IBlockState state);
}
