package net.minecraft.entity.item;

import com.murengezi.minecraft.block.BlockChest;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer
{
    public EntityMinecartChest(World world)
    {
        super(world);
    }

    public EntityMinecartChest(World world, double p_i1715_2_, double p_i1715_4_, double p_i1715_6_)
    {
        super(world, p_i1715_2_, p_i1715_4_, p_i1715_6_);
    }

    public void killMinecart(DamageSource p_94095_1_)
    {
        super.killMinecart(p_94095_1_);

        if (this.worldObj.getGameRules().getGameRuleBooleanValue("doEntityDrops"))
        {
            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.chest), 1, 0.0F);
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 27;
    }

    public EntityMinecart.EnumMinecartType getMinecartType()
    {
        return EntityMinecart.EnumMinecartType.CHEST;
    }

    public IBlockState getDefaultDisplayTile()
    {
        return Blocks.chest.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
    }

    public int getDefaultDisplayTileOffset()
    {
        return 8;
    }

    public String getGuiID()
    {
        return "minecraft:chest";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player)
    {
        return new ContainerChest(playerInventory, this, player);
    }
}
