package com.murengezi.minecraft.block;

import java.util.IdentityHashMap;
import java.util.Map;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockAir extends Block {

    private static final Map mapOriginalOpacity = new IdentityHashMap();

    protected BlockAir() {
        super(Material.air);
    }

    public int getRenderType() {
        return -1;
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
    }

    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {}

    public boolean isReplaceable(World world, BlockPos pos) {
        return true;
    }

    public static void setLightOpacity(Block block, int opacity) {
        if (!mapOriginalOpacity.containsKey(block)) {
            mapOriginalOpacity.put(block, block.lightOpacity);
        }

        block.lightOpacity = opacity;
    }

}
