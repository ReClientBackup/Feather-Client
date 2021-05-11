package com.murengezi.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import com.murengezi.minecraft.client.renderer.RegionRenderCacheBuilder;

public class ChunkCompileTaskGenerator {

    private final RenderChunk renderChunk;
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Runnable> listFinishRunnables = Lists.newArrayList();
    private final ChunkCompileTaskGenerator.Type type;
    private RegionRenderCacheBuilder regionRenderCacheBuilder;
    private CompiledChunk compiledChunk;
    private ChunkCompileTaskGenerator.Status status = ChunkCompileTaskGenerator.Status.PENDING;
    private boolean finished;

    public ChunkCompileTaskGenerator(RenderChunk renderChunk, ChunkCompileTaskGenerator.Type type) {
        this.renderChunk = renderChunk;
        this.type = type;
    }

    public ChunkCompileTaskGenerator.Status getStatus() {
        return this.status;
    }

    public RenderChunk getRenderChunk() {
        return this.renderChunk;
    }

    public CompiledChunk getCompiledChunk() {
        return this.compiledChunk;
    }

    public void setCompiledChunk(CompiledChunk compiledChunk) {
        this.compiledChunk = compiledChunk;
    }

    public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
        return this.regionRenderCacheBuilder;
    }

    public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilder) {
        this.regionRenderCacheBuilder = regionRenderCacheBuilder;
    }

    public void setStatus(ChunkCompileTaskGenerator.Status status) {
        this.lock.lock();

        try {
            this.status = status;
        } finally {
            this.lock.unlock();
        }
    }

    public void finish() {
        this.lock.lock();

        try {
            if (this.type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK && this.status != ChunkCompileTaskGenerator.Status.DONE) {
                this.renderChunk.setNeedsUpdate(true);
            }

            this.finished = true;
            this.status = ChunkCompileTaskGenerator.Status.DONE;

            for (Runnable runnable : this.listFinishRunnables) {
                runnable.run();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void addFinishRunnable(Runnable runnable) {
        this.lock.lock();

        try {
            this.listFinishRunnables.add(runnable);

            if (this.finished) {
                runnable.run();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public ReentrantLock getLock() {
        return this.lock;
    }

    public ChunkCompileTaskGenerator.Type getType() {
        return this.type;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public enum Status {
        PENDING, COMPILING, UPLOADING, DONE
    }

    public enum Type {
        REBUILD_CHUNK, RESORT_TRANSPARENCY
    }

}
