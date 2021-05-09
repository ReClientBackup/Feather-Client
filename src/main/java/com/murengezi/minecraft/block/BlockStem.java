package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyDirection;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable {

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", p_apply_1_ -> p_apply_1_ != EnumFacing.DOWN);
    private final Block crop;

    protected BlockStem(Block crop) {
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(FACING, EnumFacing.UP));
        this.crop = crop;
        this.setTickRandomly(true);
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab(null);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = state.withProperty(FACING, EnumFacing.UP);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (world.getBlockState(pos.offset(enumfacing)).getBlock() == this.crop) {
                state = state.withProperty(FACING, enumfacing);
                break;
            }
        }

        return state;
    }

    protected boolean canPlaceBlockOn(Block ground) {
        return ground == Blocks.farmland;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);

        if (world.getLightFromNeighbors(pos.up()) >= 9) {
            float f = BlockCrops.getGrowthChance(this, world, pos);

            if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
                int i = state.getValue(AGE);

                if (i < 7) {
                    state = state.withProperty(AGE, i + 1);
                    world.setBlockState(pos, state, 2);
                } else {
                    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                        if (world.getBlockState(pos.offset(enumfacing)).getBlock() == this.crop) {
                            return;
                        }
                    }

                    pos = pos.offset(EnumFacing.Plane.HORIZONTAL.random(rand));
                    Block block = world.getBlockState(pos.down()).getBlock();

                    if (world.getBlockState(pos).getBlock().blockMaterial == Material.air && (block == Blocks.farmland || block == Blocks.dirt || block == Blocks.grass)) {
                        world.setBlockState(pos, this.crop.getDefaultState());
                    }
                }
            }
        }
    }

    public void growStem(World world, BlockPos pos, IBlockState state) {
        int i = state.getValue(AGE) + MathHelper.getRandomIntegerInRange(world.rand, 2, 5);
        world.setBlockState(pos, state.withProperty(AGE, Math.min(7, i)), 2);
    }

    public int getRenderColor(IBlockState state) {
        if (state.getBlock() != this) {
            return super.getRenderColor(state);
        } else {
            int i = state.getValue(AGE);
            int j = i * 32;
            int k = 255 - i * 8;
            int l = i * 4;
            return j << 16 | k << 8 | l;
        }
    }

    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        return this.getRenderColor(world.getBlockState(pos));
    }

    public void setBlockBoundsForItemRender() {
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        this.maxY = (float)(world.getBlockState(pos).getValue(AGE) * 2 + 2) / 16.0F;
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float)this.maxY, 0.5F + f);
    }

    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);

        if (!world.isRemote) {
            Item item = this.getSeedItem();

            if (item != null) {
                int i = state.getValue(AGE);

                for (int j = 0; j < 3; ++j) {
                    if (world.rand.nextInt(15) <= i) {
                        spawnAsEntity(world, pos, new ItemStack(item));
                    }
                }
            }
        }
    }

    protected Item getSeedItem() {
        return this.crop == Blocks.pumpkin ? Items.pumpkin_seeds : (this.crop == Blocks.melon_block ? Items.melon_seeds : null);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public Item getItem(World world, BlockPos pos) {
        Item item = this.getSeedItem();
        return item;
    }

    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(AGE) != 7;
    }

    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        this.growStem(world, pos, state);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, AGE, FACING);
    }

}
