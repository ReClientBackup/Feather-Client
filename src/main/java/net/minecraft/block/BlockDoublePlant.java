package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockDoublePlant extends BlockBush implements IGrowable
{
    public static final PropertyEnum<BlockDoublePlant.EnumPlantType> VARIANT = PropertyEnum.create("variant", BlockDoublePlant.EnumPlantType.class);
    public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockDoublePlant.EnumBlockHalf.class);
    public static final PropertyEnum<EnumFacing> field_181084_N = BlockDirectional.FACING;

    public BlockDoublePlant()
    {
        super(Material.vine);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockDoublePlant.EnumPlantType.SUNFLOWER).withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER).withProperty(field_181084_N, EnumFacing.NORTH));
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.setUnlocalizedName("doublePlant");
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public BlockDoublePlant.EnumPlantType getVariant(IBlockAccess world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos);

        if (iblockstate.getBlock() == this)
        {
            iblockstate = this.getActualState(iblockstate, world, pos);
            return iblockstate.getValue(VARIANT);
        }
        else
        {
            return BlockDoublePlant.EnumPlantType.FERN;
        }
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        return super.canPlaceBlockAt(world, pos) && world.isAirBlock(pos.up());
    }

    /**
     * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
     */
    public boolean isReplaceable(World world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos);

        if (iblockstate.getBlock() != this)
        {
            return true;
        }
        else
        {
            BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = this.getActualState(iblockstate, world, pos).getValue(VARIANT);
            return blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.FERN || blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS;
        }
    }

    protected void checkAndDropBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(world, pos, state))
        {
            boolean flag = state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
            BlockPos blockpos = flag ? pos : pos.up();
            BlockPos blockpos1 = flag ? pos.down() : pos;
            Block block = flag ? this : world.getBlockState(blockpos).getBlock();
            Block block1 = flag ? world.getBlockState(blockpos1).getBlock() : this;

            if (block == this)
            {
                world.setBlockState(blockpos, Blocks.air.getDefaultState(), 2);
            }

            if (block1 == this)
            {
                world.setBlockState(blockpos1, Blocks.air.getDefaultState(), 3);

                if (!flag)
                {
                    this.dropBlockAsItem(world, blockpos1, state, 0);
                }
            }
        }
    }

    public boolean canBlockStay(World world, BlockPos pos, IBlockState state)
    {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
        {
            return world.getBlockState(pos.down()).getBlock() == this;
        }
        else
        {
            IBlockState iblockstate = world.getBlockState(pos.up());
            return iblockstate.getBlock() == this && super.canBlockStay(world, pos, iblockstate);
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *  
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
        {
            return null;
        }
        else
        {
            BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = state.getValue(VARIANT);
            return blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.FERN ? null : (blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS ? (rand.nextInt(8) == 0 ? Items.wheat_seeds : null) : Item.getItemFromBlock(this));
        }
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return state.getValue(HALF) != BlockDoublePlant.EnumBlockHalf.UPPER && state.getValue(VARIANT) != BlockDoublePlant.EnumPlantType.GRASS ? state.getValue(VARIANT).getMeta() : 0;
    }

    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass)
    {
        BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = this.getVariant(world, pos);
        return blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN ? 16777215 : BiomeColorHelper.getGrassColorAtPos(world, pos);
    }

    public void placeAt(World world, BlockPos lowerPos, BlockDoublePlant.EnumPlantType variant, int flags)
    {
        world.setBlockState(lowerPos, this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER).withProperty(VARIANT, variant), flags);
        world.setBlockState(lowerPos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), flags);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        world.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
    }

    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te)
    {
        if (world.isRemote || player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != Items.shears || state.getValue(HALF) != BlockDoublePlant.EnumBlockHalf.LOWER || !this.onHarvest(world, pos, state, player))
        {
            super.harvestBlock(world, player, pos, state, te);
        }
    }

    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
        {
            if (world.getBlockState(pos.down()).getBlock() == this)
            {
                if (!player.capabilities.isCreativeMode)
                {
                    IBlockState iblockstate = world.getBlockState(pos.down());
                    BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = iblockstate.getValue(VARIANT);

                    if (blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS)
                    {
                        world.destroyBlock(pos.down(), true);
                    }
                    else if (!world.isRemote)
                    {
                        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears)
                        {
                            this.onHarvest(world, pos, iblockstate, player);
                            world.setBlockToAir(pos.down());
                        }
                        else
                        {
                            world.destroyBlock(pos.down(), true);
                        }
                    }
                    else
                    {
                        world.setBlockToAir(pos.down());
                    }
                }
                else
                {
                    world.setBlockToAir(pos.down());
                }
            }
        }
        else if (player.capabilities.isCreativeMode && world.getBlockState(pos.up()).getBlock() == this)
        {
            world.setBlockState(pos.up(), Blocks.air.getDefaultState(), 2);
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    private boolean onHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = state.getValue(VARIANT);

        if (blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS)
        {
            return false;
        }
        else
        {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            int i = (blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS ? BlockTallGrass.EnumType.GRASS : BlockTallGrass.EnumType.FERN).getMeta();
            spawnAsEntity(world, pos, new ItemStack(Blocks.tallgrass, 2, i));
            return true;
        }
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
    {
        for (BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype : BlockDoublePlant.EnumPlantType.values())
        {
            list.add(new ItemStack(item, 1, blockdoubleplant$enumplanttype.getMeta()));
        }
    }

    public int getDamageValue(World world, BlockPos pos)
    {
        return this.getVariant(world, pos).getMeta();
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
    {
        BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = this.getVariant(world, pos);
        return blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN;
    }

    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    public void grow(World world, Random rand, BlockPos pos, IBlockState state)
    {
        spawnAsEntity(world, pos, new ItemStack(this, 1, this.getVariant(world, pos).getMeta()));
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER) : this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER).withProperty(VARIANT, BlockDoublePlant.EnumPlantType.byMetadata(meta & 7));
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
        {
            IBlockState iblockstate = world.getBlockState(pos.down());

            if (iblockstate.getBlock() == this)
            {
                state = state.withProperty(VARIANT, iblockstate.getValue(VARIANT));
            }
        }

        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? 8 | state.getValue(field_181084_N).getHorizontalIndex() : state.getValue(VARIANT).getMeta();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, HALF, VARIANT, field_181084_N);
    }

    /**
     * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
     */
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
    }

    public enum EnumBlockHalf implements IStringSerializable
    {
        UPPER,
        LOWER;

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return this == UPPER ? "upper" : "lower";
        }
    }

    public enum EnumPlantType implements IStringSerializable
    {
        SUNFLOWER(0, "sunflower"),
        SYRINGA(1, "syringa"),
        GRASS(2, "double_grass", "grass"),
        FERN(3, "double_fern", "fern"),
        ROSE(4, "double_rose", "rose"),
        PAEONIA(5, "paeonia");

        private static final BlockDoublePlant.EnumPlantType[] META_LOOKUP = new BlockDoublePlant.EnumPlantType[values().length];
        private final int meta;
        private final String name;
        private final String unlocalizedName;

        EnumPlantType(int meta, String name)
        {
            this(meta, name, name);
        }

        EnumPlantType(int meta, String name, String unlocalizedName)
        {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMeta()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockDoublePlant.EnumPlantType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        static {
            for (BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype : values())
            {
                META_LOOKUP[blockdoubleplant$enumplanttype.getMeta()] = blockdoubleplant$enumplanttype;
            }
        }
    }
}
