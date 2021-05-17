package com.murengezi.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block {
    
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15), UPPER = PropertyInteger.create("upper", 0, 2);
    public static final PropertyBool FLIP = PropertyBool.create("flip"), ALT = PropertyBool.create("alt"), NORTH = PropertyBool.create("north"), EAST = PropertyBool.create("east"), SOUTH = PropertyBool.create("south"), WEST = PropertyBool.create("west");
    private final Map<Block, Integer> encouragements = Maps.newIdentityHashMap(), flammabilities = Maps.newIdentityHashMap();

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        if (!World.doesBlockHaveSolidTopSurface(world, pos.down()) && !Blocks.fire.canCatchFire(world, pos.down())) {
            boolean flag = (i + j + k & 1) == 1;
            boolean flag1 = (i / 2 + j / 2 + k / 2 & 1) == 1;
            int l = 0;

            if (this.canCatchFire(world, pos.up())) {
                l = flag ? 1 : 2;
            }

            return state.withProperty(NORTH, this.canCatchFire(world, pos.north())).withProperty(EAST, this.canCatchFire(world, pos.east())).withProperty(SOUTH, this.canCatchFire(world, pos.south())).withProperty(WEST, this.canCatchFire(world, pos.west())).withProperty(UPPER, l).withProperty(FLIP, flag1).withProperty(ALT, flag);
        } else {
            return this.getDefaultState();
        }
    }

    protected BlockFire() {
        super(Material.fire);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(FLIP, false).withProperty(ALT, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(UPPER, 0));
        this.setTickRandomly(true);
    }

    public static void init() {
        Blocks.fire.setFireInfo(Blocks.planks, 5, 20);
        Blocks.fire.setFireInfo(Blocks.double_wooden_slab, 5, 20);
        Blocks.fire.setFireInfo(Blocks.wooden_slab, 5, 20);
        Blocks.fire.setFireInfo(Blocks.oak_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.spruce_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.birch_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.jungle_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.dark_oak_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.acacia_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.oak_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.spruce_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.birch_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.jungle_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.dark_oak_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.acacia_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.oak_stairs, 5, 20);
        Blocks.fire.setFireInfo(Blocks.birch_stairs, 5, 20);
        Blocks.fire.setFireInfo(Blocks.spruce_stairs, 5, 20);
        Blocks.fire.setFireInfo(Blocks.jungle_stairs, 5, 20);
        Blocks.fire.setFireInfo(Blocks.log, 5, 5);
        Blocks.fire.setFireInfo(Blocks.log2, 5, 5);
        Blocks.fire.setFireInfo(Blocks.leaves, 30, 60);
        Blocks.fire.setFireInfo(Blocks.leaves2, 30, 60);
        Blocks.fire.setFireInfo(Blocks.bookshelf, 30, 20);
        Blocks.fire.setFireInfo(Blocks.tnt, 15, 100);
        Blocks.fire.setFireInfo(Blocks.tallgrass, 60, 100);
        Blocks.fire.setFireInfo(Blocks.double_plant, 60, 100);
        Blocks.fire.setFireInfo(Blocks.yellow_flower, 60, 100);
        Blocks.fire.setFireInfo(Blocks.red_flower, 60, 100);
        Blocks.fire.setFireInfo(Blocks.deadbush, 60, 100);
        Blocks.fire.setFireInfo(Blocks.wool, 30, 60);
        Blocks.fire.setFireInfo(Blocks.vine, 15, 100);
        Blocks.fire.setFireInfo(Blocks.coal_block, 5, 5);
        Blocks.fire.setFireInfo(Blocks.hay_block, 60, 20);
        Blocks.fire.setFireInfo(Blocks.carpet, 60, 20);
    }

    public void setFireInfo(Block block, int encouragement, int flammability) {
        this.encouragements.put(block, encouragement);
        this.flammabilities.put(block, flammability);
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

    public int quantityDropped(Random random) {
        return 0;
    }

    public int tickRate(World world) {
        return 30;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
            if (!this.canPlaceBlockAt(world, pos)) {
                world.setBlockToAir(pos);
            }

            Block block = world.getBlockState(pos.down()).getBlock();
            boolean flag = block == Blocks.netherrack;

            if (world.provider instanceof WorldProviderEnd && block == Blocks.bedrock) {
                flag = true;
            }

            if (!flag && world.isRaining() && this.canDie(world, pos)) {
                world.setBlockToAir(pos);
            } else {
                int i = state.getValue(AGE);

                if (i < 15) {
                    state = state.withProperty(AGE, i + rand.nextInt(3) / 2);
                    world.setBlockState(pos, state, 4);
                }

                world.scheduleUpdate(pos, this, this.tickRate(world) + rand.nextInt(10));

                if (!flag) {
                    if (!this.canNeighborCatchFire(world, pos)) {
                        if (!World.doesBlockHaveSolidTopSurface(world, pos.down()) || i > 3) {
                            world.setBlockToAir(pos);
                        }

                        return;
                    }

                    if (!this.canCatchFire(world, pos.down()) && i == 15 && rand.nextInt(4) == 0) {
                        world.setBlockToAir(pos);
                        return;
                    }
                }

                boolean flag1 = world.isBlockinHighHumidity(pos);
                int j = 0;

                if (flag1) {
                    j = -50;
                }

                this.catchOnFire(world, pos.east(), 300 + j, rand, i);
                this.catchOnFire(world, pos.west(), 300 + j, rand, i);
                this.catchOnFire(world, pos.down(), 250 + j, rand, i);
                this.catchOnFire(world, pos.up(), 250 + j, rand, i);
                this.catchOnFire(world, pos.north(), 300 + j, rand, i);
                this.catchOnFire(world, pos.south(), 300 + j, rand, i);

                for (int k = -1; k <= 1; ++k) {
                    for (int l = -1; l <= 1; ++l) {
                        for (int i1 = -1; i1 <= 4; ++i1) {
                            if (k != 0 || i1 != 0 || l != 0) {
                                int j1 = 100;

                                if (i1 > 1) {
                                    j1 += (i1 - 1) * 100;
                                }

                                BlockPos blockpos = pos.add(k, i1, l);
                                int k1 = this.getNeighborEncouragement(world, blockpos);

                                if (k1 > 0) {
                                    int l1 = (k1 + 40 + world.getDifficulty().getDifficultyId() * 7) / (i + 30);

                                    if (flag1) {
                                        l1 /= 2;
                                    }

                                    if (l1 > 0 && rand.nextInt(j1) <= l1 && (!world.isRaining() || !this.canDie(world, blockpos))) {
                                        int i2 = i + rand.nextInt(5) / 4;

                                        if (i2 > 15) {
                                            i2 = 15;
                                        }

                                        world.setBlockState(blockpos, state.withProperty(AGE, i2), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean canDie(World world, BlockPos pos) {
        return world.canLightningStrike(pos) || world.canLightningStrike(pos.west()) || world.canLightningStrike(pos.east()) || world.canLightningStrike(pos.north()) || world.canLightningStrike(pos.south());
    }

    public boolean requiresUpdates() {
        return false;
    }

    private int getFlammability(Block block) {
        Integer integer = this.flammabilities.get(block);
        return integer == null ? 0 : integer;
    }

    private int getEncouragement(Block block) {
        Integer integer = this.encouragements.get(block);
        return integer == null ? 0 : integer;
    }

    private void catchOnFire(World world, BlockPos pos, int chance, Random random, int age) {
        int i = this.getFlammability(world.getBlockState(pos).getBlock());

        if (random.nextInt(chance) < i) {
            IBlockState iblockstate = world.getBlockState(pos);

            if (random.nextInt(age + 10) < 5 && !world.canLightningStrike(pos)) {
                int j = age + random.nextInt(5) / 4;

                if (j > 15) {
                    j = 15;
                }

                world.setBlockState(pos, this.getDefaultState().withProperty(AGE, j), 3);
            } else {
                world.setBlockToAir(pos);
            }

            if (iblockstate.getBlock() == Blocks.tnt) {
                Blocks.tnt.onBlockDestroyedByPlayer(world, pos, iblockstate.withProperty(BlockTNT.EXPLODE, true));
            }
        }
    }

    private boolean canNeighborCatchFire(World world, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (this.canCatchFire(world, pos.offset(enumfacing))) {
                return true;
            }
        }

        return false;
    }

    private int getNeighborEncouragement(World world, BlockPos pos) {
        if (!world.isAirBlock(pos)) {
            return 0;
        } else {
            int i = 0;

            for (EnumFacing enumfacing : EnumFacing.values()) {
                i = Math.max(this.getEncouragement(world.getBlockState(pos.offset(enumfacing)).getBlock()), i);
            }

            return i;
        }
    }

    public boolean isCollidable() {
        return false;
    }

    public boolean canCatchFire(IBlockAccess world, BlockPos pos) {
        return this.getEncouragement(world.getBlockState(pos).getBlock()) > 0;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(world, pos.down()) || this.canNeighborCatchFire(world, pos);
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!World.doesBlockHaveSolidTopSurface(world, pos.down()) && !this.canNeighborCatchFire(world, pos)) {
            world.setBlockToAir(pos);
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (world.provider.getDimensionId() > 0 || !Blocks.portal.func_176548_d(world, pos)) {
            if (!World.doesBlockHaveSolidTopSurface(world, pos.down()) && !this.canNeighborCatchFire(world, pos)) {
                world.setBlockToAir(pos);
            } else {
                world.scheduleUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(10));
            }
        }
    }

    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (rand.nextInt(24) == 0) {
            world.playSound((float)pos.getX() + 0.5F, (float)pos.getY() + 0.5F, (float)pos.getZ() + 0.5F, "fire.fire", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }

        if (!World.doesBlockHaveSolidTopSurface(world, pos.down()) && !Blocks.fire.canCatchFire(world, pos.down())) {
            if (Blocks.fire.canCatchFire(world, pos.west())) {
                for (int j = 0; j < 2; ++j) {
                    double d3 = (double)pos.getX() + rand.nextDouble() * 0.10000000149011612D;
                    double d8 = (double)pos.getY() + rand.nextDouble();
                    double d13 = (double)pos.getZ() + rand.nextDouble();
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canCatchFire(world, pos.east())) {
                for (int k = 0; k < 2; ++k) {
                    double d4 = (double)(pos.getX() + 1) - rand.nextDouble() * 0.10000000149011612D;
                    double d9 = (double)pos.getY() + rand.nextDouble();
                    double d14 = (double)pos.getZ() + rand.nextDouble();
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d4, d9, d14, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canCatchFire(world, pos.north())) {
                for (int l = 0; l < 2; ++l) {
                    double d5 = (double)pos.getX() + rand.nextDouble();
                    double d10 = (double)pos.getY() + rand.nextDouble();
                    double d15 = (double)pos.getZ() + rand.nextDouble() * 0.10000000149011612D;
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d5, d10, d15, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canCatchFire(world, pos.south())) {
                for (int i1 = 0; i1 < 2; ++i1) {
                    double d6 = (double)pos.getX() + rand.nextDouble();
                    double d11 = (double)pos.getY() + rand.nextDouble();
                    double d16 = (double)(pos.getZ() + 1) - rand.nextDouble() * 0.10000000149011612D;
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d6, d11, d16, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canCatchFire(world, pos.up())) {
                for (int j1 = 0; j1 < 2; ++j1) {
                    double d7 = (double)pos.getX() + rand.nextDouble();
                    double d12 = (double)(pos.getY() + 1) - rand.nextDouble() * 0.10000000149011612D;
                    double d17 = (double)pos.getZ() + rand.nextDouble();
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
                }
            }
        } else {
            for (int i = 0; i < 3; ++i) {
                double d0 = (double)pos.getX() + rand.nextDouble();
                double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
                double d2 = (double)pos.getZ() + rand.nextDouble();
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public MapColor getMapColor(IBlockState state) {
        return MapColor.tntColor;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, AGE, NORTH, EAST, SOUTH, WEST, UPPER, FLIP, ALT);
    }

}
