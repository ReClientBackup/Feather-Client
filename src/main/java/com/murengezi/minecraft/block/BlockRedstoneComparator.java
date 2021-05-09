package com.murengezi.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {

    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyEnum<BlockRedstoneComparator.Mode> MODE = PropertyEnum.create("mode", BlockRedstoneComparator.Mode.class);

    public BlockRedstoneComparator(boolean powered) {
        super(powered);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false).withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE));
        this.isBlockContainer = true;
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal("item.comparator.name");
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.comparator;
    }

    public Item getItem(World world, BlockPos pos) {
        return Items.comparator;
    }

    protected int getDelay(IBlockState state) {
        return 2;
    }

    protected IBlockState getPoweredState(IBlockState unpoweredState) {
        Boolean obool = unpoweredState.getValue(POWERED);
        BlockRedstoneComparator.Mode mode = unpoweredState.getValue(MODE);
        EnumFacing enumfacing = unpoweredState.getValue(FACING);
        return Blocks.powered_comparator.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, obool).withProperty(MODE, mode);
    }

    protected IBlockState getUnpoweredState(IBlockState poweredState) {
        Boolean obool = poweredState.getValue(POWERED);
        BlockRedstoneComparator.Mode mode = poweredState.getValue(MODE);
        EnumFacing enumfacing = poweredState.getValue(FACING);
        return Blocks.unpowered_comparator.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, obool).withProperty(MODE, mode);
    }

    protected boolean isPowered(IBlockState state) {
        return this.isRepeaterPowered || state.getValue(POWERED);
    }

    protected int getActiveSignal(IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).getOutputSignal() : 0;
    }

    private int calculateOutput(World world, BlockPos pos, IBlockState state) {
        return state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT ? Math.max(this.calculateInputStrength(world, pos, state) - this.getPowerOnSides(world, pos, state), 0) : this.calculateInputStrength(world, pos, state);
    }

    protected boolean shouldBePowered(World world, BlockPos pos, IBlockState state) {
        int i = this.calculateInputStrength(world, pos, state);

        if (i >= 15) {
            return true;
        } else if (i == 0) {
            return false;
        } else {
            int j = this.getPowerOnSides(world, pos, state);
            return j == 0 || i >= j;
        }
    }

    protected int calculateInputStrength(World world, BlockPos pos, IBlockState state) {
        int i = super.calculateInputStrength(world, pos, state);
        EnumFacing enumfacing = state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        Block block = world.getBlockState(blockpos).getBlock();

        if (block.hasComparatorInputOverride()) {
            i = block.getComparatorInputOverride(world, blockpos);
        } else if (i < 15 && block.isNormalCube()) {
            blockpos = blockpos.offset(enumfacing);
            block = world.getBlockState(blockpos).getBlock();

            if (block.hasComparatorInputOverride()) {
                i = block.getComparatorInputOverride(world, blockpos);
            } else if (block.getMaterial() == Material.air) {
                EntityItemFrame entityitemframe = this.findItemFrame(world, enumfacing, blockpos);

                if (entityitemframe != null) {
                    i = entityitemframe.func_174866_q();
                }
            }
        }

        return i;
    }

    private EntityItemFrame findItemFrame(World world, final EnumFacing facing, BlockPos pos) {
        List<EntityItemFrame> list = world.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), (Predicate<Entity>) p_apply_1_ -> p_apply_1_ != null && p_apply_1_.getHorizontalFacing() == facing);
        return list.size() == 1 ? list.get(0) : null;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.capabilities.allowEdit) {
            return false;
        } else {
            state = state.cycleProperty(MODE);
            world.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "random.click", 0.3F, state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT ? 0.55F : 0.5F);
            world.setBlockState(pos, state, 2);
            this.onStateChange(world, pos, state);
            return true;
        }
    }

    protected void updateState(World world, BlockPos pos, IBlockState state) {
        if (!world.isBlockTickPending(pos, this)) {
            int i = this.calculateOutput(world, pos, state);
            TileEntity tileentity = world.getTileEntity(pos);
            int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).getOutputSignal() : 0;

            if (i != j || this.isPowered(state) != this.shouldBePowered(world, pos, state)) {
                if (this.isFacingTowardsRepeater(world, pos, state)) {
                    world.updateBlockTick(pos, this, 2, -1);
                } else {
                    world.updateBlockTick(pos, this, 2, 0);
                }
            }
        }
    }

    private void onStateChange(World world, BlockPos pos, IBlockState state) {
        int i = this.calculateOutput(world, pos, state);
        TileEntity tileentity = world.getTileEntity(pos);
        int j = 0;

        if (tileentity instanceof TileEntityComparator) {
            TileEntityComparator tileentitycomparator = (TileEntityComparator)tileentity;
            j = tileentitycomparator.getOutputSignal();
            tileentitycomparator.setOutputSignal(i);
        }

        if (j != i || state.getValue(MODE) == BlockRedstoneComparator.Mode.COMPARE) {
            boolean flag1 = this.shouldBePowered(world, pos, state);
            boolean flag = this.isPowered(state);

            if (flag && !flag1) {
                world.setBlockState(pos, state.withProperty(POWERED, false), 2);
            } else if (!flag && flag1) {
                world.setBlockState(pos, state.withProperty(POWERED, true), 2);
            }

            this.notifyNeighbors(world, pos, state);
        }
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (this.isRepeaterPowered) {
            world.setBlockState(pos, this.getUnpoweredState(state).withProperty(POWERED, true), 4);
        }

        this.onStateChange(world, pos, state);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.setTileEntity(pos, this.createNewTileEntity(world, 0));
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
        this.notifyNeighbors(world, pos, state);
    }

    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventParam) {
        super.onBlockEventReceived(world, pos, state, eventID, eventParam);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam);
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityComparator();
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(POWERED, (meta & 8) > 0).withProperty(MODE, (meta & 4) > 0 ? BlockRedstoneComparator.Mode.SUBTRACT : BlockRedstoneComparator.Mode.COMPARE);
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(POWERED)) {
            i |= 8;
        }

        if (state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT) {
            i |= 4;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, MODE, POWERED);
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false).withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE);
    }

    public enum Mode implements IStringSerializable {
        COMPARE("compare"), SUBTRACT("subtract");

        private final String name;

        Mode(String name) {
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
