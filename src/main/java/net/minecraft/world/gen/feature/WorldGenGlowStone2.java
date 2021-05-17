package net.minecraft.world.gen.feature;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenGlowStone2 extends WorldGenerator
{
    public boolean generate(World world, Random rand, BlockPos position)
    {
        if (!world.isAirBlock(position))
        {
            return false;
        }
        else if (world.getBlockState(position.up()).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else
        {
            world.setBlockState(position, Blocks.glowstone.getDefaultState(), 2);

            for (int i = 0; i < 1500; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), -rand.nextInt(12), rand.nextInt(8) - rand.nextInt(8));

                if (world.getBlockState(blockpos).getBlock().getMaterial() == Material.air)
                {
                    int j = 0;

                    for (EnumFacing enumfacing : EnumFacing.values())
                    {
                        if (world.getBlockState(blockpos.offset(enumfacing)).getBlock() == Blocks.glowstone)
                        {
                            ++j;
                        }

                        if (j > 1)
                        {
                            break;
                        }
                    }

                    if (j == 1)
                    {
                        world.setBlockState(blockpos, Blocks.glowstone.getDefaultState(), 2);
                    }
                }
            }

            return true;
        }
    }
}
