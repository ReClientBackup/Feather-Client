package net.optifine.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiOptionButton;
import com.murengezi.minecraft.client.resources.I18n;
import net.optifine.config.Config;

public class GuiMessage extends Screen {
   private final Screen parentScreen;
   private final String messageLine1;
   private final String messageLine2;
   private final List listLines2 = Lists.newArrayList();
   protected String confirmButtonText;
   private int ticksUntilEnable;

   public GuiMessage(Screen parentScreen, String line1, String line2) {
      this.parentScreen = parentScreen;
      this.messageLine1 = line1;
      this.messageLine2 = line2;
      this.confirmButtonText = I18n.format("gui.done");
   }

   public void initGui() {
      this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 74, this.height / 6 + 96, this.confirmButtonText));
      this.listLines2.clear();
      this.listLines2.addAll(getFr().listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      Config.getMinecraft().displayGuiScreen(this.parentScreen);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawWorldBackground();
      getFr().drawCenteredString(this.messageLine1, this.width / 2, 70, 16777215);
      int i = 90;

      for(Object o : this.listLines2) {
         String s = (String) o;
         getFr().drawCenteredString(s, this.width / 2, i, 16777215);
         i += getFr().FONT_HEIGHT;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void setButtonDelay(int ticksUntilEnable) {
      this.ticksUntilEnable = ticksUntilEnable;

      for(GuiButton guibutton : this.buttonList) {
         guibutton.setEnabled(false);
      }
   }

   public void updateScreen() {
      super.updateScreen();
      if(--this.ticksUntilEnable == 0) {
         for(GuiButton guibutton : this.buttonList) {
            guibutton.setEnabled(true);
         }
      }
   }
}
