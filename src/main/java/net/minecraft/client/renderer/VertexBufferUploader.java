package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.optifine.config.Config;

public class VertexBufferUploader extends WorldVertexBufferUploader {

	private VertexBuffer vertexBuffer = null;

	public void func_181679_a(WorldRenderer renderer) {
		if (renderer.getDrawMode() == 7 && Config.isQuadsToTriangles()) {
			renderer.quadsToTriangles();
			this.vertexBuffer.setDrawMode(renderer.getDrawMode());
		}

		this.vertexBuffer.func_181722_a(renderer.getByteBuffer());
		renderer.reset();
	}

	public void setVertexBuffer(VertexBuffer vertexBufferIn) {
		this.vertexBuffer = vertexBufferIn;
	}

}
