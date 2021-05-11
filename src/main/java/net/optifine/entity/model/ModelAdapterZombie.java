package net.optifine.entity.model;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.model.ModelBiped;
import com.murengezi.minecraft.client.model.ModelZombie;
import com.murengezi.minecraft.client.renderer.entity.Render;
import com.murengezi.minecraft.client.renderer.entity.RenderManager;
import com.murengezi.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;

public class ModelAdapterZombie extends ModelAdapterBiped {
   public ModelAdapterZombie() {
      super(EntityZombie.class, "zombie", 0.5F);
   }

   public ModelBase makeModel() {
      return new ModelZombie();
   }

   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      RenderZombie renderzombie = new RenderZombie(rendermanager);
      Render.setModelBipedMain(renderzombie, (ModelBiped)modelBase);
      renderzombie.mainModel = modelBase;
      renderzombie.shadowSize = shadowSize;
      return renderzombie;
   }
}
