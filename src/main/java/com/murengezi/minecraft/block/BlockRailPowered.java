package com.murengezi.minecraft.block;

import com.murengezi.minecraft.block.properties.IProperty;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase {

    public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, p_apply_1_ -> p_apply_1_ != EnumRailDirection.NORTH_EAST && p_apply_1_ != EnumRailDirection.NORTH_WEST && p_apply_1_ != EnumRailDirection.SOUTH_EAST && p_apply_1_ != EnumRailDirection.SOUTH_WEST);
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    protected BlockRailPowered() {
        super(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(POWERED, false));
    }

    protected boolean func_176566_a(World world, BlockPos pos, IBlockState state, boolean p_176566_4_, int p_176566_5_) {
        if (p_176566_5_ >= 8) {
            return false;
        } else {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            boolean flag = true;
            BlockRailBase.EnumRailDirection railDirection = state.getValue(SHAPE);

            switch (railDirection) {
                case NORTH_SOUTH:
                    if (p_176566_4_)
                    {
                        ++k;
                    } else {
                        --k;
                    }

                    break;
                case EAST_WEST:
                    if (p_176566_4_) {
                        --i;
                    } else {
                        ++i;
                    }

                    break;
                case ASCENDING_EAST:
                    if (p_176566_4_) {
                        --i;
                    } else {
                        ++i;
                        ++j;
                        flag = false;
                    }

                    railDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
                    break;
                case ASCENDING_WEST:
                    if (p_176566_4_) {
                        --i;
                        ++j;
                        flag = false;
                    } else {
                        ++i;
                    }

                    railDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
                    break;
                case ASCENDING_NORTH:
                    if (p_176566_4_) {
                        ++k;
                    } else {
                        --k;
                        ++j;
                        flag = false;
                    }

                    railDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                    break;
                case ASCENDING_SOUTH:
                    if (p_176566_4_) {
                        ++k;
                        ++j;
                        flag = false;
                    } else {
                        --k;
                    }

                    railDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            return this.func_176567_a(world, new BlockPos(i, j, k), p_176566_4_, p_176566_5_, railDirection) || flag && this.func_176567_a(world, new BlockPos(i, j - 1, k), p_176566_4_, p_176566_5_, railDirection);
        }
    }

    protected boolean func_176567_a(World world, BlockPos blockPos, boolean p_176567_3_, int distance, BlockRailBase.EnumRailDirection railDirection) {
        IBlockState iblockstate = world.getBlockState(blockPos);

        if (iblockstate.getBlock() != this) {
            return false;
        } else {
            BlockRailBase.EnumRailDirection railDirection1 = iblockstate.getValue(SHAPE);
            return (railDirection != EnumRailDirection.EAST_WEST || railDirection1 != EnumRailDirection.NORTH_SOUTH && railDirection1 != EnumRailDirection.ASCENDING_NORTH && railDirection1 != EnumRailDirection.ASCENDING_SOUTH) && ((railDirection != EnumRailDirection.NORTH_SOUTH || railDirection1 != EnumRailDirection.EAST_WEST && railDirection1 != EnumRailDirection.ASCENDING_EAST && railDirection1 != EnumRailDirection.ASCENDING_WEST) && (iblockstate.getValue(POWERED) && (world.isBlockPowered(blockPos) || this.func_176566_a(world, blockPos, iblockstate, p_176567_3_, distance + 1))));
        }
    }

    protected void onNeighborChangedInternal(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        boolean flag = state.getValue(POWERED);
        boolean flag1 = world.isBlockPowered(pos) || this.func_176566_a(world, pos, state, true, 0) || this.func_176566_a(world, pos, state, false, 0);

        if (flag1 != flag) {
            world.setBlockState(pos, state.withProperty(POWERED, flag1), 3);
            world.notifyNeighborsOfStateChange(pos.down(), this);
            if (state.getValue(SHAPE).isAscending()) {
                world.notifyNeighborsOfStateChange(pos.up(), this);
            }
        }
    }

    public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
        return SHAPE;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, (meta & 8) > 0);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(SHAPE).getMetadata();

        if (state.getValue(POWERED)) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, SHAPE, POWERED);
    }

}
