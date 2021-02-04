package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.RenderModelBipedEvent;
import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Module.ModuleInfo;
import com.murengezi.feather.Module.Modules.betterhat.ModelBoxUV;
import com.murengezi.feather.Module.Modules.betterhat.ModelRendererUV;
import net.minecraft.client.renderer.GlStateManager;

@ModuleInfo(name = "BetterHat", description = "", version = "unknown", enabled = true)
public class BetterHat extends Module {

    private void initModel(ModelRendererUV model, float size) {
        ModelBoxUV modelboxuv = null;
        float f = 1.135F;


        for (int i = -4; i < 4; ++i) {
            for (int j = -4; j < 4; ++j) {
                modelboxuv = ModelBoxUV.addBox(model, (float)i * f, -8.5225F, (float)j * f, 1, 1, 1, size + 0.07F);
                modelboxuv.setAllUV(44 + i, 3 - j);
                modelboxuv = ModelBoxUV.addBox(model, (float)i * f, -0.5575F, (float)j * f, 1, 1, 1, size + 0.07F);
                modelboxuv.setAllUV(52 + i, 3 - j);
            }
        }

        for (int k = -4; k < 4; ++k) {
            for (int i1 = -8; i1 < 0; ++i1) {
                modelboxuv = ModelBoxUV.addBox(model, (float)k * f, ((float)i1 + 0.5F) * f, -4.55F, 1, 1, 1, size + 0.07F);
                modelboxuv.setAllUV(44 + k, 16 + i1);
                modelboxuv = ModelBoxUV.addBox(model, (float)k * f, ((float)i1 + 0.5F) * f, 3.415F, 1, 1, 1, size + 0.07F);
                modelboxuv.setAllUV(60 + k, 16 + i1);
            }
        }

        for (int l = -3; l < 4; ++l) {
            for (int j1 = -8; j1 < 0; ++j1) {
                modelboxuv = ModelBoxUV.addBox(model, -4.55F, ((float)j1 + 0.5F) * f, (float)l * f, 1, 1, 1, size + 0.07F);
                modelboxuv.setAllUV(36 - l - 1, 16 + j1);
                modelboxuv = ModelBoxUV.addBox(model, 3.415F, ((float)j1 + 0.5F) * f, (float)l * f, 1, 1, 1, size + 0.07F);
                modelboxuv.setAllUV(52 + l, 16 + j1);
            }
        }

        modelboxuv.initQuads();
    }

    @EventTarget
    public void onRenderBiped(RenderModelBipedEvent event) {
        event.setCancelled(true);

        if (!(event.getBipedHeadwear() instanceof ModelRendererUV)) {
            ModelRendererUV modelrendereruv = new ModelRendererUV(event.getModelBiped(), 32, 0);
            this.initModel(modelrendereruv, 0.001F);
            modelrendereruv.setRotationPoint(0.0F, 0.0F + event.getVar5(), 0.0F);
            event.setBipedHeadwear(modelrendereruv);
        } else {
            GlStateManager.resetColor();
            GlStateManager.enableDepth();
            event.getBipedHeadwear().isHidden = false;
            ((ModelRendererUV)event.getBipedHeadwear()).applyRotation(event.getBipedHead());
            ((ModelRendererUV)event.getBipedHeadwear()).renderBetterHat(event.getScale());
            event.getBipedHeadwear().isHidden = true;
        }
    }
}
