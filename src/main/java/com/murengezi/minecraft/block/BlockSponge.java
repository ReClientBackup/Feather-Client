package com.murengezi.minecraft.block;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

public class BlockSponge extends Block {

    public static final PropertyBool WET = PropertyBool.create("wet");

    protected BlockSponge() {
        super(Material.sponge);
        this.setDefaultState(this.blockState.getBaseState().withProperty(WET, Boolean.FALSE));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".dry.name");
    }

    public int damageDropped(IBlockState state) {
        return state.getValue(WET) ? 1 : 0;
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        this.tryAbsorb(world, pos, state);
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        this.tryAbsorb(world, pos, state);
        super.onNeighborBlockChange(world, pos, state, neighborBlock);
    }

    protected void tryAbsorb(World world, BlockPos pos, IBlockState state) {
        if (!state.getValue(WET) && this.absorb(world, pos)) {
            world.setBlockState(pos, state.withProperty(WET, true), 2);
            world.playAuxSFX(2001, pos, Block.getIdFromBlock(Blocks.water));
        }
    }

    private boolean absorb(World world, BlockPos pos) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        ArrayList<BlockPos> arraylist = Lists.newArrayList();
        queue.add(new Tuple(pos, 0));
        int i = 0;

        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getFirst();
            int j = tuple.getSecond();

            for (EnumFacing enumfacing : EnumFacing.values()) {
                BlockPos blockpos1 = blockpos.offset(enumfacing);

                if (world.getBlockState(blockpos1).getBlock().getMaterial() == Material.water) {
                    world.setBlockState(blockpos1, Blocks.air.getDefaultState(), 2);
                    arraylist.add(blockpos1);
                    ++i;

                    if (j < 6) {
                        queue.add(new Tuple(blockpos1, j + 1));
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        for (BlockPos blockpos2 : arraylist) {
            world.notifyNeighborsOfStateChange(blockpos2, Blocks.air);
        }

        return i > 0;
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(WET, (meta & 1) == 1);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(WET) ? 1 : 0;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, WET);
    }

    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(WET)) {
            EnumFacing enumfacing = EnumFacing.random(rand);

            if (enumfacing != EnumFacing.UP && !World.doesBlockHaveSolidTopSurface(world, pos.offset(enumfacing))) {
                double d0 = pos.getX();
                double d1 = pos.getY();
                double d2 = pos.getZ();

                if (enumfacing == EnumFacing.DOWN) {
                    d1 = d1 - 0.05D;
                    d0 += rand.nextDouble();
                    d2 += rand.nextDouble();
                } else {
                    d1 = d1 + rand.nextDouble() * 0.8D;

                    if (enumfacing.getAxis() == EnumFacing.Axis.X) {
                        d2 += rand.nextDouble();

                        if (enumfacing == EnumFacing.EAST) {
                            ++d0;
                        } else {
                            d0 += 0.05D;
                        }
                    } else {
                        d0 += rand.nextDouble();

                        if (enumfacing == EnumFacing.SOUTH) {
                            ++d2;
                        } else {
                            d2 += 0.05D;
                        }
                    }
                }

                world.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

}
