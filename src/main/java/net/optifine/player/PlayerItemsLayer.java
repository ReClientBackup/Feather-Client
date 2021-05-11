package net.optifine.player;

import java.util.Map;
import java.util.Set;

import com.murengezi.minecraft.client.entity.AbstractClientPlayer;
import com.murengezi.minecraft.client.model.ModelBiped;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.entity.RenderPlayer;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.config.Config;

public class PlayerItemsLayer implements LayerRenderer {
   private RenderPlayer renderPlayer = null;

   public PlayerItemsLayer(RenderPlayer renderPlayer) {
      this.renderPlayer = renderPlayer;
   }

   public void doRenderLayer(EntityLivingBase entityLivingBase, float limbSwing, float limbSwingAmount, float partialTicks, float ticksExisted, float headYaw, float rotationPitch, float scale) {
      this.renderEquippedItems(entityLivingBase, scale, partialTicks);
   }

   protected void renderEquippedItems(EntityLivingBase entityLiving, float scale, float partialTicks) {
      if(Config.isShowCapes()) {
         if(entityLiving instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)entityLiving;
            GlStateManager.colorAllMax();
            GlStateManager.disableRescaleNormal();
            GlStateManager.enableCull();
            ModelBiped modelbiped = this.renderPlayer.getMainModel();
            PlayerConfigurations.renderPlayerItems(modelbiped, abstractclientplayer, scale, partialTicks);
            GlStateManager.disableCull();
         }
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   public static void register(Map renderPlayerMap) {
      Set set = renderPlayerMap.keySet();
      boolean flag = false;

      for(Object object : set) {
         Object object1 = renderPlayerMap.get(object);
         if(object1 instanceof RenderPlayer) {
            RenderPlayer renderplayer = (RenderPlayer)object1;
            renderplayer.addLayer(new PlayerItemsLayer(renderplayer));
            flag = true;
         }
      }

      if(!flag) {
         Config.warn("PlayerItemsLayer not registered");
      }
   }
}
