package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    protected BlockCrops()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab(null);
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.disableStats();
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(Block ground)
    {
        return ground == Blocks.farmland;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(world, pos, state, rand);

        if (world.getLightFromNeighbors(pos.up()) >= 9)
        {
            int i = state.getValue(AGE).intValue();

            if (i < 7)
            {
                float f = getGrowthChance(this, world, pos);

                if (rand.nextInt((int)(25.0F / f) + 1) == 0)
                {
                    world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
                }
            }
        }
    }

    public void grow(World world, BlockPos pos, IBlockState state)
    {
        int i = state.getValue(AGE).intValue() + MathHelper.getRandomIntegerInRange(world.rand, 2, 5);

        if (i > 7)
        {
            i = 7;
        }

        world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i)), 2);
    }

    protected static float getGrowthChance(Block blockIn, World world, BlockPos pos)
    {
        float f = 1.0F;
        BlockPos blockpos = pos.down();

        for (int i = -1; i <= 1; ++i)
        {
            for (int j = -1; j <= 1; ++j)
            {
                float f1 = 0.0F;
                IBlockState iblockstate = world.getBlockState(blockpos.add(i, 0, j));

                if (iblockstate.getBlock() == Blocks.farmland)
                {
                    f1 = 1.0F;

                    if (iblockstate.getValue(BlockFarmland.MOISTURE).intValue() > 0)
                    {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0)
                {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = pos.north();
        BlockPos blockpos2 = pos.south();
        BlockPos blockpos3 = pos.west();
        BlockPos blockpos4 = pos.east();
        boolean flag = blockIn == world.getBlockState(blockpos3).getBlock() || blockIn == world.getBlockState(blockpos4).getBlock();
        boolean flag1 = blockIn == world.getBlockState(blockpos1).getBlock() || blockIn == world.getBlockState(blockpos2).getBlock();

        if (flag && flag1)
        {
            f /= 2.0F;
        }
        else
        {
            boolean flag2 = blockIn == world.getBlockState(blockpos3.north()).getBlock() || blockIn == world.getBlockState(blockpos4.north()).getBlock() || blockIn == world.getBlockState(blockpos4.south()).getBlock() || blockIn == world.getBlockState(blockpos3.south()).getBlock();

            if (flag2)
            {
                f /= 2.0F;
            }
        }

        return f;
    }

    public boolean canBlockStay(World world, BlockPos pos, IBlockState state)
    {
        return (world.getLight(pos) >= 8 || world.canSeeSky(pos)) && this.canPlaceBlockOn(world.getBlockState(pos.down()).getBlock());
    }

    protected Item getSeed()
    {
        return Items.wheat_seeds;
    }

    protected Item getCrop()
    {
        return Items.wheat;
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *  
     * @param chance The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(world, pos, state, chance, 0);

        if (!world.isRemote)
        {
            int i = state.getValue(AGE).intValue();

            if (i >= 7)
            {
                int j = 3 + fortune;

                for (int k = 0; k < j; ++k)
                {
                    if (world.rand.nextInt(15) <= i)
                    {
                        spawnAsEntity(world, pos, new ItemStack(this.getSeed(), 1, 0));
                    }
                }
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *  
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(AGE).intValue() == 7 ? this.getCrop() : this.getSeed();
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(World world, BlockPos pos)
    {
        return this.getSeed();
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
    {
        return state.getValue(AGE).intValue() < 7;
    }

    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    public void grow(World world, Random rand, BlockPos pos, IBlockState state)
    {
        this.grow(world, pos, state);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, AGE);
    }
}
