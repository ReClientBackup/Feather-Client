package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBed extends BlockDirectional {

    public static final PropertyEnum<BlockBed.EnumPartType> PART = PropertyEnum.create("part", BlockBed.EnumPartType.class);
    public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");

    public BlockBed() {
        super(Material.cloth);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(OCCUPIED, false));
        this.setBedBounds();
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            if (state.getValue(PART) != BlockBed.EnumPartType.HEAD) {
                pos = pos.offset(state.getValue(FACING));
                state = world.getBlockState(pos);

                if (state.getBlock() != this) {
                    return true;
                }
            }

            if (world.provider.canRespawnHere() && world.getBiomeGenForCoords(pos) != BiomeGenBase.hell) {
                if (state.getValue(OCCUPIED)) {
                    EntityPlayer entityplayer = this.getPlayerInBed(world, pos);

                    if (entityplayer != null) {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied"));
                        return true;
                    }

                    state = state.withProperty(OCCUPIED, Boolean.FALSE);
                    world.setBlockState(pos, state, 4);
                }

                EntityPlayer.EnumStatus entityplayer$enumstatus = player.trySleep(pos);

                if (entityplayer$enumstatus == EntityPlayer.EnumStatus.OK) {
                    state = state.withProperty(OCCUPIED, Boolean.TRUE);
                    world.setBlockState(pos, state, 4);
                    return true;
                } else {
                    if (entityplayer$enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep"));
                    } else if (entityplayer$enumstatus == EntityPlayer.EnumStatus.NOT_SAFE) {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
                    }

                    return true;
                }
            } else {
                world.setBlockToAir(pos);
                BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());

                if (world.getBlockState(blockpos).getBlock() == this) {
                    world.setBlockToAir(blockpos);
                }

                world.newExplosion(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, true);
                return true;
            }
        }
    }

    private EntityPlayer getPlayerInBed(World world, BlockPos pos) {
        for (EntityPlayer player : world.playerEntities) {
            if (player.isPlayerSleeping() && player.playerLocation.equals(pos)) {
                return player;
            }
        }

        return null;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        this.setBedBounds();
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        EnumFacing enumfacing = state.getValue(FACING);

        if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
            if (world.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this) {
                world.setBlockToAir(pos);
            }
        } else if (world.getBlockState(pos.offset(enumfacing)).getBlock() != this) {
            world.setBlockToAir(pos);

            if (!world.isRemote) {
                this.dropBlockAsItem(world, pos, state, 0);
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? null : Items.bed;
    }

    private void setBedBounds() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
    }

    public static BlockPos getSafeExitLocation(World world, BlockPos pos, int tries) {
        EnumFacing enumfacing = world.getBlockState(pos).getValue(FACING);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (int l = 0; l <= 1; ++l) {
            int i1 = i - enumfacing.getFrontOffsetX() * l - 1;
            int j1 = k - enumfacing.getFrontOffsetZ() * l - 1;
            int k1 = i1 + 2;
            int l1 = j1 + 2;

            for (int i2 = i1; i2 <= k1; ++i2) {
                for (int j2 = j1; j2 <= l1; ++j2) {
                    BlockPos blockpos = new BlockPos(i2, j, j2);

                    if (hasRoomForPlayer(world, blockpos)) {
                        if (tries <= 0) {
                            return blockpos;
                        }

                        --tries;
                    }
                }
            }
        }

        return null;
    }

    protected static boolean hasRoomForPlayer(World world, BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(world, pos.down()) && !world.getBlockState(pos).getBlock().getMaterial().isSolid() && !world.getBlockState(pos.up()).getBlock().getMaterial().isSolid();
    }

    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (state.getValue(PART) == BlockBed.EnumPartType.FOOT) {
            super.dropBlockAsItemWithChance(world, pos, state, chance, 0);
        }
    }

    public int getMobilityFlag() {
        return 1;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public Item getItem(World world, BlockPos pos) {
        return Items.bed;
    }

    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.capabilities.isCreativeMode && state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
            BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());

            if (world.getBlockState(blockpos).getBlock() == this) {
                world.setBlockToAir(blockpos);
            }
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(meta);
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.HEAD).withProperty(FACING, enumfacing).withProperty(OCCUPIED, Boolean.valueOf((meta & 4) > 0)) : this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(FACING, enumfacing);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(PART) == BlockBed.EnumPartType.FOOT) {
            IBlockState iblockstate = world.getBlockState(pos.offset(state.getValue(FACING)));

            if (iblockstate.getBlock() == this) {
                state = state.withProperty(OCCUPIED, iblockstate.getValue(OCCUPIED));
            }
        }

        return state;
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
            i |= 8;

            if (state.getValue(OCCUPIED)) {
                i |= 4;
            }
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, PART, OCCUPIED);
    }

    public enum EnumPartType implements IStringSerializable {
        HEAD("head"), FOOT("foot");

        private final String name;

        EnumPartType(String name) {
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
