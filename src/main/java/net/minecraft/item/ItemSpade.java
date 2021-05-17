package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.init.Blocks;

public class ItemSpade extends ItemTool
{
    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.clay, Blocks.dirt, Blocks.farmland, Blocks.grass, Blocks.gravel, Blocks.mycelium, Blocks.sand, Blocks.snow, Blocks.snow_layer, Blocks.soul_sand);

    public ItemSpade(Item.ToolMaterial material)
    {
        super(1.0F, material, EFFECTIVE_ON);
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    public boolean canHarvestBlock(Block block)
    {
        return block == Blocks.snow_layer || block == Blocks.snow;
    }
}
