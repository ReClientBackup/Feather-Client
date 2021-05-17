package com.murengezi.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid {

    int adjacentSourceBlocks;

    protected BlockDynamicLiquid(Material material) {
        super(material);
    }

    private void placeStaticBlock(World world, BlockPos pos, IBlockState currentState) {
        world.setBlockState(pos, getStaticBlock(this.blockMaterial).getDefaultState().withProperty(LEVEL, currentState.getValue(LEVEL)), 2);
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        int i = state.getValue(LEVEL);
        int j = 1;

        if (this.blockMaterial == Material.lava && !world.provider.doesWaterVaporize()) {
            j = 2;
        }

        int k = this.tickRate(world);

        if (i > 0) {
            int l = -100;
            this.adjacentSourceBlocks = 0;

            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                l = this.checkAdjacentBlock(world, pos.offset(enumfacing), l);
            }

            int i1 = l + j;

            if (i1 >= 8 || l < 0) {
                i1 = -1;
            }

            if (this.getLevel(world, pos.up()) >= 0) {
                int j1 = this.getLevel(world, pos.up());

                if (j1 >= 8) {
                    i1 = j1;
                } else {
                    i1 = j1 + 8;
                }
            }

            if (this.adjacentSourceBlocks >= 2 && this.blockMaterial == Material.water) {
                IBlockState iblockstate1 = world.getBlockState(pos.down());

                if (iblockstate1.getBlock().getMaterial().isSolid()) {
                    i1 = 0;
                } else if (iblockstate1.getBlock().getMaterial() == this.blockMaterial && iblockstate1.getValue(LEVEL) == 0) {
                    i1 = 0;
                }
            }

            if (this.blockMaterial == Material.lava && i1 < 8 && i1 > i && rand.nextInt(4) != 0) {
                k *= 4;
            }

            if (i1 == i) {
                this.placeStaticBlock(world, pos, state);
            } else {
                i = i1;

                if (i1 < 0) {
                    world.setBlockToAir(pos);
                } else {
                    state = state.withProperty(LEVEL, i1);
                    world.setBlockState(pos, state, 2);
                    world.scheduleUpdate(pos, this, k);
                    world.notifyNeighborsOfStateChange(pos, this);
                }
            }
        } else {
            this.placeStaticBlock(world, pos, state);
        }

        IBlockState iblockstate = world.getBlockState(pos.down());

        if (this.canFlowInto(world, pos.down(), iblockstate)) {
            if (this.blockMaterial == Material.lava && world.getBlockState(pos.down()).getBlock().getMaterial() == Material.water) {
                world.setBlockState(pos.down(), Blocks.stone.getDefaultState());
                this.triggerMixEffects(world, pos.down());
                return;
            }

            if (i >= 8) {
                this.tryFlowInto(world, pos.down(), iblockstate, i);
            } else {
                this.tryFlowInto(world, pos.down(), iblockstate, i + 8);
            }
        } else if (i >= 0 && (i == 0 || this.isBlocked(world, pos.down()))) {
            Set<EnumFacing> set = this.getPossibleFlowDirections(world, pos);
            int k1 = i + j;

            if (i >= 8) {
                k1 = 1;
            }

            if (k1 >= 8) {
                return;
            }

            for (EnumFacing enumfacing1 : set) {
                this.tryFlowInto(world, pos.offset(enumfacing1), world.getBlockState(pos.offset(enumfacing1)), k1);
            }
        }
    }

    private void tryFlowInto(World world, BlockPos pos, IBlockState state, int level) {
        if (this.canFlowInto(world, pos, state)) {
            if (state.getBlock() != Blocks.air) {
                if (this.blockMaterial == Material.lava) {
                    this.triggerMixEffects(world, pos);
                } else {
                    state.getBlock().dropBlockAsItem(world, pos, state, 0);
                }
            }

            world.setBlockState(pos, this.getDefaultState().withProperty(LEVEL, level), 3);
        }
    }

    private int func_176374_a(World world, BlockPos pos, int distance, EnumFacing calculateFlowCost) {
        int i = 1000;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing != calculateFlowCost) {
                BlockPos blockpos = pos.offset(enumfacing);
                IBlockState iblockstate = world.getBlockState(blockpos);

                if (!this.isBlocked(world, blockpos) && (iblockstate.getBlock().getMaterial() != this.blockMaterial || iblockstate.getValue(LEVEL) > 0)) {
                    if (!this.isBlocked(world, blockpos.down())) {
                        return distance;
                    }

                    if (distance < 4) {
                        int j = this.func_176374_a(world, blockpos, distance + 1, enumfacing.getOpposite());

                        if (j < i) {
                            i = j;
                        }
                    }
                }
            }
        }

        return i;
    }

    private Set<EnumFacing> getPossibleFlowDirections(World world, BlockPos pos) {
        int i = 1000;
        Set<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(enumfacing);
            IBlockState iblockstate = world.getBlockState(blockpos);

            if (!this.isBlocked(world, blockpos) && (iblockstate.getBlock().getMaterial() != this.blockMaterial || iblockstate.getValue(LEVEL) > 0)) {
                int j;

                if (this.isBlocked(world, blockpos.down())) {
                    j = this.func_176374_a(world, blockpos, 1, enumfacing.getOpposite());
                } else {
                    j = 0;
                }

                if (j < i) {
                    set.clear();
                }

                if (j <= i) {
                    set.add(enumfacing);
                    i = j;
                }
            }
        }

        return set;
    }

    private boolean isBlocked(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block instanceof BlockDoor || block == Blocks.standing_sign || block == Blocks.ladder || block == Blocks.reeds || (block.blockMaterial == Material.portal || block.blockMaterial.blocksMovement());
    }

    protected int checkAdjacentBlock(World world, BlockPos pos, int currentMinLevel) {
        int i = this.getLevel(world, pos);

        if (i < 0) {
            return currentMinLevel;
        } else {
            if (i == 0) {
                ++this.adjacentSourceBlocks;
            }

            if (i >= 8) {
                i = 0;
            }

            return currentMinLevel >= 0 && i >= currentMinLevel ? currentMinLevel : i;
        }
    }

    private boolean canFlowInto(World world, BlockPos pos, IBlockState state) {
        Material material = state.getBlock().getMaterial();
        return material != this.blockMaterial && material != Material.lava && !this.isBlocked(world, pos);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!this.checkForMixing(world, pos, state)) {
            world.scheduleUpdate(pos, this, this.tickRate(world));
        }
    }

}
