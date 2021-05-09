package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block {

    public BlockDragonEgg() {
        super(Material.dragonEgg, MapColor.blackColor);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.scheduleUpdate(pos, this, this.tickRate(world));
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        world.scheduleUpdate(pos, this, this.tickRate(world));
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        this.checkFall(world, pos);
    }

    private void checkFall(World world, BlockPos pos) {
        if (BlockFalling.canFallInto(world, pos.down()) && pos.getY() >= 0) {
            int i = 32;

            if (!BlockFalling.fallInstantly && world.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                world.spawnEntityInWorld(new EntityFallingBlock(world, (float)pos.getX() + 0.5F, pos.getY(), (float)pos.getZ() + 0.5F, this.getDefaultState()));
            } else {
                world.setBlockToAir(pos);
                BlockPos blockpos;

                for (blockpos = pos; BlockFalling.canFallInto(world, blockpos) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

                if (blockpos.getY() > 0) {
                    world.setBlockState(blockpos, this.getDefaultState(), 2);
                }
            }
        }
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        this.teleport(world, pos);
        return true;
    }

    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        this.teleport(world, pos);
    }

    private void teleport(World world, BlockPos pos) {
        IBlockState iblockstate = world.getBlockState(pos);

        if (iblockstate.getBlock() == this) {
            for (int i = 0; i < 1000; ++i) {
                BlockPos blockpos = pos.add(world.rand.nextInt(16) - world.rand.nextInt(16), world.rand.nextInt(8) - world.rand.nextInt(8), world.rand.nextInt(16) - world.rand.nextInt(16));

                if (world.getBlockState(blockpos).getBlock().blockMaterial == Material.air) {
                    if (world.isRemote) {
                        for (int j = 0; j < 128; ++j) {
                            double d0 = world.rand.nextDouble();
                            float f = (world.rand.nextFloat() - 0.5F) * 0.2F;
                            float f1 = (world.rand.nextFloat() - 0.5F) * 0.2F;
                            float f2 = (world.rand.nextFloat() - 0.5F) * 0.2F;
                            double d1 = (double)blockpos.getX() + (double)(pos.getX() - blockpos.getX()) * d0 + (world.rand.nextDouble() - 0.5D) + 0.5D;
                            double d2 = (double)blockpos.getY() + (double)(pos.getY() - blockpos.getY()) * d0 + world.rand.nextDouble() - 0.5D;
                            double d3 = (double)blockpos.getZ() + (double)(pos.getZ() - blockpos.getZ()) * d0 + (world.rand.nextDouble() - 0.5D) + 0.5D;
                            world.spawnParticle(EnumParticleTypes.PORTAL, d1, d2, d3, f, f1, f2);
                        }
                    } else {
                        world.setBlockState(blockpos, iblockstate, 2);
                        world.setBlockToAir(pos);
                    }

                    return;
                }
            }
        }
    }

    public int tickRate(World world) {
        return 5;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    public Item getItem(World world, BlockPos pos) {
        return null;
    }

}
