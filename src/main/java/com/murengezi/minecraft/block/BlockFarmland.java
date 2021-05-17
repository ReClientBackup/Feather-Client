package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFarmland extends Block {

    public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, 7);

    protected BlockFarmland() {
        super(Material.ground);
        this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, 0));
        this.setTickRandomly(true);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        this.setLightOpacity(255);
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        int i = state.getValue(MOISTURE);

        if (!this.hasWater(world, pos) && !world.canLightningStrike(pos.up())) {
            if (i > 0) {
                world.setBlockState(pos, state.withProperty(MOISTURE, i - 1), 2);
            } else if (!this.hasCrops(world, pos)) {
                world.setBlockState(pos, Blocks.dirt.getDefaultState());
            }
        } else if (i < 7) {
            world.setBlockState(pos, state.withProperty(MOISTURE, 7), 2);
        }
    }

    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        if (entity instanceof EntityLivingBase) {
            if (!world.isRemote && world.rand.nextFloat() < fallDistance - 0.5F) {
                if (!(entity instanceof EntityPlayer) && !world.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                    return;
                }

                world.setBlockState(pos, Blocks.dirt.getDefaultState());
            }

            super.onFallenUpon(world, pos, entity, fallDistance);
        }
    }

    private boolean hasCrops(World world, BlockPos pos) {
        Block block = world.getBlockState(pos.up()).getBlock();
        return block instanceof BlockCrops || block instanceof BlockStem;
    }

    private boolean hasWater(World world, BlockPos pos) {
        for (BlockPos.MutableBlockPos blockPos : BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (world.getBlockState(blockPos).getBlock().getMaterial() == Material.water) {
                return true;
            }
        }

        return false;
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChange(world, pos, state, neighborBlock);

        if (world.getBlockState(pos.up()).getBlock().getMaterial().isSolid()) {
            world.setBlockState(pos, Blocks.dirt.getDefaultState());
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        switch (side) {
            case UP:
                return true;

            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                Block block = world.getBlockState(pos).getBlock();
                return !block.isOpaqueCube() && block != Blocks.farmland;

            default:
                return super.shouldSideBeRendered(world, pos, side);
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
    }

    public Item getItem(World world, BlockPos pos) {
        return Item.getItemFromBlock(Blocks.dirt);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(MOISTURE, meta & 7);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(MOISTURE);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, MOISTURE);
    }
}
