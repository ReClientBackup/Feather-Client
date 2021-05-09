package com.murengezi.minecraft.block;

import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional {

    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");

    public BlockFenceGate(BlockPlanks.EnumType p_i46394_1_) {
        super(Material.wood, p_i46394_1_.getMapColor());
        this.setDefaultState(this.blockState.getBaseState().withProperty(OPEN, false).withProperty(POWERED, false).withProperty(IN_WALL, false));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing.Axis axis = state.getValue(FACING).getAxis();

        if (axis == EnumFacing.Axis.Z && (world.getBlockState(pos.west()).getBlock() == Blocks.cobblestone_wall || world.getBlockState(pos.east()).getBlock() == Blocks.cobblestone_wall) || axis == EnumFacing.Axis.X && (world.getBlockState(pos.north()).getBlock() == Blocks.cobblestone_wall || world.getBlockState(pos.south()).getBlock() == Blocks.cobblestone_wall)) {
            state = state.withProperty(IN_WALL, true);
        }

        return state;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos.down()).getBlock().getMaterial().isSolid() && super.canPlaceBlockAt(world, pos);
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        if (state.getValue(OPEN)) {
            return null;
        } else {
            EnumFacing.Axis axis = state.getValue(FACING).getAxis();
            return axis == EnumFacing.Axis.Z ? new AxisAlignedBB(pos.getX(), pos.getY(), (float)pos.getZ() + 0.375F, pos.getX() + 1, (float)pos.getY() + 1.5F, (float)pos.getZ() + 0.625F) : new AxisAlignedBB((float)pos.getX() + 0.375F, pos.getY(), pos.getZ(), (float)pos.getX() + 0.625F, (float)pos.getY() + 1.5F, pos.getZ() + 1);
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        EnumFacing.Axis axis = world.getBlockState(pos).getValue(FACING).getAxis();

        if (axis == EnumFacing.Axis.Z) {
            this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        } else {
            this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        }
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(OPEN);
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(OPEN, false).withProperty(POWERED, false).withProperty(IN_WALL, false);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (state.getValue(OPEN)) {
            state = state.withProperty(OPEN, false);
        } else {
            EnumFacing enumfacing = EnumFacing.fromAngle(player.rotationYaw);

            if (state.getValue(FACING) == enumfacing.getOpposite()) {
                state = state.withProperty(FACING, enumfacing);
            }

            state = state.withProperty(OPEN, true);
        }
        world.setBlockState(pos, state, 2);

        world.playAuxSFXAtEntity(player, state.getValue(OPEN) ? 1003 : 1006, pos, 0);
        return true;
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!world.isRemote) {
            boolean flag = world.isBlockPowered(pos);

            if (flag || neighborBlock.canProvidePower()) {
                if (flag && !state.getValue(OPEN) && !state.getValue(POWERED)) {
                    world.setBlockState(pos, state.withProperty(OPEN, true).withProperty(POWERED, true), 2);
                    world.playAuxSFXAtEntity(null, 1003, pos, 0);
                } else if (!flag && state.getValue(OPEN) && state.getValue(POWERED)) {
                    world.setBlockState(pos, state.withProperty(OPEN, false).withProperty(POWERED, false), 2);
                    world.playAuxSFXAtEntity(null, 1006, pos, 0);
                } else if (flag != state.getValue(POWERED)) {
                    world.setBlockState(pos, state.withProperty(POWERED, flag), 2);
                }
            }
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(OPEN, (meta & 4) != 0).withProperty(POWERED, (meta & 8) != 0);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(POWERED)) {
            i |= 8;
        }

        if (state.getValue(OPEN)) {
            i |= 4;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, OPEN, POWERED, IN_WALL);
    }

}
