package net.minecraft.world.gen.feature;

import java.util.Random;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenLiquids extends WorldGenerator
{
    private final Block block;

    public WorldGenLiquids(Block p_i45465_1_)
    {
        this.block = p_i45465_1_;
    }

    public boolean generate(World world, Random rand, BlockPos position)
    {
        if (world.getBlockState(position.up()).getBlock() != Blocks.stone)
        {
            return false;
        }
        else if (world.getBlockState(position.down()).getBlock() != Blocks.stone)
        {
            return false;
        }
        else if (world.getBlockState(position).getBlock().getMaterial() != Material.air && world.getBlockState(position).getBlock() != Blocks.stone)
        {
            return false;
        }
        else
        {
            int i = 0;

            if (world.getBlockState(position.west()).getBlock() == Blocks.stone)
            {
                ++i;
            }

            if (world.getBlockState(position.east()).getBlock() == Blocks.stone)
            {
                ++i;
            }

            if (world.getBlockState(position.north()).getBlock() == Blocks.stone)
            {
                ++i;
            }

            if (world.getBlockState(position.south()).getBlock() == Blocks.stone)
            {
                ++i;
            }

            int j = 0;

            if (world.isAirBlock(position.west()))
            {
                ++j;
            }

            if (world.isAirBlock(position.east()))
            {
                ++j;
            }

            if (world.isAirBlock(position.north()))
            {
                ++j;
            }

            if (world.isAirBlock(position.south()))
            {
                ++j;
            }

            if (i == 3 && j == 1)
            {
                world.setBlockState(position, this.block.getDefaultState(), 2);
                world.forceBlockUpdateTick(this.block, position, rand);
            }

            return true;
        }
    }
}
