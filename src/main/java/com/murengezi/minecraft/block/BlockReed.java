package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.init.Blocks;
import com.murengezi.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReed extends Block {

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

    protected BlockReed() {
        super(Material.plants);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
        float f = 0.375F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
        this.setTickRandomly(true);
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.getBlockState(pos.down()).getBlock() == Blocks.reeds || this.checkForDrop(world, pos, state)) {
            if (world.isAirBlock(pos.up())) {
                int i;

                for (i = 1; world.getBlockState(pos.down(i)).getBlock() == this; ++i) {}

                if (i < 3) {
                    int j = state.getValue(AGE);

                    if (j == 15) {
                        world.setBlockState(pos.up(), this.getDefaultState());
                        world.setBlockState(pos, state.withProperty(AGE, 0), 4);
                    } else {
                        world.setBlockState(pos, state.withProperty(AGE, j + 1), 4);
                    }
                }
            }
        }
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        Block block = world.getBlockState(pos.down()).getBlock();

        if (block == this) {
            return true;
        } else if (block != Blocks.grass && block != Blocks.dirt && block != Blocks.sand) {
            return false;
        } else {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                if (world.getBlockState(pos.offset(enumfacing).down()).getBlock().getMaterial() == Material.water) {
                    return true;
                }
            }

            return false;
        }
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        this.checkForDrop(world, pos, state);
    }

    protected final boolean checkForDrop(World world, BlockPos pos, IBlockState state) {
        if (this.canBlockStay(world, pos)) {
            return true;
        } else {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            return false;
        }
    }

    public boolean canBlockStay(World world, BlockPos pos) {
        return this.canPlaceBlockAt(world, pos);
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return null;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.reeds;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public Item getItem(World world, BlockPos pos) {
        return Items.reeds;
    }

    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        return world.getBiomeGenForCoords(pos).getGrassColorAtPos(pos);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
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
