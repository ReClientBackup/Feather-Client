package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyDirection;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {

    public static final PropertyDirection FACING = BlockPistonExtension.FACING;
    public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;

    public BlockPistonMoving() {
        super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT));
        this.setHardness(-1.0F);
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return null;
    }

    public static TileEntity newTileEntity(IBlockState state, EnumFacing facing, boolean extending, boolean renderHead) {
        return new TileEntityPiston(state, facing, extending, renderHead);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityPiston) {
            ((TileEntityPiston)tileentity).clearPistonTileEntity();
        } else {
            super.breakBlock(world, pos, state);
        }
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return false;
    }

    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return false;
    }

    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());
        IBlockState iblockstate = world.getBlockState(blockpos);

        if (iblockstate.getBlock() instanceof BlockPistonBase && iblockstate.getValue(BlockPistonBase.EXTENDED))
        {
            world.setBlockToAir(blockpos);
        }
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && world.getTileEntity(pos) == null) {
            world.setBlockToAir(pos);
            return true;
        } else {
            return false;
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (!world.isRemote) {
            TileEntityPiston tileentitypiston = this.getTileEntity(world, pos);

            if (tileentitypiston != null) {
                IBlockState iblockstate = tileentitypiston.getPistonState();
                iblockstate.getBlock().dropBlockAsItem(world, pos, iblockstate, 0);
            }
        }
    }

    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
        return null;
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!world.isRemote) {
            world.getTileEntity(pos);
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        TileEntityPiston tileentitypiston = this.getTileEntity(world, pos);

        if (tileentitypiston == null) {
            return null;
        } else {
            float f = tileentitypiston.getProgress(0.0F);

            if (tileentitypiston.isExtending()) {
                f = 1.0F - f;
            }

            return this.getBoundingBox(world, pos, tileentitypiston.getPistonState(), f, tileentitypiston.getFacing());
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        TileEntityPiston tileentitypiston = this.getTileEntity(world, pos);

        if (tileentitypiston != null) {
            IBlockState iblockstate = tileentitypiston.getPistonState();
            Block block = iblockstate.getBlock();

            if (block == this || block.getMaterial() == Material.air) {
                return;
            }

            float f = tileentitypiston.getProgress(0.0F);

            if (tileentitypiston.isExtending()) {
                f = 1.0F - f;
            }

            block.setBlockBoundsBasedOnState(world, pos);

            if (block == Blocks.piston || block == Blocks.sticky_piston) {
                f = 0.0F;
            }

            EnumFacing enumfacing = tileentitypiston.getFacing();
            this.minX = block.getBlockBoundsMinX() - (double)((float)enumfacing.getFrontOffsetX() * f);
            this.minY = block.getBlockBoundsMinY() - (double)((float)enumfacing.getFrontOffsetY() * f);
            this.minZ = block.getBlockBoundsMinZ() - (double)((float)enumfacing.getFrontOffsetZ() * f);
            this.maxX = block.getBlockBoundsMaxX() - (double)((float)enumfacing.getFrontOffsetX() * f);
            this.maxY = block.getBlockBoundsMaxY() - (double)((float)enumfacing.getFrontOffsetY() * f);
            this.maxZ = block.getBlockBoundsMaxZ() - (double)((float)enumfacing.getFrontOffsetZ() * f);
        }
    }

    public AxisAlignedBB getBoundingBox(World world, BlockPos pos, IBlockState extendingBlock, float progress, EnumFacing direction) {
        if (extendingBlock.getBlock() != this && extendingBlock.getBlock().getMaterial() != Material.air) {
            AxisAlignedBB axisalignedbb = extendingBlock.getBlock().getCollisionBoundingBox(world, pos, extendingBlock);

            if (axisalignedbb == null) {
                return null;
            } else {
                double d0 = axisalignedbb.minX;
                double d1 = axisalignedbb.minY;
                double d2 = axisalignedbb.minZ;
                double d3 = axisalignedbb.maxX;
                double d4 = axisalignedbb.maxY;
                double d5 = axisalignedbb.maxZ;

                if (direction.getFrontOffsetX() < 0) {
                    d0 -= (float)direction.getFrontOffsetX() * progress;
                } else {
                    d3 -= (float)direction.getFrontOffsetX() * progress;
                }

                if (direction.getFrontOffsetY() < 0) {
                    d1 -= (float)direction.getFrontOffsetY() * progress;
                } else {
                    d4 -= (float)direction.getFrontOffsetY() * progress;
                }

                if (direction.getFrontOffsetZ() < 0) {
                    d2 -= (float)direction.getFrontOffsetZ() * progress;
                } else {
                    d5 -= (float)direction.getFrontOffsetZ() * progress;
                }

                return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
            }
        } else {
            return null;
        }
    }

    private TileEntityPiston getTileEntity(IBlockAccess world, BlockPos pos) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity instanceof TileEntityPiston ? (TileEntityPiston)tileentity : null;
    }

    public Item getItem(World world, BlockPos pos) {
        return null;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, BlockPistonExtension.getFacing(meta)).withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, TYPE);
    }

}
