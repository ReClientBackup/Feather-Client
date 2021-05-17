package net.minecraft.world.gen.feature;

import java.util.Random;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenCactus extends WorldGenerator
{
    public boolean generate(World world, Random rand, BlockPos position)
    {
        for (int i = 0; i < 10; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (world.isAirBlock(blockpos))
            {
                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    if (Blocks.cactus.canBlockStay(world, blockpos))
                    {
                        world.setBlockState(blockpos.up(k), Blocks.cactus.getDefaultState(), 2);
                    }
                }
            }
        }

        return true;
    }
}
