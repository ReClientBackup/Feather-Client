package net.minecraft.client.gui;

import java.io.IOException;

import com.murengezi.minecraft.client.gui.Chat.ChatScreen;
import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.resources.I18n;
import com.murengezi.minecraft.client.settings.GameSettings;
import net.optifine.config.Config;
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

public class GuiVideoSettings extends GuiScreenOF {
    
    private final Screen previousScreen;
    private static final GameSettings.Options[] videoOptions = new GameSettings.Options[] {GameSettings.Options.GRAPHICS, GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION, GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.AO_LEVEL, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE, GameSettings.Options.USE_VBO, GameSettings.Options.GAMMA, GameSettings.Options.BLOCK_ALTERNATIVES, GameSettings.Options.DYNAMIC_LIGHTS, GameSettings.Options.DYNAMIC_FOV};
    private final TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    private static final int SHADERS = 231, QUALITY = 202, DETAILS = 201, PERFORMANCE = 212, ANIMATIONS = 211, OTHER = 222, DONE = 200;

    public GuiVideoSettings(Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        for (int i = 0; i < videoOptions.length; ++i) {
            GameSettings.Options option = videoOptions[i];

            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 + 21 * (i / 2) - 12;

            if (option.getEnumFloat()) {
                addButton(new GuiOptionSliderOF(option.returnEnumOrdinal(), j, k, option));
            } else {
                addButton(new GuiOptionButtonOF(option.returnEnumOrdinal(), j, k, option, getGs().getKeyBinding(option)));
            }
        }

        int l = this.height / 6 + 21 * (videoOptions.length / 2) - 12;
        addButton(new GuiOptionButton(SHADERS, this.width / 2 - 155, l, Lang.get("of.options.shaders")));
        addButton(new GuiOptionButton(QUALITY, this.width / 2 + 5, l, Lang.get("of.options.quality")));
        l = l + 21;
        addButton(new GuiOptionButton(DETAILS, this.width /2 - 155, l, Lang.get("of.options.details")));
        addButton(new GuiOptionButton(PERFORMANCE, this.width / 2 + 5, l, Lang.get("of.options.performance")));
        l = l + 21;
        addButton(new GuiOptionButton(ANIMATIONS, this.width /2 - 155, l, Lang.get("of.options.animations")));
        addButton(new GuiOptionButton(OTHER, this.width / 2 + 5, l, Lang.get("of.options.other")));
        addButton(new GuiButton(DONE, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
        super.initGui();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        this.actionPerformed(button, 1);
    }

    protected void actionPerformedRightClick(GuiButton button) {
        if (button.getId() == GameSettings.Options.GUI_SCALE.ordinal()) {
            this.actionPerformed(button, -1);
        }
    }

    private void actionPerformed(GuiButton button, int p_actionPerformed_2_) {
        if (button.isEnabled()) {
            int i = getGs().guiScale;

            if (button.getId() < 200 && button instanceof GuiOptionButton) {
                getGs().setOptionValue(((GuiOptionButton)button).getOptions(), p_actionPerformed_2_);
                button.displayString = getGs().getKeyBinding(GameSettings.Options.getEnumOptions(button.getId()));
            }

            if (button.getId() == DONE) {
                saveSettings();
                changeScreen(this.previousScreen);
            }

            if (getGs().guiScale != i) {
                ScaledResolution resolution = new ScaledResolution();
                this.setWorldAndResolution(resolution.getScaledWidth(), resolution.getScaledHeight());
            }

            if (button.getId() == DETAILS) {
                saveSettings();
                changeScreen(new GuiDetailSettingsOF(this, getGs()));
            }

            if (button.getId() == QUALITY) {
                saveSettings();
                changeScreen(new GuiQualitySettingsOF(this));
            }

            if (button.getId() == ANIMATIONS) {
                saveSettings();
                changeScreen(new GuiAnimationSettingsOF(this, getGs()));
            }

            if (button.getId() == PERFORMANCE) {
                saveSettings();
                changeScreen(new GuiPerformanceSettingsOF(this, getGs()));
            }

            if (button.getId() == OTHER) {
                saveSettings();
                changeScreen(new GuiOtherSettingsOF(this, getGs()));
            }

            if (button.getId() == SHADERS) {
                if (Config.isAntialiasing() || Config.isAntialiasingConfigured()) {
                    Config.showGuiMessage(Lang.get("of.message.shaders.aa1"), Lang.get("of.message.shaders.aa2"));
                    return;
                }

                if (Config.isAnisotropicFiltering()) {
                    Config.showGuiMessage(Lang.get("of.message.shaders.af1"), Lang.get("of.message.shaders.af2"));
                    return;
                }

                if (Config.isFastRender()) {
                    Config.showGuiMessage(Lang.get("of.message.shaders.fr1"), Lang.get("of.message.shaders.fr2"));
                    return;
                }

                if (Config.getGameSettings().anaglyph) {
                    Config.showGuiMessage(Lang.get("of.message.shaders.an1"), Lang.get("of.message.shaders.an2"));
                    return;
                }

                saveSettings();
                changeScreen(new GuiShaders(this, getGs()));
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(mouseX, mouseY, 60);
        getFr().drawCenteredString(I18n.format("options.videoTitle"), this.width / 2, 15, 16777215);
        String ofVersion = "OptiFine HD M5 Ultra";
        getFr().drawString(ofVersion, 2, this.height - 10, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static int getButtonWidth(GuiButton button) {
        return button.getWidth();
    }

    public static int getButtonHeight(GuiButton button) {
        return button.getHeight();
    }

    public static String getGuiChatText(ChatScreen chat) {
        return chat.getInputField().getText();
    }
}
