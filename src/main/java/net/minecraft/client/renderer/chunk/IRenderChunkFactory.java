package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IRenderChunkFactory {

    RenderChunk makeRenderChunk(World world, RenderGlobal globalRenderer, BlockPos pos, int index);

}
