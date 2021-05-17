package com.murengezi.minecraft.block;

import com.murengezi.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockCarrot extends BlockCrops {

    protected Item getSeed() {
        return Items.carrot;
    }

    protected Item getCrop() {
        return Items.carrot;
    }

}
