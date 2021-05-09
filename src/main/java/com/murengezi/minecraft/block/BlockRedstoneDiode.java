package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional {

    protected final boolean isRepeaterPowered;

    protected BlockRedstoneDiode(boolean powered) {
        super(Material.circuits);
        this.isRepeaterPowered = powered;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(world, pos.down()) && super.canPlaceBlockAt(world, pos);
    }

    public boolean canBlockStay(World world, BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(world, pos.down());
    }

    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {}

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!this.isLocked(world, pos, state)) {
            boolean flag = this.shouldBePowered(world, pos, state);

            if (this.isRepeaterPowered && !flag) {
                world.setBlockState(pos, this.getUnpoweredState(state), 2);
            } else if (!this.isRepeaterPowered) {
                world.setBlockState(pos, this.getPoweredState(state), 2);

                if (!flag) {
                    world.updateBlockTick(pos, this.getPoweredState(state).getBlock(), this.getTickDelay(state), -1);
                }
            }
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side.getAxis() != EnumFacing.Axis.Y;
    }

    protected boolean isPowered(IBlockState state) {
        return this.isRepeaterPowered;
    }

    public int isProvidingStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        return this.isProvidingWeakPower(world, pos, state, side);
    }

    public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        return !this.isPowered(state) ? 0 : (state.getValue(FACING) == side ? this.getActiveSignal(world, pos, state) : 0);
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (this.canBlockStay(world, pos)) {
            this.updateState(world, pos, state);
        } else {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);

            for (EnumFacing enumfacing : EnumFacing.values()) {
                world.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }
    }

    protected void updateState(World world, BlockPos pos, IBlockState state) {
        if (!this.isLocked(world, pos, state)) {
            boolean flag = this.shouldBePowered(world, pos, state);

            if ((this.isRepeaterPowered && !flag || !this.isRepeaterPowered && flag) && !world.isBlockTickPending(pos, this)) {
                int i = -1;

                if (this.isFacingTowardsRepeater(world, pos, state)) {
                    i = -3;
                } else if (this.isRepeaterPowered) {
                    i = -2;
                }

                world.updateBlockTick(pos, this, this.getDelay(state), i);
            }
        }
    }

    public boolean isLocked(IBlockAccess world, BlockPos pos, IBlockState state) {
        return false;
    }

    protected boolean shouldBePowered(World world, BlockPos pos, IBlockState state) {
        return this.calculateInputStrength(world, pos, state) > 0;
    }

    protected int calculateInputStrength(World world, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        int i = world.getRedstonePower(blockpos, enumfacing);

        if (i >= 15) {
            return i;
        } else {
            IBlockState iblockstate = world.getBlockState(blockpos);
            return Math.max(i, iblockstate.getBlock() == Blocks.redstone_wire ? iblockstate.getValue(BlockRedstoneWire.POWER) : 0);
        }
    }

    protected int getPowerOnSides(IBlockAccess world, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = state.getValue(FACING);
        EnumFacing enumfacing1 = enumfacing.rotateY();
        EnumFacing enumfacing2 = enumfacing.rotateYCCW();
        return Math.max(this.getPowerOnSide(world, pos.offset(enumfacing1), enumfacing1), this.getPowerOnSide(world, pos.offset(enumfacing2), enumfacing2));
    }

    protected int getPowerOnSide(IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return this.canPowerSide(block) ? (block == Blocks.redstone_wire ? iblockstate.getValue(BlockRedstoneWire.POWER) : world.getStrongPower(pos, side)) : 0;
    }

    public boolean canProvidePower() {
        return true;
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (this.shouldBePowered(world, pos, state)) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        this.notifyNeighbors(world, pos, state);
    }

    protected void notifyNeighbors(World world, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing.getOpposite());
        world.notifyBlockOfStateChange(blockpos, this);
        world.notifyNeighborsOfStateExcept(blockpos, this, enumfacing);
    }

    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        if (this.isRepeaterPowered) {
            for (EnumFacing enumfacing : EnumFacing.values()) {
                world.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }

        super.onBlockDestroyedByPlayer(world, pos, state);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    protected boolean canPowerSide(Block block) {
        return block.canProvidePower();
    }

    protected int getActiveSignal(IBlockAccess world, BlockPos pos, IBlockState state) {
        return 15;
    }

    public static boolean isRedstoneRepeaterBlockID(Block block) {
        return Blocks.unpowered_repeater.isAssociated(block) || Blocks.unpowered_comparator.isAssociated(block);
    }

    public boolean isAssociated(Block other) {
        return other == this.getPoweredState(this.getDefaultState()).getBlock() || other == this.getUnpoweredState(this.getDefaultState()).getBlock();
    }

    public boolean isFacingTowardsRepeater(World world, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = state.getValue(FACING).getOpposite();
        BlockPos blockpos = pos.offset(enumfacing);
        return isRedstoneRepeaterBlockID(world.getBlockState(blockpos).getBlock()) && world.getBlockState(blockpos).getValue(FACING) != enumfacing;
    }

    protected int getTickDelay(IBlockState state) {
        return this.getDelay(state);
    }

    protected abstract int getDelay(IBlockState state);

    protected abstract IBlockState getPoweredState(IBlockState unpoweredState);

    protected abstract IBlockState getUnpoweredState(IBlockState poweredState);

    public boolean isAssociatedBlock(Block other) {
        return this.isAssociated(other);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

}
