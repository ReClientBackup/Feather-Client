package com.murengezi.minecraft.block;

import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockContainer extends Block implements ITileEntityProvider {

    protected BlockContainer(Material material) {
        this(material, material.getMaterialMapColor());
    }

    protected BlockContainer(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.isBlockContainer = true;
    }

    protected boolean func_181086_a(World world, BlockPos pos, EnumFacing enumFacing) {
        return world.getBlockState(pos.offset(enumFacing)).getBlock().getMaterial() == Material.cactus;
    }

    protected boolean func_181087_e(World world, BlockPos pos) {
        return this.func_181086_a(world, pos, EnumFacing.NORTH) || this.func_181086_a(world, pos, EnumFacing.SOUTH) || this.func_181086_a(world, pos, EnumFacing.WEST) || this.func_181086_a(world, pos, EnumFacing.EAST);
    }

    public int getRenderType() {
        return -1;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventParam) {
        super.onBlockEventReceived(world, pos, state, eventID, eventParam);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam);
    }

}
