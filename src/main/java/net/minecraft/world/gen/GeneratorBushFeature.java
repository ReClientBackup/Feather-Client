package net.minecraft.world.gen;

import java.util.Random;
import com.murengezi.minecraft.block.BlockBush;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class GeneratorBushFeature extends WorldGenerator
{
    private final BlockBush field_175908_a;

    public GeneratorBushFeature(BlockBush p_i45633_1_)
    {
        this.field_175908_a = p_i45633_1_;
    }

    public boolean generate(World world, Random rand, BlockPos position)
    {
        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (world.isAirBlock(blockpos) && (!world.provider.getHasNoSky() || blockpos.getY() < 255) && this.field_175908_a.canBlockStay(world, blockpos, this.field_175908_a.getDefaultState()))
            {
                world.setBlockState(blockpos, this.field_175908_a.getDefaultState(), 2);
            }
        }

        return true;
    }
}
