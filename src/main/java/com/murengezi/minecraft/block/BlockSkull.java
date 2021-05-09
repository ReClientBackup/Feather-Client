package com.murengezi.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.properties.PropertyDirection;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.BlockWorldState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.block.state.pattern.BlockPattern;
import com.murengezi.minecraft.block.state.pattern.BlockStateHelper;
import com.murengezi.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool NODROP = PropertyBool.create("nodrop");
    private static final Predicate<BlockWorldState> IS_WITHER_SKELETON = p_apply_1_ -> p_apply_1_.getBlockState() != null && p_apply_1_.getBlockState().getBlock() == Blocks.skull && p_apply_1_.getTileEntity() instanceof TileEntitySkull && ((TileEntitySkull)p_apply_1_.getTileEntity()).getSkullType() == 1;
    private BlockPattern witherBasePattern;
    private BlockPattern witherPattern;

    protected BlockSkull() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NODROP, false));
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal("tile.skull.skeleton.name");
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        switch (world.getBlockState(pos).getValue(FACING)) {
            case UP:
            default:
                this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;
            case NORTH:
                this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                break;
            case SOUTH:
                this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                break;
            case WEST:
                this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;
            case EAST:
                this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        this.setBlockBoundsBasedOnState(world, pos);
        return super.getCollisionBoundingBox(world, pos, state);
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(NODROP, false);
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySkull();
    }

    public Item getItem(World world, BlockPos pos) {
        return Items.skull;
    }

    public int getDamageValue(World world, BlockPos pos) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity instanceof TileEntitySkull ? ((TileEntitySkull)tileentity).getSkullType() : super.getDamageValue(world, pos);
    }

    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {}

    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            state = state.withProperty(NODROP, true);
            world.setBlockState(pos, state, 4);
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            if (!state.getValue(NODROP)) {
                TileEntity tileentity = world.getTileEntity(pos);

                if (tileentity instanceof TileEntitySkull) {
                    TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
                    ItemStack itemstack = new ItemStack(Items.skull, 1, this.getDamageValue(world, pos));

                    if (tileentityskull.getSkullType() == 3 && tileentityskull.getPlayerProfile() != null) {
                        itemstack.setTagCompound(new NBTTagCompound());
                        NBTTagCompound nbttagcompound = new NBTTagCompound();
                        NBTUtil.writeGameProfile(nbttagcompound, tileentityskull.getPlayerProfile());
                        itemstack.getTagCompound().setTag("SkullOwner", nbttagcompound);
                    }

                    spawnAsEntity(world, pos, itemstack);
                }
            }

            super.breakBlock(world, pos, state);
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.skull;
    }

    public boolean canDispenserPlace(World world, BlockPos pos, ItemStack stack) {
        return stack.getMetadata() == 1 && pos.getY() >= 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL && !world.isRemote && this.getWitherBasePattern().match(world, pos) != null;
    }

    public void checkWitherSpawn(World world, BlockPos pos, TileEntitySkull te) {
        if (te.getSkullType() == 1 && pos.getY() >= 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL && !world.isRemote) {
            BlockPattern blockpattern = this.getWitherPattern();
            BlockPattern.PatternHelper patternHelper = blockpattern.match(world, pos);

            if (patternHelper != null) {
                for (int i = 0; i < 3; ++i) {
                    BlockWorldState blockworldstate = patternHelper.translateOffset(i, 0, 0);
                    world.setBlockState(blockworldstate.getPos(), blockworldstate.getBlockState().withProperty(NODROP, true), 2);
                }

                for (int j = 0; j < blockpattern.getPalmLength(); ++j) {
                    for (int k = 0; k < blockpattern.getThumbLength(); ++k) {
                        BlockWorldState blockworldstate1 = patternHelper.translateOffset(j, k, 0);
                        world.setBlockState(blockworldstate1.getPos(), Blocks.air.getDefaultState(), 2);
                    }
                }

                BlockPos blockpos = patternHelper.translateOffset(1, 0, 0).getPos();
                EntityWither entitywither = new EntityWither(world);
                BlockPos blockpos1 = patternHelper.translateOffset(1, 2, 0).getPos();
                entitywither.setLocationAndAngles((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.55D, (double)blockpos1.getZ() + 0.5D, patternHelper.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F);
                entitywither.renderYawOffset = patternHelper.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F;
                entitywither.func_82206_m();

                for (EntityPlayer entityplayer : world.getEntitiesWithinAABB(EntityPlayer.class, entitywither.getEntityBoundingBox().expand(50.0D, 50.0D, 50.0D))) {
                    entityplayer.triggerAchievement(AchievementList.spawnWither);
                }

                world.spawnEntityInWorld(entitywither);

                for (int l = 0; l < 120; ++l) {
                    world.spawnParticle(EnumParticleTypes.SNOWBALL, (double)blockpos.getX() + world.rand.nextDouble(), (double)(blockpos.getY() - 2) + world.rand.nextDouble() * 3.9D, (double)blockpos.getZ() + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                }

                for (int i1 = 0; i1 < blockpattern.getPalmLength(); ++i1) {
                    for (int j1 = 0; j1 < blockpattern.getThumbLength(); ++j1) {
                        BlockWorldState blockworldstate2 = patternHelper.translateOffset(i1, j1, 0);
                        world.notifyNeighborsRespectDebug(blockworldstate2.getPos(), Blocks.air);
                    }
                }
            }
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(NODROP, (meta & 8) > 0);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(NODROP)) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, NODROP);
    }

    protected BlockPattern getWitherBasePattern() {
        if (this.witherBasePattern == null) {
            this.witherBasePattern = FactoryBlockPattern.start().aisle("   ", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.witherBasePattern;
    }

    protected BlockPattern getWitherPattern() {
        if (this.witherPattern == null) {
            this.witherPattern = FactoryBlockPattern.start().aisle("^^^", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand))).where('^', IS_WITHER_SKELETON).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.witherPattern;
    }

}
