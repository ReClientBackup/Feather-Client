package net.optifine.shaders.gui;

import com.murengezi.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonDownloadShaders extends GuiButton {
   public GuiButtonDownloadShaders(int buttonID, int xPos, int yPos) {
      super(buttonID, xPos, yPos, 22, 20, "");
   }

   @Override
   public void drawButton(int mouseX, int mouseY) {
      if(this.isVisible()) {
         super.drawButton(mouseX, mouseY);
         ResourceLocation resourcelocation = new ResourceLocation("optifine/textures/icons.png");
         getMc().getTextureManager().bindTexture(resourcelocation);
         GlStateManager.colorAllMax();
         this.drawTexturedModalRect(this.getX() + 3, this.getY() + 2, 0, 0, 16, 16);
      }
   }
}
