package com.murengezi.minecraft.client.renderer.chunk;

import com.murengezi.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ListChunkFactory implements IRenderChunkFactory {

    public RenderChunk makeRenderChunk(World world, RenderGlobal globalRenderer, BlockPos pos, int index) {
        return new ListedRenderChunk(world, globalRenderer, pos, index);
    }

}
