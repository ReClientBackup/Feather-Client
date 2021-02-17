package com.murengezi.minecraft.client.gui.Multiplayer;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-20 at 09:52
 */
public class DirectConnectScreen extends Screen {

    private final Screen parentScreen;
    private final ServerData serverData;
    private GuiTextField guiTextField;

    public static final int SELECT = 0;
    public static final int CANCEL = 1;

    public DirectConnectScreen(Screen parentScreen, ServerData serverData) {
        this.parentScreen = parentScreen;
        this.serverData = serverData;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(SELECT, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectServer.select")));
        this.buttonList.add(new GuiButton(CANCEL, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        this.guiTextField = new GuiTextField(2, this.width / 2 - 100, 116, 200, 20);
        this.guiTextField.setMaxStringLength(128);
        this.guiTextField.setFocused(true);
        this.guiTextField.setText(getMc().gameSettings.lastServer);
        getButton(SELECT).setEnabled(this.guiTextField.getText().length() > 0 && this.guiTextField.getText().split(":").length > 0);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        getMc().gameSettings.lastServer = guiTextField.getText();
        saveSettings();
        super.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            switch (button.getId()) {
                case SELECT:
                    this.serverData.serverIP = this.guiTextField.getText();
                    this.parentScreen.confirmClicked(true, 0);
                    break;
                case CANCEL:
                    this.parentScreen.confirmClicked(false, 0);
                    break;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.guiTextField.textBoxKeyTyped(typedChar, keyCode)) {
            getButton(SELECT).setEnabled(this.guiTextField.getText().length() > 0 && this.guiTextField.getText().split(":").length > 0);
        } else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            this.actionPerformed(getButton(SELECT));
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        this.guiTextField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(mouseX, mouseY, 60);
        getFr().drawCenteredString(I18n.format("selectServer.direct"), this.width / 2, 20, 16777215);
        getFr().drawString(I18n.format("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
        this.guiTextField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
