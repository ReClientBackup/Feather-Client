package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {

    protected BlockBasePressurePlate(Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    protected BlockBasePressurePlate(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        this.setBlockBoundsBasedOnState0(world.getBlockState(pos));
    }

    protected void setBlockBoundsBasedOnState0(IBlockState state) {
        boolean flag = this.getRedstoneStrength(state) > 0;

        if (flag) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
        } else {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World world) {
        return 20;
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return null;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return true;
    }

    public boolean canSpawnInBlock() {
        return true;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return this.canBePlacedOn(world, pos.down());
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!this.canBePlacedOn(world, pos.down())) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    private boolean canBePlacedOn(World world, BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(world, pos) || world.getBlockState(pos).getBlock() instanceof BlockFence;
    }

    /**
     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
     */
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {}

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            int i = this.getRedstoneStrength(state);

            if (i > 0) {
                this.updateState(world, pos, state, i);
            }
        }
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!world.isRemote) {
            int i = this.getRedstoneStrength(state);

            if (i == 0) {
                this.updateState(world, pos, state, i);
            }
        }
    }

    /**
     * Updates the pressure plate when stepped on
     */
    protected void updateState(World world, BlockPos pos, IBlockState state, int oldRedstoneStrength) {
        int i = this.computeRedstoneStrength(world, pos);
        boolean flag = oldRedstoneStrength > 0;
        boolean flag1 = i > 0;

        if (oldRedstoneStrength != i) {
            state = this.setRedstoneStrength(state, i);
            world.setBlockState(pos, state, 2);
            this.updateNeighbors(world, pos);
            world.markBlockRangeForRenderUpdate(pos, pos);
        }

        if (!flag1 && flag) {
            world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.3F, 0.5F);
        } else if (flag1 && !flag) {
            world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (flag1) {
            world.scheduleUpdate(pos, this, this.tickRate(world));
        }
    }

    /**
     * Returns the cubic AABB inset by 1/8 on all sides
     */
    protected AxisAlignedBB getSensitiveAABB(BlockPos pos) {
        float f = 0.125F;
        return new AxisAlignedBB((float)pos.getX() + 0.125F, pos.getY(), (float)pos.getZ() + 0.125F, (float)(pos.getX() + 1) - 0.125F, (double)pos.getY() + 0.25D, (float)(pos.getZ() + 1) - 0.125F);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (this.getRedstoneStrength(state) > 0) {
            this.updateNeighbors(world, pos);
        }

        super.breakBlock(world, pos, state);
    }

    /**
     * Notify block and block below of changes
     */
    protected void updateNeighbors(World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, this);
        world.notifyNeighborsOfStateChange(pos.down(), this);
    }

    public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        return this.getRedstoneStrength(state);
    }

    public int isProvidingStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        return side == EnumFacing.UP ? this.getRedstoneStrength(state) : 0;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return true;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
    }

    public int getMobilityFlag() {
        return 1;
    }

    protected abstract int computeRedstoneStrength(World world, BlockPos pos);

    protected abstract int getRedstoneStrength(IBlockState state);

    protected abstract IBlockState setRedstoneStrength(IBlockState state, int strength);
}
