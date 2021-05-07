package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class BlockBrewingStand extends BlockContainer {

    public static final PropertyBool[] HAS_BOTTLE = new PropertyBool[] {PropertyBool.create("has_bottle_0"), PropertyBool.create("has_bottle_1"), PropertyBool.create("has_bottle_2")};

    public BlockBrewingStand() {
        super(Material.iron);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HAS_BOTTLE[0], Boolean.FALSE).withProperty(HAS_BOTTLE[1], Boolean.FALSE).withProperty(HAS_BOTTLE[2], Boolean.FALSE));
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName() {
        return StatCollector.translateToLocal("item.brewingStand.name");
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 3;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityBrewingStand();
    }

    public boolean isFullCube() {
        return false;
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     *  
     * @param collidingEntity the Entity colliding with this Block
     */
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        this.setBlockBoundsForItemRender();
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof TileEntityBrewingStand) {
                player.displayGUIChest((TileEntityBrewingStand)tileentity);
                player.triggerAchievement(StatList.field_181729_M);
            }

            return true;
        }
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity instanceof TileEntityBrewingStand) {
                ((TileEntityBrewingStand)tileEntity).setName(stack.getDisplayName());
            }
        }
    }

    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        double d0 = (float)pos.getX() + 0.4F + rand.nextFloat() * 0.2F;
        double d1 = (float)pos.getY() + 0.7F + rand.nextFloat() * 0.3F;
        double d2 = (float)pos.getZ() + 0.4F + rand.nextFloat() * 0.2F;
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileEntityBrewingStand) {
            InventoryHelper.dropInventoryItems(world, pos, (TileEntityBrewingStand)tileEntity);
        }

        super.breakBlock(world, pos, state);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *  
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.brewing_stand;
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(World world, BlockPos pos) {
        return Items.brewing_stand;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, BlockPos pos) {
        return Container.calcRedstone(world.getTileEntity(pos));
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState();

        for (int i = 0; i < 3; ++i) {
            iblockstate = iblockstate.withProperty(HAS_BOTTLE[i], (meta & 1 << i) > 0);
        }

        return iblockstate;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        int i = 0;

        for (int j = 0; j < 3; ++j) {
            if (state.getValue(HAS_BOTTLE[j])) {
                i |= 1 << j;
            }
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
    }

}
