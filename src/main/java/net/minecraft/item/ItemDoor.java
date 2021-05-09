package net.minecraft.item;

import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.BlockDoor;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDoor extends Item
{
    private final Block block;

    public ItemDoor(Block block)
    {
        this.block = block;
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
        if (side != EnumFacing.UP)
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = world.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if (!block.isReplaceable(world, pos))
            {
                pos = pos.offset(side);
            }

            if (!player.canPlayerEdit(pos, side, stack))
            {
                return false;
            }
            else if (!this.block.canPlaceBlockAt(world, pos))
            {
                return false;
            }
            else
            {
                placeDoor(world, pos, EnumFacing.fromAngle(player.rotationYaw), this.block);
                --stack.stackSize;
                return true;
            }
        }
    }

    public static void placeDoor(World world, BlockPos pos, EnumFacing facing, Block door)
    {
        BlockPos blockpos = pos.offset(facing.rotateY());
        BlockPos blockpos1 = pos.offset(facing.rotateYCCW());
        int i = (world.getBlockState(blockpos1).getBlock().isNormalCube() ? 1 : 0) + (world.getBlockState(blockpos1.up()).getBlock().isNormalCube() ? 1 : 0);
        int j = (world.getBlockState(blockpos).getBlock().isNormalCube() ? 1 : 0) + (world.getBlockState(blockpos.up()).getBlock().isNormalCube() ? 1 : 0);
        boolean flag = world.getBlockState(blockpos1).getBlock() == door || world.getBlockState(blockpos1.up()).getBlock() == door;
        boolean flag1 = world.getBlockState(blockpos).getBlock() == door || world.getBlockState(blockpos.up()).getBlock() == door;
        boolean flag2 = false;

        if (flag && !flag1 || j > i)
        {
            flag2 = true;
        }

        BlockPos blockpos2 = pos.up();
        IBlockState iblockstate = door.getDefaultState().withProperty(BlockDoor.FACING, facing).withProperty(BlockDoor.HINGE, flag2 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT);
        world.setBlockState(pos, iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
        world.setBlockState(blockpos2, iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
        world.notifyNeighborsOfStateChange(pos, door);
        world.notifyNeighborsOfStateChange(blockpos2, door);
    }
}
