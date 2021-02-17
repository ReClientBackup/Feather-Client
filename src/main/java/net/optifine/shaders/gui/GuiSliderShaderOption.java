package net.optifine.shaders.gui;

import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.optifine.shaders.config.ShaderOption;

public class GuiSliderShaderOption extends GuiButtonShaderOption {
   private float sliderValue = 1.0F;
   public boolean dragging;
   private ShaderOption shaderOption = null;

   public GuiSliderShaderOption(int buttonId, int x, int y, int w, int h, ShaderOption shaderOption, String text) {
      super(buttonId, x, y, w, h, shaderOption, text);
      this.shaderOption = shaderOption;
      this.sliderValue = shaderOption.getIndexNormalized();
      this.displayString = GuiShaderOptions.getButtonText(shaderOption, this.getWidth());
   }

   protected int getHoverState(boolean mouseOver) {
      return 0;
   }

   protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
      if(this.isVisible()) {
         if(this.dragging && !Screen.isShiftKeyDown()) {
            this.sliderValue = (float)(mouseX - (this.getX() + 4)) / (float)(this.getWidth() - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            this.shaderOption.setIndexNormalized(this.sliderValue);
            this.sliderValue = this.shaderOption.getIndexNormalized();
            this.displayString = GuiShaderOptions.getButtonText(this.shaderOption, this.getWidth());
         }

         getMc().getTextureManager().bindTexture(buttonTextures);
         GlStateManager.colorAllMax();
         this.drawTexturedModalRect(this.getX() + (int)(this.sliderValue * (float)(this.getWidth() - 8)), this.getY(), 0, 66, 4, 20);
         this.drawTexturedModalRect(this.getX() + (int)(this.sliderValue * (float)(this.getWidth() - 8)) + 4, this.getY(), 196, 66, 4, 20);
      }
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if(super.mousePressed(mc, mouseX, mouseY)) {
         this.sliderValue = (float)(mouseX - (this.getX() + 4)) / (float)(this.getWidth() - 8);
         this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
         this.shaderOption.setIndexNormalized(this.sliderValue);
         this.displayString = GuiShaderOptions.getButtonText(this.shaderOption, this.getWidth());
         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   public void mouseReleased(int mouseX, int mouseY) {
      this.dragging = false;
   }

   public void valueChanged() {
      this.sliderValue = this.shaderOption.getIndexNormalized();
   }

   public boolean isSwitchable() {
      return false;
   }
}
