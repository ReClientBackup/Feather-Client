package com.murengezi.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.init.Blocks;
import com.murengezi.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block {

    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> NORTH = PropertyEnum.create("north", BlockRedstoneWire.EnumAttachPosition.class), EAST = PropertyEnum.create("east", BlockRedstoneWire.EnumAttachPosition.class), SOUTH = PropertyEnum.create("south", BlockRedstoneWire.EnumAttachPosition.class), WEST = PropertyEnum.create("west", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private boolean canProvidePower = true;
    private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

    public BlockRedstoneWire() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(EAST, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(SOUTH, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(WEST, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(POWER, Integer.valueOf(0)));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = state.withProperty(WEST, this.getAttachPosition(world, pos, EnumFacing.WEST));
        state = state.withProperty(EAST, this.getAttachPosition(world, pos, EnumFacing.EAST));
        state = state.withProperty(NORTH, this.getAttachPosition(world, pos, EnumFacing.NORTH));
        state = state.withProperty(SOUTH, this.getAttachPosition(world, pos, EnumFacing.SOUTH));
        return state;
    }

    private BlockRedstoneWire.EnumAttachPosition getAttachPosition(IBlockAccess world, BlockPos pos, EnumFacing direction) {
        BlockPos blockpos = pos.offset(direction);
        Block block = world.getBlockState(pos.offset(direction)).getBlock();

        if (!canConnectTo(world.getBlockState(blockpos), direction) && (block.isBlockNormalCube() || !canConnectUpwardsTo(world.getBlockState(blockpos.down())))) {
            Block block1 = world.getBlockState(pos.up()).getBlock();
            return !block1.isBlockNormalCube() && block.isBlockNormalCube() && canConnectUpwardsTo(world.getBlockState(blockpos.up())) ? BlockRedstoneWire.EnumAttachPosition.UP : BlockRedstoneWire.EnumAttachPosition.NONE;
        } else {
            return BlockRedstoneWire.EnumAttachPosition.SIDE;
        }
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

    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        IBlockState iblockstate = world.getBlockState(pos);
        return iblockstate.getBlock() != this ? super.colorMultiplier(world, pos, renderPass) : this.colorMultiplier(iblockstate.getValue(POWER));
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(world, pos.down()) || world.getBlockState(pos.down()).getBlock() == Blocks.glowstone;
    }

    private IBlockState updateSurroundingRedstone(World world, BlockPos pos, IBlockState state) {
        state = this.calculateCurrentChanges(world, pos, pos, state);
        List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for (BlockPos blockpos : list) {
            world.notifyNeighborsOfStateChange(blockpos, this);
        }

        return state;
    }

    private IBlockState calculateCurrentChanges(World world, BlockPos pos1, BlockPos pos2, IBlockState state) {
        IBlockState iblockstate = state;
        int i = state.getValue(POWER);
        int j = 0;
        j = this.getMaxCurrentStrength(world, pos2, j);
        this.canProvidePower = false;
        int k = world.isBlockIndirectlyGettingPowered(pos1);
        this.canProvidePower = true;

        if (k > 0 && k > j - 1) {
            j = k;
        }

        int l = 0;

        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos1.offset(facing);
            boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();

            if (flag) {
                l = this.getMaxCurrentStrength(world, blockpos, l);
            }

            if (world.getBlockState(blockpos).getBlock().isNormalCube() && !world.getBlockState(pos1.up()).getBlock().isNormalCube()) {
                if (flag && pos1.getY() >= pos2.getY()) {
                    l = this.getMaxCurrentStrength(world, blockpos.up(), l);
                }
            } else if (!world.getBlockState(blockpos).getBlock().isNormalCube() && flag && pos1.getY() <= pos2.getY()) {
                l = this.getMaxCurrentStrength(world, blockpos.down(), l);
            }
        }

        if (l > j) {
            j = l - 1;
        } else if (j > 0) {
            --j;
        } else {
            j = 0;
        }

        if (k > j - 1) {
            j = k;
        }

        if (i != j) {
            state = state.withProperty(POWER, j);

            if (world.getBlockState(pos1) == iblockstate) {
                world.setBlockState(pos1, state, 2);
            }

            this.blocksNeedingUpdate.add(pos1);

            for (EnumFacing enumfacing1 : EnumFacing.values())
            {
                this.blocksNeedingUpdate.add(pos1.offset(enumfacing1));
            }
        }

        return state;
    }

    private void notifyWireNeighborsOfStateChange(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == this) {
            world.notifyNeighborsOfStateChange(pos, this);

            for (EnumFacing facing : EnumFacing.values()) {
                world.notifyNeighborsOfStateChange(pos.offset(facing), this);
            }
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            this.updateSurroundingRedstone(world, pos, state);

            for (EnumFacing enumfacing : EnumFacing.Plane.VERTICAL) {
                world.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }

            for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(world, pos.offset(enumfacing1));
            }

            for (EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.offset(enumfacing2);

                if (world.getBlockState(blockpos).getBlock().isNormalCube()) {
                    this.notifyWireNeighborsOfStateChange(world, blockpos.up());
                } else {
                    this.notifyWireNeighborsOfStateChange(world, blockpos.down());
                }
            }
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);

        if (!world.isRemote) {
            for (EnumFacing enumfacing : EnumFacing.values()) {
                world.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }

            this.updateSurroundingRedstone(world, pos, state);

            for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(world, pos.offset(enumfacing1));
            }

            for (EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.offset(enumfacing2);

                if (world.getBlockState(blockpos).getBlock().isNormalCube()) {
                    this.notifyWireNeighborsOfStateChange(world, blockpos.up());
                } else {
                    this.notifyWireNeighborsOfStateChange(world, blockpos.down());
                }
            }
        }
    }

    private int getMaxCurrentStrength(World world, BlockPos pos, int strength) {
        if (world.getBlockState(pos).getBlock() != this) {
            return strength;
        } else {
            int i = world.getBlockState(pos).getValue(POWER);
            return Math.max(i, strength);
        }
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!world.isRemote) {
            if (this.canPlaceBlockAt(world, pos)) {
                this.updateSurroundingRedstone(world, pos, state);
            } else {
                this.dropBlockAsItem(world, pos, state, 0);
                world.setBlockToAir(pos);
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.redstone;
    }

    public int isProvidingStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        return !this.canProvidePower ? 0 : this.isProvidingWeakPower(world, pos, state, side);
    }

    public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        if (!this.canProvidePower) {
            return 0;
        } else {
            int i = state.getValue(POWER);

            if (i == 0) {
                return 0;
            } else if (side == EnumFacing.UP) {
                return i;
            } else {
                EnumSet<EnumFacing> enumset = EnumSet.noneOf(EnumFacing.class);

                for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
                    if (this.func_176339_d(world, pos, facing)) {
                        enumset.add(facing);
                    }
                }

                if (side.getAxis().isHorizontal() && enumset.isEmpty()) {
                    return i;
                } else if (enumset.contains(side) && !enumset.contains(side.rotateYCCW()) && !enumset.contains(side.rotateY())) {
                    return i;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean func_176339_d(IBlockAccess world, BlockPos pos, EnumFacing side) {
        BlockPos blockpos = pos.offset(side);
        IBlockState iblockstate = world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();
        boolean flag = block.isNormalCube();
        boolean flag1 = world.getBlockState(pos.up()).getBlock().isNormalCube();
        return !flag1 && flag && canConnectUpwardsTo(world, blockpos.up()) || (canConnectTo(iblockstate, side) || (block == Blocks.powered_repeater && iblockstate.getValue(BlockRedstoneDiode.FACING) == side || !flag && canConnectUpwardsTo(world, blockpos.down())));
    }

    protected static boolean canConnectUpwardsTo(IBlockAccess world, BlockPos pos) {
        return canConnectUpwardsTo(world.getBlockState(pos));
    }

    protected static boolean canConnectUpwardsTo(IBlockState state) {
        return canConnectTo(state, null);
    }

    protected static boolean canConnectTo(IBlockState blockState, EnumFacing side) {
        Block block = blockState.getBlock();

        if (block == Blocks.redstone_wire) {
            return true;
        } else if (Blocks.unpowered_repeater.isAssociated(block)) {
            EnumFacing enumfacing = blockState.getValue(BlockRedstoneRepeater.FACING);
            return enumfacing == side || enumfacing.getOpposite() == side;
        } else {
            return block.canProvidePower() && side != null;
        }
    }

    public boolean canProvidePower() {
        return this.canProvidePower;
    }

    private int colorMultiplier(int powerLevel) {
        float f = (float)powerLevel / 15.0F;
        float f1 = f * 0.6F + 0.4F;

        if (powerLevel == 0) {
            f1 = 0.3F;
        }

        float f2 = f * f * 0.7F - 0.5F;
        float f3 = f * f * 0.6F - 0.7F;

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f3 < 0.0F) {
            f3 = 0.0F;
        }

        int i = MathHelper.clamp_int((int)(f1 * 255.0F), 0, 255);
        int j = MathHelper.clamp_int((int)(f2 * 255.0F), 0, 255);
        int k = MathHelper.clamp_int((int)(f3 * 255.0F), 0, 255);
        return -16777216 | i << 16 | j << 8 | k;
    }

    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        int i = state.getValue(POWER);

        if (i != 0) {
            double d0 = (double)pos.getX() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
            double d1 = (float)pos.getY() + 0.0625F;
            double d2 = (double)pos.getZ() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
            float f = (float)i / 15.0F;
            float f1 = f * 0.6F + 0.4F;
            float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
            float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
            world.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, f1, f2, f3);
        }
    }

    public Item getItem(World world, BlockPos pos) {
        return Items.redstone;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWER, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWER);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, NORTH, EAST, SOUTH, WEST, POWER);
    }

    enum EnumAttachPosition implements IStringSerializable {
        UP("up"), SIDE("side"), NONE("none");

        private final String name;

        EnumAttachPosition(String name) {
            this.name = name;
        }

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this.name;
        }
    }

}
