package com.murengezi.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentLootBonus extends Enchantment {

    protected EnchantmentLootBonus(int p_i45767_1_, ResourceLocation location, int p_i45767_3_, EnumEnchantmentType type) {
        super(p_i45767_1_, location, p_i45767_3_, type);

        if (type == EnumEnchantmentType.DIGGER) {
            this.setName("lootBonusDigger");
        } else if (type == EnumEnchantmentType.FISHING_ROD) {
            this.setName("lootBonusFishing");
        } else {
            this.setName("lootBonus");
        }
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 15 + (enchantmentLevel - 1) * 9;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean canApplyTogether(Enchantment ench) {
        return super.canApplyTogether(ench) && ench.effectId != silkTouch.effectId;
    }

}
