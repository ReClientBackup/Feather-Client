package net.optifine.entity.model;

import java.util.Iterator;
import java.util.List;
import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.model.ModelSheep1;
import com.murengezi.minecraft.client.model.ModelSheep2;
import com.murengezi.minecraft.client.renderer.entity.Render;
import com.murengezi.minecraft.client.renderer.entity.RenderManager;
import com.murengezi.minecraft.client.renderer.entity.RenderSheep;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerRenderer;
import com.murengezi.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.entity.passive.EntitySheep;
import net.optifine.config.Config;

public class ModelAdapterSheepWool extends ModelAdapterQuadruped {
   public ModelAdapterSheepWool() {
      super(EntitySheep.class, "sheep_wool", 0.7F);
   }

   public ModelBase makeModel() {
      return new ModelSheep1();
   }

   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      Render render = rendermanager.getEntityRenderMap().get(EntitySheep.class);
      if(!(render instanceof RenderSheep)) {
         Config.warn("Not a RenderSheep: " + render);
         return null;
      } else {
         if(render.getEntityClass() == null) {
            render = new RenderSheep(rendermanager, new ModelSheep2(), 0.7F);
         }

         RenderSheep rendersheep = (RenderSheep)render;
         List<LayerRenderer<EntitySheep>> list = rendersheep.getLayerRenderers();
         Iterator iterator = list.iterator();

         while(iterator.hasNext()) {
            LayerRenderer layerrenderer = (LayerRenderer)iterator.next();
            if(layerrenderer instanceof LayerSheepWool) {
               iterator.remove();
            }
         }

         LayerSheepWool layersheepwool = new LayerSheepWool(rendersheep);
         layersheepwool.sheepModel = (ModelSheep1)modelBase;
         rendersheep.addLayer(layersheepwool);
         return rendersheep;
      }
   }
}
