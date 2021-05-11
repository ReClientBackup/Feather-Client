package net.optifine.entity.model;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.renderer.entity.RenderManager;
import com.murengezi.minecraft.client.renderer.entity.RenderMinecartMobSpawner;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.optifine.config.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMinecartMobSpawner extends ModelAdapterMinecart {
   public ModelAdapterMinecartMobSpawner() {
      super(EntityMinecartMobSpawner.class, "spawner_minecart", 0.5F);
   }

   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      RenderMinecartMobSpawner renderminecartmobspawner = new RenderMinecartMobSpawner(rendermanager);
      if(!Reflector.RenderMinecart_modelMinecart.exists()) {
         Config.warn("Field not found: RenderMinecart.modelMinecart");
         return null;
      } else {
         Reflector.setFieldValue(renderminecartmobspawner, Reflector.RenderMinecart_modelMinecart, modelBase);
         renderminecartmobspawner.shadowSize = shadowSize;
         return renderminecartmobspawner;
      }
   }
}
