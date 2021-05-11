package com.murengezi.minecraft.client.renderer.block.statemap;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class BlockStateMapper {

    private final Map<Block, IStateMapper> blockStateMap = Maps.newIdentityHashMap();
    private final Set<Block> setBuiltInBlocks = Sets.newIdentityHashSet();

    public void registerBlockStateMapper(Block block, IStateMapper mapper) {
        this.blockStateMap.put(block, mapper);
    }

    public void registerBuiltInBlocks(Block... blocks) {
        Collections.addAll(this.setBuiltInBlocks, blocks);
    }

    public Map<IBlockState, ModelResourceLocation> putAllStateModelLocations() {
        Map<IBlockState, ModelResourceLocation> map = Maps.newIdentityHashMap();

        for (Block block : Block.blockRegistry) {
            if (!this.setBuiltInBlocks.contains(block)) {
                map.putAll(Objects.firstNonNull(this.blockStateMap.get(block), new DefaultStateMapper()).putStateModelLocations(block));
            }
        }

        return map;
    }

}
