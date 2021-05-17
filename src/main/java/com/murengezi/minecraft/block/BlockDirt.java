package com.murengezi.minecraft.block;

import java.util.List;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDirt extends Block {

    public static final PropertyEnum<BlockDirt.DirtType> VARIANT = PropertyEnum.create("variant", BlockDirt.DirtType.class);
    public static final PropertyBool SNOWY = PropertyBool.create("snowy");

    protected BlockDirt() {
        super(Material.ground);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockDirt.DirtType.DIRT).withProperty(SNOWY, false));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public MapColor getMapColor(IBlockState state) {
        return state.getValue(VARIANT).func_181066_d();
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(VARIANT) == BlockDirt.DirtType.PODZOL) {
            Block block = world.getBlockState(pos.up()).getBlock();
            state = state.withProperty(SNOWY, block == Blocks.snow || block == Blocks.snow_layer);
        }

        return state;
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, BlockDirt.DirtType.DIRT.getMetadata()));
        list.add(new ItemStack(this, 1, BlockDirt.DirtType.COARSE_DIRT.getMetadata()));
        list.add(new ItemStack(this, 1, BlockDirt.DirtType.PODZOL.getMetadata()));
    }

    public int getDamageValue(World world, BlockPos pos) {
        IBlockState iblockstate = world.getBlockState(pos);
        return iblockstate.getBlock() != this ? 0 : iblockstate.getValue(VARIANT).getMetadata();
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, BlockDirt.DirtType.byMetadata(meta));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, VARIANT, SNOWY);
    }

    public int damageDropped(IBlockState state) {
        BlockDirt.DirtType dirtType = state.getValue(VARIANT);

        if (dirtType == BlockDirt.DirtType.PODZOL) {
            dirtType = BlockDirt.DirtType.DIRT;
        }

        return dirtType.getMetadata();
    }

    public enum DirtType implements IStringSerializable {
        DIRT(0, "dirt", "default", MapColor.dirtColor), COARSE_DIRT(1, "coarse_dirt", "coarse", MapColor.dirtColor), PODZOL(2, "podzol", MapColor.obsidianColor);

        private static final BlockDirt.DirtType[] METADATA_LOOKUP = new BlockDirt.DirtType[values().length];
        private final int metadata;
        private final String name, unlocalizedName;
        private final MapColor mapColor;

        DirtType(int metadata, String name, MapColor mapColor) {
            this(metadata, name, name, mapColor);
        }

        DirtType(int metadata, String name, String unlocalizedName, MapColor mapColor) {
            this.metadata = metadata;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
            this.mapColor = mapColor;
        }

        public int getMetadata() {
            return this.metadata;
        }

        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }

        public MapColor func_181066_d() {
            return this.mapColor;
        }

        public String toString() {
            return this.name;
        }

        public static BlockDirt.DirtType byMetadata(int metadata) {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        public String getName() {
            return this.name;
        }

        static {
            for (BlockDirt.DirtType dirtType : values()) {
                METADATA_LOOKUP[dirtType.getMetadata()] = dirtType;
            }
        }
    }
}
