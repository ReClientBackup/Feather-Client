package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLiquid extends Block {

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

    protected BlockLiquid(Material material) {
        super(material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setTickRandomly(true);
    }

    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return this.blockMaterial != Material.lava;
    }

    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        return this.blockMaterial == Material.water ? BiomeColorHelper.getWaterColorAtPos(world, pos) : 16777215;
    }

    public static float getLiquidHeightPercent(int meta) {
        if (meta >= 8) {
            meta = 0;
        }

        return (float)(meta + 1) / 9.0F;
    }

    protected int getLevel(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial ? world.getBlockState(pos).getValue(LEVEL) : -1;
    }

    protected int getEffectiveFlowDecay(IBlockAccess world, BlockPos pos) {
        int i = this.getLevel(world, pos);
        return i >= 8 ? 0 : i;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return hitIfLiquid && state.getValue(LEVEL) == 0;
    }

    public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        Material material = world.getBlockState(pos).getBlock().getMaterial();
        return material != this.blockMaterial && (side == EnumFacing.UP || (material != Material.ice && super.isBlockSolid(world, pos, side)));
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return world.getBlockState(pos).getBlock().getMaterial() != this.blockMaterial && (side == EnumFacing.UP || super.shouldSideBeRendered(world, pos, side));
    }

    public boolean func_176364_g(IBlockAccess blockAccess, BlockPos pos) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                IBlockState iblockstate = blockAccess.getBlockState(pos.add(i, 0, j));
                Block block = iblockstate.getBlock();
                Material material = block.getMaterial();

                if (material != this.blockMaterial && !block.isFullBlock()) {
                    return true;
                }
            }
        }

        return false;
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return null;
    }

    public int getRenderType() {
        return 1;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    protected Vec3 getFlowVector(IBlockAccess world, BlockPos pos) {
        Vec3 vec3 = new Vec3(0.0D, 0.0D, 0.0D);
        int i = this.getEffectiveFlowDecay(world, pos);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(enumfacing);
            int j = this.getEffectiveFlowDecay(world, blockpos);

            if (j < 0) {
                if (!world.getBlockState(blockpos).getBlock().getMaterial().blocksMovement()) {
                    j = this.getEffectiveFlowDecay(world, blockpos.down());

                    if (j >= 0) {
                        int k = j - (i - 8);
                        vec3 = vec3.addVector((blockpos.getX() - pos.getX()) * k, (blockpos.getY() - pos.getY()) * k, (blockpos.getZ() - pos.getZ()) * k);
                    }
                }
            } else {
                int l = j - i;
                vec3 = vec3.addVector((blockpos.getX() - pos.getX()) * l, (blockpos.getY() - pos.getY()) * l, (blockpos.getZ() - pos.getZ()) * l);
            }
        }

        if (world.getBlockState(pos).getValue(LEVEL) >= 8) {
            for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                BlockPos blockpos1 = pos.offset(enumfacing1);

                if (this.isBlockSolid(world, blockpos1, enumfacing1) || this.isBlockSolid(world, blockpos1.up(), enumfacing1)) {
                    vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return vec3.normalize();
    }

    public Vec3 modifyAcceleration(World world, BlockPos pos, Entity entityIn, Vec3 motion) {
        return motion.add(this.getFlowVector(world, pos));
    }

    public int tickRate(World world) {
        return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (world.provider.getHasNoSky() ? 10 : 30) : 0);
    }

    public int getMixedBrightnessForBlock(IBlockAccess world, BlockPos pos) {
        int i = world.getCombinedLight(pos, 0);
        int j = world.getCombinedLight(pos.up(), 0);
        int k = i & 255;
        int l = j & 255;
        int i1 = i >> 16 & 255;
        int j1 = j >> 16 & 255;
        return (Math.max(k, l)) | (Math.max(i1, j1)) << 16;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return this.blockMaterial == Material.water ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.SOLID;
    }

    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        double d0 = pos.getX();
        double d1 = pos.getY();
        double d2 = pos.getZ();

        if (this.blockMaterial == Material.water) {
            int i = state.getValue(LEVEL);

            if (i > 0 && i < 8) {
                if (rand.nextInt(64) == 0) {
                    world.playSound(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() + 0.5F, false);
                }
            } else if (rand.nextInt(10) == 0) {
                world.spawnParticle(EnumParticleTypes.SUSPENDED, d0 + (double)rand.nextFloat(), d1 + (double)rand.nextFloat(), d2 + (double)rand.nextFloat(), 0.0D, 0.0D, 0.0D);
            }
        }

        if (this.blockMaterial == Material.lava && world.getBlockState(pos.up()).getBlock().getMaterial() == Material.air && !world.getBlockState(pos.up()).getBlock().isOpaqueCube()) {
            if (rand.nextInt(100) == 0) {
                double d8 = d0 + (double)rand.nextFloat();
                double d4 = d1 + this.maxY;
                double d6 = d2 + (double)rand.nextFloat();
                world.spawnParticle(EnumParticleTypes.LAVA, d8, d4, d6, 0.0D, 0.0D, 0.0D);
                world.playSound(d8, d4, d6, "liquid.lavapop", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
            }

            if (rand.nextInt(200) == 0) {
                world.playSound(d0, d1, d2, "liquid.lava", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
            }
        }

        if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(world, pos.down())) {
            Material material = world.getBlockState(pos.down(2)).getBlock().getMaterial();

            if (!material.blocksMovement() && !material.isLiquid()) {
                double d3 = d0 + (double)rand.nextFloat();
                double d5 = d1 - 1.05D;
                double d7 = d2 + (double)rand.nextFloat();

                if (this.blockMaterial == Material.water) {
                    world.spawnParticle(EnumParticleTypes.DRIP_WATER, d3, d5, d7, 0.0D, 0.0D, 0.0D);
                } else {
                    world.spawnParticle(EnumParticleTypes.DRIP_LAVA, d3, d5, d7, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    public static double getFlowDirection(IBlockAccess world, BlockPos pos, Material material) {
        Vec3 vec3 = getFlowingBlock(material).getFlowVector(world, pos);
        return vec3.xCoord == 0.0D && vec3.zCoord == 0.0D ? -1000.0D : MathHelper.func_181159_b(vec3.zCoord, vec3.xCoord) - (Math.PI / 2D);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        this.checkForMixing(world, pos, state);
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        this.checkForMixing(world, pos, state);
    }

    public boolean checkForMixing(World world, BlockPos pos, IBlockState state) {
        if (this.blockMaterial == Material.lava) {
            boolean flag = false;

            for (EnumFacing enumfacing : EnumFacing.values()) {
                if (enumfacing != EnumFacing.DOWN && world.getBlockState(pos.offset(enumfacing)).getBlock().getMaterial() == Material.water) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                Integer integer = state.getValue(LEVEL);

                if (integer == 0) {
                    world.setBlockState(pos, Blocks.obsidian.getDefaultState());
                    this.triggerMixEffects(world, pos);
                    return true;
                }

                if (integer <= 4) {
                    world.setBlockState(pos, Blocks.cobblestone.getDefaultState());
                    this.triggerMixEffects(world, pos);
                    return true;
                }
            }
        }

        return false;
    }

    protected void triggerMixEffects(World world, BlockPos pos) {
        double d0 = pos.getX();
        double d1 = pos.getY();
        double d2 = pos.getZ();
        world.playSoundEffect(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, LEVEL);
    }

    public static BlockDynamicLiquid getFlowingBlock(Material material) {
        if (material == Material.water) {
            return Blocks.flowing_water;
        } else if (material == Material.lava) {
            return Blocks.flowing_lava;
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    public static BlockStaticLiquid getStaticBlock(Material material) {
        if (material == Material.water) {
            return Blocks.water;
        } else if (material == Material.lava) {
            return Blocks.lava;
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }

}
