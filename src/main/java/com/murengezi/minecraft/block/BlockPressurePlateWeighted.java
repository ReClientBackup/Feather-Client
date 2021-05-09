package com.murengezi.minecraft.block;

import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {

    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private final int field_150068_a;

    protected BlockPressurePlateWeighted(Material material, int p_i46379_2_) {
        this(material, p_i46379_2_, material.getMaterialMapColor());
    }

    protected BlockPressurePlateWeighted(Material material, int p_i46380_2_, MapColor mapColor) {
        super(material, mapColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
        this.field_150068_a = p_i46380_2_;
    }

    protected int computeRedstoneStrength(World world, BlockPos pos) {
        int i = Math.min(world.getEntitiesWithinAABB(Entity.class, this.getSensitiveAABB(pos)).size(), this.field_150068_a);

        if (i > 0) {
            float f = (float)Math.min(this.field_150068_a, i) / (float)this.field_150068_a;
            return MathHelper.ceiling_float_int(f * 15.0F);
        } else {
            return 0;
        }
    }

    protected int getRedstoneStrength(IBlockState state) {
        return state.getValue(POWER);
    }

    protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
        return state.withProperty(POWER, strength);
    }

    public int tickRate(World world) {
        return 10;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWER, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWER);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, POWER);
    }

}
