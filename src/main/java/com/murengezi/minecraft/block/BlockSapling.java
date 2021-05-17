package com.murengezi.minecraft.block;

import java.util.List;
import java.util.Random;

import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockSapling extends BlockBush implements IGrowable {

    public static final PropertyEnum<BlockPlanks.EnumType> TYPE = PropertyEnum.create("type", BlockPlanks.EnumType.class);
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);

    protected BlockSapling() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockPlanks.EnumType.OAK).withProperty(STAGE, 0));
        float f = 0.4F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockPlanks.EnumType.OAK.getUnlocalizedName() + ".name");
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            super.updateTick(world, pos, state, rand);

            if (world.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
                this.grow(world, pos, state, rand);
            }
        }
    }

    public void grow(World world, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(STAGE) == 0) {
            world.setBlockState(pos, state.cycleProperty(STAGE), 4);
        } else {
            this.generateTree(world, pos, state, rand);
        }
    }

    public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
        WorldGenerator worldgenerator = rand.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
        int i = 0;
        int j = 0;
        boolean flag = false;

        switch (state.getValue(TYPE)) {
            case SPRUCE:
                label114:
                for (i = 0; i >= -1; --i)
                {
                    for (j = 0; j >= -1; --j)
                    {
                        if (this.func_181624_a(world, pos, i, j, BlockPlanks.EnumType.SPRUCE))
                        {
                            worldgenerator = new WorldGenMegaPineTree(false, rand.nextBoolean());
                            flag = true;
                            break label114;
                        }
                    }
                }

                if (!flag)
                {
                    j = 0;
                    i = 0;
                    worldgenerator = new WorldGenTaiga2(true);
                }

                break;
            case BIRCH:
                worldgenerator = new WorldGenForest(true, false);
                break;
            case JUNGLE:
                IBlockState iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
                IBlockState iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, false);
                label269:

                for (i = 0; i >= -1; --i)
                {
                    for (j = 0; j >= -1; --j)
                    {
                        if (this.func_181624_a(world, pos, i, j, BlockPlanks.EnumType.JUNGLE))
                        {
                            worldgenerator = new WorldGenMegaJungle(true, 10, 20, iblockstate, iblockstate1);
                            flag = true;
                            break label269;
                        }
                    }
                }

                if (!flag)
                {
                    j = 0;
                    i = 0;
                    worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
                }

                break;
            case ACACIA:
                worldgenerator = new WorldGenSavannaTree(true);
                break;
            case DARK_OAK:
                label390:
                for (i = 0; i >= -1; --i)
                {
                    for (j = 0; j >= -1; --j)
                    {
                        if (this.func_181624_a(world, pos, i, j, BlockPlanks.EnumType.DARK_OAK))
                        {
                            worldgenerator = new WorldGenCanopyTree(true);
                            flag = true;
                            break label390;
                        }
                    }
                }

                if (!flag)
                {
                    return;
                }

            case OAK:
        }

        IBlockState iblockstate2 = Blocks.air.getDefaultState();

        if (flag) {
            world.setBlockState(pos.add(i, 0, j), iblockstate2, 4);
            world.setBlockState(pos.add(i + 1, 0, j), iblockstate2, 4);
            world.setBlockState(pos.add(i, 0, j + 1), iblockstate2, 4);
            world.setBlockState(pos.add(i + 1, 0, j + 1), iblockstate2, 4);
        } else {
            world.setBlockState(pos, iblockstate2, 4);
        }

        if (!worldgenerator.generate(world, rand, pos.add(i, 0, j))) {
            if (flag) {
                world.setBlockState(pos.add(i, 0, j), state, 4);
                world.setBlockState(pos.add(i + 1, 0, j), state, 4);
                world.setBlockState(pos.add(i, 0, j + 1), state, 4);
                world.setBlockState(pos.add(i + 1, 0, j + 1), state, 4);
            } else {
                world.setBlockState(pos, state, 4);
            }
        }
    }

    private boolean func_181624_a(World world, BlockPos blockPos, int p_181624_3_, int p_181624_4_, BlockPlanks.EnumType type) {
        return this.isTypeAt(world, blockPos.add(p_181624_3_, 0, p_181624_4_), type) && this.isTypeAt(world, blockPos.add(p_181624_3_ + 1, 0, p_181624_4_), type) && this.isTypeAt(world, blockPos.add(p_181624_3_, 0, p_181624_4_ + 1), type) && this.isTypeAt(world, blockPos.add(p_181624_3_ + 1, 0, p_181624_4_ + 1), type);
    }

    public boolean isTypeAt(World world, BlockPos pos, BlockPlanks.EnumType type) {
        IBlockState iblockstate = world.getBlockState(pos);
        return iblockstate.getBlock() == this && iblockstate.getValue(TYPE) == type;
    }

    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getMetadata();
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            list.add(new ItemStack(item, 1, type.getMetadata()));
        }
    }

    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return (double)world.rand.nextFloat() < 0.45D;
    }

    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        this.grow(world, pos, state, rand);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, BlockPlanks.EnumType.byMetadata(meta & 7)).withProperty(STAGE, (meta & 8) >> 3);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(TYPE).getMetadata();
        i = i | state.getValue(STAGE) << 3;
        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, TYPE, STAGE);
    }

}
