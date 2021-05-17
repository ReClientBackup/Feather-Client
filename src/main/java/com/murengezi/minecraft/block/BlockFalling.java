package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockFalling extends Block {

    public static boolean fallInstantly;

    public BlockFalling() {
        super(Material.sand);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public BlockFalling(Material material) {
        super(material);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.scheduleUpdate(pos, this, this.tickRate(world));
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        world.scheduleUpdate(pos, this, this.tickRate(world));
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            this.checkFallable(world, pos);
        }
    }

    private void checkFallable(World world, BlockPos pos) {
        if (canFallInto(world, pos.down()) && pos.getY() >= 0) {
            int i = 32;

            if (!fallInstantly && world.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                if (!world.isRemote) {
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D, world.getBlockState(pos));
                    this.onStartFalling(entityfallingblock);
                    world.spawnEntityInWorld(entityfallingblock);
                }
            } else {
                world.setBlockToAir(pos);
                BlockPos blockpos;

                for (blockpos = pos.down(); canFallInto(world, blockpos) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

                if (blockpos.getY() > 0) {
                    world.setBlockState(blockpos.up(), this.getDefaultState());
                }
            }
        }
    }

    protected void onStartFalling(EntityFallingBlock fallingEntity) {}

    public int tickRate(World world) {
        return 2;
    }

    public static boolean canFallInto(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        Material material = block.blockMaterial;
        return block == Blocks.fire || material == Material.air || material == Material.water || material == Material.lava;
    }

    public void onEndFalling(World world, BlockPos pos) {}

}
