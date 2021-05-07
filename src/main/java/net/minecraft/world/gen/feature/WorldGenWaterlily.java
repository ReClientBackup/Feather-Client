package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenWaterlily extends WorldGenerator
{
    public boolean generate(World world, Random rand, BlockPos position)
    {
        for (int i = 0; i < 10; ++i)
        {
            int j = position.getX() + rand.nextInt(8) - rand.nextInt(8);
            int k = position.getY() + rand.nextInt(4) - rand.nextInt(4);
            int l = position.getZ() + rand.nextInt(8) - rand.nextInt(8);

            if (world.isAirBlock(new BlockPos(j, k, l)) && Blocks.waterlily.canPlaceBlockAt(world, new BlockPos(j, k, l)))
            {
                world.setBlockState(new BlockPos(j, k, l), Blocks.waterlily.getDefaultState(), 2);
            }
        }

        return true;
    }
}
