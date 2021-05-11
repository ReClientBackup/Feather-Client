package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.config.Config;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

public class RenderList extends ChunkRenderContainer {

	private double viewEntityX, viewEntityY, viewEntityZ;
	IntBuffer bufferLists = GLAllocation.createDirectIntBuffer(16);

	public void renderChunkLayer(EnumWorldBlockLayer layer) {
		if (this.initialized) {
			if (!Config.isRenderRegions()) {
				for (RenderChunk chunk : this.renderChunks) {
					ListedRenderChunk renderChunk = (ListedRenderChunk) chunk;
					GlStateManager.pushMatrix();
					this.preRenderChunk(chunk);
					GL11.glCallList(renderChunk.getDisplayList(layer, renderChunk.getCompiledChunk()));
					GlStateManager.popMatrix();
				}
			} else {
				int i = Integer.MIN_VALUE;
				int j = Integer.MIN_VALUE;

				for (RenderChunk renderchunk : this.renderChunks) {
					ListedRenderChunk listedrenderchunk = (ListedRenderChunk) renderchunk;

					if (i != renderchunk.regionX || j != renderchunk.regionZ) {
						if (this.bufferLists.position() > 0) {
							this.drawRegion(i, j, this.bufferLists);
						}

						i = renderchunk.regionX;
						j = renderchunk.regionZ;
					}

					if (this.bufferLists.position() >= this.bufferLists.capacity()) {
						IntBuffer intbuffer = GLAllocation.createDirectIntBuffer(this.bufferLists.capacity() * 2);
						this.bufferLists.flip();
						intbuffer.put(this.bufferLists);
						this.bufferLists = intbuffer;
					}

					this.bufferLists.put(listedrenderchunk.getDisplayList(layer, listedrenderchunk.getCompiledChunk()));
				}

				if (this.bufferLists.position() > 0) {
					this.drawRegion(i, j, this.bufferLists);
				}
			}

			if (Config.isMultiTexture()) {
				GlStateManager.bindCurrentTexture();
			}

			GlStateManager.resetColor();
			this.renderChunks.clear();
		}
	}

	public void initialize(double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
		this.viewEntityX = viewEntityXIn;
		this.viewEntityY = viewEntityYIn;
		this.viewEntityZ = viewEntityZIn;
		super.initialize(viewEntityXIn, viewEntityYIn, viewEntityZIn);
	}

	private void drawRegion(int p_drawRegion_1_, int p_drawRegion_2_, IntBuffer p_drawRegion_3_) {
		GlStateManager.pushMatrix();
		this.preRenderRegion(p_drawRegion_1_, 0, p_drawRegion_2_);
		p_drawRegion_3_.flip();
		GlStateManager.callLists(p_drawRegion_3_);
		p_drawRegion_3_.clear();
		GlStateManager.popMatrix();
	}

	public void preRenderRegion(int p_preRenderRegion_1_, int p_preRenderRegion_2_, int p_preRenderRegion_3_) {
		GlStateManager.translate((float) ((double) p_preRenderRegion_1_ - this.viewEntityX), (float) ((double) p_preRenderRegion_2_ - this.viewEntityY), (float) ((double) p_preRenderRegion_3_ - this.viewEntityZ));
	}

}
