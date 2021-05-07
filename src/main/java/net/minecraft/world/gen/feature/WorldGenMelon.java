package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenMelon extends WorldGenerator
{
    public boolean generate(World world, Random rand, BlockPos position)
    {
        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (Blocks.melon_block.canPlaceBlockAt(world, blockpos) && world.getBlockState(blockpos.down()).getBlock() == Blocks.grass)
            {
                world.setBlockState(blockpos, Blocks.melon_block.getDefaultState(), 2);
            }
        }

        return true;
    }
}
