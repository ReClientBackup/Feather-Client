package net.minecraft.item;

import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.BlockSnow;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemReed extends Item
{
    private final Block block;

    public ItemReed(Block block)
    {
        this.block = block;
    }

    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.snow_layer && iblockstate.getValue(BlockSnow.LAYERS).intValue() < 1)
        {
            side = EnumFacing.UP;
        }
        else if (!block.isReplaceable(world, pos))
        {
            pos = pos.offset(side);
        }

        if (!player.canPlayerEdit(pos, side, stack))
        {
            return false;
        }
        else if (stack.stackSize == 0)
        {
            return false;
        }
        else
        {
            if (world.canBlockBePlaced(this.block, pos, false, side, null))
            {
                IBlockState iblockstate1 = this.block.onBlockPlaced(world, pos, side, hitX, hitY, hitZ, 0, player);

                if (world.setBlockState(pos, iblockstate1, 3))
                {
                    iblockstate1 = world.getBlockState(pos);

                    if (iblockstate1.getBlock() == this.block)
                    {
                        ItemBlock.setTileEntityNBT(world, player, pos, stack);
                        iblockstate1.getBlock().onBlockPlacedBy(world, pos, iblockstate1, player, stack);
                    }

                    world.playSoundEffect((float)pos.getX() + 0.5F, (float)pos.getY() + 0.5F, (float)pos.getZ() + 0.5F, this.block.stepSound.getPlaceSound(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getFrequency() * 0.8F);
                    --stack.stackSize;
                    return true;
                }
            }

            return false;
        }
    }
}
