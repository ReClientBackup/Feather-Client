package com.murengezi.minecraft.block;

import java.util.Random;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer {

    public static final PropertyInteger LEGACY_DATA = PropertyInteger.create("legacy_data", 0, 15);
    public static final PropertyEnum<BlockFlowerPot.EnumFlowerType> CONTENTS = PropertyEnum.create("contents", BlockFlowerPot.EnumFlowerType.class);

    public BlockFlowerPot() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CONTENTS, BlockFlowerPot.EnumFlowerType.EMPTY).withProperty(LEGACY_DATA, 0));
        this.setBlockBoundsForItemRender();
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal("item.flowerPot.name");
    }

    public void setBlockBoundsForItemRender() {
        float f = 0.375F;
        float f1 = f / 2.0F;
        this.setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, f, 0.5F + f1);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int getRenderType() {
        return 3;
    }

    public boolean isFullCube() {
        return false;
    }

    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityFlowerPot) {
            Item item = ((TileEntityFlowerPot)tileentity).getFlowerPotItem();

            if (item instanceof ItemBlock) {
                return Block.getBlockFromItem(item).colorMultiplier(world, pos, renderPass);
            }
        }

        return 16777215;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.inventory.getCurrentItem();

        if (itemstack != null && itemstack.getItem() instanceof ItemBlock) {
            TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(world, pos);

            if (tileentityflowerpot == null) {
                return false;
            } else if (tileentityflowerpot.getFlowerPotItem() != null) {
                return false;
            } else {
                Block block = Block.getBlockFromItem(itemstack.getItem());

                if (!this.canNotContain(block, itemstack.getMetadata())) {
                    return false;
                } else {
                    tileentityflowerpot.setFlowerPotData(itemstack.getItem(), itemstack.getMetadata());
                    tileentityflowerpot.markDirty();
                    world.markBlockForUpdate(pos);
                    player.triggerAchievement(StatList.field_181736_T);

                    if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private boolean canNotContain(Block block, int meta) {
        return block == Blocks.yellow_flower || block == Blocks.red_flower || block == Blocks.cactus || block == Blocks.brown_mushroom || block == Blocks.red_mushroom || block == Blocks.sapling || block == Blocks.deadbush || block == Blocks.tallgrass && meta == BlockTallGrass.EnumType.FERN.getMeta();
    }

    public Item getItem(World world, BlockPos pos) {
        TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(world, pos);
        return tileentityflowerpot != null && tileentityflowerpot.getFlowerPotItem() != null ? tileentityflowerpot.getFlowerPotItem() : Items.flower_pot;
    }

    public int getDamageValue(World world, BlockPos pos) {
        TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(world, pos);
        return tileentityflowerpot != null && tileentityflowerpot.getFlowerPotItem() != null ? tileentityflowerpot.getFlowerPotData() : 0;
    }

    public boolean isFlowerPot() {
        return true;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && World.doesBlockHaveSolidTopSurface(world, pos.down());
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!World.doesBlockHaveSolidTopSurface(world, pos.down())) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(world, pos);

        if (tileentityflowerpot != null && tileentityflowerpot.getFlowerPotItem() != null) {
            spawnAsEntity(world, pos, new ItemStack(tileentityflowerpot.getFlowerPotItem(), 1, tileentityflowerpot.getFlowerPotData()));
        }

        super.breakBlock(world, pos, state);
    }

    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(world, pos, state, player);

        if (player.capabilities.isCreativeMode) {
            TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(world, pos);

            if (tileentityflowerpot != null) {
                tileentityflowerpot.setFlowerPotData(null, 0);
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.flower_pot;
    }

    private TileEntityFlowerPot getTileEntity(World world, BlockPos pos) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)tileentity : null;
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        Block block = null;
        int i = 0;

        switch (meta) {
            case 1:
                block = Blocks.red_flower;
                i = BlockFlower.EnumFlowerType.POPPY.getMeta();
                break;
            case 2:
                block = Blocks.yellow_flower;
                break;
            case 3:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.OAK.getMetadata();
                break;
            case 4:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.SPRUCE.getMetadata();
                break;
            case 5:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.BIRCH.getMetadata();
                break;
            case 6:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.JUNGLE.getMetadata();
                break;
            case 7:
                block = Blocks.red_mushroom;
                break;
            case 8:
                block = Blocks.brown_mushroom;
                break;
            case 9:
                block = Blocks.cactus;
                break;
            case 10:
                block = Blocks.deadbush;
                break;
            case 11:
                block = Blocks.tallgrass;
                i = BlockTallGrass.EnumType.FERN.getMeta();
                break;
            case 12:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.ACACIA.getMetadata();
                break;
            case 13:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.DARK_OAK.getMetadata();
        }

        return new TileEntityFlowerPot(Item.getItemFromBlock(block), i);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, CONTENTS, LEGACY_DATA);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEGACY_DATA);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        BlockFlowerPot.EnumFlowerType flowerType = BlockFlowerPot.EnumFlowerType.EMPTY;
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityFlowerPot) {
            TileEntityFlowerPot tileentityflowerpot = (TileEntityFlowerPot)tileentity;
            Item item = tileentityflowerpot.getFlowerPotItem();

            if (item instanceof ItemBlock) {
                int i = tileentityflowerpot.getFlowerPotData();
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.sapling) {
                    switch (BlockPlanks.EnumType.byMetadata(i)) {
                        case OAK:
                            flowerType = BlockFlowerPot.EnumFlowerType.OAK_SAPLING;
                            break;
                        case SPRUCE:
                            flowerType = BlockFlowerPot.EnumFlowerType.SPRUCE_SAPLING;
                            break;
                        case BIRCH:
                            flowerType = BlockFlowerPot.EnumFlowerType.BIRCH_SAPLING;
                            break;
                        case JUNGLE:
                            flowerType = BlockFlowerPot.EnumFlowerType.JUNGLE_SAPLING;
                            break;
                        case ACACIA:
                            flowerType = BlockFlowerPot.EnumFlowerType.ACACIA_SAPLING;
                            break;
                        case DARK_OAK:
                            flowerType = BlockFlowerPot.EnumFlowerType.DARK_OAK_SAPLING;
                            break;
                        default:
                            flowerType = BlockFlowerPot.EnumFlowerType.EMPTY;
                    }
                } else if (block == Blocks.tallgrass) {
                    switch (i) {
                        case 0:
                            flowerType = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
                            break;
                        case 2:
                            flowerType = BlockFlowerPot.EnumFlowerType.FERN;
                            break;
                        default:
                            flowerType = BlockFlowerPot.EnumFlowerType.EMPTY;
                    }
                } else if (block == Blocks.yellow_flower) {
                    flowerType = BlockFlowerPot.EnumFlowerType.DANDELION;
                } else if (block == Blocks.red_flower) {
                    switch (BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, i)) {
                        case POPPY:
                            flowerType = BlockFlowerPot.EnumFlowerType.POPPY;
                            break;
                        case BLUE_ORCHID:
                            flowerType = BlockFlowerPot.EnumFlowerType.BLUE_ORCHID;
                            break;
                        case ALLIUM:
                            flowerType = BlockFlowerPot.EnumFlowerType.ALLIUM;
                            break;
                        case HOUSTONIA:
                            flowerType = BlockFlowerPot.EnumFlowerType.HOUSTONIA;
                            break;
                        case RED_TULIP:
                            flowerType = BlockFlowerPot.EnumFlowerType.RED_TULIP;
                            break;
                        case ORANGE_TULIP:
                            flowerType = BlockFlowerPot.EnumFlowerType.ORANGE_TULIP;
                            break;
                        case WHITE_TULIP:
                            flowerType = BlockFlowerPot.EnumFlowerType.WHITE_TULIP;
                            break;
                        case PINK_TULIP:
                            flowerType = BlockFlowerPot.EnumFlowerType.PINK_TULIP;
                            break;
                        case OXEYE_DAISY:
                            flowerType = BlockFlowerPot.EnumFlowerType.OXEYE_DAISY;
                            break;
                        default:
                            flowerType = BlockFlowerPot.EnumFlowerType.EMPTY;
                    }
                } else if (block == Blocks.red_mushroom) {
                    flowerType = BlockFlowerPot.EnumFlowerType.MUSHROOM_RED;
                } else if (block == Blocks.brown_mushroom) {
                    flowerType = BlockFlowerPot.EnumFlowerType.MUSHROOM_BROWN;
                } else if (block == Blocks.deadbush) {
                    flowerType = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
                } else if (block == Blocks.cactus) {
                    flowerType = BlockFlowerPot.EnumFlowerType.CACTUS;
                }
            }
        }

        return state.withProperty(CONTENTS, flowerType);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public enum EnumFlowerType implements IStringSerializable {
        EMPTY("empty"), POPPY("rose"), BLUE_ORCHID("blue_orchid"), ALLIUM("allium"), HOUSTONIA("houstonia"), RED_TULIP("red_tulip"), ORANGE_TULIP("orange_tulip"), WHITE_TULIP("white_tulip"), PINK_TULIP("pink_tulip"), OXEYE_DAISY("oxeye_daisy"), DANDELION("dandelion"), OAK_SAPLING("oak_sapling"), SPRUCE_SAPLING("spruce_sapling"), BIRCH_SAPLING("birch_sapling"), JUNGLE_SAPLING("jungle_sapling"), ACACIA_SAPLING("acacia_sapling"), DARK_OAK_SAPLING("dark_oak_sapling"), MUSHROOM_RED("mushroom_red"), MUSHROOM_BROWN("mushroom_brown"), DEAD_BUSH("dead_bush"), FERN("fern"), CACTUS("cactus");

        private final String name;

        EnumFlowerType(String name) {
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
