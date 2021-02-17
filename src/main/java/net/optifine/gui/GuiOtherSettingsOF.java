package net.optifine.gui;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.YesNoScreen;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiOtherSettingsOF extends Screen {
   private final Screen prevScreen;
   protected String title;
   private final GameSettings settings;
   private static final GameSettings.Options[] enumOptions = new GameSettings.Options[]{GameSettings.Options.LAGOMETER, GameSettings.Options.PROFILER, GameSettings.Options.SHOW_FPS, GameSettings.Options.ADVANCED_TOOLTIPS, GameSettings.Options.WEATHER, GameSettings.Options.TIME, GameSettings.Options.USE_FULLSCREEN, GameSettings.Options.FULLSCREEN_MODE, GameSettings.Options.ANAGLYPH, GameSettings.Options.AUTOSAVE_TICKS, GameSettings.Options.SCREENSHOT_SIZE, GameSettings.Options.SHOW_GL_ERRORS};
   private final TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

   public GuiOtherSettingsOF(Screen guiscreen, GameSettings gamesettings) {
      this.prevScreen = guiscreen;
      this.settings = gamesettings;
   }

   public void initGui() {
      this.title = I18n.format("of.options.otherTitle");
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

      addButton(new GuiButton(210, this.width / 2 - 100, this.height / 6 + 168 + 11 - 44, I18n.format("of.options.other.reset")));
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

         if(guibutton.getId() == 210) {
            saveSettings();
            YesNoScreen yesNoScreen = new YesNoScreen(this, I18n.format("of.message.other.reset"), "", 9999);
            changeScreen(yesNoScreen);
         }
      }
   }

   public void confirmClicked(boolean flag, int i) {
      if(flag) {
         getGs().resetSettings();
      }

      changeScreen(this);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawWorldBackground();
      getFr().drawCenteredString(this.title, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.tooltipManager.drawTooltips(mouseX, mouseY, this.getButtonList());
   }
}
