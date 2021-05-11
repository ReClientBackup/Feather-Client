package net.optifine.gui;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiOptionButton;
import com.murengezi.minecraft.client.resources.I18n;
import com.murengezi.minecraft.client.settings.GameSettings;

public class GuiPerformanceSettingsOF extends Screen {
   private final Screen prevScreen;
   protected String title;
   private final GameSettings settings;
   private static final GameSettings.Options[] enumOptions = new GameSettings.Options[]{GameSettings.Options.SMOOTH_FPS, GameSettings.Options.SMOOTH_WORLD, GameSettings.Options.FAST_RENDER, GameSettings.Options.FAST_MATH, GameSettings.Options.CHUNK_UPDATES, GameSettings.Options.CHUNK_UPDATES_DYNAMIC, GameSettings.Options.RENDER_REGIONS, GameSettings.Options.LAZY_CHUNK_LOADING, GameSettings.Options.SMART_ANIMATIONS};
   private final TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

   public GuiPerformanceSettingsOF(Screen guiscreen, GameSettings gamesettings) {
      this.prevScreen = guiscreen;
      this.settings = gamesettings;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.performanceTitle");
      this.getButtonList().clear();

      for(int i = 0; i < enumOptions.length; ++i) {
         GameSettings.Options gamesettings$options = enumOptions[i];
         int j = this.width / 2 - 155 + i % 2 * 160;
         int k = this.height / 6 + 21 * (i / 2) - 12;
         if(!gamesettings$options.getEnumFloat()) {
            addButton(new GuiOptionButtonOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options, this.settings.getKeyBinding(gamesettings$options)));
         } else {
            addButton(new GuiOptionSliderOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options));
         }
      }

      addButton(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   protected void actionPerformed(GuiButton guibutton) {
      if(guibutton.isEnabled()) {
         if(guibutton.getId() < 200 && guibutton instanceof GuiOptionButton) {
            this.settings.setOptionValue(((GuiOptionButton)guibutton).getOptions(), 1);
            guibutton.displayString = this.settings.getKeyBinding(GameSettings.Options.getEnumOptions(guibutton.getId()));
         }

         if(guibutton.getId() == 200) {
            saveSettings();
            changeScreen(this.prevScreen);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawWorldBackground();
      getFr().drawCenteredString(this.title, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.tooltipManager.drawTooltips(mouseX, mouseY, this.getButtonList());
   }
}
