package net.optifine.entity.model;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.model.ModelCow;
import com.murengezi.minecraft.client.renderer.entity.RenderCow;
import com.murengezi.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityCow;

public class ModelAdapterCow extends ModelAdapterQuadruped {
   public ModelAdapterCow() {
      super(EntityCow.class, "cow", 0.7F);
   }

   public ModelBase makeModel() {
      return new ModelCow();
   }

   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      return new RenderCow(rendermanager, modelBase, shadowSize);
   }
}
