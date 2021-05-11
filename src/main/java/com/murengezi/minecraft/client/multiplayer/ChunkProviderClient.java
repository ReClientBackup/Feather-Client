package com.murengezi.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderClient implements IChunkProvider {

    private static final Logger logger = LogManager.getLogger();
    private final Chunk blankChunk;
    private final LongHashMap<Chunk> chunkMapping = new LongHashMap();
    private final List<Chunk> chunkListing = Lists.newArrayList();
    private final World worldObj;

    public ChunkProviderClient(World world) {
        this.blankChunk = new EmptyChunk(world, 0, 0);
        this.worldObj = world;
    }

    public boolean chunkExists(int x, int z) {
        return true;
    }

    public void unloadChunk(int x, int z) {
        Chunk chunk = this.provideChunk(x, z);

        if (!chunk.isEmpty()) {
            chunk.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(x, z));
        this.chunkListing.remove(chunk);
    }

    public Chunk loadChunk(int x, int z) {
        Chunk chunk = new Chunk(this.worldObj, x, z);
        this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(x, z), chunk);
        this.chunkListing.add(chunk);
        chunk.setChunkLoaded(true);
        return chunk;
    }

    public Chunk provideChunk(int x, int z) {
        Chunk chunk = this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
        return chunk == null ? this.blankChunk : chunk;
    }

    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate progressCallback) {
        return true;
    }

    public void saveExtraData() {}

    public boolean unloadQueuedChunks() {
        long i = System.currentTimeMillis();

        for (Chunk chunk : this.chunkListing) {
            chunk.func_150804_b(System.currentTimeMillis() - i > 5L);
        }

        if (System.currentTimeMillis() - i > 100L) {
            logger.info("Warning: Clientside chunk ticking took {} ms", Long.valueOf(System.currentTimeMillis() - i));
        }

        return false;
    }

    public boolean canSave() {
        return false;
    }

    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {}

    public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_) {
        return false;
    }

    public String makeString() {
        return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return null;
    }

    public BlockPos getStrongholdGen(World world, String structureName, BlockPos position) {
        return null;
    }

    public int getLoadedChunkCount() {
        return this.chunkListing.size();
    }

    public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_) {}

    public Chunk provideChunk(BlockPos blockPos) {
        return this.provideChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

}
