package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer
{
    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");

    public BlockCommandBlock()
    {
        super(Material.iron, MapColor.adobeColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TRIGGERED, Boolean.valueOf(false)));
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityCommandBlock();
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!world.isRemote)
        {
            boolean flag = world.isBlockPowered(pos);
            boolean flag1 = state.getValue(TRIGGERED).booleanValue();

            if (flag && !flag1)
            {
                world.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)), 4);
                world.scheduleUpdate(pos, this, this.tickRate(world));
            }
            else if (!flag && flag1)
            {
                world.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)), 4);
            }
        }
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock)
        {
            ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().trigger(world);
            world.updateComparatorOutputLevel(pos, this);
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World world)
    {
        return 1;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity instanceof TileEntityCommandBlock && ((TileEntityCommandBlock) tileentity).getCommandBlockLogic().tryOpenEditCommandBlock(player);
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World world, BlockPos pos)
    {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() : 0;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock)
        {
            CommandBlockLogic commandblocklogic = ((TileEntityCommandBlock)tileentity).getCommandBlockLogic();

            if (stack.hasDisplayName())
            {
                commandblocklogic.setName(stack.getDisplayName());
            }

            if (!world.isRemote)
            {
                commandblocklogic.setTrackOutput(world.getGameRules().getGameRuleBooleanValue("sendCommandFeedback"));
            }
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
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
        return this.getDefaultState().withProperty(TRIGGERED, Boolean.valueOf((meta & 1) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;

        if (state.getValue(TRIGGERED).booleanValue())
        {
            i |= 1;
        }

        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, TRIGGERED);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(TRIGGERED, Boolean.valueOf(false));
    }
}
