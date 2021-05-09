package com.murengezi.minecraft.block;

import com.murengezi.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockLeavesBase extends Block {

    protected boolean fancyGraphics;

    protected BlockLeavesBase(Material material, boolean fancyGraphics) {
        super(material);
        this.fancyGraphics = fancyGraphics;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return (this.fancyGraphics || world.getBlockState(pos).getBlock() != this) && super.shouldSideBeRendered(world, pos, side);
    }

}
