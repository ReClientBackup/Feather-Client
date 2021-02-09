package net.optifine.player;

import java.awt.image.BufferedImage;

import com.murengezi.feather.Util.CapeUtils;
import com.murengezi.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer extends ImageBufferDownload {

   private AbstractClientPlayer player;
   private ResourceLocation resourceLocation;

   public CapeImageBuffer(AbstractClientPlayer player, ResourceLocation resourceLocation) {
      this.player = player;
      this.resourceLocation = resourceLocation;
   }

   public BufferedImage parseUserSkin(BufferedImage imageRaw) {
      BufferedImage bufferedimage = CapeUtils.parseCape(imageRaw);
      return bufferedimage;
   }

   public void skinAvailable() {
      if(this.player != null) {
         this.player.setCapeLocation(this.resourceLocation);
      }

      this.cleanup();
   }

   public void cleanup() {
      this.player = null;
   }

}
