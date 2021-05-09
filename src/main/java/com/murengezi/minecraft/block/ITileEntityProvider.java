package com.murengezi.minecraft.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ITileEntityProvider {

    TileEntity createNewTileEntity(World world, int meta);

}
