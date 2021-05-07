package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
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
