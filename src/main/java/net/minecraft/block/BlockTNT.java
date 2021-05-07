package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block
{
    public static final PropertyBool EXPLODE = PropertyBool.create("explode");

    public BlockTNT()
    {
        super(Material.tnt);
        this.setDefaultState(this.blockState.getBaseState().withProperty(EXPLODE, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(world, pos, state);

        if (world.isBlockPowered(pos))
        {
            this.onBlockDestroyedByPlayer(world, pos, state.withProperty(EXPLODE, Boolean.valueOf(true)));
            world.setBlockToAir(pos);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (world.isBlockPowered(pos))
        {
            this.onBlockDestroyedByPlayer(world, pos, state.withProperty(EXPLODE, Boolean.valueOf(true)));
            world.setBlockToAir(pos);
        }
    }

    /**
     * Called when this Block is destroyed by an Explosion
     */
    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn)
    {
        if (!world.isRemote)
        {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (float)pos.getX() + 0.5F, pos.getY(), (float)pos.getZ() + 0.5F, explosionIn.getExplosivePlacedBy());
            entitytntprimed.fuse = world.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
            world.spawnEntityInWorld(entitytntprimed);
        }
    }

    /**
     * Called when a player destroys this Block
     */
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state)
    {
        this.explode(world, pos, state, null);
    }

    public void explode(World world, BlockPos pos, IBlockState state, EntityLivingBase igniter)
    {
        if (!world.isRemote)
        {
            if (state.getValue(EXPLODE).booleanValue())
            {
                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (float)pos.getX() + 0.5F, pos.getY(), (float)pos.getZ() + 0.5F, igniter);
                world.spawnEntityInWorld(entitytntprimed);
                world.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
            }
        }
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (player.getCurrentEquippedItem() != null)
        {
            Item item = player.getCurrentEquippedItem().getItem();

            if (item == Items.flint_and_steel || item == Items.fire_charge)
            {
                this.explode(world, pos, state.withProperty(EXPLODE, Boolean.valueOf(true)), player);
                world.setBlockToAir(pos);

                if (item == Items.flint_and_steel)
                {
                    player.getCurrentEquippedItem().damageItem(1, player);
                }
                else if (!player.capabilities.isCreativeMode)
                {
                    --player.getCurrentEquippedItem().stackSize;
                }

                return true;
            }
        }

        return super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!world.isRemote && entityIn instanceof EntityArrow)
        {
            EntityArrow entityarrow = (EntityArrow)entityIn;

            if (entityarrow.isBurning())
            {
                this.explode(world, pos, world.getBlockState(pos).withProperty(EXPLODE, Boolean.valueOf(true)), entityarrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase)entityarrow.shootingEntity : null);
                world.setBlockToAir(pos);
            }
        }
    }

    /**
     * Return whether this block can drop from an explosion.
     */
    public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return false;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(EXPLODE, Boolean.valueOf((meta & 1) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(EXPLODE).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, EXPLODE);
    }
}
