package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class VboChunkFactory implements IRenderChunkFactory {

    public RenderChunk makeRenderChunk(World world, RenderGlobal globalRenderer, BlockPos pos, int index) {
        return new RenderChunk(world, globalRenderer, pos, index);
    }

}
