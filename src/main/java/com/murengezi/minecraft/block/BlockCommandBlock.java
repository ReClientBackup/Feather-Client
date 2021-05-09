package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer {

    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");

    public BlockCommandBlock() {
        super(Material.iron, MapColor.adobeColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TRIGGERED, false));
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCommandBlock();
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!world.isRemote) {
            boolean flag = world.isBlockPowered(pos);
            boolean flag1 = state.getValue(TRIGGERED);

            if (flag && !flag1) {
                world.setBlockState(pos, state.withProperty(TRIGGERED, true), 4);
                world.scheduleUpdate(pos, this, this.tickRate(world));
            } else if (!flag && flag1) {
                world.setBlockState(pos, state.withProperty(TRIGGERED, false), 4);
            }
        }
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock) {
            ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().trigger(world);
            world.updateComparatorOutputLevel(pos, this);
        }
    }

    public int tickRate(World world) {
        return 1;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity instanceof TileEntityCommandBlock && ((TileEntityCommandBlock) tileentity).getCommandBlockLogic().tryOpenEditCommandBlock(player);
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, BlockPos pos) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() : 0;
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock) {
            CommandBlockLogic commandblocklogic = ((TileEntityCommandBlock)tileentity).getCommandBlockLogic();

            if (stack.hasDisplayName()) {
                commandblocklogic.setName(stack.getDisplayName());
            }

            if (!world.isRemote) {
                commandblocklogic.setTrackOutput(world.getGameRules().getGameRuleBooleanValue("sendCommandFeedback"));
            }
        }
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public int getRenderType() {
        return 3;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TRIGGERED, (meta & 1) > 0);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;

        if (state.getValue(TRIGGERED)) {
            i |= 1;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, TRIGGERED);
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(TRIGGERED, false);
    }

}
