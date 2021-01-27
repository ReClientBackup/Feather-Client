package com.murengezi.minecraft.client.Gui.WorldSelection;

import com.murengezi.minecraft.client.Gui.Screen;
import com.murengezi.minecraft.client.Gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-14 at 22:08
 */
public class WorldRenameScreen extends Screen {

    private final GuiScreen previousScreen;
    private GuiTextField textField;
    private final String saveName;

    private static final int RENAME = 0;
    private static final int CANCEL = 1;

    public WorldRenameScreen(GuiScreen previousScreen, String saveName) {
        this.previousScreen = previousScreen;
        this.saveName = saveName;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        addButton(new GuiButton(RENAME, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectWorld.renameButton")));
        addButton(new GuiButton(CANCEL, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        ISaveFormat isaveformat = mc.getSaveLoader();
        WorldInfo worldinfo = isaveformat.getWorldInfo(saveName);
        String worldName = worldinfo.getWorldName();
        textField = new GuiTextField(2, fontRendererObj, width / 2 - 100, 60, 200, 20);
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
                    ISaveFormat isaveformat = mc.getSaveLoader();
                    isaveformat.renameWorld(saveName, textField.getText().trim());
                    mc.displayGuiScreen(previousScreen);
                    break;
                case CANCEL:
                    mc.displayGuiScreen(previousScreen);
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
        drawDefaultBackground(mouseX, mouseY, 60);

        this.drawCenteredString(this.fontRendererObj, I18n.format("selectWorld.renameTitle"), this.width / 2, 20, 0xffffffff);
        this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, 0xffA0A0A0);
        textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
