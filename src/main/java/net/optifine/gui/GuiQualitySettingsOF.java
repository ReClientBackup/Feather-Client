package net.optifine.gui;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

import java.io.IOException;

public class GuiQualitySettingsOF extends Screen {
   private final Screen previousScreen;
   protected String title;
   private static final GameSettings.Options[] enumOptions = new GameSettings.Options[]{GameSettings.Options.MIPMAP_LEVELS, GameSettings.Options.MIPMAP_TYPE, GameSettings.Options.AF_LEVEL, GameSettings.Options.AA_LEVEL, GameSettings.Options.CLEAR_WATER, GameSettings.Options.RANDOM_ENTITIES, GameSettings.Options.BETTER_GRASS, GameSettings.Options.BETTER_SNOW, GameSettings.Options.CUSTOM_FONTS, GameSettings.Options.CUSTOM_COLORS, GameSettings.Options.CONNECTED_TEXTURES, GameSettings.Options.NATURAL_TEXTURES, GameSettings.Options.CUSTOM_SKY, GameSettings.Options.CUSTOM_ITEMS, GameSettings.Options.CUSTOM_ENTITY_MODELS, GameSettings.Options.CUSTOM_GUIS, GameSettings.Options.EMISSIVE_TEXTURES};
   private final TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

   public GuiQualitySettingsOF(Screen previousScreen) {
      this.previousScreen = previousScreen;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.qualityTitle");
      this.buttonList.clear();

      for(int i = 0; i < enumOptions.length; ++i) {
         GameSettings.Options gamesettings$options = enumOptions[i];
         int j = this.width / 2 - 155 + i % 2 * 160;
         int k = this.height / 6 + 21 * (i / 2) - 12;
         if(!gamesettings$options.getEnumFloat()) {
            addButton(new GuiOptionButtonOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options, getGs().getKeyBinding(gamesettings$options)));
         } else {
            addButton(new GuiOptionSliderOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options));
         }
      }

      addButton(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
      super.initGui();
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if(button.isEnabled()) {
         if(button.getId() < 200 && button instanceof GuiOptionButton) {
            getGs().setOptionValue(((GuiOptionButton)button).getOptions(), 1);
            button.displayString = getGs().getKeyBinding(GameSettings.Options.getEnumOptions(button.getId()));
         }

         if(button.getId() == 200) {
            saveSettings();
            changeScreen(previousScreen);
         }

         if(button.getId() != GameSettings.Options.AA_LEVEL.ordinal()) {
            ScaledResolution resolution = new ScaledResolution();
            this.setWorldAndResolution(resolution.getScaledWidth(), resolution.getScaledHeight());
         }
      }
      super.actionPerformed(button);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawWorldBackground();
      getFr().drawCenteredString(this.title, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.tooltipManager.drawTooltips(mouseX, mouseY, this.getButtonList());
   }
}
