package net.optifine.entity.model;

import com.murengezi.minecraft.client.model.ModelBanner;
import com.murengezi.minecraft.client.model.ModelBase;
import com.murengezi.minecraft.client.model.ModelRenderer;
import com.murengezi.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import com.murengezi.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import com.murengezi.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.optifine.config.Config;
import net.minecraft.tileentity.TileEntityBanner;
import net.optifine.reflect.Reflector;

public class ModelAdapterBanner extends ModelAdapter {
   public ModelAdapterBanner() {
      super(TileEntityBanner.class, "banner", 0.0F);
   }

   public ModelBase makeModel() {
      return new ModelBanner();
   }

   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if(!(model instanceof ModelBanner)) {
         return null;
      } else {
         ModelBanner modelbanner = (ModelBanner)model;
         return modelPart.equals("slate")?modelbanner.bannerSlate:(modelPart.equals("stand")?modelbanner.bannerStand:(modelPart.equals("top")?modelbanner.bannerTop:null));
      }
   }

   public String[] getModelRendererNames() {
      return new String[]{"slate", "stand", "top"};
   }

   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntityBanner.class);
      if(!(tileentityspecialrenderer instanceof TileEntityBannerRenderer)) {
         return null;
      } else {
         if(tileentityspecialrenderer.getEntityClass() == null) {
            tileentityspecialrenderer = new TileEntityBannerRenderer();
            tileentityspecialrenderer.setRendererDispatcher(tileentityrendererdispatcher);
         }

         if(!Reflector.TileEntityBannerRenderer_bannerModel.exists()) {
            Config.warn("Field not found: TileEntityBannerRenderer.bannerModel");
            return null;
         } else {
            Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntityBannerRenderer_bannerModel, modelBase);
            return tileentityspecialrenderer;
         }
      }
   }
}
