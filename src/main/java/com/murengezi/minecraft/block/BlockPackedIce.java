package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.creativetab.CreativeTabs;

public class BlockPackedIce extends Block {

    public BlockPackedIce() {
        super(Material.packedIce);
        this.slipperiness = 0.98F;
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public int quantityDropped(Random random) {
        return 0;
    }

}
