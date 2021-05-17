package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockGrass extends Block implements IGrowable
{
    public static final PropertyBool SNOWY = PropertyBool.create("snowy");

    protected BlockGrass() {
        super(Material.grass);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SNOWY, false));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        Block block = world.getBlockState(pos.up()).getBlock();
        return state.withProperty(SNOWY, block == Blocks.snow || block == Blocks.snow_layer);
    }

    public int getBlockColor() {
        return ColorizerGrass.getGrassColor(0.5D, 1.0D);
    }

    public int getRenderColor(IBlockState state) {
        return this.getBlockColor();
    }

    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        return BiomeColorHelper.getGrassColorAtPos(world, pos);
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            if (world.getLightFromNeighbors(pos.up()) < 4 && world.getBlockState(pos.up()).getBlock().getLightOpacity() > 2) {
                world.setBlockState(pos, Blocks.dirt.getDefaultState());
            } else {
                if (world.getLightFromNeighbors(pos.up()) >= 9) {
                    for (int i = 0; i < 4; ++i) {
                        BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
                        Block block = world.getBlockState(blockpos.up()).getBlock();
                        IBlockState iblockstate = world.getBlockState(blockpos);

                        if (iblockstate.getBlock() == Blocks.dirt && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT && world.getLightFromNeighbors(blockpos.up()) >= 4 && block.getLightOpacity() <= 2) {
                            world.setBlockState(blockpos, Blocks.grass.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
    }

    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        BlockPos blockpos = pos.up();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    if (world.getBlockState(blockpos1).getBlock().blockMaterial == Material.air) {
                        if (rand.nextInt(8) == 0) {
                            BlockFlower.EnumFlowerType flowerType = world.getBiomeGenForCoords(blockpos1).pickRandomFlower(rand, blockpos1);
                            BlockFlower blockflower = flowerType.getBlockType().getBlock();
                            IBlockState iblockstate = blockflower.getDefaultState().withProperty(blockflower.getTypeProperty(), flowerType);

                            if (blockflower.canBlockStay(world, blockpos1, iblockstate)) {
                                world.setBlockState(blockpos1, iblockstate, 3);
                            }
                        } else {
                            IBlockState iblockstate1 = Blocks.tallgrass.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);

                            if (Blocks.tallgrass.canBlockStay(world, blockpos1, iblockstate1)) {
                                world.setBlockState(blockpos1, iblockstate1, 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (world.getBlockState(blockpos1.down()).getBlock() != Blocks.grass || world.getBlockState(blockpos1).getBlock().isNormalCube()) {
                    break;
                }

                ++j;
            }
        }
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, SNOWY);
    }

}
