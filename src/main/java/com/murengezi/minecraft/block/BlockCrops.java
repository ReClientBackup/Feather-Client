package com.murengezi.minecraft.block;

import java.util.Random;

import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.init.Blocks;
import com.murengezi.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable {

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    protected BlockCrops() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
        this.setTickRandomly(true);
        float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab(null);
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.disableStats();
    }

    protected boolean canPlaceBlockOn(Block ground) {
        return ground == Blocks.farmland;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);

        if (world.getLightFromNeighbors(pos.up()) >= 9) {
            int i = state.getValue(AGE);

            if (i < 7) {
                float f = getGrowthChance(this, world, pos);

                if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
                    world.setBlockState(pos, state.withProperty(AGE, i + 1), 2);
                }
            }
        }
    }

    public void grow(World world, BlockPos pos, IBlockState state) {
        int i = state.getValue(AGE) + MathHelper.getRandomIntegerInRange(world.rand, 2, 5);

        if (i > 7) {
            i = 7;
        }

        world.setBlockState(pos, state.withProperty(AGE, i), 2);
    }

    protected static float getGrowthChance(Block block, World world, BlockPos pos) {
        float f = 1.0F;
        BlockPos blockpos = pos.down();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                IBlockState iblockstate = world.getBlockState(blockpos.add(i, 0, j));

                if (iblockstate.getBlock() == Blocks.farmland) {
                    f1 = 1.0F;

                    if (iblockstate.getValue(BlockFarmland.MOISTURE) > 0) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = pos.north();
        BlockPos blockpos2 = pos.south();
        BlockPos blockpos3 = pos.west();
        BlockPos blockpos4 = pos.east();
        boolean flag = block == world.getBlockState(blockpos3).getBlock() || block == world.getBlockState(blockpos4).getBlock();
        boolean flag1 = block == world.getBlockState(blockpos1).getBlock() || block == world.getBlockState(blockpos2).getBlock();

        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = block == world.getBlockState(blockpos3.north()).getBlock() || block == world.getBlockState(blockpos4.north()).getBlock() || block == world.getBlockState(blockpos4.south()).getBlock() || block == world.getBlockState(blockpos3.south()).getBlock();

            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        return (world.getLight(pos) >= 8 || world.canSeeSky(pos)) && this.canPlaceBlockOn(world.getBlockState(pos.down()).getBlock());
    }

    protected Item getSeed() {
        return Items.wheat_seeds;
    }

    protected Item getCrop() {
        return Items.wheat;
    }

    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(world, pos, state, chance, 0);

        if (!world.isRemote) {
            int i = state.getValue(AGE);

            if (i >= 7) {
                int j = 3 + fortune;

                for (int k = 0; k < j; ++k) {
                    if (world.rand.nextInt(15) <= i) {
                        spawnAsEntity(world, pos, new ItemStack(this.getSeed(), 1, 0));
                    }
                }
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(AGE) == 7 ? this.getCrop() : this.getSeed();
    }

    public Item getItem(World world, BlockPos pos) {
        return this.getSeed();
    }

    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(AGE) < 7;
    }

    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        this.grow(world, pos, state);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, AGE);
    }

}
