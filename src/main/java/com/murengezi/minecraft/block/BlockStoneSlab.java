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
import com.murengezi.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public abstract class BlockStoneSlab extends BlockSlab {

    public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");
    public static final PropertyEnum<BlockStoneSlab.EnumType> VARIANT = PropertyEnum.create("variant", BlockStoneSlab.EnumType.class);

    public BlockStoneSlab() {
        super(Material.rock);
        IBlockState iblockstate = this.blockState.getBaseState();

        if (this.isDouble()) {
            iblockstate = iblockstate.withProperty(SEAMLESS, false);
        } else {
            iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        }

        this.setDefaultState(iblockstate.withProperty(VARIANT, BlockStoneSlab.EnumType.STONE));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.stone_slab);
    }

    public Item getItem(World world, BlockPos pos) {
        return Item.getItemFromBlock(Blocks.stone_slab);
    }

    public String getUnlocalizedName(int meta) {
        return super.getUnlocalizedName() + "." + BlockStoneSlab.EnumType.byMetadata(meta).getUnlocalizedName();
    }

    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    public Object getVariant(ItemStack stack) {
        return BlockStoneSlab.EnumType.byMetadata(stack.getMetadata() & 7);
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (item != Item.getItemFromBlock(Blocks.double_stone_slab)) {
            for (BlockStoneSlab.EnumType blockstoneslab$enumtype : BlockStoneSlab.EnumType.values()) {
                if (blockstoneslab$enumtype != BlockStoneSlab.EnumType.WOOD) {
                    list.add(new ItemStack(item, 1, blockstoneslab$enumtype.getMetadata()));
                }
            }
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockStoneSlab.EnumType.byMetadata(meta & 7));

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

    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    public MapColor getMapColor(IBlockState state) {
        return state.getValue(VARIANT).func_181074_c();
    }

    public enum EnumType implements IStringSerializable {
        STONE(0, MapColor.stoneColor, "stone"), SAND(1, MapColor.sandColor, "sandstone", "sand"), WOOD(2, MapColor.woodColor, "wood_old", "wood"), COBBLESTONE(3, MapColor.stoneColor, "cobblestone", "cobble"), BRICK(4, MapColor.redColor, "brick"), SMOOTHBRICK(5, MapColor.stoneColor, "stone_brick", "smoothStoneBrick"), NETHERBRICK(6, MapColor.netherrackColor, "nether_brick", "netherBrick"), QUARTZ(7, MapColor.quartzColor, "quartz");

        private static final BlockStoneSlab.EnumType[] META_LOOKUP = new BlockStoneSlab.EnumType[values().length];
        private final int meta;
        private final MapColor mapColor;
        private final String name, unlocalizedName;

        EnumType(int meta, MapColor mapColor, String name) {
            this(meta, mapColor, name, name);
        }

        EnumType(int meta, MapColor mapColor, String name, String unlocalizedName) {
            this.meta = meta;
            this.mapColor = mapColor;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMetadata() {
            return this.meta;
        }

        public MapColor func_181074_c() {
            return this.mapColor;
        }

        public String toString() {
            return this.name;
        }

        public static BlockStoneSlab.EnumType byMetadata(int meta) {
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
            for (BlockStoneSlab.EnumType type : values()) {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }

}
