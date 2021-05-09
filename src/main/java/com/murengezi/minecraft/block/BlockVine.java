package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVine extends Block {

    public static final PropertyBool UP = PropertyBool.create("up"), NORTH = PropertyBool.create("north"), EAST = PropertyBool.create("east"), SOUTH = PropertyBool.create("south"), WEST = PropertyBool.create("west");
    public static final PropertyBool[] ALL_FACES = new PropertyBool[] {UP, NORTH, SOUTH, WEST, EAST};

    public BlockVine() {
        super(Material.vine);
        this.setDefaultState(this.blockState.getBaseState().withProperty(UP, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(UP, world.getBlockState(pos.up()).getBlock().isBlockNormalCube());
    }

    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isReplaceable(World world, BlockPos pos) {
        return true;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        float f1 = 1.0F;
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag = false;

        if (world.getBlockState(pos).getValue(WEST)) {
            f4 = 0.0625F;
            f1 = 0.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if (world.getBlockState(pos).getValue(EAST)) {
            f1 = Math.min(f1, 0.9375F);
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if (world.getBlockState(pos).getValue(NORTH)) {
            f6 = Math.max(f6, 0.0625F);
            f3 = 0.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if (world.getBlockState(pos).getValue(SOUTH)) {
            f3 = Math.min(f3, 0.9375F);
            f6 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if (!flag && this.canPlaceOn(world.getBlockState(pos.up()).getBlock())) {
            f2 = 0.9375F;
            f5 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
        }

        this.setBlockBounds(f1, f2, f3, f4, f5, f6);
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return null;
    }

    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        switch (side) {
            case UP:
                return this.canPlaceOn(world.getBlockState(pos.up()).getBlock());
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
                return this.canPlaceOn(world.getBlockState(pos.offset(side.getOpposite())).getBlock());
            default:
                return false;
        }
    }

    private boolean canPlaceOn(Block block)
    {
        return block.isFullCube() && block.blockMaterial.blocksMovement();
    }

    private boolean recheckGrownSides(World world, BlockPos pos, IBlockState state) {
        IBlockState iblockstate = state;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            PropertyBool propertybool = getPropertyFor(enumfacing);

            if (state.getValue(propertybool) && !this.canPlaceOn(world.getBlockState(pos.offset(enumfacing)).getBlock())) {
                IBlockState iblockstate1 = world.getBlockState(pos.up());

                if (iblockstate1.getBlock() != this || !iblockstate1.getValue(propertybool)) {
                    state = state.withProperty(propertybool, false);
                }
            }
        }

        if (getNumGrownFaces(state) == 0) {
            return false;
        } else {
            if (iblockstate != state) {
                world.setBlockState(pos, state, 2);
            }

            return true;
        }
    }

    public int getBlockColor() {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    public int getRenderColor(IBlockState state) {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        return world.getBiomeGenForCoords(pos).getFoliageColorAtPos(pos);
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!world.isRemote && !this.recheckGrownSides(world, pos, state)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            if (world.rand.nextInt(4) == 0) {
                int i = 4;
                int j = 5;
                boolean flag = false;
                label62:

                for (int k = -i; k <= i; ++k) {
                    for (int l = -i; l <= i; ++l) {
                        for (int i1 = -1; i1 <= 1; ++i1) {
                            if (world.getBlockState(pos.add(k, i1, l)).getBlock() == this) {
                                --j;

                                if (j <= 0) {
                                    flag = true;
                                    break label62;
                                }
                            }
                        }
                    }
                }

                EnumFacing enumfacing1 = EnumFacing.random(rand);
                BlockPos blockpos1 = pos.up();

                if (enumfacing1 == EnumFacing.UP && pos.getY() < 255 && world.isAirBlock(blockpos1)) {
                    if (!flag) {
                        IBlockState iblockstate2 = state;

                        for (EnumFacing enumfacing3 : EnumFacing.Plane.HORIZONTAL) {
                            if (rand.nextBoolean() || !this.canPlaceOn(world.getBlockState(blockpos1.offset(enumfacing3)).getBlock())) {
                                iblockstate2 = iblockstate2.withProperty(getPropertyFor(enumfacing3), false);
                            }
                        }

                        if (iblockstate2.getValue(NORTH) || iblockstate2.getValue(EAST) || iblockstate2.getValue(SOUTH) || iblockstate2.getValue(WEST)) {
                            world.setBlockState(blockpos1, iblockstate2, 2);
                        }
                    }
                } else if (enumfacing1.getAxis().isHorizontal() && !state.getValue(getPropertyFor(enumfacing1))) {
                    if (!flag) {
                        BlockPos blockpos3 = pos.offset(enumfacing1);
                        Block block1 = world.getBlockState(blockpos3).getBlock();

                        if (block1.blockMaterial == Material.air) {
                            EnumFacing enumfacing2 = enumfacing1.rotateY();
                            EnumFacing enumfacing4 = enumfacing1.rotateYCCW();
                            boolean flag1 = state.getValue(getPropertyFor(enumfacing2));
                            boolean flag2 = state.getValue(getPropertyFor(enumfacing4));
                            BlockPos blockpos4 = blockpos3.offset(enumfacing2);
                            BlockPos blockpos = blockpos3.offset(enumfacing4);

                            if (flag1 && this.canPlaceOn(world.getBlockState(blockpos4).getBlock())) {
                                world.setBlockState(blockpos3, this.getDefaultState().withProperty(getPropertyFor(enumfacing2), true), 2);
                            } else if (flag2 && this.canPlaceOn(world.getBlockState(blockpos).getBlock())) {
                                world.setBlockState(blockpos3, this.getDefaultState().withProperty(getPropertyFor(enumfacing4), true), 2);
                            } else if (flag1 && world.isAirBlock(blockpos4) && this.canPlaceOn(world.getBlockState(pos.offset(enumfacing2)).getBlock())) {
                                world.setBlockState(blockpos4, this.getDefaultState().withProperty(getPropertyFor(enumfacing1.getOpposite()), true), 2);
                            } else if (flag2 && world.isAirBlock(blockpos) && this.canPlaceOn(world.getBlockState(pos.offset(enumfacing4)).getBlock())) {
                                world.setBlockState(blockpos, this.getDefaultState().withProperty(getPropertyFor(enumfacing1.getOpposite()), true), 2);
                            } else if (this.canPlaceOn(world.getBlockState(blockpos3.up()).getBlock())) {
                                world.setBlockState(blockpos3, this.getDefaultState(), 2);
                            }
                        } else if (block1.blockMaterial.isOpaque() && block1.isFullCube()) {
                            world.setBlockState(pos, state.withProperty(getPropertyFor(enumfacing1), true), 2);
                        }
                    }
                } else {
                    if (pos.getY() > 1) {
                        BlockPos blockpos2 = pos.down();
                        IBlockState iblockstate = world.getBlockState(blockpos2);
                        Block block = iblockstate.getBlock();

                        if (block.blockMaterial == Material.air) {
                            IBlockState iblockstate1 = state;

                            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                                if (rand.nextBoolean()) {
                                    iblockstate1 = iblockstate1.withProperty(getPropertyFor(enumfacing), false);
                                }
                            }

                            if (iblockstate1.getValue(NORTH) || iblockstate1.getValue(EAST) || iblockstate1.getValue(SOUTH) || iblockstate1.getValue(WEST)) {
                                world.setBlockState(blockpos2, iblockstate1, 2);
                            }
                        } else if (block == this) {
                            IBlockState iblockstate3 = iblockstate;

                            for (EnumFacing enumfacing5 : EnumFacing.Plane.HORIZONTAL) {
                                PropertyBool propertybool = getPropertyFor(enumfacing5);

                                if (rand.nextBoolean() && state.getValue(propertybool)) {
                                    iblockstate3 = iblockstate3.withProperty(propertybool, true);
                                }
                            }

                            if (iblockstate3.getValue(NORTH) || iblockstate3.getValue(EAST) || iblockstate3.getValue(SOUTH) || iblockstate3.getValue(WEST)) {
                                world.setBlockState(blockpos2, iblockstate3, 2);
                            }
                        }
                    }
                }
            }
        }
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = this.getDefaultState().withProperty(UP, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false);
        return facing.getAxis().isHorizontal() ? iblockstate.withProperty(getPropertyFor(facing.getOpposite()), true) : iblockstate;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
        if (!world.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            spawnAsEntity(world, pos, new ItemStack(Blocks.vine, 1, 0));
        } else {
            super.harvestBlock(world, player, pos, state, te);
        }
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(SOUTH, (meta & 1) > 0).withProperty(WEST, (meta & 2) > 0).withProperty(NORTH, (meta & 4) > 0).withProperty(EAST, (meta & 8) > 0);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;

        if (state.getValue(SOUTH)) {
            i |= 1;
        }

        if (state.getValue(WEST)) {
            i |= 2;
        }

        if (state.getValue(NORTH)) {
            i |= 4;
        }

        if (state.getValue(EAST)) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, UP, NORTH, EAST, SOUTH, WEST);
    }

    public static PropertyBool getPropertyFor(EnumFacing side) {
        switch (side) {
            case UP:
                return UP;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            default:
                throw new IllegalArgumentException(side + " is an invalid choice");
        }
    }

    public static int getNumGrownFaces(IBlockState state) {
        int i = 0;

        for (PropertyBool propertybool : ALL_FACES) {
            if (state.getValue(propertybool)) {
                ++i;
            }
        }

        return i;
    }

}
