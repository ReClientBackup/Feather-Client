package com.murengezi.minecraft.block;

import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block {

    private final boolean ignoreSimilarity;

    protected BlockBreakable(Material material, boolean ignoreSimilarity) {
        this(material, ignoreSimilarity, material.getMaterialMapColor());
    }

    protected BlockBreakable(Material material, boolean ignoreSimilarity, MapColor mapColor) {
        super(material, mapColor);
        this.ignoreSimilarity = ignoreSimilarity;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (this == Blocks.glass || this == Blocks.stained_glass) {
            if (world.getBlockState(pos.offset(side.getOpposite())) != iblockstate) {
                return true;
            }

            if (block == this) {
                return false;
            }
        }

        return (this.ignoreSimilarity || block != this) && super.shouldSideBeRendered(world, pos, side);
    }

}
