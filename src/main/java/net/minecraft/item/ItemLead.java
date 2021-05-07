package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemLead extends Item
{
    public ItemLead()
    {
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        Block block = world.getBlockState(pos).getBlock();

        if (block instanceof BlockFence)
        {
            if (world.isRemote)
            {
                return true;
            }
            else
            {
                attachToFence(player, world, pos);
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean attachToFence(EntityPlayer player, World world, BlockPos fence)
    {
        EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(world, fence);
        boolean flag = false;
        double d0 = 7.0D;
        int i = fence.getX();
        int j = fence.getY();
        int k = fence.getZ();

        for (EntityLiving entityliving : world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB((double)i - d0, (double)j - d0, (double)k - d0, (double)i + d0, (double)j + d0, (double)k + d0)))
        {
            if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player)
            {
                if (entityleashknot == null)
                {
                    entityleashknot = EntityLeashKnot.createKnot(world, fence);
                }

                entityliving.setLeashedToEntity(entityleashknot, true);
                flag = true;
            }
        }

        return flag;
    }
}
