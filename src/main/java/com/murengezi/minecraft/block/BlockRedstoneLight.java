package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneLight extends Block {

    private final boolean isOn;

    public BlockRedstoneLight(boolean isOn) {
        super(Material.redstoneLight);
        this.isOn = isOn;

        if (isOn) {
            this.setLightLevel(1.0F);
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            if (this.isOn && !world.isBlockPowered(pos)) {
                world.setBlockState(pos, Blocks.redstone_lamp.getDefaultState(), 2);
            } else if (!this.isOn && world.isBlockPowered(pos)) {
                world.setBlockState(pos, Blocks.lit_redstone_lamp.getDefaultState(), 2);
            }
        }
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!world.isRemote) {
            if (this.isOn && !world.isBlockPowered(pos)) {
                world.scheduleUpdate(pos, this, 4);
            } else if (!this.isOn && world.isBlockPowered(pos)) {
                world.setBlockState(pos, Blocks.lit_redstone_lamp.getDefaultState(), 2);
            }
        }
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            if (this.isOn && !world.isBlockPowered(pos)) {
                world.setBlockState(pos, Blocks.redstone_lamp.getDefaultState(), 2);
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.redstone_lamp);
    }

    public Item getItem(World world, BlockPos pos) {
        return Item.getItemFromBlock(Blocks.redstone_lamp);
    }

    protected ItemStack createStackedBlock(IBlockState state) {
        return new ItemStack(Blocks.redstone_lamp);
    }

}
