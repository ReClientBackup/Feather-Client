package com.murengezi.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.IProperty;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {

    protected final boolean isPowered;

    public static boolean isRailBlock(World world, BlockPos pos) {
        return isRailBlock(world.getBlockState(pos));
    }

    public static boolean isRailBlock(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.rail || block == Blocks.golden_rail || block == Blocks.detector_rail || block == Blocks.activator_rail;
    }

    protected BlockRailBase(boolean isPowered) {
        super(Material.circuits);
        this.isPowered = isPowered;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
        this.setBlockBoundsBasedOnState(world, pos);
        return super.collisionRayTrace(world, pos, start, end);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        IBlockState iblockstate = world.getBlockState(pos);
        BlockRailBase.EnumRailDirection railDirection = iblockstate.getBlock() == this ? iblockstate.getValue(this.getShapeProperty()) : null;

        if (railDirection != null && railDirection.isAscending()) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        }
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(world, pos.down());
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            state = this.func_176564_a(world, pos, state, true);

            if (this.isPowered) {
                this.onNeighborBlockChange(world, pos, state, this);
            }
        }
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!world.isRemote) {
            BlockRailBase.EnumRailDirection railDirection = state.getValue(this.getShapeProperty());
            boolean flag = !World.doesBlockHaveSolidTopSurface(world, pos.down());

            if (railDirection == BlockRailBase.EnumRailDirection.ASCENDING_EAST && !World.doesBlockHaveSolidTopSurface(world, pos.east())) {
                flag = true;
            } else if (railDirection == BlockRailBase.EnumRailDirection.ASCENDING_WEST && !World.doesBlockHaveSolidTopSurface(world, pos.west())) {
                flag = true;
            } else if (railDirection == BlockRailBase.EnumRailDirection.ASCENDING_NORTH && !World.doesBlockHaveSolidTopSurface(world, pos.north())) {
                flag = true;
            } else if (railDirection == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH && !World.doesBlockHaveSolidTopSurface(world, pos.south())) {
                flag = true;
            }

            if (flag) {
                this.dropBlockAsItem(world, pos, state, 0);
                world.setBlockToAir(pos);
            } else {
                this.onNeighborChangedInternal(world, pos, state, neighborBlock);
            }
        }
    }

    protected void onNeighborChangedInternal(World world, BlockPos pos, IBlockState state, Block neighborBlock) {}

    protected IBlockState func_176564_a(World world, BlockPos blockPos, IBlockState blockState, boolean p_176564_4_) {
        return world.isRemote ? blockState : (new BlockRailBase.Rail(world, blockPos, blockState)).func_180364_a(world.isBlockPowered(blockPos), p_176564_4_).getBlockState();
    }

    public int getMobilityFlag() {
        return 0;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);

        if (state.getValue(this.getShapeProperty()).isAscending()) {
            world.notifyNeighborsOfStateChange(pos.up(), this);
        }

        if (this.isPowered) {
            world.notifyNeighborsOfStateChange(pos, this);
            world.notifyNeighborsOfStateChange(pos.down(), this);
        }
    }

    public abstract IProperty<BlockRailBase.EnumRailDirection> getShapeProperty();

    public enum EnumRailDirection implements IStringSerializable {
        NORTH_SOUTH(0, "north_south"), EAST_WEST(1, "east_west"), ASCENDING_EAST(2, "ascending_east"), ASCENDING_WEST(3, "ascending_west"), ASCENDING_NORTH(4, "ascending_north"), ASCENDING_SOUTH(5, "ascending_south"), SOUTH_EAST(6, "south_east"), SOUTH_WEST(7, "south_west"), NORTH_WEST(8, "north_west"), NORTH_EAST(9, "north_east");

        private static final BlockRailBase.EnumRailDirection[] META_LOOKUP = new BlockRailBase.EnumRailDirection[values().length];
        private final int meta;
        private final String name;

        EnumRailDirection(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String toString() {
            return this.name;
        }

        public boolean isAscending() {
            return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
        }

        public static BlockRailBase.EnumRailDirection byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName() {
            return this.name;
        }

        static {
            for (BlockRailBase.EnumRailDirection railDirection : values()) {
                META_LOOKUP[railDirection.getMetadata()] = railDirection;
            }
        }
    }

    public class Rail {

        private final World world;
        private final BlockPos pos;
        private final BlockRailBase block;
        private IBlockState state;
        private final boolean isPowered;
        private final List<BlockPos> field_150657_g = Lists.newArrayList();

        public Rail(World world, BlockPos pos, IBlockState state) {
            this.world = world;
            this.pos = pos;
            this.state = state;
            this.block = (BlockRailBase)state.getBlock();
            BlockRailBase.EnumRailDirection railDirection = state.getValue(BlockRailBase.this.getShapeProperty());
            this.isPowered = this.block.isPowered;
            this.func_180360_a(railDirection);
        }

        private void func_180360_a(BlockRailBase.EnumRailDirection railDirection) {
            this.field_150657_g.clear();

            switch (railDirection) {
                case NORTH_SOUTH:
                    this.field_150657_g.add(this.pos.north());
                    this.field_150657_g.add(this.pos.south());
                    break;
                case EAST_WEST:
                    this.field_150657_g.add(this.pos.west());
                    this.field_150657_g.add(this.pos.east());
                    break;
                case ASCENDING_EAST:
                    this.field_150657_g.add(this.pos.west());
                    this.field_150657_g.add(this.pos.east().up());
                    break;
                case ASCENDING_WEST:
                    this.field_150657_g.add(this.pos.west().up());
                    this.field_150657_g.add(this.pos.east());
                    break;
                case ASCENDING_NORTH:
                    this.field_150657_g.add(this.pos.north().up());
                    this.field_150657_g.add(this.pos.south());
                    break;
                case ASCENDING_SOUTH:
                    this.field_150657_g.add(this.pos.north());
                    this.field_150657_g.add(this.pos.south().up());
                    break;
                case SOUTH_EAST:
                    this.field_150657_g.add(this.pos.east());
                    this.field_150657_g.add(this.pos.south());
                    break;
                case SOUTH_WEST:
                    this.field_150657_g.add(this.pos.west());
                    this.field_150657_g.add(this.pos.south());
                    break;
                case NORTH_WEST:
                    this.field_150657_g.add(this.pos.west());
                    this.field_150657_g.add(this.pos.north());
                    break;
                case NORTH_EAST:
                    this.field_150657_g.add(this.pos.east());
                    this.field_150657_g.add(this.pos.north());
            }
        }

        private void func_150651_b() {
            for (int i = 0; i < this.field_150657_g.size(); ++i) {
                BlockRailBase.Rail rail = this.findRailAt(this.field_150657_g.get(i));

                if (rail != null && rail.func_150653_a(this)) {
                    this.field_150657_g.set(i, rail.pos);
                } else {
                    this.field_150657_g.remove(i--);
                }
            }
        }

        private boolean hasRailAt(BlockPos pos) {
            return BlockRailBase.isRailBlock(this.world, pos) || BlockRailBase.isRailBlock(this.world, pos.up()) || BlockRailBase.isRailBlock(this.world, pos.down());
        }

        private BlockRailBase.Rail findRailAt(BlockPos pos) {
            IBlockState iblockstate = this.world.getBlockState(pos);

            if (BlockRailBase.isRailBlock(iblockstate)) {
                return BlockRailBase.this.new Rail(this.world, pos, iblockstate);
            } else {
                BlockPos lvt_2_1_ = pos.up();
                iblockstate = this.world.getBlockState(lvt_2_1_);

                if (BlockRailBase.isRailBlock(iblockstate)) {
                    return BlockRailBase.this.new Rail(this.world, lvt_2_1_, iblockstate);
                } else {
                    lvt_2_1_ = pos.down();
                    iblockstate = this.world.getBlockState(lvt_2_1_);
                    return BlockRailBase.isRailBlock(iblockstate) ? BlockRailBase.this.new Rail(this.world, lvt_2_1_, iblockstate) : null;
                }
            }
        }

        private boolean func_150653_a(BlockRailBase.Rail rail) {
            return this.func_180363_c(rail.pos);
        }

        private boolean func_180363_c(BlockPos blockPos) {
            for (BlockPos blockpos : this.field_150657_g) {
                if (blockpos.getX() == blockPos.getX() && blockpos.getZ() == blockPos.getZ()) {
                    return true;
                }
            }

            return false;
        }

        protected int countAdjacentRails() {
            int i = 0;

            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                if (this.hasRailAt(this.pos.offset(enumfacing))) {
                    ++i;
                }
            }

            return i;
        }

        private boolean func_150649_b(BlockRailBase.Rail rail) {
            return this.func_150653_a(rail) || this.field_150657_g.size() != 2;
        }

        private void func_150645_c(BlockRailBase.Rail rail) {
            this.field_150657_g.add(rail.pos);
            BlockPos northPos = this.pos.north();
            BlockPos southPos = this.pos.south();
            BlockPos westPos = this.pos.west();
            BlockPos eastPos = this.pos.east();
            boolean flag = this.func_180363_c(northPos);
            boolean flag1 = this.func_180363_c(southPos);
            boolean flag2 = this.func_180363_c(westPos);
            boolean flag3 = this.func_180363_c(eastPos);
            BlockRailBase.EnumRailDirection railDirection = null;

            if (flag || flag1) {
                railDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if (flag2 || flag3) {
                railDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            railDirection = getEnumRailDirection(flag, flag1, flag2, flag3, railDirection);

            railDirection = getEnumRailDirection(northPos, southPos, westPos, eastPos, railDirection);

            this.state = this.state.withProperty(this.block.getShapeProperty(), railDirection);
            this.world.setBlockState(this.pos, this.state, 3);
        }

        private EnumRailDirection getEnumRailDirection(BlockPos northPos, BlockPos southPos, BlockPos westPos, BlockPos eastPos, EnumRailDirection railDirection) {
            if (railDirection == EnumRailDirection.NORTH_SOUTH) {
                if (BlockRailBase.isRailBlock(this.world, northPos.up())) {
                    railDirection = EnumRailDirection.ASCENDING_NORTH;
                }

                if (BlockRailBase.isRailBlock(this.world, southPos.up())) {
                    railDirection = EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (railDirection == EnumRailDirection.EAST_WEST) {
                if (BlockRailBase.isRailBlock(this.world, eastPos.up())) {
                    railDirection = EnumRailDirection.ASCENDING_EAST;
                }

                if (BlockRailBase.isRailBlock(this.world, westPos.up())) {
                    railDirection = EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (railDirection == null) {
                railDirection = EnumRailDirection.NORTH_SOUTH;
            }
            return railDirection;
        }

        private EnumRailDirection getEnumRailDirection(boolean flag, boolean flag1, boolean flag2, boolean flag3, EnumRailDirection railDirection) {
            if (!this.isPowered) {
                if (flag1 && flag3 && !flag && !flag2) {
                    railDirection = EnumRailDirection.SOUTH_EAST;
                }

                if (flag1 && flag2 && !flag && !flag3) {
                    railDirection = EnumRailDirection.SOUTH_WEST;
                }

                if (flag && flag2 && !flag1 && !flag3) {
                    railDirection = EnumRailDirection.NORTH_WEST;
                }

                if (flag && flag3 && !flag1 && !flag2) {
                    railDirection = EnumRailDirection.NORTH_EAST;
                }
            }
            return railDirection;
        }

        private boolean func_180361_d(BlockPos blockPos) {
            BlockRailBase.Rail rail = this.findRailAt(blockPos);

            if (rail == null) {
                return false;
            } else {
                rail.func_150651_b();
                return rail.func_150649_b(this);
            }
        }

        public BlockRailBase.Rail func_180364_a(boolean p_180364_1_, boolean p_180364_2_) {
            BlockPos northPos = this.pos.north();
            BlockPos southPos = this.pos.south();
            BlockPos westPos = this.pos.west();
            BlockPos eastPos = this.pos.east();
            boolean flag = this.func_180361_d(northPos);
            boolean flag1 = this.func_180361_d(southPos);
            boolean flag2 = this.func_180361_d(westPos);
            boolean flag3 = this.func_180361_d(eastPos);
            BlockRailBase.EnumRailDirection railDirection = null;

            if ((flag || flag1) && !flag2 && !flag3) {
                railDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if ((flag2 || flag3) && !flag && !flag1) {
                railDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            railDirection = getEnumRailDirection(flag, flag1, flag2, flag3, railDirection);

            if (railDirection == null) {
                if (flag || flag1) {
                    railDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                }

                if (flag2 || flag3) {
                    railDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
                }

                if (!this.isPowered) {
                    if (p_180364_1_) {
                        if (flag1 && flag3) {
                            railDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                        }

                        if (flag2 && flag1) {
                            railDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                        }

                        if (flag3 && flag) {
                            railDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                        }

                        if (flag && flag2) {
                            railDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                        }
                    } else {
                        if (flag && flag2) {
                            railDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                        }

                        if (flag3 && flag) {
                            railDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                        }

                        if (flag2 && flag1) {
                            railDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                        }

                        if (flag1 && flag3) {
                            railDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                        }
                    }
                }
            }

            railDirection = getEnumRailDirection(northPos, southPos, westPos, eastPos, railDirection);

            this.func_180360_a(railDirection);
            this.state = this.state.withProperty(this.block.getShapeProperty(), railDirection);

            if (p_180364_2_ || this.world.getBlockState(this.pos) != this.state) {
                this.world.setBlockState(this.pos, this.state, 3);

                for (int i = 0; i < this.field_150657_g.size(); ++i) {
                    BlockRailBase.Rail blockrailbase$rail = this.findRailAt(this.field_150657_g.get(i));

                    if (blockrailbase$rail != null) {
                        blockrailbase$rail.func_150651_b();

                        if (blockrailbase$rail.func_150649_b(this)) {
                            blockrailbase$rail.func_150645_c(this);
                        }
                    }
                }
            }

            return this;
        }

        public IBlockState getBlockState() {
            return this.state;
        }
    }

}
