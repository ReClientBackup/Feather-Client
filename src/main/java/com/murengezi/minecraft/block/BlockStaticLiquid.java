package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid
{
    protected BlockStaticLiquid(Material material)
    {
        super(material);
        this.setTickRandomly(false);

        if (material == Material.lava)
        {
            this.setTickRandomly(true);
        }
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!this.checkForMixing(world, pos, state))
        {
            this.updateLiquid(world, pos, state);
        }
    }

    private void updateLiquid(World world, BlockPos pos, IBlockState state)
    {
        BlockDynamicLiquid blockdynamicliquid = getFlowingBlock(this.blockMaterial);
        world.setBlockState(pos, blockdynamicliquid.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
        world.scheduleUpdate(pos, blockdynamicliquid, this.tickRate(world));
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.blockMaterial == Material.lava)
        {
            if (world.getGameRules().getGameRuleBooleanValue("doFireTick"))
            {
                int i = rand.nextInt(3);

                if (i > 0)
                {
                    BlockPos blockpos = pos;

                    for (int j = 0; j < i; ++j)
                    {
                        blockpos = blockpos.add(rand.nextInt(3) - 1, 1, rand.nextInt(3) - 1);
                        Block block = world.getBlockState(blockpos).getBlock();

                        if (block.blockMaterial == Material.air)
                        {
                            if (this.isSurroundingBlockFlammable(world, blockpos))
                            {
                                world.setBlockState(blockpos, Blocks.fire.getDefaultState());
                                return;
                            }
                        }
                        else if (block.blockMaterial.blocksMovement())
                        {
                            return;
                        }
                    }
                }
                else
                {
                    for (int k = 0; k < 3; ++k)
                    {
                        BlockPos blockpos1 = pos.add(rand.nextInt(3) - 1, 0, rand.nextInt(3) - 1);

                        if (world.isAirBlock(blockpos1.up()) && this.getCanBlockBurn(world, blockpos1))
                        {
                            world.setBlockState(blockpos1.up(), Blocks.fire.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    protected boolean isSurroundingBlockFlammable(World world, BlockPos pos)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (this.getCanBlockBurn(world, pos.offset(enumfacing)))
            {
                return true;
            }
        }

        return false;
    }

    private boolean getCanBlockBurn(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getBlock().getMaterial().getCanBurn();
    }
}
