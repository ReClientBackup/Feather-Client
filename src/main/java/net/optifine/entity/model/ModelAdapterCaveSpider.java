package net.optifine.entity.model;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.renderer.entity.RenderCaveSpider;
import com.murengezi.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCaveSpider;

public class ModelAdapterCaveSpider extends ModelAdapterSpider {
   public ModelAdapterCaveSpider() {
      super(EntityCaveSpider.class, "cave_spider", 0.7F);
   }

   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      RenderCaveSpider rendercavespider = new RenderCaveSpider(rendermanager);
      rendercavespider.mainModel = modelBase;
      rendercavespider.shadowSize = shadowSize;
      return rendercavespider;
   }
}
