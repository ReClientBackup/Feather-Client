package com.murengezi.minecraft.client.renderer.chunk;

import com.murengezi.minecraft.client.renderer.GLAllocation;
import com.murengezi.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class ListedRenderChunk extends RenderChunk {

    private final int baseDisplayList = GLAllocation.generateDisplayLists(EnumWorldBlockLayer.values().length);

    public ListedRenderChunk(World world, RenderGlobal renderGlobal, BlockPos pos, int indexIn) {
        super(world, renderGlobal, pos, indexIn);
    }

    public int getDisplayList(EnumWorldBlockLayer layer, CompiledChunk chunk) {
        return !chunk.isLayerEmpty(layer) ? this.baseDisplayList + layer.ordinal() : -1;
    }

    public void deleteGlResources() {
        super.deleteGlResources();
        GLAllocation.deleteDisplayLists(this.baseDisplayList, EnumWorldBlockLayer.values().length);
    }

}
