package net.minecraft.item;

import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item
{
    public ItemGlassBottle()
    {
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World world, EntityPlayer player)
    {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);

        if (movingobjectposition == null)
        {
            return itemStackIn;
        }
        else
        {
            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                BlockPos blockpos = movingobjectposition.getBlockPos();

                if (!world.isBlockModifiable(player, blockpos))
                {
                    return itemStackIn;
                }

                if (!player.canPlayerEdit(blockpos.offset(movingobjectposition.sideHit), movingobjectposition.sideHit, itemStackIn))
                {
                    return itemStackIn;
                }

                if (world.getBlockState(blockpos).getBlock().getMaterial() == Material.water)
                {
                    --itemStackIn.stackSize;
                    player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

                    if (itemStackIn.stackSize <= 0)
                    {
                        return new ItemStack(Items.potionitem);
                    }

                    if (!player.inventory.addItemStackToInventory(new ItemStack(Items.potionitem)))
                    {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(Items.potionitem, 1, 0));
                    }
                }
            }

            return itemStackIn;
        }
    }
}
