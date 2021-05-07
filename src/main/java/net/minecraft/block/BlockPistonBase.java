package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool EXTENDED = PropertyBool.create("extended");

    /** This piston is the sticky one? */
    private final boolean isSticky;

    public BlockPistonBase(boolean isSticky)
    {
        super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, Boolean.valueOf(false)));
        this.isSticky = isSticky;
        this.setStepSound(soundTypePiston);
        this.setHardness(0.5F);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        world.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(world, pos, placer)), 2);

        if (!world.isRemote)
        {
            this.checkForMove(world, pos, state);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!world.isRemote)
        {
            this.checkForMove(world, pos, state);
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote && world.getTileEntity(pos) == null)
        {
            this.checkForMove(world, pos, state);
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, getFacingFromEntity(world, pos, placer)).withProperty(EXTENDED, Boolean.valueOf(false));
    }

    private void checkForMove(World world, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = state.getValue(FACING);
        boolean flag = this.shouldBeExtended(world, pos, enumfacing);

        if (flag && !state.getValue(EXTENDED).booleanValue())
        {
            if ((new BlockPistonStructureHelper(world, pos, enumfacing, true)).canMove())
            {
                world.addBlockEvent(pos, this, 0, enumfacing.getIndex());
            }
        }
        else if (!flag && state.getValue(EXTENDED).booleanValue())
        {
            world.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(false)), 2);
            world.addBlockEvent(pos, this, 1, enumfacing.getIndex());
        }
    }

    private boolean shouldBeExtended(World world, BlockPos pos, EnumFacing facing)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (enumfacing != facing && world.isSidePowered(pos.offset(enumfacing), enumfacing))
            {
                return true;
            }
        }

        if (world.isSidePowered(pos, EnumFacing.DOWN))
        {
            return true;
        }
        else
        {
            BlockPos blockpos = pos.up();

            for (EnumFacing enumfacing1 : EnumFacing.values())
            {
                if (enumfacing1 != EnumFacing.DOWN && world.isSidePowered(blockpos.offset(enumfacing1), enumfacing1))
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called
     */
    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventParam)
    {
        EnumFacing enumfacing = state.getValue(FACING);

        if (!world.isRemote)
        {
            boolean flag = this.shouldBeExtended(world, pos, enumfacing);

            if (flag && eventID == 1)
            {
                world.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
                return false;
            }

            if (!flag && eventID == 0)
            {
                return false;
            }
        }

        if (eventID == 0)
        {
            if (!this.doMove(world, pos, enumfacing, true))
            {
                return false;
            }

            world.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
            world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (eventID == 1)
        {
            TileEntity tileentity1 = world.getTileEntity(pos.offset(enumfacing));

            if (tileentity1 instanceof TileEntityPiston)
            {
                ((TileEntityPiston)tileentity1).clearPistonTileEntity();
            }

            world.setBlockState(pos, Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, enumfacing).withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            world.setTileEntity(pos, BlockPistonMoving.newTileEntity(this.getStateFromMeta(eventParam), enumfacing, false, true));

            if (this.isSticky)
            {
                BlockPos blockpos = pos.add(enumfacing.getFrontOffsetX() * 2, enumfacing.getFrontOffsetY() * 2, enumfacing.getFrontOffsetZ() * 2);
                Block block = world.getBlockState(blockpos).getBlock();
                boolean flag1 = false;

                if (block == Blocks.piston_extension)
                {
                    TileEntity tileentity = world.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityPiston)
                    {
                        TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity;

                        if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending())
                        {
                            tileentitypiston.clearPistonTileEntity();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && block.getMaterial() != Material.air && canPush(block, world, blockpos, enumfacing.getOpposite(), false) && (block.getMobilityFlag() == 0 || block == Blocks.piston || block == Blocks.sticky_piston))
                {
                    this.doMove(world, pos, enumfacing, false);
                }
            }
            else
            {
                world.setBlockToAir(pos.offset(enumfacing));
            }

            world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos);

        if (iblockstate.getBlock() == this && iblockstate.getValue(EXTENDED).booleanValue())
        {
            float f = 0.25F;
            EnumFacing enumfacing = iblockstate.getValue(FACING);

            if (enumfacing != null)
            {
                switch (enumfacing)
                {
                    case DOWN:
                        this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                        break;

                    case UP:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                        break;

                    case NORTH:
                        this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                        break;

                    case SOUTH:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                        break;

                    case WEST:
                        this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                        break;

                    case EAST:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
                }
            }
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     *  
     * @param collidingEntity the Entity colliding with this Block
     */
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state)
    {
        this.setBlockBoundsBasedOnState(world, pos);
        return super.getCollisionBoundingBox(world, pos, state);
    }

    public boolean isFullCube()
    {
        return false;
    }

    public static EnumFacing getFacing(int meta)
    {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public static EnumFacing getFacingFromEntity(World world, BlockPos clickedBlock, EntityLivingBase entityIn)
    {
        if (MathHelper.abs((float)entityIn.posX - (float)clickedBlock.getX()) < 2.0F && MathHelper.abs((float)entityIn.posZ - (float)clickedBlock.getZ()) < 2.0F)
        {
            double d0 = entityIn.posY + (double)entityIn.getEyeHeight();

            if (d0 - (double)clickedBlock.getY() > 2.0D)
            {
                return EnumFacing.UP;
            }

            if ((double)clickedBlock.getY() - d0 > 0.0D)
            {
                return EnumFacing.DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
    }

    public static boolean canPush(Block blockIn, World world, BlockPos pos, EnumFacing direction, boolean allowDestroy)
    {
        if (blockIn == Blocks.obsidian)
        {
            return false;
        }
        else if (!world.getWorldBorder().contains(pos))
        {
            return false;
        }
        else if (pos.getY() >= 0 && (direction != EnumFacing.DOWN || pos.getY() != 0))
        {
            if (pos.getY() <= world.getHeight() - 1 && (direction != EnumFacing.UP || pos.getY() != world.getHeight() - 1))
            {
                if (blockIn != Blocks.piston && blockIn != Blocks.sticky_piston)
                {
                    if (blockIn.getBlockHardness() == -1.0F)
                    {
                        return false;
                    }

                    if (blockIn.getMobilityFlag() == 2)
                    {
                        return false;
                    }

                    if (blockIn.getMobilityFlag() == 1)
                    {
                        return allowDestroy;
                    }
                }
                else if (world.getBlockState(pos).getValue(EXTENDED).booleanValue())
                {
                    return false;
                }

                return !(blockIn instanceof ITileEntityProvider);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean doMove(World world, BlockPos pos, EnumFacing direction, boolean extending)
    {
        if (!extending)
        {
            world.setBlockToAir(pos.offset(direction));
        }

        BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(world, pos, direction, extending);
        List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
        List<BlockPos> list1 = blockpistonstructurehelper.getBlocksToDestroy();

        if (!blockpistonstructurehelper.canMove())
        {
            return false;
        }
        else
        {
            int i = list.size() + list1.size();
            Block[] ablock = new Block[i];
            EnumFacing enumfacing = extending ? direction : direction.getOpposite();

            for (int j = list1.size() - 1; j >= 0; --j)
            {
                BlockPos blockpos = list1.get(j);
                Block block = world.getBlockState(blockpos).getBlock();
                block.dropBlockAsItem(world, blockpos, world.getBlockState(blockpos), 0);
                world.setBlockToAir(blockpos);
                --i;
                ablock[i] = block;
            }

            for (int k = list.size() - 1; k >= 0; --k)
            {
                BlockPos blockpos2 = list.get(k);
                IBlockState iblockstate = world.getBlockState(blockpos2);
                Block block1 = iblockstate.getBlock();
                block1.getMetaFromState(iblockstate);
                world.setBlockToAir(blockpos2);
                blockpos2 = blockpos2.offset(enumfacing);
                world.setBlockState(blockpos2, Blocks.piston_extension.getDefaultState().withProperty(FACING, direction), 4);
                world.setTileEntity(blockpos2, BlockPistonMoving.newTileEntity(iblockstate, direction, extending, false));
                --i;
                ablock[i] = block1;
            }

            BlockPos blockpos1 = pos.offset(direction);

            if (extending)
            {
                BlockPistonExtension.EnumPistonType blockpistonextension$enumpistontype = this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
                IBlockState iblockstate1 = Blocks.piston_head.getDefaultState().withProperty(BlockPistonExtension.FACING, direction).withProperty(BlockPistonExtension.TYPE, blockpistonextension$enumpistontype);
                IBlockState iblockstate2 = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, direction).withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
                world.setBlockState(blockpos1, iblockstate2, 4);
                world.setTileEntity(blockpos1, BlockPistonMoving.newTileEntity(iblockstate1, direction, true, false));
            }

            for (int l = list1.size() - 1; l >= 0; --l)
            {
                world.notifyNeighborsOfStateChange(list1.get(l), ablock[i++]);
            }

            for (int i1 = list.size() - 1; i1 >= 0; --i1)
            {
                world.notifyNeighborsOfStateChange(list.get(i1), ablock[i++]);
            }

            if (extending)
            {
                world.notifyNeighborsOfStateChange(blockpos1, Blocks.piston_head);
                world.notifyNeighborsOfStateChange(pos, this);
            }

            return true;
        }
    }

    /**
     * Possibly modify the given BlockState before rendering it on an Entity (Minecarts, Endermen, ...)
     */
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.UP);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(EXTENDED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(EXTENDED).booleanValue())
        {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING, EXTENDED);
    }
}
