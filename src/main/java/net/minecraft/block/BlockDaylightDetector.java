package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer
{
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private final boolean inverted;

    public BlockDaylightDetector(boolean inverted)
    {
        super(Material.wood);
        this.inverted = inverted;
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, Integer.valueOf(0)));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setHardness(0.2F);
        this.setStepSound(soundTypeWood);
        this.setUnlocalizedName("daylightDetector");
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
    }

    public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return state.getValue(POWER).intValue();
    }

    public void updatePower(World world, BlockPos pos)
    {
        if (!world.provider.getHasNoSky())
        {
            IBlockState iblockstate = world.getBlockState(pos);
            int i = world.getLightFor(EnumSkyBlock.SKY, pos) - world.getSkylightSubtracted();
            float f = world.getCelestialAngleRadians(1.0F);
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            i = Math.round((float)i * MathHelper.cos(f));
            i = MathHelper.clamp_int(i, 0, 15);

            if (this.inverted)
            {
                i = 15 - i;
            }

            if (iblockstate.getValue(POWER).intValue() != i)
            {
                world.setBlockState(pos, iblockstate.withProperty(POWER, Integer.valueOf(i)), 3);
            }
        }
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (player.isAllowEdit())
        {
            if (world.isRemote)
            {
                return true;
            }
            else
            {
                if (this.inverted)
                {
                    world.setBlockState(pos, Blocks.daylight_detector.getDefaultState().withProperty(POWER, state.getValue(POWER)), 4);
                    Blocks.daylight_detector.updatePower(world, pos);
                }
                else
                {
                    world.setBlockState(pos, Blocks.daylight_detector_inverted.getDefaultState().withProperty(POWER, state.getValue(POWER)), 4);
                    Blocks.daylight_detector_inverted.updatePower(world, pos);
                }

                return true;
            }
        }
        else
        {
            return super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *  
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.daylight_detector);
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(World world, BlockPos pos)
    {
        return Item.getItemFromBlock(Blocks.daylight_detector);
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityDaylightDetector();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(POWER, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(POWER).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, POWER);
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
    {
        if (!this.inverted)
        {
            super.getSubBlocks(item, tab, list);
        }
    }
}
