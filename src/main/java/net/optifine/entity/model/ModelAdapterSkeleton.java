package net.optifine.entity.model;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.model.ModelBiped;
import com.murengezi.minecraft.client.model.ModelSkeleton;
import com.murengezi.minecraft.client.renderer.entity.Render;
import com.murengezi.minecraft.client.renderer.entity.RenderManager;
import com.murengezi.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;

public class ModelAdapterSkeleton extends ModelAdapterBiped {

   public ModelAdapterSkeleton() {
      super(EntitySkeleton.class, "skeleton", 0.7F);
   }

   public ModelBase makeModel() {
      return new ModelSkeleton();
   }

   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      RenderSkeleton renderskeleton = new RenderSkeleton(rendermanager);
      Render.setModelBipedMain(renderskeleton, (ModelBiped)modelBase);
      renderskeleton.mainModel = modelBase;
      renderskeleton.shadowSize = shadowSize;
      return renderskeleton;
   }
}
