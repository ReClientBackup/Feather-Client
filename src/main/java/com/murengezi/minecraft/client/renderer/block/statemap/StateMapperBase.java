package com.murengezi.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.properties.IProperty;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.client.resources.model.ModelResourceLocation;

public abstract class StateMapperBase implements IStateMapper {

    protected Map<IBlockState, ModelResourceLocation> mapStateModelLocations = Maps.newLinkedHashMap();

    public String getPropertyString(Map<IProperty, Comparable> p_178131_1_) {
        StringBuilder stringbuilder = new StringBuilder();

        for (Entry<IProperty, Comparable> entry : p_178131_1_.entrySet()) {
            if (stringbuilder.length() != 0) {
                stringbuilder.append(",");
            }

            IProperty iproperty = entry.getKey();
            Comparable comparable = entry.getValue();
            stringbuilder.append(iproperty.getName());
            stringbuilder.append("=");
            stringbuilder.append(iproperty.getName(comparable));
        }

        if (stringbuilder.length() == 0) {
            stringbuilder.append("normal");
        }

        return stringbuilder.toString();
    }

    public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
        for (IBlockState iblockstate : block.getBlockState().getValidStates()) {
            this.mapStateModelLocations.put(iblockstate, this.getModelResourceLocation(iblockstate));
        }

        return this.mapStateModelLocations;
    }

    protected abstract ModelResourceLocation getModelResourceLocation(IBlockState state);

}
