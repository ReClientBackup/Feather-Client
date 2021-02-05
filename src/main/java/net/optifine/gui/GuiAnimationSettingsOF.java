package net.optifine.gui;

import com.murengezi.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.optifine.Lang;

public class GuiAnimationSettingsOF extends Screen
{
    private final Screen prevScreen;
    protected String title;
    private final GameSettings settings;
    private static final GameSettings.Options[] enumOptions = new GameSettings.Options[] {GameSettings.Options.ANIMATED_WATER, GameSettings.Options.ANIMATED_LAVA, GameSettings.Options.ANIMATED_FIRE, GameSettings.Options.ANIMATED_PORTAL, GameSettings.Options.ANIMATED_REDSTONE, GameSettings.Options.ANIMATED_EXPLOSION, GameSettings.Options.ANIMATED_FLAME, GameSettings.Options.ANIMATED_SMOKE, GameSettings.Options.VOID_PARTICLES, GameSettings.Options.WATER_PARTICLES, GameSettings.Options.RAIN_SPLASH, GameSettings.Options.PORTAL_PARTICLES, GameSettings.Options.POTION_PARTICLES, GameSettings.Options.DRIPPING_WATER_LAVA, GameSettings.Options.ANIMATED_TERRAIN, GameSettings.Options.ANIMATED_TEXTURES, GameSettings.Options.FIREWORK_PARTICLES, GameSettings.Options.PARTICLES};

    public GuiAnimationSettingsOF(Screen guiscreen, GameSettings gamesettings)
    {
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.title = I18n.format("of.options.animationsTitle");

        for (int i = 0; i < enumOptions.length; ++i)
        {
            GameSettings.Options gamesettings$options = enumOptions[i];
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 + 21 * (i / 2) - 12;

            if (!gamesettings$options.getEnumFloat())
            {
                this.buttonList.add(new GuiOptionButtonOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options, this.settings.getKeyBinding(gamesettings$options)));
            }
            else
            {
                this.buttonList.add(new GuiOptionSliderOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options));
            }
        }

        this.buttonList.add(new GuiButton(210, this.width / 2 - 155, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOn")));
        this.buttonList.add(new GuiButton(211, this.width / 2 - 155 + 80, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOff")));
        this.buttonList.add(new GuiOptionButton(200, this.width / 2 + 5, this.height / 6 + 168 + 11, I18n.format("gui.done")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.isEnabled())
        {
            if (guibutton.getId() < 200 && guibutton instanceof GuiOptionButton)
            {
                this.settings.setOptionValue(((GuiOptionButton)guibutton).getOptions(), 1);
                guibutton.displayString = this.settings.getKeyBinding(GameSettings.Options.getEnumOptions(guibutton.getId()));
            }

            if (guibutton.getId() == 200)
            {
                saveSettings();
                changeScreen(this.prevScreen);
            }

            if (guibutton.getId() == 210)
            {
                getMc().gameSettings.setAllAnimations(true);
            }

            if (guibutton.getId() == 211)
            {
                getMc().gameSettings.setAllAnimations(false);
            }

            ScaledResolution scaledresolution = new ScaledResolution();
            this.setWorldAndResolution(scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int x, int y, float f)
    {
        this.drawDefaultBackground();
        getFr().drawCenteredString(this.title, this.width / 2, 15, 16777215);
        super.drawScreen(x, y, f);
    }
}
