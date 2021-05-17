package com.murengezi.minecraft.block;

import java.util.List;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.ItemLead;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block {

    public static final PropertyBool NORTH = PropertyBool.create("north"), EAST = PropertyBool.create("east"), SOUTH = PropertyBool.create("south"), WEST = PropertyBool.create("west");

    public BlockFence(Material material) {
        this(material, material.getMaterialMapColor());
    }

    public BlockFence(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        boolean flag = this.canConnectTo(world, pos.north());
        boolean flag1 = this.canConnectTo(world, pos.south());
        boolean flag2 = this.canConnectTo(world, pos.west());
        boolean flag3 = this.canConnectTo(world, pos.east());
        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = 0.375F;
        float f3 = 0.625F;

        if (flag) {
            f2 = 0.0F;
        }

        if (flag1) {
            f3 = 1.0F;
        }

        if (flag || flag1) {
            this.setBlockBounds(f, 0.0F, f2, f1, 1.5F, f3);
            super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        }

        f2 = 0.375F;
        f3 = 0.625F;

        if (flag2) {
            f = 0.0F;
        }

        if (flag3) {
            f1 = 1.0F;
        }

        if (flag2 || flag3 || !flag && !flag1) {
            this.setBlockBounds(f, 0.0F, f2, f1, 1.5F, f3);
            super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        }

        if (flag) {
            f2 = 0.0F;
        }

        if (flag1) {
            f3 = 1.0F;
        }

        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        boolean flag = this.canConnectTo(world, pos.north());
        boolean flag1 = this.canConnectTo(world, pos.south());
        boolean flag2 = this.canConnectTo(world, pos.west());
        boolean flag3 = this.canConnectTo(world, pos.east());
        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = 0.375F;
        float f3 = 0.625F;

        if (flag) {
            f2 = 0.0F;
        }

        if (flag1) {
            f3 = 1.0F;
        }

        if (flag2) {
            f = 0.0F;
        }

        if (flag3) {
            f1 = 1.0F;
        }

        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return false;
    }

    public boolean canConnectTo(IBlockAccess world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block != Blocks.barrier && ((block instanceof BlockFence && block.blockMaterial == this.blockMaterial) || block instanceof BlockFenceGate || (block.blockMaterial.isOpaque() && block.isFullCube() && block.blockMaterial != Material.gourd));
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        return world.isRemote || ItemLead.attachToFence(player, world, pos);
    }

    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(NORTH, this.canConnectTo(world, pos.north())).withProperty(EAST, this.canConnectTo(world, pos.east())).withProperty(SOUTH, this.canConnectTo(world, pos.south())).withProperty(WEST, this.canConnectTo(world, pos.west()));
    }

    protected BlockState createBlockState() {
        return new BlockState(this, NORTH, EAST, WEST, SOUTH);
    }

}
