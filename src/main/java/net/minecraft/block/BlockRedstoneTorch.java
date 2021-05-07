package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch
{
    private static final Map<World, List<BlockRedstoneTorch.Toggle>> toggles = Maps.newHashMap();
    private final boolean isOn;

    private boolean isBurnedOut(World world, BlockPos pos, boolean turnOff)
    {
        if (!toggles.containsKey(world))
        {
            toggles.put(world, Lists.newArrayList());
        }

        List<BlockRedstoneTorch.Toggle> list = toggles.get(world);

        if (turnOff)
        {
            list.add(new BlockRedstoneTorch.Toggle(pos, world.getTotalWorldTime()));
        }

        int i = 0;

        for (int j = 0; j < list.size(); ++j)
        {
            BlockRedstoneTorch.Toggle blockredstonetorch$toggle = list.get(j);

            if (blockredstonetorch$toggle.pos.equals(pos))
            {
                ++i;

                if (i >= 8)
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockRedstoneTorch(boolean isOn)
    {
        this.isOn = isOn;
        this.setTickRandomly(true);
        this.setCreativeTab(null);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World world)
    {
        return 2;
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        if (this.isOn)
        {
            for (EnumFacing enumfacing : EnumFacing.values())
            {
                world.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (this.isOn)
        {
            for (EnumFacing enumfacing : EnumFacing.values())
            {
                world.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }
    }

    public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return this.isOn && state.getValue(FACING) != side ? 15 : 0;
    }

    private boolean shouldBeOff(World world, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = state.getValue(FACING).getOpposite();
        return world.isSidePowered(pos.offset(enumfacing), enumfacing);
    }

    /**
     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
     */
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        boolean flag = this.shouldBeOff(world, pos, state);
        List<BlockRedstoneTorch.Toggle> list = toggles.get(world);

        while (list != null && !list.isEmpty() && world.getTotalWorldTime() - list.get(0).time > 60L)
        {
            list.remove(0);
        }

        if (this.isOn)
        {
            if (flag)
            {
                world.setBlockState(pos, Blocks.unlit_redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);

                if (this.isBurnedOut(world, pos, true))
                {
                    world.playSoundEffect((float)pos.getX() + 0.5F, (float)pos.getY() + 0.5F, (float)pos.getZ() + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                    for (int i = 0; i < 5; ++i)
                    {
                        double d0 = (double)pos.getX() + rand.nextDouble() * 0.6D + 0.2D;
                        double d1 = (double)pos.getY() + rand.nextDouble() * 0.6D + 0.2D;
                        double d2 = (double)pos.getZ() + rand.nextDouble() * 0.6D + 0.2D;
                        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }

                    world.scheduleUpdate(pos, world.getBlockState(pos).getBlock(), 160);
                }
            }
        }
        else if (!flag && !this.isBurnedOut(world, pos, false))
        {
            world.setBlockState(pos, Blocks.redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!this.onNeighborChangeInternal(world, pos, state))
        {
            if (this.isOn == this.shouldBeOff(world, pos, state))
            {
                world.scheduleUpdate(pos, this, this.tickRate(world));
            }
        }
    }

    public int isProvidingStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return side == EnumFacing.DOWN ? this.isProvidingWeakPower(world, pos, state, side) : 0;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *  
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.isOn)
        {
            double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            double d1 = (double)pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
            double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            EnumFacing enumfacing = state.getValue(FACING);

            if (enumfacing.getAxis().isHorizontal())
            {
                EnumFacing enumfacing1 = enumfacing.getOpposite();
                double d3 = 0.27D;
                d0 += 0.27D * (double)enumfacing1.getFrontOffsetX();
                d1 += 0.22D;
                d2 += 0.27D * (double)enumfacing1.getFrontOffsetZ();
            }

            world.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(World world, BlockPos pos)
    {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    public boolean isAssociatedBlock(Block other)
    {
        return other == Blocks.unlit_redstone_torch || other == Blocks.redstone_torch;
    }

    static class Toggle
    {
        BlockPos pos;
        long time;

        public Toggle(BlockPos pos, long time)
        {
            this.pos = pos;
            this.time = time;
        }
    }
}
