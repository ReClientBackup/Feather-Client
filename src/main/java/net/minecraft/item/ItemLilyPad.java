package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored
{
    public ItemLilyPad(Block block)
    {
        super(block, false);
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

                BlockPos blockpos1 = blockpos.up();
                IBlockState iblockstate = world.getBlockState(blockpos);

                if (iblockstate.getBlock().getMaterial() == Material.water && iblockstate.getValue(BlockLiquid.LEVEL).intValue() == 0 && world.isAirBlock(blockpos1))
                {
                    world.setBlockState(blockpos1, Blocks.waterlily.getDefaultState());

                    if (!player.capabilities.isCreativeMode)
                    {
                        --itemStackIn.stackSize;
                    }

                    player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                }
            }

            return itemStackIn;
        }
    }

    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        return Blocks.waterlily.getRenderColor(Blocks.waterlily.getStateFromMeta(stack.getMetadata()));
    }
}
