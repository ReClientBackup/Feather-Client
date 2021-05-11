package net.optifine.shaders;

import com.murengezi.minecraft.client.renderer.texture.AbstractTexture;
import com.murengezi.minecraft.client.resources.IResourceManager;

public class DefaultTexture extends AbstractTexture {

   public DefaultTexture() {
      this.loadTexture(null);
   }

   public void loadTexture(IResourceManager resourcemanager) {
      int[] aint = ShadersTex.createAIntImage(1, -1);
      ShadersTex.setupTexture(this.getMultiTexID(), aint, 1, 1, false, false);
   }
}
