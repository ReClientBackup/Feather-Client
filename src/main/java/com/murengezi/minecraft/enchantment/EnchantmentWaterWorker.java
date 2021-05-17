package com.murengezi.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWorker extends Enchantment {

    public EnchantmentWaterWorker(int p_i45761_1_, ResourceLocation location, int p_i45761_3_) {
        super(p_i45761_1_, location, p_i45761_3_, EnumEnchantmentType.ARMOR_HEAD);
        this.setName("waterWorker");
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 1;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 40;
    }

    public int getMaxLevel() {
        return 1;
    }

}
