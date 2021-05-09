package com.murengezi.minecraft.block;

import java.util.List;
import java.util.Random;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.IProperty;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public abstract class BlockStoneSlabNew extends BlockSlab {

    public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");
    public static final PropertyEnum<BlockStoneSlabNew.EnumType> VARIANT = PropertyEnum.create("variant", BlockStoneSlabNew.EnumType.class);

    public BlockStoneSlabNew() {
        super(Material.rock);
        IBlockState iblockstate = this.blockState.getBaseState();

        if (this.isDouble()) {
            iblockstate = iblockstate.withProperty(SEAMLESS, false);
        } else {
            iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        }

        this.setDefaultState(iblockstate.withProperty(VARIANT, BlockStoneSlabNew.EnumType.RED_SANDSTONE));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".red_sandstone.name");
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.stone_slab2);
    }

    public Item getItem(World world, BlockPos pos) {
        return Item.getItemFromBlock(Blocks.stone_slab2);
    }

    public String getUnlocalizedName(int meta) {
        return super.getUnlocalizedName() + "." + BlockStoneSlabNew.EnumType.byMetadata(meta).getUnlocalizedName();
    }

    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    public Object getVariant(ItemStack stack) {
        return BlockStoneSlabNew.EnumType.byMetadata(stack.getMetadata() & 7);
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (item != Item.getItemFromBlock(Blocks.double_stone_slab2)) {
            for (BlockStoneSlabNew.EnumType type : BlockStoneSlabNew.EnumType.values()) {
                list.add(new ItemStack(item, 1, type.getMetadata()));
            }
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockStoneSlabNew.EnumType.byMetadata(meta & 7));

        if (this.isDouble()) {
            iblockstate = iblockstate.withProperty(SEAMLESS, (meta & 8) != 0);
        } else {
            iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }

        return iblockstate;
    }

    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(VARIANT).getMetadata();

        if (this.isDouble()) {
            if (state.getValue(SEAMLESS)) {
                i |= 8;
            }
        } else if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return this.isDouble() ? new BlockState(this, SEAMLESS, VARIANT): new BlockState(this, HALF, VARIANT);
    }

    public MapColor getMapColor(IBlockState state) {
        return state.getValue(VARIANT).func_181068_c();
    }

    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    public enum EnumType implements IStringSerializable {
        RED_SANDSTONE(0, "red_sandstone", BlockSand.EnumType.RED_SAND.getMapColor());

        private static final BlockStoneSlabNew.EnumType[] META_LOOKUP = new BlockStoneSlabNew.EnumType[values().length];
        private final int meta;
        private final String name;
        private final MapColor mapColor;

        EnumType(int meta, String name, MapColor mapColor) {
            this.meta = meta;
            this.name = name;
            this.mapColor = mapColor;
        }

        public int getMetadata() {
            return this.meta;
        }

        public MapColor func_181068_c() {
            return this.mapColor;
        }

        public String toString() {
            return this.name;
        }

        public static BlockStoneSlabNew.EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName() {
            return this.name;
        }

        public String getUnlocalizedName() {
            return this.name;
        }

        static {
            for (BlockStoneSlabNew.EnumType type : values()) {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }

}
