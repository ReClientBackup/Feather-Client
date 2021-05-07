package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockCactus extends Block {

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

    protected BlockCactus() {
        super(Material.cactus);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        BlockPos blockpos = pos.up();

        if (world.isAirBlock(blockpos)) {
            int i;

            for (i = 1; world.getBlockState(pos.down(i)).getBlock() == this; ++i) {}

            if (i < 3) {
                int j = state.getValue(AGE).intValue();

                if (j == 15) {
                    world.setBlockState(blockpos, this.getDefaultState());
                    IBlockState iblockstate = state.withProperty(AGE, Integer.valueOf(0));
                    world.setBlockState(pos, iblockstate, 4);
                    this.onNeighborBlockChange(world, blockpos, iblockstate, this);
                } else {
                    world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 4);
                }
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        float f = 0.0625F;
        return new AxisAlignedBB((float)pos.getX() + f, pos.getY(), (float)pos.getZ() + f, (float)(pos.getX() + 1) - f, (float)(pos.getY() + 1) - f, (float)(pos.getZ() + 1) - f);
    }

    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        float f = 0.0625F;
        return new AxisAlignedBB((float)pos.getX() + f, pos.getY(), (float)pos.getZ() + f, (float)(pos.getX() + 1) - f, pos.getY() + 1, (float)(pos.getZ() + 1) - f);
    }

    public boolean isFullCube() {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && this.canBlockStay(world, pos);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!this.canBlockStay(world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(World world, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (world.getBlockState(pos.offset(enumfacing)).getBlock().getMaterial().isSolid()) {
                return false;
            }
        }

        Block block = world.getBlockState(pos.down()).getBlock();
        return block == Blocks.cactus || block == Blocks.sand;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.attackEntityFrom(DamageSource.cactus, 1.0F);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE).intValue();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, AGE);
    }

}
