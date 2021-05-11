package net.optifine.entity.model;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.model.ModelPig;
import com.murengezi.minecraft.client.renderer.entity.RenderManager;
import com.murengezi.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.passive.EntityPig;

public class ModelAdapterPig extends ModelAdapterQuadruped {
   public ModelAdapterPig() {
      super(EntityPig.class, "pig", 0.7F);
   }

   public ModelBase makeModel() {
      return new ModelPig();
   }

   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      return new RenderPig(rendermanager, modelBase, shadowSize);
   }
}
