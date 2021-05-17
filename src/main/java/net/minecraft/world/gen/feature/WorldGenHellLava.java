package net.minecraft.world.gen.feature;

import java.util.Random;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator
{
    private final Block field_150553_a;
    private final boolean field_94524_b;

    public WorldGenHellLava(Block p_i45453_1_, boolean p_i45453_2_)
    {
        this.field_150553_a = p_i45453_1_;
        this.field_94524_b = p_i45453_2_;
    }

    public boolean generate(World world, Random rand, BlockPos position)
    {
        if (world.getBlockState(position.up()).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else if (world.getBlockState(position).getBlock().getMaterial() != Material.air && world.getBlockState(position).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else
        {
            int i = 0;

            if (world.getBlockState(position.west()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (world.getBlockState(position.east()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (world.getBlockState(position.north()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (world.getBlockState(position.south()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (world.getBlockState(position.down()).getBlock() == Blocks.netherrack)
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

            if (world.isAirBlock(position.down()))
            {
                ++j;
            }

            if (!this.field_94524_b && i == 4 && j == 1 || i == 5)
            {
                world.setBlockState(position, this.field_150553_a.getDefaultState(), 2);
                world.forceBlockUpdateTick(this.field_150553_a, position, rand);
            }

            return true;
        }
    }
}
