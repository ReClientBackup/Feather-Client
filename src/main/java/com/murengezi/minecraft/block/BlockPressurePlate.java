package com.murengezi.minecraft.block;

import java.util.List;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyBool;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {

    public static final PropertyBool POWERED = PropertyBool.create("powered");
    private final BlockPressurePlate.Sensitivity sensitivity;

    protected BlockPressurePlate(Material material, BlockPressurePlate.Sensitivity sensitivityIn) {
        super(material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false));
        this.sensitivity = sensitivityIn;
    }

    protected int getRedstoneStrength(IBlockState state) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
        return state.withProperty(POWERED, strength > 0);
    }

    protected int computeRedstoneStrength(World world, BlockPos pos) {
        AxisAlignedBB axisalignedbb = this.getSensitiveAABB(pos);
        List <? extends Entity > list;

        switch (this.sensitivity) {
            case EVERYTHING:
                list = world.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);
                break;
            case MOBS:
                list = world.<Entity>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
                break;
            default:
                return 0;
        }

        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (!entity.doesEntityNotTriggerPressurePlate()) {
                    return 15;
                }
            }
        }

        return 0;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWERED, meta == 1);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWERED) ? 1 : 0;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, POWERED);
    }

    public enum Sensitivity {
        EVERYTHING, MOBS
    }

}
