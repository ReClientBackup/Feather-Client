package net.minecraft.item;

import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.BlockBed;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item
{
    public ItemBed()
    {
        this.setCreativeTab(CreativeTabs.tabDecorations);
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
        else if (side != EnumFacing.UP)
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = world.getBlockState(pos);
            Block block = iblockstate.getBlock();
            boolean flag = block.isReplaceable(world, pos);

            if (!flag)
            {
                pos = pos.up();
            }

            int i = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            EnumFacing enumfacing = EnumFacing.getHorizontal(i);
            BlockPos blockpos = pos.offset(enumfacing);

            if (player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(blockpos, side, stack))
            {
                boolean flag1 = world.getBlockState(blockpos).getBlock().isReplaceable(world, blockpos);
                boolean flag2 = flag || world.isAirBlock(pos);
                boolean flag3 = flag1 || world.isAirBlock(blockpos);

                if (flag2 && flag3 && World.doesBlockHaveSolidTopSurface(world, pos.down()) && World.doesBlockHaveSolidTopSurface(world, blockpos.down()))
                {
                    IBlockState iblockstate1 = Blocks.bed.getDefaultState().withProperty(BlockBed.OCCUPIED, false).withProperty(BlockBed.FACING, enumfacing).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);

                    if (world.setBlockState(pos, iblockstate1, 3))
                    {
                        IBlockState iblockstate2 = iblockstate1.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
                        world.setBlockState(blockpos, iblockstate2, 3);
                    }

                    --stack.stackSize;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
    }
}
