package com.murengezi.minecraft.block;

import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;

public abstract class BlockDirectional extends Block {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    protected BlockDirectional(Material material) {
        super(material);
    }

    protected BlockDirectional(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

}
