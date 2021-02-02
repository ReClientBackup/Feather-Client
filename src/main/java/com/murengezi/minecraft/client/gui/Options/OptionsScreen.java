package com.murengezi.minecraft.client.gui.Options;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Options.Controls.ControlsScreen;
import com.murengezi.minecraft.client.gui.Options.ResourcePack.ResourcePacksScreen;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-13 at 14:21
 */
public class OptionsScreen extends Screen {

    private final GuiScreen previousScreen;
    private final GameSettings gameSettings;

    private static final int SOUNDS = 0;
    private static final int SKINCUSTOMISATION = 1;
    private static final int VIDEO = 2;
    private static final int CONTROLS = 3;
    private static final int LANGUAGE = 4;
    private static final int CHAT = 5;
    private static final int RESOURCEPACK = 6;
    private static final int SNOOPER = 7;
    private static final int DONE = 8;

    public OptionsScreen(GuiScreen previousScreen, GameSettings gameSettings) {
        this.previousScreen = previousScreen;
        this.gameSettings = gameSettings;
    }

    @Override
    public void initGui() {
        addButton(new GuiButton(SOUNDS, this.width / 2 - 155, this.height / 6 + 42, 150, 20, I18n.format("options.sounds")));
        addButton(new GuiButton(SKINCUSTOMISATION, this.width / 2 + 5, this.height / 6 + 42, 150, 20, I18n.format("options.skinCustomisation")));
        addButton(new GuiButton(VIDEO, this.width / 2 - 155, this.height / 6 + 66, 150, 20, I18n.format("options.video")));
        addButton(new GuiButton(CONTROLS, this.width / 2 + 5, this.height / 6 + 66, 150, 20, I18n.format("options.controls")));
        addButton(new GuiButton(LANGUAGE, this.width / 2 - 155, this.height / 6 + 90, 150, 20, I18n.format("options.language")));
        addButton(new GuiButton(CHAT, this.width / 2 + 5, this.height / 6 + 90, 150, 20, I18n.format("options.chat.title")));
        addButton(new GuiButton(RESOURCEPACK, this.width / 2 - 155, this.height / 6 + 114, 150, 20, I18n.format("options.resourcepack")));
        addButton(new GuiButton(SNOOPER, this.width / 2 + 5, this.height / 6 + 114, 150, 20, I18n.format("options.snooper.view")));
        addButton(new GuiButton(DONE, this.width / 2 - 75, this.height / 6 + 144, 150, 20, I18n.format("gui.done")));
        super.initGui();
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        changeScreen(this);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.getId()) {
            case SOUNDS:
                saveSettings();
                changeScreen(new SoundsOptionsScreen(this, gameSettings));
                break;
            case SKINCUSTOMISATION:
                saveSettings();
                changeScreen(new SkinOptionsScreen(this));
                break;
            case VIDEO:
                saveSettings();
                changeScreen(new GuiVideoSettings(this, gameSettings));
                break;
            case CONTROLS:
                saveSettings();
                changeScreen(new ControlsScreen(this, gameSettings));
                break;
            case LANGUAGE:
                saveSettings();
                changeScreen(new LanguageScreen(this, gameSettings, mc.getLanguageManager()));
                break;
            case CHAT:
                saveSettings();
                changeScreen(new ChatOptionsScreen(this, gameSettings));
                break;
            case RESOURCEPACK:
                saveSettings();
                changeScreen(new ResourcePacksScreen(this));
                break;
            case SNOOPER:
                saveSettings();
                changeScreen(new SnooperScreen(this, gameSettings));
                break;
            case DONE:
                saveSettings();
                changeScreen(previousScreen);
                break;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground(mouseX, mouseY, 120);

        drawRect(this.width / 2 - 160, this.height / 6 + 15, this.width / 2 + 160, this.height / 6 + 169, Integer.MIN_VALUE);
        fontRendererObj.drawCenteredString(EnumChatFormatting.UNDERLINE + I18n.format("options.title"), this.width / 2, this.height / 6 + 20, 0xffffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void changeScreen(GuiScreen guiScreen) {
        mc.displayGuiScreen(guiScreen);
    }

}
