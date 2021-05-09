package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockIce extends BlockBreakable {

    public BlockIce() {
        super(Material.ice, false);
        this.slipperiness = 0.98F;
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
        player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
        player.addExhaustion(0.025F);

        if (this.canSilkHarvest() && EnchantmentHelper.getSilkTouchModifier(player)) {
            ItemStack itemstack = this.createStackedBlock(state);

            if (itemstack != null) {
                spawnAsEntity(world, pos, itemstack);
            }
        } else {
            if (world.provider.doesWaterVaporize()) {
                world.setBlockToAir(pos);
                return;
            }

            int i = EnchantmentHelper.getFortuneModifier(player);
            this.dropBlockAsItem(world, pos, state, i);
            Material material = world.getBlockState(pos.down()).getBlock().getMaterial();

            if (material.blocksMovement() || material.isLiquid()) {
                world.setBlockState(pos, Blocks.flowing_water.getDefaultState());
            }
        }
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - this.getLightOpacity()) {
            if (world.provider.doesWaterVaporize()) {
                world.setBlockToAir(pos);
            } else {
                this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
                world.setBlockState(pos, Blocks.water.getDefaultState());
            }
        }
    }

    public int getMobilityFlag() {
        return 0;
    }

}
