package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLeaves extends BlockLeavesBase {

    public static final PropertyBool DECAYABLE = PropertyBool.create("decayable");
    public static final PropertyBool CHECK_DECAY = PropertyBool.create("check_decay");
    int[] surroundings;
    protected int iconIndex;
    protected boolean isTransparent;

    public BlockLeaves() {
        super(Material.leaves, false);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        this.setStepSound(soundTypeGrass);
    }

    public int getBlockColor() {
        return ColorizerFoliage.getFoliageColor(0.5D, 1.0D);
    }

    public int getRenderColor(IBlockState state) {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        return BiomeColorHelper.getFoliageColorAtPos(world, pos);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        int i = 1;
        int j = i + 1;
        int k = pos.getX();
        int l = pos.getY();
        int i1 = pos.getZ();

        if (world.isAreaLoaded(new BlockPos(k - j, l - j, i1 - j), new BlockPos(k + j, l + j, i1 + j))) {
            for (int j1 = -i; j1 <= i; ++j1) {
                for (int k1 = -i; k1 <= i; ++k1) {
                    for (int l1 = -i; l1 <= i; ++l1) {
                        BlockPos blockpos = pos.add(j1, k1, l1);
                        IBlockState iblockstate = world.getBlockState(blockpos);

                        if (iblockstate.getBlock().getMaterial() == Material.leaves && !iblockstate.getValue(CHECK_DECAY)) {
                            world.setBlockState(blockpos, iblockstate.withProperty(CHECK_DECAY, true), 4);
                        }
                    }
                }
            }
        }
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            if (state.getValue(CHECK_DECAY) && state.getValue(DECAYABLE)) {
                int i = 4;
                int j = i + 1;
                int k = pos.getX();
                int l = pos.getY();
                int i1 = pos.getZ();
                int j1 = 32;
                int k1 = j1 * j1;
                int l1 = j1 / 2;

                if (this.surroundings == null) {
                    this.surroundings = new int[j1 * j1 * j1];
                }

                if (world.isAreaLoaded(new BlockPos(k - j, l - j, i1 - j), new BlockPos(k + j, l + j, i1 + j))) {
                    BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

                    for (int i2 = -i; i2 <= i; ++i2) {
                        for (int j2 = -i; j2 <= i; ++j2) {
                            for (int k2 = -i; k2 <= i; ++k2) {
                                Block block = world.getBlockState(blockPos.func_181079_c(k + i2, l + j2, i1 + k2)).getBlock();

                                if (block != Blocks.log && block != Blocks.log2) {
                                    if (block.getMaterial() == Material.leaves) {
                                        this.surroundings[(i2 + l1) * k1 + (j2 + l1) * j1 + k2 + l1] = -2;
                                    } else {
                                        this.surroundings[(i2 + l1) * k1 + (j2 + l1) * j1 + k2 + l1] = -1;
                                    }
                                } else {
                                    this.surroundings[(i2 + l1) * k1 + (j2 + l1) * j1 + k2 + l1] = 0;
                                }
                            }
                        }
                    }

                    for (int i3 = 1; i3 <= 4; ++i3) {
                        for (int j3 = -i; j3 <= i; ++j3) {
                            for (int k3 = -i; k3 <= i; ++k3) {
                                for (int l3 = -i; l3 <= i; ++l3) {
                                    if (this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + l3 + l1] == i3 - 1) {
                                        if (this.surroundings[(j3 + l1 - 1) * k1 + (k3 + l1) * j1 + l3 + l1] == -2) {
                                            this.surroundings[(j3 + l1 - 1) * k1 + (k3 + l1) * j1 + l3 + l1] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1 + 1) * k1 + (k3 + l1) * j1 + l3 + l1] == -2) {
                                            this.surroundings[(j3 + l1 + 1) * k1 + (k3 + l1) * j1 + l3 + l1] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1) * k1 + (k3 + l1 - 1) * j1 + l3 + l1] == -2) {
                                            this.surroundings[(j3 + l1) * k1 + (k3 + l1 - 1) * j1 + l3 + l1] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1) * k1 + (k3 + l1 + 1) * j1 + l3 + l1] == -2) {
                                            this.surroundings[(j3 + l1) * k1 + (k3 + l1 + 1) * j1 + l3 + l1] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + (l3 + l1 - 1)] == -2) {
                                            this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + (l3 + l1 - 1)] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + l3 + l1 + 1] == -2) {
                                            this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + l3 + l1 + 1] = i3;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int l2 = this.surroundings[l1 * k1 + l1 * j1 + l1];

                if (l2 >= 0) {
                    world.setBlockState(pos, state.withProperty(CHECK_DECAY, false), 4);
                } else {
                    this.destroy(world, pos);
                }
            }
        }
    }

    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.canLightningStrike(pos.up()) && !World.doesBlockHaveSolidTopSurface(world, pos.down()) && rand.nextInt(15) == 1) {
            double d0 = (float)pos.getX() + rand.nextFloat();
            double d1 = (double)pos.getY() - 0.05D;
            double d2 = (float)pos.getZ() + rand.nextFloat();
            world.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    private void destroy(World world, BlockPos pos) {
        this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        world.setBlockToAir(pos);
    }

    public int quantityDropped(Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.sapling);
    }

    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (!world.isRemote) {
            int i = this.getSaplingDropChance(state);

            if (fortune > 0) {
                i -= 2 << fortune;

                if (i < 10) {
                    i = 10;
                }
            }

            if (world.rand.nextInt(i) == 0) {
                Item item = this.getItemDropped(state, world.rand, fortune);
                spawnAsEntity(world, pos, new ItemStack(item, 1, this.damageDropped(state)));
            }

            i = 200;

            if (fortune > 0) {
                i -= 10 << fortune;

                if (i < 40) {
                    i = 40;
                }
            }

            this.dropApple(world, pos, state, i);
        }
    }

    protected void dropApple(World world, BlockPos pos, IBlockState state, int chance) {}

    protected int getSaplingDropChance(IBlockState state) {
        return 20;
    }

    public boolean isOpaqueCube() {
        return !this.fancyGraphics;
    }

    public void setGraphicsLevel(boolean fancy) {
        this.isTransparent = fancy;
        this.fancyGraphics = fancy;
        this.iconIndex = fancy ? 0 : 1;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return this.isTransparent ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
    }

    public boolean isVisuallyOpaque() {
        return false;
    }

    public abstract BlockPlanks.EnumType getWoodType(int meta);

}
