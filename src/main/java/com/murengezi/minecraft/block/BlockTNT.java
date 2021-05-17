package com.murengezi.minecraft.block;

import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import com.murengezi.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block {

    public static final PropertyBool EXPLODE = PropertyBool.create("explode");

    public BlockTNT() {
        super(Material.tnt);
        this.setDefaultState(this.blockState.getBaseState().withProperty(EXPLODE, false));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);

        if (world.isBlockPowered(pos)) {
            this.onBlockDestroyedByPlayer(world, pos, state.withProperty(EXPLODE, true));
            world.setBlockToAir(pos);
        }
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (world.isBlockPowered(pos)) {
            this.onBlockDestroyedByPlayer(world, pos, state.withProperty(EXPLODE, true));
            world.setBlockToAir(pos);
        }
    }

    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
        if (!world.isRemote) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (float)pos.getX() + 0.5F, pos.getY(), (float)pos.getZ() + 0.5F, explosionIn.getExplosivePlacedBy());
            entitytntprimed.fuse = world.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
            world.spawnEntityInWorld(entitytntprimed);
        }
    }

    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        this.explode(world, pos, state, null);
    }

    public void explode(World world, BlockPos pos, IBlockState state, EntityLivingBase igniter) {
        if (!world.isRemote) {
            if (state.getValue(EXPLODE)) {
                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (float)pos.getX() + 0.5F, pos.getY(), (float)pos.getZ() + 0.5F, igniter);
                world.spawnEntityInWorld(entitytntprimed);
                world.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
            }
        }
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.getCurrentEquippedItem() != null) {
            Item item = player.getCurrentEquippedItem().getItem();

            if (item == Items.flint_and_steel || item == Items.fire_charge) {
                this.explode(world, pos, state.withProperty(EXPLODE, true), player);
                world.setBlockToAir(pos);

                if (item == Items.flint_and_steel) {
                    player.getCurrentEquippedItem().damageItem(1, player);
                } else if (!player.capabilities.isCreativeMode) {
                    --player.getCurrentEquippedItem().stackSize;
                }

                return true;
            }
        }

        return super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!world.isRemote && entityIn instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow)entityIn;

            if (entityarrow.isBurning()) {
                this.explode(world, pos, world.getBlockState(pos).withProperty(EXPLODE, true), entityarrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase)entityarrow.shootingEntity : null);
                world.setBlockToAir(pos);
            }
        }
    }

    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(EXPLODE, (meta & 1) > 0);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(EXPLODE) ? 1 : 0;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, EXPLODE);
    }

}
