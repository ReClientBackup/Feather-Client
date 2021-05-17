package com.murengezi.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.BlockWorldState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {

    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);

    public BlockPortal() {
        super(Material.portal, false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
        this.setTickRandomly(true);
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);

        if (world.provider.isSurfaceWorld() && world.getGameRules().getGameRuleBooleanValue("doMobSpawning") && rand.nextInt(2000) < world.getDifficulty().getDifficultyId()) {
            int i = pos.getY();
            BlockPos blockpos;

            for (blockpos = pos; !World.doesBlockHaveSolidTopSurface(world, blockpos) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

            if (i > 0 && !world.getBlockState(blockpos.up()).getBlock().isNormalCube()) {
                Entity entity = ItemMonsterPlacer.spawnCreature(world, 57, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 1.1D, (double)blockpos.getZ() + 0.5D);

                if (entity != null) {
                    entity.timeUntilPortal = entity.getPortalCooldown();
                }
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return null;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        EnumFacing.Axis axis = world.getBlockState(pos).getValue(AXIS);
        float f = 0.125F;
        float f1 = 0.125F;

        if (axis == EnumFacing.Axis.X) {
            f = 0.5F;
        }

        if (axis == EnumFacing.Axis.Z) {
            f1 = 0.5F;
        }

        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
    }

    public static int getMetaForAxis(EnumFacing.Axis axis) {
        return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean func_176548_d(World world, BlockPos blockPos) {
        BlockPortal.Size size = new BlockPortal.Size(world, blockPos, EnumFacing.Axis.X);

        if (size.func_150860_b() && size.field_150864_e == 0) {
            size.func_150859_c();
            return true;
        } else {
            BlockPortal.Size size1 = new BlockPortal.Size(world, blockPos, EnumFacing.Axis.Z);

            if (size1.func_150860_b() && size1.field_150864_e == 0) {
                size1.func_150859_c();
                return true;
            } else {
                return false;
            }
        }
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        EnumFacing.Axis axis = state.getValue(AXIS);

        if (axis == EnumFacing.Axis.X) {
            BlockPortal.Size size = new BlockPortal.Size(world, pos, EnumFacing.Axis.X);

            if (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g) {
                world.setBlockState(pos, Blocks.air.getDefaultState());
            }
        } else if (axis == EnumFacing.Axis.Z) {
            BlockPortal.Size size = new BlockPortal.Size(world, pos, EnumFacing.Axis.Z);

            if (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g) {
                world.setBlockState(pos, Blocks.air.getDefaultState());
            }
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        EnumFacing.Axis axis = null;
        IBlockState iblockstate = world.getBlockState(pos);

        if (world.getBlockState(pos).getBlock() == this) {
            axis = iblockstate.getValue(AXIS);

            if (axis == null) {
                return false;
            }

            if (axis == EnumFacing.Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST) {
                return false;
            }

            if (axis == EnumFacing.Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH) {
                return false;
            }
        }

        boolean flag = world.getBlockState(pos.west()).getBlock() == this && world.getBlockState(pos.west(2)).getBlock() != this;
        boolean flag1 = world.getBlockState(pos.east()).getBlock() == this && world.getBlockState(pos.east(2)).getBlock() != this;
        boolean flag2 = world.getBlockState(pos.north()).getBlock() == this && world.getBlockState(pos.north(2)).getBlock() != this;
        boolean flag3 = world.getBlockState(pos.south()).getBlock() == this && world.getBlockState(pos.south(2)).getBlock() != this;
        boolean flag4 = flag || flag1 || axis == EnumFacing.Axis.X;
        boolean flag5 = flag2 || flag3 || axis == EnumFacing.Axis.Z;
        return flag4 && side == EnumFacing.WEST || (flag4 && side == EnumFacing.EAST || (flag5 && side == EnumFacing.NORTH || flag5 && side == EnumFacing.SOUTH));
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null) {
            entityIn.func_181015_d(pos);
        }
    }

    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (rand.nextInt(100) == 0) {
            world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "portal.portal", 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
        }

        for (int i = 0; i < 4; ++i) {
            double d0 = (float)pos.getX() + rand.nextFloat();
            double d1 = (float)pos.getY() + rand.nextFloat();
            double d2 = (float)pos.getZ() + rand.nextFloat();
            double d3 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d4 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d5 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            int j = rand.nextInt(2) * 2 - 1;

            if (world.getBlockState(pos.west()).getBlock() != this && world.getBlockState(pos.east()).getBlock() != this) {
                d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
                d3 = rand.nextFloat() * 2.0F * (float)j;
            } else {
                d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)j;
                d5 = rand.nextFloat() * 2.0F * (float)j;
            }

            world.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
    }

    public Item getItem(World world, BlockPos pos) {
        return null;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
    }

    public int getMetaFromState(IBlockState state) {
        return getMetaForAxis(state.getValue(AXIS));
    }

    protected BlockState createBlockState() {
        return new BlockState(this, AXIS);
    }

    public BlockPattern.PatternHelper func_181089_f(World world, BlockPos blockPos) {
        EnumFacing.Axis axis = EnumFacing.Axis.Z;
        BlockPortal.Size size = new BlockPortal.Size(world, blockPos, EnumFacing.Axis.X);
        LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.func_181627_a(world, true);

        if (!size.func_150860_b()) {
            axis = EnumFacing.Axis.X;
            size = new BlockPortal.Size(world, blockPos, EnumFacing.Axis.Z);
        }

        if (!size.func_150860_b()) {
            return new BlockPattern.PatternHelper(blockPos, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1, 1, 1);
        } else {
            int[] aint = new int[EnumFacing.AxisDirection.values().length];
            EnumFacing enumfacing = size.field_150866_c.rotateYCCW();
            BlockPos blockpos = size.field_150861_f.up(size.func_181100_a() - 1);

            for (EnumFacing.AxisDirection axisDirection : EnumFacing.AxisDirection.values()) {
                BlockPattern.PatternHelper patternHelper = new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == axisDirection ? blockpos : blockpos.offset(size.field_150866_c, size.func_181101_b() - 1), EnumFacing.func_181076_a(axisDirection, axis), EnumFacing.UP, loadingcache, size.func_181101_b(), size.func_181100_a(), 1);

                for (int i = 0; i < size.func_181101_b(); ++i) {
                    for (int j = 0; j < size.func_181100_a(); ++j) {
                        BlockWorldState blockworldstate = patternHelper.translateOffset(i, j, 1);

                        if (blockworldstate.getBlockState() != null && blockworldstate.getBlockState().getBlock().getMaterial() != Material.air) {
                            ++aint[axisDirection.ordinal()];
                        }
                    }
                }
            }

            EnumFacing.AxisDirection axisDirection = EnumFacing.AxisDirection.POSITIVE;

            for (EnumFacing.AxisDirection enumfacing$axisdirection2 : EnumFacing.AxisDirection.values()) {
                if (aint[enumfacing$axisdirection2.ordinal()] < aint[axisDirection.ordinal()]) {
                    axisDirection = enumfacing$axisdirection2;
                }
            }

            return new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == axisDirection ? blockpos : blockpos.offset(size.field_150866_c, size.func_181101_b() - 1), EnumFacing.func_181076_a(axisDirection, axis), EnumFacing.UP, loadingcache, size.func_181101_b(), size.func_181100_a(), 1);
        }
    }

    public static class Size {
        private final World world;
        private final EnumFacing.Axis axis;
        private final EnumFacing field_150866_c, field_150863_d;
        private int field_150864_e = 0;
        private BlockPos field_150861_f;
        private int field_150862_g, field_150868_h;

        public Size(World world, BlockPos blockPos, EnumFacing.Axis axis) {
            this.world = world;
            this.axis = axis;

            if (axis == EnumFacing.Axis.X) {
                this.field_150863_d = EnumFacing.EAST;
                this.field_150866_c = EnumFacing.WEST;
            } else {
                this.field_150863_d = EnumFacing.NORTH;
                this.field_150866_c = EnumFacing.SOUTH;
            }

            for (BlockPos blockpos = blockPos; blockPos.getY() > blockpos.getY() - 21 && blockPos.getY() > 0 && this.func_150857_a(world.getBlockState(blockPos.down()).getBlock()); blockPos = blockPos.down()) {}

            int i = this.func_180120_a(blockPos, this.field_150863_d) - 1;

            if (i >= 0) {
                this.field_150861_f = blockPos.offset(this.field_150863_d, i);
                this.field_150868_h = this.func_180120_a(this.field_150861_f, this.field_150866_c);

                if (this.field_150868_h < 2 || this.field_150868_h > 21) {
                    this.field_150861_f = null;
                    this.field_150868_h = 0;
                }
            }

            if (this.field_150861_f != null) {
                this.field_150862_g = this.func_150858_a();
            }
        }

        protected int func_180120_a(BlockPos blockPos, EnumFacing enumFacing) {
            int i;

            for (i = 0; i < 22; ++i) {
                BlockPos blockpos = blockPos.offset(enumFacing, i);

                if (!this.func_150857_a(this.world.getBlockState(blockpos).getBlock()) || this.world.getBlockState(blockpos.down()).getBlock() != Blocks.obsidian) {
                    break;
                }
            }

            Block block = this.world.getBlockState(blockPos.offset(enumFacing, i)).getBlock();
            return block == Blocks.obsidian ? i : 0;
        }

        public int func_181100_a() {
            return this.field_150862_g;
        }

        public int func_181101_b() {
            return this.field_150868_h;
        }

        protected int func_150858_a() {
            label24:

            for (this.field_150862_g = 0; this.field_150862_g < 21; ++this.field_150862_g) {
                for (int i = 0; i < this.field_150868_h; ++i) {
                    BlockPos blockpos = this.field_150861_f.offset(this.field_150866_c, i).up(this.field_150862_g);
                    Block block = this.world.getBlockState(blockpos).getBlock();

                    if (!this.func_150857_a(block)) {
                        break label24;
                    }

                    if (block == Blocks.portal) {
                        ++this.field_150864_e;
                    }

                    if (i == 0) {
                        block = this.world.getBlockState(blockpos.offset(this.field_150863_d)).getBlock();

                        if (block != Blocks.obsidian) {
                            break label24;
                        }
                    } else if (i == this.field_150868_h - 1) {
                        block = this.world.getBlockState(blockpos.offset(this.field_150866_c)).getBlock();

                        if (block != Blocks.obsidian) {
                            break label24;
                        }
                    }
                }
            }

            for (int j = 0; j < this.field_150868_h; ++j) {
                if (this.world.getBlockState(this.field_150861_f.offset(this.field_150866_c, j).up(this.field_150862_g)).getBlock() != Blocks.obsidian) {
                    this.field_150862_g = 0;
                    break;
                }
            }

            if (this.field_150862_g <= 21 && this.field_150862_g >= 3) {
                return this.field_150862_g;
            } else {
                this.field_150861_f = null;
                this.field_150868_h = 0;
                this.field_150862_g = 0;
                return 0;
            }
        }

        protected boolean func_150857_a(Block block) {
            return block.blockMaterial == Material.air || block == Blocks.fire || block == Blocks.portal;
        }

        public boolean func_150860_b() {
            return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
        }

        public void func_150859_c() {
            for (int i = 0; i < this.field_150868_h; ++i) {
                BlockPos blockpos = this.field_150861_f.offset(this.field_150866_c, i);

                for (int j = 0; j < this.field_150862_g; ++j) {
                    this.world.setBlockState(blockpos.up(j), Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, this.axis), 2);
                }
            }
        }
    }

}
