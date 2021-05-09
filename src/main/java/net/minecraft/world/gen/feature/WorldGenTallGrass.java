package net.minecraft.world.gen.feature;

import java.util.Random;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.BlockTallGrass;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenTallGrass extends WorldGenerator
{
    private final IBlockState tallGrassState;

    public WorldGenTallGrass(BlockTallGrass.EnumType p_i45629_1_)
    {
        this.tallGrassState = Blocks.tallgrass.getDefaultState().withProperty(BlockTallGrass.TYPE, p_i45629_1_);
    }

    public boolean generate(World world, Random rand, BlockPos position)
    {
        Block block;

        while (((block = world.getBlockState(position).getBlock()).getMaterial() == Material.air || block.getMaterial() == Material.leaves) && position.getY() > 0)
        {
            position = position.down();
        }

        for (int i = 0; i < 128; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (world.isAirBlock(blockpos) && Blocks.tallgrass.canBlockStay(world, blockpos, this.tallGrassState))
            {
                world.setBlockState(blockpos, this.tallGrassState, 2);
            }
        }

        return true;
    }
}
