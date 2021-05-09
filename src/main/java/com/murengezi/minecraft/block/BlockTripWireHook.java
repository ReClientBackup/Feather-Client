package com.murengezi.minecraft.block;

import com.google.common.base.Objects;
import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.properties.PropertyDirection;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool POWERED = PropertyBool.create("powered"), ATTACHED = PropertyBool.create("attached"), SUSPENDED = PropertyBool.create("suspended");

    public BlockTripWireHook() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false).withProperty(ATTACHED, false).withProperty(SUSPENDED, false));
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(SUSPENDED, Boolean.valueOf(!World.doesBlockHaveSolidTopSurface(world, pos.down())));
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return side.getAxis().isHorizontal() && world.getBlockState(pos.offset(side.getOpposite())).getBlock().isNormalCube();
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (world.getBlockState(pos.offset(enumfacing)).getBlock().isNormalCube()) {
                return true;
            }
        }

        return false;
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = this.getDefaultState().withProperty(POWERED, false).withProperty(ATTACHED, false).withProperty(SUSPENDED, false);

        if (facing.getAxis().isHorizontal()) {
            iblockstate = iblockstate.withProperty(FACING, facing);
        }

        return iblockstate;
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        this.func_176260_a(world, pos, state, false, false, -1, null);
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (neighborBlock != this) {
            if (this.checkForDrop(world, pos, state)) {
                EnumFacing enumfacing = state.getValue(FACING);

                if (!world.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock().isNormalCube()) {
                    this.dropBlockAsItem(world, pos, state, 0);
                    world.setBlockToAir(pos);
                }
            }
        }
    }

    public void func_176260_a(World world, BlockPos pos, IBlockState hookState, boolean p_176260_4_, boolean p_176260_5_, int p_176260_6_, IBlockState p_176260_7_) {
        EnumFacing enumfacing = hookState.getValue(FACING);
        boolean flag = hookState.getValue(ATTACHED);
        boolean flag1 = hookState.getValue(POWERED);
        boolean flag2 = !World.doesBlockHaveSolidTopSurface(world, pos.down());
        boolean flag3 = !p_176260_4_;
        boolean flag4 = false;
        int i = 0;
        IBlockState[] aiblockstate = new IBlockState[42];

        for (int j = 1; j < 42; ++j) {
            BlockPos blockpos = pos.offset(enumfacing, j);
            IBlockState iblockstate = world.getBlockState(blockpos);

            if (iblockstate.getBlock() == Blocks.tripwire_hook) {
                if (iblockstate.getValue(FACING) == enumfacing.getOpposite()) {
                    i = j;
                }

                break;
            }

            if (iblockstate.getBlock() != Blocks.tripwire && j != p_176260_6_) {
                aiblockstate[j] = null;
                flag3 = false;
            } else {
                if (j == p_176260_6_) {
                    iblockstate = Objects.firstNonNull(p_176260_7_, iblockstate);
                }

                boolean flag5 = !iblockstate.getValue(BlockTripWire.DISARMED);
                boolean flag6 = iblockstate.getValue(BlockTripWire.POWERED);
                boolean flag7 = iblockstate.getValue(BlockTripWire.SUSPENDED);
                flag3 &= flag7 == flag2;
                flag4 |= flag5 && flag6;
                aiblockstate[j] = iblockstate;

                if (j == p_176260_6_) {
                    world.scheduleUpdate(pos, this, this.tickRate(world));
                    flag3 &= flag5;
                }
            }
        }

        flag3 = flag3 & i > 1;
        flag4 = flag4 & flag3;
        IBlockState iblockstate1 = this.getDefaultState().withProperty(ATTACHED, Boolean.valueOf(flag3)).withProperty(POWERED, Boolean.valueOf(flag4));

        if (i > 0) {
            BlockPos blockpos1 = pos.offset(enumfacing, i);
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            world.setBlockState(blockpos1, iblockstate1.withProperty(FACING, enumfacing1), 3);
            this.func_176262_b(world, blockpos1, enumfacing1);
            this.func_180694_a(world, blockpos1, flag3, flag4, flag, flag1);
        }

        this.func_180694_a(world, pos, flag3, flag4, flag, flag1);

        if (!p_176260_4_) {
            world.setBlockState(pos, iblockstate1.withProperty(FACING, enumfacing), 3);

            if (p_176260_5_) {
                this.func_176262_b(world, pos, enumfacing);
            }
        }

        if (flag != flag3) {
            for (int k = 1; k < i; ++k) {
                BlockPos blockpos2 = pos.offset(enumfacing, k);
                IBlockState iblockstate2 = aiblockstate[k];

                if (iblockstate2 != null && world.getBlockState(blockpos2).getBlock() != Blocks.air) {
                    world.setBlockState(blockpos2, iblockstate2.withProperty(ATTACHED, Boolean.valueOf(flag3)), 3);
                }
            }
        }
    }

    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {}

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        this.func_176260_a(world, pos, state, false, true, -1, null);
    }

    private void func_180694_a(World world, BlockPos pos, boolean p_180694_3_, boolean p_180694_4_, boolean p_180694_5_, boolean p_180694_6_) {
        if (p_180694_4_ && !p_180694_6_) {
            world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.4F, 0.6F);
        } else if (!p_180694_4_ && p_180694_6_) {
            world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.4F, 0.5F);
        } else if (p_180694_3_ && !p_180694_5_) {
            world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.4F, 0.7F);
        } else if (!p_180694_3_ && p_180694_5_) {
            world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.bowhit", 0.4F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
        }
    }

    private void func_176262_b(World world, BlockPos blockPos, EnumFacing facing) {
        world.notifyNeighborsOfStateChange(blockPos, this);
        world.notifyNeighborsOfStateChange(blockPos.offset(facing.getOpposite()), this);
    }

    private boolean checkForDrop(World world, BlockPos pos, IBlockState state) {
        if (!this.canPlaceBlockAt(world, pos)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            return false;
        } else {
            return true;
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        float f = 0.1875F;

        switch (world.getBlockState(pos).getValue(FACING)) {
            case EAST:
                this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
                break;
            case WEST:
                this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
                break;
            case SOUTH:
                this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
                break;
            case NORTH:
                this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        boolean flag = state.getValue(ATTACHED);
        boolean flag1 = state.getValue(POWERED);

        if (flag || flag1) {
            this.func_176260_a(world, pos, state, true, false, -1, null);
        }

        if (flag1) {
            world.notifyNeighborsOfStateChange(pos, this);
            world.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), this);
        }

        super.breakBlock(world, pos, state);
    }

    public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    public int isProvidingStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        return !state.getValue(POWERED) ? 0 : (state.getValue(FACING) == side ? 15 : 0);
    }

    public boolean canProvidePower() {
        return true;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(POWERED, (meta & 8) > 0).withProperty(ATTACHED, Boolean.valueOf((meta & 4) > 0));
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(POWERED)) {
            i |= 8;
        }

        if (state.getValue(ATTACHED)) {
            i |= 4;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, POWERED, ATTACHED, SUSPENDED);
    }

}
