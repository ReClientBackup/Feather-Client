package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPotato extends BlockCrops
{
    protected Item getSeed()
    {
        return Items.potato;
    }

    protected Item getCrop()
    {
        return Items.potato;
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *  
     * @param chance The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);

        if (!world.isRemote)
        {
            if (state.getValue(AGE).intValue() >= 7 && world.rand.nextInt(50) == 0)
            {
                spawnAsEntity(world, pos, new ItemStack(Items.poisonous_potato));
            }
        }
    }
}
