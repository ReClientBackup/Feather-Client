package net.minecraft.item;

import net.minecraft.block.Block;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemRedstone extends Item
{
    public ItemRedstone()
    {
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        boolean flag = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
        BlockPos blockpos = flag ? pos : pos.offset(side);

        if (!player.canPlayerEdit(blockpos, side, stack))
        {
            return false;
        }
        else
        {
            Block block = world.getBlockState(blockpos).getBlock();

            if (!world.canBlockBePlaced(block, blockpos, false, side, null))
            {
                return false;
            }
            else if (Blocks.redstone_wire.canPlaceBlockAt(world, blockpos))
            {
                --stack.stackSize;
                world.setBlockState(blockpos, Blocks.redstone_wire.getDefaultState());
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
