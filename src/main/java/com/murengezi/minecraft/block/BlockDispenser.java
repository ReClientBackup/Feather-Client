package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.properties.PropertyDirection;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import com.murengezi.minecraft.dispenser.BehaviorDefaultDispenseItem;
import com.murengezi.minecraft.dispenser.IBehaviorDispenseItem;
import com.murengezi.minecraft.dispenser.IBlockSource;
import com.murengezi.minecraft.dispenser.IPosition;
import com.murengezi.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");
    public static final RegistryDefaulted<Item, IBehaviorDispenseItem> dispenseBehaviorRegistry = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
    protected Random rand = new Random();

    protected BlockDispenser() {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TRIGGERED, false));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public int tickRate(World world) {
        return 4;
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        this.setDefaultDirection(world, pos, state);
    }

    private void setDefaultDirection(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            EnumFacing enumfacing = state.getValue(FACING);
            boolean flag = world.getBlockState(pos.north()).getBlock().isFullBlock();
            boolean flag1 = world.getBlockState(pos.south()).getBlock().isFullBlock();

            if (enumfacing == EnumFacing.NORTH && flag && !flag1) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && flag1 && !flag) {
                enumfacing = EnumFacing.NORTH;
            } else {
                boolean flag2 = world.getBlockState(pos.west()).getBlock().isFullBlock();
                boolean flag3 = world.getBlockState(pos.east()).getBlock().isFullBlock();

                if (enumfacing == EnumFacing.WEST && flag2 && !flag3) {
                    enumfacing = EnumFacing.EAST;
                } else if (enumfacing == EnumFacing.EAST && flag3 && !flag2) {
                    enumfacing = EnumFacing.WEST;
                }
            }

            world.setBlockState(pos, state.withProperty(FACING, enumfacing).withProperty(TRIGGERED, false), 2);
        }
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof TileEntityDispenser) {
                player.displayGUIChest((TileEntityDispenser) tileentity);

                if (tileentity instanceof TileEntityDropper) {
                    player.triggerAchievement(StatList.field_181731_O);
                } else {
                    player.triggerAchievement(StatList.field_181733_Q);
                }
            }
        }
        return true;
    }

    protected void dispense(World world, BlockPos pos) {
        BlockSourceImpl blocksourceimpl = new BlockSourceImpl(world, pos);
        TileEntityDispenser tileentitydispenser = blocksourceimpl.getBlockTileEntity();

        if (tileentitydispenser != null) {
            int i = tileentitydispenser.getDispenseSlot();

            if (i < 0) {
                world.playAuxSFX(1001, pos, 0);
            } else {
                ItemStack itemstack = tileentitydispenser.getStackInSlot(i);
                IBehaviorDispenseItem ibehaviordispenseitem = this.getBehavior(itemstack);

                if (ibehaviordispenseitem != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
                    ItemStack itemstack1 = ibehaviordispenseitem.dispense(blocksourceimpl, itemstack);
                    tileentitydispenser.setInventorySlotContents(i, itemstack1.stackSize <= 0 ? null : itemstack1);
                }
            }
        }
    }

    protected IBehaviorDispenseItem getBehavior(ItemStack stack) {
        return dispenseBehaviorRegistry.getObject(stack == null ? null : stack.getItem());
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        boolean flag = world.isBlockPowered(pos) || world.isBlockPowered(pos.up());
        boolean flag1 = state.getValue(TRIGGERED);

        if (flag && !flag1) {
            world.scheduleUpdate(pos, this, this.tickRate(world));
            world.setBlockState(pos, state.withProperty(TRIGGERED, true), 4);
        } else if (!flag && flag1) {
            world.setBlockState(pos, state.withProperty(TRIGGERED, false), 4);
        }
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            this.dispense(world, pos);
        }
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityDispenser();
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(pos, placer)).withProperty(TRIGGERED, false);
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, BlockPistonBase.getFacingFromEntity(pos, placer)), 2);

        if (stack.hasDisplayName()) {
            TileEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof TileEntityDispenser) {
                ((TileEntityDispenser)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityDispenser) {
            InventoryHelper.dropInventoryItems(world, pos, (TileEntityDispenser)tileentity);
            world.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(world, pos, state);
    }

    public static IPosition getDispensePosition(IBlockSource coords) {
        EnumFacing enumfacing = getFacing(coords.getBlockMetadata());
        double d0 = coords.getX() + 0.7D * (double)enumfacing.getFrontOffsetX();
        double d1 = coords.getY() + 0.7D * (double)enumfacing.getFrontOffsetY();
        double d2 = coords.getZ() + 0.7D * (double)enumfacing.getFrontOffsetZ();
        return new PositionImpl(d0, d1, d2);
    }

    public static EnumFacing getFacing(int meta) {
        return EnumFacing.getFront(meta & 7);
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, BlockPos pos) {
        return Container.calcRedstone(world.getTileEntity(pos));
    }

    public int getRenderType() {
        return 3;
    }

    public IBlockState getStateForEntityRender(IBlockState state) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(TRIGGERED, (meta & 8) > 0);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(TRIGGERED)) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, TRIGGERED);
    }

}
