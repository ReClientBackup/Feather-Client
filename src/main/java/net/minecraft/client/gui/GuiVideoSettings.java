package net.minecraft.client.gui;

import java.io.IOException;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.gui.GuiAnimationSettingsOF;
import net.optifine.gui.GuiDetailSettingsOF;
import net.optifine.gui.GuiOptionButtonOF;
import net.optifine.gui.GuiOptionSliderOF;
import net.optifine.gui.GuiOtherSettingsOF;
import net.optifine.gui.GuiPerformanceSettingsOF;
import net.optifine.gui.GuiQualitySettingsOF;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderOptions;
import net.optifine.shaders.gui.GuiShaders;

public class GuiVideoSettings extends GuiScreenOF
{
    private Screen parentGuiScreen;
    protected String screenTitle = "Video Settings";
    private GameSettings guiGameSettings;

    /** An array of all of GameSettings.Options's video options. */
    private static GameSettings.Options[] videoOptions = new GameSettings.Options[] {GameSettings.Options.GRAPHICS, GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION, GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.AO_LEVEL, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE, GameSettings.Options.USE_VBO, GameSettings.Options.GAMMA, GameSettings.Options.BLOCK_ALTERNATIVES, GameSettings.Options.DYNAMIC_LIGHTS, GameSettings.Options.DYNAMIC_FOV};
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public GuiVideoSettings(Screen parentScreenIn, GameSettings gameSettingsIn)
    {
        this.parentGuiScreen = parentScreenIn;
        this.guiGameSettings = gameSettingsIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.screenTitle = I18n.format("options.videoTitle");
        this.buttonList.clear();

        for (int i = 0; i < videoOptions.length; ++i)
        {
            GameSettings.Options gamesettings$options = videoOptions[i];

            if (gamesettings$options != null)
            {
                int j = this.width / 2 - 155 + i % 2 * 160;
                int k = this.height / 6 + 21 * (i / 2) - 12;

                if (gamesettings$options.getEnumFloat())
                {
                    this.buttonList.add(new GuiOptionSliderOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options));
                }
                else
                {
                    this.buttonList.add(new GuiOptionButtonOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options, this.guiGameSettings.getKeyBinding(gamesettings$options)));
                }
            }
        }

        int l = this.height / 6 + 21 * (videoOptions.length / 2) - 12;
        int i1 = 0;
        i1 = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiOptionButton(231, i1, l, Lang.get("of.options.shaders")));
        i1 = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiOptionButton(202, i1, l, Lang.get("of.options.quality")));
        l = l + 21;
        i1 = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiOptionButton(201, i1, l, Lang.get("of.options.details")));
        i1 = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiOptionButton(212, i1, l, Lang.get("of.options.performance")));
        l = l + 21;
        i1 = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiOptionButton(211, i1, l, Lang.get("of.options.animations")));
        i1 = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiOptionButton(222, i1, l, Lang.get("of.options.other")));
        l = l + 21;
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        this.actionPerformed(button, 1);
    }

    protected void actionPerformedRightClick(GuiButton p_actionPerformedRightClick_1_)
    {
        if (p_actionPerformedRightClick_1_.getId() == GameSettings.Options.GUI_SCALE.ordinal())
        {
            this.actionPerformed(p_actionPerformedRightClick_1_, -1);
        }
    }

    private void actionPerformed(GuiButton p_actionPerformed_1_, int p_actionPerformed_2_)
    {
        if (p_actionPerformed_1_.isEnabled())
        {
            int i = this.guiGameSettings.guiScale;

            if (p_actionPerformed_1_.getId() < 200 && p_actionPerformed_1_ instanceof GuiOptionButton)
            {
                this.guiGameSettings.setOptionValue(((GuiOptionButton)p_actionPerformed_1_).returnEnumOptions(), p_actionPerformed_2_);
                p_actionPerformed_1_.displayString = this.guiGameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(p_actionPerformed_1_.getId()));
            }

            if (p_actionPerformed_1_.getId() == 200)
            {
                saveSettings();
                changeScreen(this.parentGuiScreen);
            }

            if (this.guiGameSettings.guiScale != i)
            {
                ScaledResolution resolution = new ScaledResolution();
                this.setWorldAndResolution(resolution.getScaledWidth(), resolution.getScaledHeight());
            }

            if (p_actionPerformed_1_.getId() == 201)
            {
                saveSettings();
                GuiDetailSettingsOF guidetailsettingsof = new GuiDetailSettingsOF(this, this.guiGameSettings);
                changeScreen(guidetailsettingsof);
            }

            if (p_actionPerformed_1_.getId() == 202)
            {
                saveSettings();
                GuiQualitySettingsOF guiqualitysettingsof = new GuiQualitySettingsOF(this, this.guiGameSettings);
                changeScreen(guiqualitysettingsof);
            }

            if (p_actionPerformed_1_.getId() == 211)
            {
                saveSettings();
                GuiAnimationSettingsOF guianimationsettingsof = new GuiAnimationSettingsOF(this, this.guiGameSettings);
                changeScreen(guianimationsettingsof);
            }

            if (p_actionPerformed_1_.getId() == 212)
            {
                saveSettings();
                GuiPerformanceSettingsOF guiperformancesettingsof = new GuiPerformanceSettingsOF(this, this.guiGameSettings);
                changeScreen(guiperformancesettingsof);
            }

            if (p_actionPerformed_1_.getId() == 222)
            {
                saveSettings();
                GuiOtherSettingsOF guiothersettingsof = new GuiOtherSettingsOF(this, this.guiGameSettings);
                changeScreen(guiothersettingsof);
            }

            if (p_actionPerformed_1_.getId() == 231)
            {
                if (Config.isAntialiasing() || Config.isAntialiasingConfigured())
                {
                    Config.showGuiMessage(Lang.get("of.message.shaders.aa1"), Lang.get("of.message.shaders.aa2"));
                    return;
                }

                if (Config.isAnisotropicFiltering())
                {
                    Config.showGuiMessage(Lang.get("of.message.shaders.af1"), Lang.get("of.message.shaders.af2"));
                    return;
                }

                if (Config.isFastRender())
                {
                    Config.showGuiMessage(Lang.get("of.message.shaders.fr1"), Lang.get("of.message.shaders.fr2"));
                    return;
                }

                if (Config.getGameSettings().anaglyph)
                {
                    Config.showGuiMessage(Lang.get("of.message.shaders.an1"), Lang.get("of.message.shaders.an2"));
                    return;
                }

                saveSettings();
                GuiShaders guishaders = new GuiShaders(this, this.guiGameSettings);
                changeScreen(guishaders);
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        getFr().drawCenteredString(this.screenTitle, this.width / 2, 15, 16777215);
        String ofVersion = "OptiFine HD L5 Ultra";
        getFr().drawString(ofVersion, 2, this.height - 10, 8421504);
        String mcVersion = "Minecraft 1.8.10";
        int stringWidth = getFr().getStringWidth(mcVersion);
        getFr().drawString(mcVersion, this.width - stringWidth - 2, this.height - 10, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
    }

    public static int getButtonWidth(GuiButton button)
    {
        return button.getWidth();
    }

    public static int getButtonHeight(GuiButton button)
    {
        return button.getHeight();
    }

    public static String getGuiChatText(GuiChat chat) {
        return chat.inputField.getText();
    }
}
