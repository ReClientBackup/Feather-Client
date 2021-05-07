package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /** 0 : Normal chest, 1 : Trapped chest */
    public final int chestType;

    protected BlockChest(int type) {
        super(Material.wood);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.chestType = type;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 2;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        if (world.getBlockState(pos.north()).getBlock() == this) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
        } else if (world.getBlockState(pos.south()).getBlock() == this) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        } else if (world.getBlockState(pos.west()).getBlock() == this) {
            this.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        } else if (world.getBlockState(pos.east()).getBlock() == this) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        } else {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        this.checkForSurroundingChests(world, pos, state);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(enumfacing);
            IBlockState iblockstate = world.getBlockState(blockpos);

            if (iblockstate.getBlock() == this) {
                this.checkForSurroundingChests(world, blockpos, iblockstate);
            }
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
        state = state.withProperty(FACING, enumfacing);
        BlockPos blockpos = pos.north();
        BlockPos blockpos1 = pos.south();
        BlockPos blockpos2 = pos.west();
        BlockPos blockpos3 = pos.east();
        boolean flag = this == world.getBlockState(blockpos).getBlock();
        boolean flag1 = this == world.getBlockState(blockpos1).getBlock();
        boolean flag2 = this == world.getBlockState(blockpos2).getBlock();
        boolean flag3 = this == world.getBlockState(blockpos3).getBlock();

        if (!flag && !flag1 && !flag2 && !flag3) {
            world.setBlockState(pos, state, 3);
        } else if (enumfacing.getAxis() != EnumFacing.Axis.X || !flag && !flag1) {
            if (enumfacing.getAxis() == EnumFacing.Axis.Z && (flag2 || flag3)) {
                if (flag2) {
                    world.setBlockState(blockpos2, state, 3);
                } else {
                    world.setBlockState(blockpos3, state, 3);
                }

                world.setBlockState(pos, state, 3);
            }
        } else {
            if (flag) {
                world.setBlockState(blockpos, state, 3);
            } else {
                world.setBlockState(blockpos1, state, 3);
            }

            world.setBlockState(pos, state, 3);
        }

        if (stack.hasDisplayName()) {
            TileEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    public void checkForSurroundingChests(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            IBlockState iblockstate = world.getBlockState(pos.north());
            IBlockState iblockstate1 = world.getBlockState(pos.south());
            IBlockState iblockstate2 = world.getBlockState(pos.west());
            IBlockState iblockstate3 = world.getBlockState(pos.east());
            EnumFacing enumfacing = state.getValue(FACING);
            Block block = iblockstate.getBlock();
            Block block1 = iblockstate1.getBlock();
            Block block2 = iblockstate2.getBlock();
            Block block3 = iblockstate3.getBlock();

            if (block != this && block1 != this) {
                boolean flag = block.isFullBlock();
                boolean flag1 = block1.isFullBlock();

                if (block2 == this || block3 == this) {
                    BlockPos blockpos1 = block2 == this ? pos.west() : pos.east();
                    IBlockState iblockstate6 = world.getBlockState(blockpos1.north());
                    IBlockState iblockstate7 = world.getBlockState(blockpos1.south());
                    enumfacing = EnumFacing.SOUTH;
                    EnumFacing enumfacing2;

                    if (block2 == this) {
                        enumfacing2 = iblockstate2.getValue(FACING);
                    } else {
                        enumfacing2 = iblockstate3.getValue(FACING);
                    }

                    if (enumfacing2 == EnumFacing.NORTH) {
                        enumfacing = EnumFacing.NORTH;
                    }

                    Block block6 = iblockstate6.getBlock();
                    Block block7 = iblockstate7.getBlock();

                    if ((flag || block6.isFullBlock()) && !flag1 && !block7.isFullBlock()) {
                        enumfacing = EnumFacing.SOUTH;
                    }

                    if ((flag1 || block7.isFullBlock()) && !flag && !block6.isFullBlock()) {
                        enumfacing = EnumFacing.NORTH;
                    }
                }
            } else {
                BlockPos blockpos = block == this ? pos.north() : pos.south();
                IBlockState iblockstate4 = world.getBlockState(blockpos.west());
                IBlockState iblockstate5 = world.getBlockState(blockpos.east());
                enumfacing = EnumFacing.EAST;
                EnumFacing enumfacing1;

                if (block == this) {
                    enumfacing1 = iblockstate.getValue(FACING);
                } else {
                    enumfacing1 = iblockstate1.getValue(FACING);
                }

                if (enumfacing1 == EnumFacing.WEST) {
                    enumfacing = EnumFacing.WEST;
                }

                Block block4 = iblockstate4.getBlock();
                Block block5 = iblockstate5.getBlock();

                if ((block2.isFullBlock() || block4.isFullBlock()) && !block3.isFullBlock() && !block5.isFullBlock()) {
                    enumfacing = EnumFacing.EAST;
                }

                if ((block3.isFullBlock() || block5.isFullBlock()) && !block2.isFullBlock() && !block4.isFullBlock()) {
                    enumfacing = EnumFacing.WEST;
                }
            }

            state = state.withProperty(FACING, enumfacing);
            world.setBlockState(pos, state, 3);
        }
    }

    public IBlockState correctFacing(World world, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = null;

        for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL)
        {
            IBlockState iblockstate = world.getBlockState(pos.offset(enumfacing1));

            if (iblockstate.getBlock() == this)
            {
                return state;
            }

            if (iblockstate.getBlock().isFullBlock())
            {
                if (enumfacing != null)
                {
                    enumfacing = null;
                    break;
                }

                enumfacing = enumfacing1;
            }
        }

        if (enumfacing != null)
        {
            return state.withProperty(FACING, enumfacing.getOpposite());
        }
        else
        {
            EnumFacing enumfacing2 = state.getValue(FACING);

            if (world.getBlockState(pos.offset(enumfacing2)).getBlock().isFullBlock())
            {
                enumfacing2 = enumfacing2.getOpposite();
            }

            if (world.getBlockState(pos.offset(enumfacing2)).getBlock().isFullBlock())
            {
                enumfacing2 = enumfacing2.rotateY();
            }

            if (world.getBlockState(pos.offset(enumfacing2)).getBlock().isFullBlock())
            {
                enumfacing2 = enumfacing2.getOpposite();
            }

            return state.withProperty(FACING, enumfacing2);
        }
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        int i = 0;
        BlockPos blockpos = pos.west();
        BlockPos blockpos1 = pos.east();
        BlockPos blockpos2 = pos.north();
        BlockPos blockpos3 = pos.south();

        if (world.getBlockState(blockpos).getBlock() == this) {
            if (this.isDoubleChest(world, blockpos)) {
                return false;
            }

            ++i;
        }

        if (world.getBlockState(blockpos1).getBlock() == this) {
            if (this.isDoubleChest(world, blockpos1)) {
                return false;
            }

            ++i;
        }

        if (world.getBlockState(blockpos2).getBlock() == this) {
            if (this.isDoubleChest(world, blockpos2)) {
                return false;
            }

            ++i;
        }

        if (world.getBlockState(blockpos3).getBlock() == this) {
            if (this.isDoubleChest(world, blockpos3)) {
                return false;
            }

            ++i;
        }

        return i <= 1;
    }

    private boolean isDoubleChest(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == this) {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                if (world.getBlockState(pos.offset(enumfacing)).getBlock() == this) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChange(world, pos, state, neighborBlock);
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityChest) {
            tileentity.updateContainingBlockInfo();
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileentity);
            world.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(world, pos, state);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ILockableContainer ilockablecontainer = this.getLockableContainer(world, pos);

            if (ilockablecontainer != null) {
                player.displayGUIChest(ilockablecontainer);

                if (this.chestType == 0) {
                    player.triggerAchievement(StatList.field_181723_aa);
                } else if (this.chestType == 1) {
                    player.triggerAchievement(StatList.field_181737_U);
                }
            }
        }
        return true;
    }

    public ILockableContainer getLockableContainer(World world, BlockPos pos) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityChest)) {
            return null;
        } else {
            ILockableContainer ilockablecontainer = (TileEntityChest)tileentity;

            if (this.isBlocked(world, pos)) {
                return null;
            } else {
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                    BlockPos blockpos = pos.offset(enumfacing);
                    Block block = world.getBlockState(blockpos).getBlock();

                    if (block == this) {
                        if (this.isBlocked(world, blockpos)) {
                            return null;
                        }

                        TileEntity tileentity1 = world.getTileEntity(blockpos);

                        if (tileentity1 instanceof TileEntityChest) {
                            if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH) {
                                ilockablecontainer = new InventoryLargeChest("container.chestDouble", ilockablecontainer, (TileEntityChest)tileentity1);
                            } else {
                                ilockablecontainer = new InventoryLargeChest("container.chestDouble", (TileEntityChest)tileentity1, ilockablecontainer);
                            }
                        }
                    }
                }

                return ilockablecontainer;
            }
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityChest();
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return this.chestType == 1;
    }

    public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        if (!this.canProvidePower()) {
            return 0;
        } else {
            int i = 0;
            TileEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof TileEntityChest) {
                i = ((TileEntityChest)tileentity).numPlayersUsing;
            }

            return MathHelper.clamp_int(i, 0, 15);
        }
    }

    public int isProvidingStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        return side == EnumFacing.UP ? this.isProvidingWeakPower(world, pos, state, side) : 0;
    }

    private boolean isBlocked(World world, BlockPos pos) {
        return this.isBelowSolidBlock(world, pos) || this.isOcelotSittingOnChest(world, pos);
    }

    private boolean isBelowSolidBlock(World world, BlockPos pos) {
        return world.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    private boolean isOcelotSittingOnChest(World world, BlockPos pos) {
        for (Entity entity : world.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1))) {
            EntityOcelot entityocelot = (EntityOcelot)entity;

            if (entityocelot.isSitting()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, BlockPos pos) {
        return Container.calcRedstoneFromInventory(this.getLockableContainer(world, pos));
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING);
    }

}
