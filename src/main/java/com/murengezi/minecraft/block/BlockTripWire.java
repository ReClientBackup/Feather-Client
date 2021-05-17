package com.murengezi.minecraft.block;

import java.util.List;
import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.init.Blocks;
import com.murengezi.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
    
    public static final PropertyBool POWERED = PropertyBool.create("powered"), SUSPENDED = PropertyBool.create("suspended"), ATTACHED = PropertyBool.create("attached"), DISARMED = PropertyBool.create("disarmed"), NORTH = PropertyBool.create("north"), EAST = PropertyBool.create("east"), SOUTH = PropertyBool.create("south"), WEST = PropertyBool.create("west");

    public BlockTripWire() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false).withProperty(SUSPENDED, false).withProperty(ATTACHED, false).withProperty(DISARMED, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
        this.setTickRandomly(true);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(NORTH, isConnectedTo(world, pos, state, EnumFacing.NORTH)).withProperty(EAST, isConnectedTo(world, pos, state, EnumFacing.EAST)).withProperty(SOUTH, isConnectedTo(world, pos, state, EnumFacing.SOUTH)).withProperty(WEST, isConnectedTo(world, pos, state, EnumFacing.WEST));
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

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.string;
    }

    public Item getItem(World world, BlockPos pos) {
        return Items.string;
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        boolean flag = state.getValue(SUSPENDED);
        boolean flag1 = !World.doesBlockHaveSolidTopSurface(world, pos.down());

        if (flag != flag1) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        IBlockState iblockstate = world.getBlockState(pos);
        boolean flag = iblockstate.getValue(ATTACHED);
        boolean flag1 = iblockstate.getValue(SUSPENDED);

        if (!flag1) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
        } else if (!flag) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        state = state.withProperty(SUSPENDED, !World.doesBlockHaveSolidTopSurface(world, pos.down()));
        world.setBlockState(pos, state, 3);
        this.notifyHook(world, pos, state);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        this.notifyHook(world, pos, state.withProperty(POWERED, true));
    }

    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!world.isRemote) {
            if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
                world.setBlockState(pos, state.withProperty(DISARMED, true), 4);
            }
        }
    }

    private void notifyHook(World world, BlockPos pos, IBlockState state) {
        for (EnumFacing enumfacing : new EnumFacing[] {EnumFacing.SOUTH, EnumFacing.WEST}) {
            for (int i = 1; i < 42; ++i) {
                BlockPos blockpos = pos.offset(enumfacing, i);
                IBlockState iblockstate = world.getBlockState(blockpos);

                if (iblockstate.getBlock() == Blocks.tripwire_hook) {
                    if (iblockstate.getValue(BlockTripWireHook.FACING) == enumfacing.getOpposite()) {
                        Blocks.tripwire_hook.func_176260_a(world, blockpos, iblockstate, false, true, i, state);
                    }

                    break;
                }

                if (iblockstate.getBlock() != Blocks.tripwire) {
                    break;
                }
            }
        }
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!world.isRemote) {
            if (!state.getValue(POWERED)) {
                this.updateState(world, pos);
            }
        }
    }

    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {}

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            if (world.getBlockState(pos).getValue(POWERED)) {
                this.updateState(world, pos);
            }
        }
    }

    private void updateState(World world, BlockPos pos) {
        IBlockState iblockstate = world.getBlockState(pos);
        boolean flag = iblockstate.getValue(POWERED);
        boolean flag1 = false;
        List <? extends Entity > list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB((double)pos.getX() + this.minX, (double)pos.getY() + this.minY, (double)pos.getZ() + this.minZ, (double)pos.getX() + this.maxX, (double)pos.getY() + this.maxY, (double)pos.getZ() + this.maxZ));

        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (!entity.doesEntityNotTriggerPressurePlate()) {
                    flag1 = true;
                    break;
                }
            }
        }

        if (flag1 != flag) {
            iblockstate = iblockstate.withProperty(POWERED, flag1);
            world.setBlockState(pos, iblockstate, 3);
            this.notifyHook(world, pos, iblockstate);
        }

        if (flag1) {
            world.scheduleUpdate(pos, this, this.tickRate(world));
        }
    }

    public static boolean isConnectedTo(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing direction) {
        BlockPos blockpos = pos.offset(direction);
        IBlockState iblockstate = world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.tripwire_hook) {
            EnumFacing enumfacing = direction.getOpposite();
            return iblockstate.getValue(BlockTripWireHook.FACING) == enumfacing;
        } else if (block == Blocks.tripwire) {
            boolean flag = state.getValue(SUSPENDED);
            boolean flag1 = iblockstate.getValue(SUSPENDED);
            return flag == flag1;
        } else {
            return false;
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWERED, (meta & 1) > 0).withProperty(SUSPENDED, (meta & 2) > 0).withProperty(ATTACHED, (meta & 4) > 0).withProperty(DISARMED, (meta & 8) > 0);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;

        if (state.getValue(POWERED)) {
            i |= 1;
        }

        if (state.getValue(SUSPENDED)) {
            i |= 2;
        }

        if (state.getValue(ATTACHED)) {
            i |= 4;
        }

        if (state.getValue(DISARMED)) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, POWERED, SUSPENDED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
    }

}
