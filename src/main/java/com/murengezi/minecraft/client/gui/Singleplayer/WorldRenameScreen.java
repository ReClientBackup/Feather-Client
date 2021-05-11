package com.murengezi.minecraft.client.gui.Singleplayer;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiTextField;
import com.murengezi.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-14 at 22:08
 */
public class WorldRenameScreen extends Screen {

    private final Screen previousScreen;
    private GuiTextField textField;
    private final String saveName;

    private static final int RENAME = 0, CANCEL = 1;

    public WorldRenameScreen(Screen previousScreen, String saveName) {
        this.previousScreen = previousScreen;
        this.saveName = saveName;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        addButton(new GuiButton(RENAME, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectWorld.renameButton")));
        addButton(new GuiButton(CANCEL, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        ISaveFormat saveFormat = getMc().getSaveLoader();
        WorldInfo worldinfo = saveFormat.getWorldInfo(saveName);
        String worldName = worldinfo.getWorldName();
        textField = new GuiTextField(2, width / 2 - 100, 60, 200, 20);
        textField.setFocused(true);
        textField.setText(worldName);
    }

    @Override
    public void updateScreen() {
        textField.updateCursorCounter();
        super.updateScreen();
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            switch (button.getId()) {
                case RENAME:
                    ISaveFormat saveFormat = getMc().getSaveLoader();
                    saveFormat.renameWorld(saveName, textField.getText().trim());
                    changeScreen(previousScreen);
                    break;
                case CANCEL:
                    changeScreen(previousScreen);
                    break;
            }
        }
        super.actionPerformed(button);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        textField.textBoxKeyTyped(typedChar, keyCode);
        getButton(RENAME).setEnabled(textField.getText().trim().length() > 0);

        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed(getButton(RENAME));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(mouseX, mouseY, 60);

        getFr().drawCenteredString(I18n.format("selectWorld.renameTitle"), this.width / 2, 20, 0xffffffff);
        getFr().drawString(I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, 0xffA0A0A0);
        textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
