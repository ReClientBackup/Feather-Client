package com.murengezi.minecraft.block;

import java.util.List;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyEnum;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockRedSandstone extends Block {

    public static final PropertyEnum<BlockRedSandstone.EnumType> TYPE = PropertyEnum.create("type", BlockRedSandstone.EnumType.class);

    public BlockRedSandstone() {
        super(Material.rock, BlockSand.EnumType.RED_SAND.getMapColor());
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockRedSandstone.EnumType.DEFAULT));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getMetadata();
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlockRedSandstone.EnumType enumType : BlockRedSandstone.EnumType.values()) {
            list.add(new ItemStack(item, 1, enumType.getMetadata()));
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, BlockRedSandstone.EnumType.byMetadata(meta));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMetadata();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, TYPE);
    }

    public enum EnumType implements IStringSerializable {
        DEFAULT(0, "red_sandstone", "default"), CHISELED(1, "chiseled_red_sandstone", "chiseled"), SMOOTH(2, "smooth_red_sandstone", "smooth");

        private static final BlockRedSandstone.EnumType[] META_LOOKUP = new BlockRedSandstone.EnumType[values().length];
        private final int meta;
        private final String name, unlocalizedName;

        EnumType(int meta, String name, String unlocalizedName) {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String toString() {
            return this.name;
        }

        public static BlockRedSandstone.EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName() {
            return this.name;
        }

        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }

        static {
            for (BlockRedSandstone.EnumType enumType : values()) {
                META_LOOKUP[enumType.getMetadata()] = enumType;
            }
        }
    }

}
