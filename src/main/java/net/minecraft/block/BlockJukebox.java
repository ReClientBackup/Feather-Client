package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer
{
    public static final PropertyBool HAS_RECORD = PropertyBool.create("has_record");

    protected BlockJukebox()
    {
        super(Material.wood, MapColor.dirtColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HAS_RECORD, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (state.getValue(HAS_RECORD).booleanValue())
        {
            this.dropRecord(world, pos, state);
            state = state.withProperty(HAS_RECORD, Boolean.valueOf(false));
            world.setBlockState(pos, state, 2);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void insertRecord(World world, BlockPos pos, IBlockState state, ItemStack recordStack)
    {
        if (!world.isRemote)
        {
            TileEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof BlockJukebox.TileEntityJukebox)
            {
                ((BlockJukebox.TileEntityJukebox)tileentity).setRecord(new ItemStack(recordStack.getItem(), 1, recordStack.getMetadata()));
                world.setBlockState(pos, state.withProperty(HAS_RECORD, Boolean.valueOf(true)), 2);
            }
        }
    }

    private void dropRecord(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            TileEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof BlockJukebox.TileEntityJukebox)
            {
                BlockJukebox.TileEntityJukebox blockjukebox$tileentityjukebox = (BlockJukebox.TileEntityJukebox)tileentity;
                ItemStack itemstack = blockjukebox$tileentityjukebox.getRecord();

                if (itemstack != null)
                {
                    world.playAuxSFX(1005, pos, 0);
                    world.playRecord(pos, null);
                    blockjukebox$tileentityjukebox.setRecord(null);
                    float f = 0.7F;
                    double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.2D + 0.6D;
                    double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    ItemStack itemstack1 = itemstack.copy();
                    EntityItem entityitem = new EntityItem(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, itemstack1);
                    entityitem.setDefaultPickupDelay();
                    world.spawnEntityInWorld(entityitem);
                }
            }
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        this.dropRecord(world, pos, state);
        super.breakBlock(world, pos, state);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *  
     * @param chance The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (!world.isRemote)
        {
            super.dropBlockAsItemWithChance(world, pos, state, chance, 0);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new BlockJukebox.TileEntityJukebox();
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World world, BlockPos pos)
    {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof BlockJukebox.TileEntityJukebox)
        {
            ItemStack itemstack = ((BlockJukebox.TileEntityJukebox)tileentity).getRecord();

            if (itemstack != null)
            {
                return Item.getIdFromItem(itemstack.getItem()) + 1 - Item.getIdFromItem(Items.record_13);
            }
        }

        return 0;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(HAS_RECORD, Boolean.valueOf(meta > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(HAS_RECORD).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, HAS_RECORD);
    }

    public static class TileEntityJukebox extends TileEntity
    {
        private ItemStack record;

        public void readFromNBT(NBTTagCompound compound)
        {
            super.readFromNBT(compound);

            if (compound.hasKey("RecordItem", 10))
            {
                this.setRecord(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("RecordItem")));
            }
            else if (compound.getInteger("Record") > 0)
            {
                this.setRecord(new ItemStack(Item.getItemById(compound.getInteger("Record")), 1, 0));
            }
        }

        public void writeToNBT(NBTTagCompound compound)
        {
            super.writeToNBT(compound);

            if (this.getRecord() != null)
            {
                compound.setTag("RecordItem", this.getRecord().writeToNBT(new NBTTagCompound()));
            }
        }

        public ItemStack getRecord()
        {
            return this.record;
        }

        public void setRecord(ItemStack recordStack)
        {
            this.record = recordStack;
            this.markDirty();
        }
    }
}
