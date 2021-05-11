package net.optifine.entity.model;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.model.ModelSheep2;
import com.murengezi.minecraft.client.renderer.entity.RenderManager;
import com.murengezi.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.entity.passive.EntitySheep;

public class ModelAdapterSheep extends ModelAdapterQuadruped {
   public ModelAdapterSheep() {
      super(EntitySheep.class, "sheep", 0.7F);
   }

   public ModelBase makeModel() {
      return new ModelSheep2();
   }

   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      return new RenderSheep(rendermanager, modelBase, shadowSize);
   }
}
