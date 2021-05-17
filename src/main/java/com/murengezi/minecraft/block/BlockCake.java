package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCake extends Block {

    public static final PropertyInteger BITES = PropertyInteger.create("bites", 0, 6);

    protected BlockCake() {
        super(Material.cake);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BITES, 0));
        this.setTickRandomly(true);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        float f = 0.0625F;
        float f1 = (float)(1 + world.getBlockState(pos).getValue(BITES) * 2) / 16.0F;
        float f2 = 0.5F;
        this.setBlockBounds(f1, 0.0F, f, 1.0F - f, f2, 1.0F - f);
    }

    public void setBlockBoundsForItemRender() {
        float f = 0.0625F;
        float f1 = 0.5F;
        this.setBlockBounds(f, 0.0F, f, 1.0F - f, f1, 1.0F - f);
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        float f = 0.0625F;
        float f1 = (float)(1 + state.getValue(BITES) * 2) / 16.0F;
        float f2 = 0.5F;
        return new AxisAlignedBB((float)pos.getX() + f1, pos.getY(), (float)pos.getZ() + f, (float)(pos.getX() + 1) - f, (float)pos.getY() + f2, (float)(pos.getZ() + 1) - f);
    }

    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        return this.getCollisionBoundingBox(world, pos, world.getBlockState(pos));
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        this.eatCake(world, pos, state, player);
        return true;
    }

    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
    {
        this.eatCake(world, pos, world.getBlockState(pos), player);
    }

    private void eatCake(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.canEat(false)) {
            player.triggerAchievement(StatList.field_181724_H);
            player.getFoodStats().addStats(2, 0.1F);
            int i = state.getValue(BITES);

            if (i < 6) {
                world.setBlockState(pos, state.withProperty(BITES, i + 1), 3);
            } else {
                world.setBlockToAir(pos);
            }
        }
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && this.canBlockStay(world, pos);
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!this.canBlockStay(world, pos)) {
            world.setBlockToAir(pos);
        }
    }

    private boolean canBlockStay(World world, BlockPos pos) {
        return world.getBlockState(pos.down()).getBlock().getMaterial().isSolid();
    }
    
    public int quantityDropped(Random random) {
        return 0;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public Item getItem(World world, BlockPos pos) {
        return Items.cake;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BITES, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(BITES);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, BITES);
    }

    public int getComparatorInputOverride(World world, BlockPos pos) {
        return (7 - world.getBlockState(pos).getValue(BITES)) * 2;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

}
