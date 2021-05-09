package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockMushroom extends BlockBush implements IGrowable {

    protected BlockMushroom() {
        float f = 0.2F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setTickRandomly(true);
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (rand.nextInt(25) == 0) {
            int i = 5;

            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
                if (world.getBlockState(blockpos).getBlock() == this) {
                    --i;

                    if (i <= 0) {
                        return;
                    }
                }
            }

            BlockPos blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

            for (int k = 0; k < 4; ++k) {
                if (world.isAirBlock(blockpos1) && this.canBlockStay(world, blockpos1, this.getDefaultState())) {
                    pos = blockpos1;
                }

                blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
            }

            if (world.isAirBlock(blockpos1) && this.canBlockStay(world, blockpos1, this.getDefaultState())) {
                world.setBlockState(blockpos1, this.getDefaultState(), 2);
            }
        }
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && this.canBlockStay(world, pos, this.getDefaultState());
    }

    protected boolean canPlaceBlockOn(Block ground) {
        return ground.isFullBlock();
    }

    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        if (pos.getY() >= 0 && pos.getY() < 256) {
            IBlockState iblockstate = world.getBlockState(pos.down());
            return iblockstate.getBlock() == Blocks.mycelium || (iblockstate.getBlock() == Blocks.dirt && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL || world.getLight(pos) < 13 && this.canPlaceBlockOn(iblockstate.getBlock()));
        } else {
            return false;
        }
    }

    public void generateBigMushroom(World world, BlockPos pos, IBlockState state, Random rand) {
        world.setBlockToAir(pos);
        WorldGenerator worldgenerator = null;

        if (this == Blocks.brown_mushroom) {
            worldgenerator = new WorldGenBigMushroom(Blocks.brown_mushroom_block);
        } else if (this == Blocks.red_mushroom) {
            worldgenerator = new WorldGenBigMushroom(Blocks.red_mushroom_block);
        }

        if (worldgenerator != null && worldgenerator.generate(world, rand, pos)) {
        } else {
            world.setBlockState(pos, state, 3);
        }
    }

    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return (double)rand.nextFloat() < 0.4D;
    }

    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        this.generateBigMushroom(world, pos, state, rand);
    }

}
