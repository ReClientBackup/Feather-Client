package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockOre extends Block {

    public BlockOre() {
        this(Material.rock.getMaterialMapColor());
    }

    public BlockOre(MapColor mapColor) {
        super(Material.rock, mapColor);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return this == Blocks.coal_ore ? Items.coal : (this == Blocks.diamond_ore ? Items.diamond : (this == Blocks.lapis_ore ? Items.dye : (this == Blocks.emerald_ore ? Items.emerald : (this == Blocks.quartz_ore ? Items.quartz : Item.getItemFromBlock(this)))));
    }

    public int quantityDropped(Random random) {
        return this == Blocks.lapis_ore ? 4 + random.nextInt(5) : 1;
    }

    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(this.getBlockState().getValidStates().iterator().next(), random, fortune)) {
            int i = random.nextInt(fortune + 2) - 1;

            if (i < 0) {
                i = 0;
            }

            return this.quantityDropped(random) * (i + 1);
        } else {
            return this.quantityDropped(random);
        }
    }

    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);

        if (this.getItemDropped(state, world.rand, fortune) != Item.getItemFromBlock(this)) {
            int i = 0;

            if (this == Blocks.coal_ore) {
                i = MathHelper.getRandomIntegerInRange(world.rand, 0, 2);
            } else if (this == Blocks.diamond_ore) {
                i = MathHelper.getRandomIntegerInRange(world.rand, 3, 7);
            } else if (this == Blocks.emerald_ore) {
                i = MathHelper.getRandomIntegerInRange(world.rand, 3, 7);
            } else if (this == Blocks.lapis_ore) {
                i = MathHelper.getRandomIntegerInRange(world.rand, 2, 5);
            } else if (this == Blocks.quartz_ore) {
                i = MathHelper.getRandomIntegerInRange(world.rand, 2, 5);
            }

            this.dropXpOnBlockBreak(world, pos, i);
        }
    }

    public int getDamageValue(World world, BlockPos pos) {
        return 0;
    }

    public int damageDropped(IBlockState state)
    {
        return this == Blocks.lapis_ore ? EnumDyeColor.BLUE.getDyeDamage() : 0;
    }

}
