package net.minecraft.item;

import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemFireball extends Item
{
    public ItemFireball()
    {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            pos = pos.offset(side);

            if (!player.canPlayerEdit(pos, side, stack))
            {
                return false;
            }
            else
            {
                if (world.getBlockState(pos).getBlock().getMaterial() == Material.air)
                {
                    world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "item.fireCharge.use", 1.0F, (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2F + 1.0F);
                    world.setBlockState(pos, Blocks.fire.getDefaultState());
                }

                if (!player.capabilities.isCreativeMode)
                {
                    --stack.stackSize;
                }

                return true;
            }
        }
    }
}
