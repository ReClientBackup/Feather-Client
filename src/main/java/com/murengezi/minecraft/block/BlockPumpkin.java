package com.murengezi.minecraft.block;

import com.google.common.base.Predicate;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.BlockWorldState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.block.state.pattern.BlockPattern;
import com.murengezi.minecraft.block.state.pattern.BlockStateHelper;
import com.murengezi.minecraft.block.state.pattern.FactoryBlockPattern;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockDirectional {

    private BlockPattern snowmanBasePattern, snowmanPattern, golemBasePattern, golemPattern;
    private static final Predicate<IBlockState> field_181085_Q = p_apply_1_ -> p_apply_1_ != null && (p_apply_1_.getBlock() == Blocks.pumpkin || p_apply_1_.getBlock() == Blocks.lit_pumpkin);

    protected BlockPumpkin() {
        super(Material.gourd, MapColor.adobeColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        this.trySpawnGolem(world, pos);
    }

    public boolean canDispenserPlace(World world, BlockPos pos) {
        return this.getSnowmanBasePattern().match(world, pos) != null || this.getGolemBasePattern().match(world, pos) != null;
    }

    private void trySpawnGolem(World world, BlockPos pos) {
        BlockPattern.PatternHelper patternHelper;

        if ((patternHelper = this.getSnowmanPattern().match(world, pos)) != null) {
            for (int i = 0; i < this.getSnowmanPattern().getThumbLength(); ++i) {
                BlockWorldState blockworldstate = patternHelper.translateOffset(0, i, 0);
                world.setBlockState(blockworldstate.getPos(), Blocks.air.getDefaultState(), 2);
            }

            EntitySnowman entitysnowman = new EntitySnowman(world);
            BlockPos blockpos1 = patternHelper.translateOffset(0, 2, 0).getPos();
            entitysnowman.setLocationAndAngles((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.05D, (double)blockpos1.getZ() + 0.5D, 0.0F, 0.0F);
            world.spawnEntityInWorld(entitysnowman);

            for (int j = 0; j < 120; ++j) {
                world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double)blockpos1.getX() + world.rand.nextDouble(), (double)blockpos1.getY() + world.rand.nextDouble() * 2.5D, (double)blockpos1.getZ() + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

            for (int i1 = 0; i1 < this.getSnowmanPattern().getThumbLength(); ++i1) {
                BlockWorldState blockworldstate1 = patternHelper.translateOffset(0, i1, 0);
                world.notifyNeighborsRespectDebug(blockworldstate1.getPos(), Blocks.air);
            }
        } else if ((patternHelper = this.getGolemPattern().match(world, pos)) != null) {
            for (int k = 0; k < this.getGolemPattern().getPalmLength(); ++k) {
                for (int l = 0; l < this.getGolemPattern().getThumbLength(); ++l) {
                    world.setBlockState(patternHelper.translateOffset(k, l, 0).getPos(), Blocks.air.getDefaultState(), 2);
                }
            }

            BlockPos blockpos = patternHelper.translateOffset(1, 2, 0).getPos();
            EntityIronGolem entityirongolem = new EntityIronGolem(world);
            entityirongolem.setPlayerCreated(true);
            entityirongolem.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
            world.spawnEntityInWorld(entityirongolem);

            for (int j1 = 0; j1 < 120; ++j1) {
                world.spawnParticle(EnumParticleTypes.SNOWBALL, (double)blockpos.getX() + world.rand.nextDouble(), (double)blockpos.getY() + world.rand.nextDouble() * 3.9D, (double)blockpos.getZ() + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

            for (int k1 = 0; k1 < this.getGolemPattern().getPalmLength(); ++k1) {
                for (int l1 = 0; l1 < this.getGolemPattern().getThumbLength(); ++l1) {
                    BlockWorldState blockWorldState = patternHelper.translateOffset(k1, l1, 0);
                    world.notifyNeighborsRespectDebug(blockWorldState.getPos(), Blocks.air);
                }
            }
        }
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().blockMaterial.isReplaceable() && World.doesBlockHaveSolidTopSurface(world, pos.down());
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING);
    }

    protected BlockPattern getSnowmanBasePattern() {
        if (this.snowmanBasePattern == null) {
            this.snowmanBasePattern = FactoryBlockPattern.start().aisle(" ", "#", "#").where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow))).build();
        }

        return this.snowmanBasePattern;
    }

    protected BlockPattern getSnowmanPattern() {
        if (this.snowmanPattern == null) {
            this.snowmanPattern = FactoryBlockPattern.start().aisle("^", "#", "#").where('^', BlockWorldState.hasState(field_181085_Q)).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow))).build();
        }

        return this.snowmanPattern;
    }

    protected BlockPattern getGolemBasePattern() {
        if (this.golemBasePattern == null) {
            this.golemBasePattern = FactoryBlockPattern.start().aisle("~ ~", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.golemBasePattern;
    }

    protected BlockPattern getGolemPattern() {
        if (this.golemPattern == null) {
            this.golemPattern = FactoryBlockPattern.start().aisle("~^~", "###", "~#~").where('^', BlockWorldState.hasState(field_181085_Q)).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.golemPattern;
    }

}
