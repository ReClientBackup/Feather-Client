package net.minecraft.item;

import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase
{
    protected ItemEmptyMap()
    {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World world, EntityPlayer player)
    {
        ItemStack itemstack = new ItemStack(Items.filled_map, 1, world.getUniqueDataId("map"));
        String s = "map_" + itemstack.getMetadata();
        MapData mapdata = new MapData(s);
        world.setItemData(s, mapdata);
        mapdata.scale = 0;
        mapdata.calculateMapCenter(player.posX, player.posZ, mapdata.scale);
        mapdata.dimension = (byte)world.provider.getDimensionId();
        mapdata.markDirty();
        --itemStackIn.stackSize;

        if (itemStackIn.stackSize <= 0)
        {
            return itemstack;
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(itemstack.copy()))
            {
                player.dropPlayerItemWithRandomChoice(itemstack);
            }

            player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            return itemStackIn;
        }
    }
}
