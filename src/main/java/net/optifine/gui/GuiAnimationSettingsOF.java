package net.optifine.gui;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.optifine.Lang;

public class GuiAnimationSettingsOF extends Screen {

   private final Screen prevScreen;
   protected String title;
   private final GameSettings settings;
   private static final GameSettings.Options[] enumOptions = new GameSettings.Options[]{GameSettings.Options.ANIMATED_WATER, GameSettings.Options.ANIMATED_LAVA, GameSettings.Options.ANIMATED_FIRE, GameSettings.Options.ANIMATED_PORTAL, GameSettings.Options.ANIMATED_REDSTONE, GameSettings.Options.ANIMATED_EXPLOSION, GameSettings.Options.ANIMATED_FLAME, GameSettings.Options.ANIMATED_SMOKE, GameSettings.Options.VOID_PARTICLES, GameSettings.Options.WATER_PARTICLES, GameSettings.Options.RAIN_SPLASH, GameSettings.Options.PORTAL_PARTICLES, GameSettings.Options.POTION_PARTICLES, GameSettings.Options.DRIPPING_WATER_LAVA, GameSettings.Options.ANIMATED_TERRAIN, GameSettings.Options.ANIMATED_TEXTURES, GameSettings.Options.FIREWORK_PARTICLES, GameSettings.Options.PARTICLES};

   public GuiAnimationSettingsOF(Screen guiscreen, GameSettings gamesettings) {
      this.prevScreen = guiscreen;
      this.settings = gamesettings;
   }

   public void initGui() {
      this.title = I18n.format("of.options.animationsTitle");
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

      addButton(new GuiButton(210, this.width / 2 - 155, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOn")));
      addButton(new GuiButton(211, this.width / 2 - 155 + 80, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOff")));
      addButton(new GuiOptionButton(200, this.width / 2 + 5, this.height / 6 + 168 + 11, I18n.format("gui.done")));
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
            getGs().setAllAnimations(true);
         }

         if(guibutton.getId() == 211) {
            getGs().setAllAnimations(false);
         }

         ScaledResolution resolution = new ScaledResolution();
         this.setWorldAndResolution(resolution.getScaledWidth(), resolution.getScaledHeight());
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawWorldBackground();
      getFr().drawCenteredString(this.title, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
