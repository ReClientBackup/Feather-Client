package com.murengezi.minecraft.block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.properties.PropertyDirection;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<BlockStairs.EnumHalf> HALF = PropertyEnum.create("half", BlockStairs.EnumHalf.class);
    public static final PropertyEnum<BlockStairs.EnumShape> SHAPE = PropertyEnum.create("shape", BlockStairs.EnumShape.class);
    private static final int[][] field_150150_a = new int[][] {{4, 5}, {5, 7}, {6, 7}, {4, 6}, {0, 1}, {1, 3}, {2, 3}, {0, 2}};
    private final Block modelBlock;
    private final IBlockState modelState;
    private boolean hasRaytraced;
    private int rayTracePass;

    protected BlockStairs(IBlockState modelState) {
        super(modelState.getBlock().blockMaterial);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT));
        this.modelBlock = modelState.getBlock();
        this.modelState = modelState;
        this.setHardness(this.modelBlock.blockHardness);
        this.setResistance(this.modelBlock.blockResistance / 3.0F);
        this.setStepSound(this.modelBlock.stepSound);
        this.setLightOpacity(255);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        if (this.hasRaytraced) {
            this.setBlockBounds(0.5F * (float)(this.rayTracePass % 2), 0.5F * (float)(this.rayTracePass / 4 % 2), 0.5F * (float)(this.rayTracePass / 2 % 2), 0.5F + 0.5F * (float)(this.rayTracePass % 2), 0.5F + 0.5F * (float)(this.rayTracePass / 4 % 2), 0.5F + 0.5F * (float)(this.rayTracePass / 2 % 2));
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public void setBaseCollisionBounds(IBlockAccess world, BlockPos pos) {
        if (world.getBlockState(pos).getValue(HALF) == BlockStairs.EnumHalf.TOP) {
            this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    public static boolean isBlockStairs(Block block) {
        return block instanceof BlockStairs;
    }

    public static boolean isSameStair(IBlockAccess world, BlockPos pos, IBlockState state) {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return isBlockStairs(block) && iblockstate.getValue(HALF) == state.getValue(HALF) && iblockstate.getValue(FACING) == state.getValue(FACING);
    }

    public int func_176307_f(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = iblockstate.getValue(FACING);
        BlockStairs.EnumHalf half = iblockstate.getValue(HALF);
        boolean flag = half == BlockStairs.EnumHalf.TOP;

        if (enumfacing == EnumFacing.EAST) {
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.east());
            Block block = iblockstate1.getBlock();

            if (isBlockStairs(block) && half == iblockstate1.getValue(HALF)) {
                EnumFacing enumfacing1 = iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    return flag ? 1 : 2;
                }

                if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    return flag ? 2 : 1;
                }
            }
        } else if (enumfacing == EnumFacing.WEST) {
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.west());
            Block block1 = iblockstate2.getBlock();

            if (isBlockStairs(block1) && half == iblockstate2.getValue(HALF)) {
                EnumFacing enumfacing2 = iblockstate2.getValue(FACING);

                if (enumfacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    return flag ? 2 : 1;
                }

                if (enumfacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    return flag ? 1 : 2;
                }
            }
        } else if (enumfacing == EnumFacing.SOUTH) {
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.south());
            Block block2 = iblockstate3.getBlock();

            if (isBlockStairs(block2) && half == iblockstate3.getValue(HALF)) {
                EnumFacing enumfacing3 = iblockstate3.getValue(FACING);

                if (enumfacing3 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    return flag ? 2 : 1;
                }

                if (enumfacing3 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    return flag ? 1 : 2;
                }
            }
        } else if (enumfacing == EnumFacing.NORTH) {
            IBlockState iblockstate4 = blockAccess.getBlockState(pos.north());
            Block block3 = iblockstate4.getBlock();

            if (isBlockStairs(block3) && half == iblockstate4.getValue(HALF)) {
                EnumFacing enumfacing4 = iblockstate4.getValue(FACING);

                if (enumfacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    return flag ? 1 : 2;
                }

                if (enumfacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    return flag ? 2 : 1;
                }
            }
        }

        return 0;
    }

    public int func_176305_g(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = iblockstate.getValue(FACING);
        BlockStairs.EnumHalf blockstairs$enumhalf = iblockstate.getValue(HALF);
        boolean flag = blockstairs$enumhalf == BlockStairs.EnumHalf.TOP;

        if (enumfacing == EnumFacing.EAST) {
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.west());
            Block block = iblockstate1.getBlock();

            if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF)) {
                EnumFacing enumfacing1 = iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    return flag ? 1 : 2;
                }

                if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    return flag ? 2 : 1;
                }
            }
        } else if (enumfacing == EnumFacing.WEST) {
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.east());
            Block block1 = iblockstate2.getBlock();

            if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF)) {
                EnumFacing enumfacing2 = iblockstate2.getValue(FACING);

                if (enumfacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    return flag ? 2 : 1;
                }

                if (enumfacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    return flag ? 1 : 2;
                }
            }
        } else if (enumfacing == EnumFacing.SOUTH) {
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.north());
            Block block2 = iblockstate3.getBlock();

            if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF)) {
                EnumFacing enumfacing3 = iblockstate3.getValue(FACING);

                if (enumfacing3 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    return flag ? 2 : 1;
                }

                if (enumfacing3 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    return flag ? 1 : 2;
                }
            }
        } else if (enumfacing == EnumFacing.NORTH) {
            IBlockState iblockstate4 = blockAccess.getBlockState(pos.south());
            Block block3 = iblockstate4.getBlock();

            if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.getValue(HALF)) {
                EnumFacing enumfacing4 = iblockstate4.getValue(FACING);

                if (enumfacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    return flag ? 1 : 2;
                }

                if (enumfacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    return flag ? 2 : 1;
                }
            }
        }

        return 0;
    }

    public boolean func_176306_h(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = iblockstate.getValue(FACING);
        BlockStairs.EnumHalf blockstairs$enumhalf = iblockstate.getValue(HALF);
        boolean flag = blockstairs$enumhalf == BlockStairs.EnumHalf.TOP;
        float f = 0.5F;
        float f1 = 1.0F;

        if (flag) {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.5F;
        boolean flag1 = true;

        if (enumfacing == EnumFacing.EAST) {
            f2 = 0.5F;
            f5 = 1.0F;
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.east());
            Block block = iblockstate1.getBlock();

            if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF)) {
                EnumFacing enumfacing1 = iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f5 = 0.5F;
                    flag1 = false;
                } else if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        } else if (enumfacing == EnumFacing.WEST) {
            f3 = 0.5F;
            f5 = 1.0F;
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.west());
            Block block1 = iblockstate2.getBlock();

            if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF)) {
                EnumFacing enumfacing2 = iblockstate2.getValue(FACING);

                if (enumfacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f5 = 0.5F;
                    flag1 = false;
                } else if (enumfacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        } else if (enumfacing == EnumFacing.SOUTH) {
            f4 = 0.5F;
            f5 = 1.0F;
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.south());
            Block block2 = iblockstate3.getBlock();

            if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF)) {
                EnumFacing enumfacing3 = iblockstate3.getValue(FACING);

                if (enumfacing3 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    f3 = 0.5F;
                    flag1 = false;
                } else if (enumfacing3 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        } else if (enumfacing == EnumFacing.NORTH) {
            IBlockState iblockstate4 = blockAccess.getBlockState(pos.north());
            Block block3 = iblockstate4.getBlock();

            if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.getValue(HALF)) {
                EnumFacing enumfacing4 = iblockstate4.getValue(FACING);

                if (enumfacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    f3 = 0.5F;
                    flag1 = false;
                } else if (enumfacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        }

        this.setBlockBounds(f2, f, f4, f3, f1, f5);
        return flag1;
    }

    public boolean func_176304_i(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = iblockstate.getValue(FACING);
        BlockStairs.EnumHalf blockstairs$enumhalf = iblockstate.getValue(HALF);
        boolean flag = blockstairs$enumhalf == BlockStairs.EnumHalf.TOP;
        float f = 0.5F;
        float f1 = 1.0F;

        if (flag) {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = 0.5F;
        float f5 = 1.0F;
        boolean flag1 = false;

        if (enumfacing == EnumFacing.EAST) {
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.west());
            Block block = iblockstate1.getBlock();

            if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF)) {
                EnumFacing enumfacing1 = iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag1 = true;
                } else if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag1 = true;
                }
            }
        } else if (enumfacing == EnumFacing.WEST) {
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.east());
            Block block1 = iblockstate2.getBlock();

            if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF)) {
                f2 = 0.5F;
                f3 = 1.0F;
                EnumFacing enumfacing2 = iblockstate2.getValue(FACING);

                if (enumfacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag1 = true;
                } else if (enumfacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag1 = true;
                }
            }
        } else if (enumfacing == EnumFacing.SOUTH) {
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.north());
            Block block2 = iblockstate3.getBlock();

            if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF)) {
                f4 = 0.0F;
                f5 = 0.5F;
                EnumFacing enumfacing3 = iblockstate3.getValue(FACING);

                if (enumfacing3 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    flag1 = true;
                } else if (enumfacing3 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag1 = true;
                }
            }
        } else if (enumfacing == EnumFacing.NORTH) {
            IBlockState iblockstate4 = blockAccess.getBlockState(pos.south());
            Block block3 = iblockstate4.getBlock();

            if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.getValue(HALF)) {
                EnumFacing enumfacing4 = iblockstate4.getValue(FACING);

                if (enumfacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    flag1 = true;
                } else if (enumfacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag1 = true;
                }
            }
        }

        if (flag1) {
            this.setBlockBounds(f2, f, f4, f3, f1, f5);
        }

        return flag1;
    }

    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        this.setBaseCollisionBounds(world, pos);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        boolean flag = this.func_176306_h(world, pos);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);

        if (flag && this.func_176304_i(world, pos)) {
            super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        this.modelBlock.randomDisplayTick(world, pos, state, rand);
    }

    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        this.modelBlock.onBlockClicked(world, pos, player);
    }

    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        this.modelBlock.onBlockDestroyedByPlayer(world, pos, state);
    }

    public int getMixedBrightnessForBlock(IBlockAccess world, BlockPos pos) {
        return this.modelBlock.getMixedBrightnessForBlock(world, pos);
    }

    public float getExplosionResistance(Entity exploder) {
        return this.modelBlock.getExplosionResistance(exploder);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return this.modelBlock.getBlockLayer();
    }

    public int tickRate(World world) {
        return this.modelBlock.tickRate(world);
    }

    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        return this.modelBlock.getSelectedBoundingBox(world, pos);
    }

    public Vec3 modifyAcceleration(World world, BlockPos pos, Entity entityIn, Vec3 motion) {
        return this.modelBlock.modifyAcceleration(world, pos, entityIn, motion);
    }

    public boolean isCollidable() {
        return this.modelBlock.isCollidable();
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return this.modelBlock.canCollideCheck(state, hitIfLiquid);
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return this.modelBlock.canPlaceBlockAt(world, pos);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        this.onNeighborBlockChange(world, pos, this.modelState, Blocks.air);
        this.modelBlock.onBlockAdded(world, pos, this.modelState);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        this.modelBlock.breakBlock(world, pos, this.modelState);
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
        this.modelBlock.onEntityCollidedWithBlock(world, pos, entity);
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        this.modelBlock.updateTick(world, pos, state, rand);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        return this.modelBlock.onBlockActivated(world, pos, this.modelState, player, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
    }

    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
        this.modelBlock.onBlockDestroyedByExplosion(world, pos, explosionIn);
    }

    public MapColor getMapColor(IBlockState state) {
        return this.modelBlock.getMapColor(this.modelState);
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        iblockstate = iblockstate.withProperty(FACING, placer.getHorizontalFacing()).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
        return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double)hitY <= 0.5D) ? iblockstate.withProperty(HALF, BlockStairs.EnumHalf.BOTTOM) : iblockstate.withProperty(HALF, BlockStairs.EnumHalf.TOP);
    }

    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
        MovingObjectPosition[] positions = new MovingObjectPosition[8];
        IBlockState iblockstate = world.getBlockState(pos);
        int i = iblockstate.getValue(FACING).getHorizontalIndex();
        boolean flag = iblockstate.getValue(HALF) == BlockStairs.EnumHalf.TOP;
        int[] aint = field_150150_a[i + (flag ? 4 : 0)];
        this.hasRaytraced = true;

        for (int j = 0; j < 8; ++j) {
            this.rayTracePass = j;

            if (Arrays.binarySearch(aint, j) < 0) {
                positions[j] = super.collisionRayTrace(world, pos, start, end);
            }
        }

        for (int k : aint) {
            positions[k] = null;
        }

        MovingObjectPosition movingobjectposition1 = null;
        double d1 = 0.0D;

        for (MovingObjectPosition movingobjectposition : positions) {
            if (movingobjectposition != null) {
                double d0 = movingobjectposition.hitVec.squareDistanceTo(end);

                if (d0 > d1) {
                    movingobjectposition1 = movingobjectposition;
                    d1 = d0;
                }
            }
        }

        return movingobjectposition1;
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(HALF, (meta & 4) > 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM);
        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
        return iblockstate;
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;

        if (state.getValue(HALF) == BlockStairs.EnumHalf.TOP) {
            i |= 4;
        }

        i = i | 5 - state.getValue(FACING).getIndex();
        return i;
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (this.func_176306_h(world, pos)) {
            switch (this.func_176305_g(world, pos)) {
                case 0:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
                    break;
                case 1:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_RIGHT);
                    break;
                case 2:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_LEFT);
            }
        } else {
            switch (this.func_176307_f(world, pos)) {
                case 0:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
                    break;
                case 1:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
                    break;
                case 2:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
            }
        }

        return state;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, HALF, SHAPE);
    }

    public enum EnumHalf implements IStringSerializable {
        TOP("top"), BOTTOM("bottom");

        private final String name;

        EnumHalf(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum EnumShape implements IStringSerializable {
        STRAIGHT("straight"), INNER_LEFT("inner_left"), INNER_RIGHT("inner_right"), OUTER_LEFT("outer_left"), OUTER_RIGHT("outer_right");

        private final String name;

        EnumShape(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

}
