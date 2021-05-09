package com.murengezi.minecraft.block;

import java.util.List;
import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.IProperty;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockSlab extends Block {

    public static final PropertyEnum<BlockSlab.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockSlab.EnumBlockHalf.class);

    public BlockSlab(Material material) {
        super(material);

        if (this.isDouble()) {
            this.fullBlock = true;
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }

        this.setLightOpacity(255);
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        if (this.isDouble()) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            IBlockState iblockstate = world.getBlockState(pos);

            if (iblockstate.getBlock() == this) {
                if (iblockstate.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
                    this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
                }
            }
        }
    }

    public void setBlockBoundsForItemRender() {
        if (this.isDouble()) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        this.setBlockBoundsBasedOnState(world, pos);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
    }

    public boolean isOpaqueCube() {
        return this.isDouble();
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        return this.isDouble() ? iblockstate : (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double)hitY <= 0.5D) ? iblockstate : iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.TOP));
    }

    public int quantityDropped(Random random) {
        return this.isDouble() ? 2 : 1;
    }

    public boolean isFullCube() {
        return this.isDouble();
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (this.isDouble()) {
            return super.shouldSideBeRendered(world, pos, side);
        } else if (side != EnumFacing.UP && side != EnumFacing.DOWN && !super.shouldSideBeRendered(world, pos, side)) {
            return false;
        } else {
            BlockPos blockpos = pos.offset(side.getOpposite());
            IBlockState iblockstate = world.getBlockState(pos);
            IBlockState iblockstate1 = world.getBlockState(blockpos);
            boolean flag = isSlab(iblockstate.getBlock()) && iblockstate.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP;
            boolean flag1 = isSlab(iblockstate1.getBlock()) && iblockstate1.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP;
            return flag1 ? (side == EnumFacing.DOWN || (side == EnumFacing.UP && super.shouldSideBeRendered(world, pos, side) || !isSlab(iblockstate.getBlock()) || !flag)) : (side == EnumFacing.UP || (side == EnumFacing.DOWN && super.shouldSideBeRendered(world, pos, side) || !isSlab(iblockstate.getBlock()) || flag));
        }
    }

    protected static boolean isSlab(Block block) {
        return block == Blocks.stone_slab || block == Blocks.wooden_slab || block == Blocks.stone_slab2;
    }

    public abstract String getUnlocalizedName(int meta);

    public int getDamageValue(World world, BlockPos pos) {
        return super.getDamageValue(world, pos) & 7;
    }

    public abstract boolean isDouble();

    public abstract IProperty<?> getVariantProperty();

    public abstract Object getVariant(ItemStack stack);

    public enum EnumBlockHalf implements IStringSerializable {
        TOP("top"), BOTTOM("bottom");

        private final String name;

        EnumBlockHalf(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

}
