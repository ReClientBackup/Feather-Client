package com.murengezi.minecraft.client.gui.Multiplayer;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiTextField;
import com.murengezi.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-20 at 14:54
 */
public class AddServerScreen extends Screen {

    private final Screen previousScreen;
    private final ServerData serverData;
    private GuiTextField serverName, serverIp;

    private static final int ADD = 0;
    private static final int CANCEL = 1;
    private static final int RESOURCEPACK = 2;

    public AddServerScreen(Screen previousScreen, ServerData serverData) {
        this.previousScreen = previousScreen;
        this.serverData = serverData;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        addButton(new GuiButton(ADD, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.format("addServer.add")));
        addButton(new GuiButton(CANCEL, this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.format("gui.cancel")));
        addButton(new GuiButton(RESOURCEPACK, this.width / 2 - 100, this.height / 4 + 72, I18n.format("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText()));
        this.serverName = new GuiTextField(0, this.width / 2 - 100, 66, 200, 20);
        this.serverName.setFocused(true);
        this.serverName.setText(this.serverData.serverName);
        this.serverIp = new GuiTextField(1, this.width / 2 - 100, 106, 200, 20);
        this.serverIp.setMaxStringLength(128);
        this.serverIp.setText(this.serverData.serverIP);
        getButton(ADD).setEnabled(this.serverIp.getText().length() > 0 && this.serverIp.getText().split(":").length > 0 && this.serverName.getText().length() > 0);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            switch (button.getId()) {
                case ADD:
                    this.serverData.serverName = this.serverName.getText();
                    this.serverData.serverIP = this.serverIp.getText();
                    this.previousScreen.confirmClicked(true, 0);
                    break;
                case CANCEL:
                    this.previousScreen.confirmClicked(false, 0);
                    break;
                case RESOURCEPACK:
                    this.serverData.setResourceMode(ServerData.ServerResourceMode.values()[(this.serverData.getResourceMode().ordinal() + 1) % ServerData.ServerResourceMode.values().length]);
                    getButton(RESOURCEPACK).displayString = I18n.format("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText();
                    break;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void updateScreen() {
        this.serverName.updateCursorCounter();
        this.serverIp.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.serverName.textBoxKeyTyped(typedChar, keyCode);
        this.serverIp.textBoxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_TAB) {
            if (this.serverName.isFocused() || this.serverIp.isFocused()) {
                this.serverName.setFocused(!this.serverName.isFocused());
                this.serverIp.setFocused(!this.serverIp.isFocused());
            } else {
                this.serverName.setFocused(true);
            }
        }

        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            this.actionPerformed(getButton(ADD));
        }
        getButton(ADD).setEnabled(this.serverIp.getText().length() > 0 && this.serverIp.getText().split(":").length > 0 && this.serverName.getText().length() > 0);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.serverName.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverIp.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWorldBackground(mouseX, mouseY, 60);
        getFr().drawCenteredString(I18n.format("addServer.title"), this.width / 2, 17, 16777215);
        getFr().drawString(I18n.format("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
        getFr().drawString(I18n.format("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
        this.serverName.drawTextBox();
        this.serverIp.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
