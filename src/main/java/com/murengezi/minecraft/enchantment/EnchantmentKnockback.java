package com.murengezi.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentKnockback extends Enchantment {

    protected EnchantmentKnockback(int enchID, ResourceLocation location, int enchWeight) {
        super(enchID, location, enchWeight, EnumEnchantmentType.WEAPON);
        this.setName("knockback");
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 5 + 20 * (enchantmentLevel - 1);
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return 2;
    }

}
