package net.minecraft.world.gen.feature;

import java.util.Random;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenClay extends WorldGenerator
{
    private final Block field_150546_a = Blocks.clay;

    /** The number of blocks to generate. */
    private final int numberOfBlocks;

    public WorldGenClay(int p_i2011_1_)
    {
        this.numberOfBlocks = p_i2011_1_;
    }

    public boolean generate(World world, Random rand, BlockPos position)
    {
        if (world.getBlockState(position).getBlock().getMaterial() != Material.water)
        {
            return false;
        }
        else
        {
            int i = rand.nextInt(this.numberOfBlocks - 2) + 2;
            int j = 1;

            for (int k = position.getX() - i; k <= position.getX() + i; ++k)
            {
                for (int l = position.getZ() - i; l <= position.getZ() + i; ++l)
                {
                    int i1 = k - position.getX();
                    int j1 = l - position.getZ();

                    if (i1 * i1 + j1 * j1 <= i * i)
                    {
                        for (int k1 = position.getY() - j; k1 <= position.getY() + j; ++k1)
                        {
                            BlockPos blockpos = new BlockPos(k, k1, l);
                            Block block = world.getBlockState(blockpos).getBlock();

                            if (block == Blocks.dirt || block == Blocks.clay)
                            {
                                world.setBlockState(blockpos, this.field_150546_a.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
