package com.murengezi.minecraft.client.gui.Options;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Options.Controls.ControlsScreen;
import com.murengezi.minecraft.client.gui.Options.ResourcePack.ResourcePacksScreen;
import com.murengezi.minecraft.client.gui.Screen;
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

    private final Screen previousScreen;
    private final GameSettings gameSettings;

    private static final int SOUNDS = 0, SKINCUSTOMISATION = 1, VIDEO = 2, CONTROLS = 3, LANGUAGE = 4, CHAT = 5, RESOURCEPACK = 6, SNOOPER = 7, DONE = 8;

    public OptionsScreen(Screen previousScreen, GameSettings gameSettings) {
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
                changeScreen(new SoundsOptionsScreen(this));
                break;
            case SKINCUSTOMISATION:
                changeScreen(new SkinOptionsScreen(this));
                break;
            case VIDEO:
                changeScreen(new GuiVideoSettings(this));
                break;
            case CONTROLS:
                changeScreen(new ControlsScreen(this));
                break;
            case LANGUAGE:
                changeScreen(new LanguageScreen(this));
                break;
            case CHAT:
                changeScreen(new ChatOptionsScreen(this));
                break;
            case RESOURCEPACK:
                changeScreen(new ResourcePacksScreen(this));
                break;
            case SNOOPER:
                changeScreen(new SnooperScreen(this));
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
        drawWorldBackground(mouseX, mouseY, 120);

        drawRect(this.width / 2 - 160, this.height / 6 + 15, this.width / 2 + 160, this.height / 6 + 169, Integer.MIN_VALUE);
        getFr().drawCenteredString(EnumChatFormatting.UNDERLINE + I18n.format("options.title"), this.width / 2, this.height / 6 + 20, 0xffffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
