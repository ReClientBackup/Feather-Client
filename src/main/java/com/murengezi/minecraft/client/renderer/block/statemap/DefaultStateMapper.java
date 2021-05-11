package com.murengezi.minecraft.client.renderer.block.statemap;

import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.client.resources.model.ModelResourceLocation;

public class DefaultStateMapper extends StateMapperBase {

    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation(Block.blockRegistry.getNameForObject(state.getBlock()), this.getPropertyString(state.getProperties()));
    }

}
